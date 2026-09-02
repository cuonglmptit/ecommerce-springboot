import { Component, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MediaApi, MediaItemResponse } from '@buytamin/shared/data-access';
import { FileDropzoneDirective } from '@buytamin/shared/ui-common';
import { ProductFormData } from '../product-form-data';
import { ProductMedia } from '@buytamin/seller/data-access';

interface UploadingItem {
  tempId: string;
  fileName: string;
  progress: boolean;
}

@Component({
  selector: 'seller-media-upload',
  standalone: true,
  imports: [CommonModule, FileDropzoneDirective],
  templateUrl: './media-upload.html',
  styleUrl: './media-upload.scss',
})
export class MediaUpload {
  private readonly mediaApi = inject(MediaApi);
  protected readonly formDataService = inject(ProductFormData);

  private static readonly MAX_MEDIA_COUNT = 7;
  private static readonly MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
  private static readonly ALLOWED_TYPES = [
    'image/jpeg',
    'image/png',
    'image/webp',
  ];

  protected readonly uploadingQueue = signal<UploadingItem[]>([]);
  protected readonly errorMessage = signal<string | null>(null);

  protected readonly mediaList = computed(() =>
    this.formDataService.productMediaList(),
  );

  protected readonly canUploadMore = computed(() => {
    return (
      this.mediaList().length + this.uploadingQueue().length <
      MediaUpload.MAX_MEDIA_COUNT
    );
  });

  protected readonly remainingSlots = computed(() => {
    return (
      MediaUpload.MAX_MEDIA_COUNT -
      (this.mediaList().length + this.uploadingQueue().length)
    );
  });

  /**
   * Xử lý khi người dùng chọn file qua nút bấm Input
   */
  onInputFileChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    this.processFiles(Array.from(input.files));
    input.value = ''; // Reset input để có thể chọn lại file cùng tên nếu muốn
  }

  /**
   * Hàm nhận danh sách File (từ nút bấm hoặc từ Directive kéo thả phát ra)
   */
  processFiles(files: File[]): void {
    this.errorMessage.set(null);

    const availableSlots = this.remainingSlots();
    if (availableSlots <= 0) {
      this.errorMessage.set(
        `Đã đạt giới hạn tối đa ${MediaUpload.MAX_MEDIA_COUNT} hình ảnh cho sản phẩm.`,
      );
      return;
    }

    // Chỉ lấy số lượng file vừa đủ slot còn lại
    const filesToUpload = files.slice(0, availableSlots);

    for (const file of filesToUpload) {
      if (!MediaUpload.ALLOWED_TYPES.includes(file.type)) {
        this.errorMessage.set(
          `File "${file.name}" không hợp lệ. Chỉ chấp nhận JPG, PNG, WEBP.`,
        );
        continue;
      }

      if (file.size > MediaUpload.MAX_FILE_SIZE) {
        this.errorMessage.set(
          `File "${file.name}" vượt quá dung lượng tối đa 10MB.`,
        );
        continue;
      }

      this.executeUpload(file);
    }
  }

  private executeUpload(file: File): void {
    const tempId = crypto.randomUUID();

    // Đẩy vào hàng đợi hiển thị Loading
    this.uploadingQueue.update((prev) => [
      ...prev,
      { tempId, fileName: file.name, progress: true },
    ]);

    this.mediaApi.uploadMediaDirect(file, 'PUBLIC').subscribe({
      next: (res: MediaItemResponse) => {
        // Xóa khỏi hàng đợi loading
        this.uploadingQueue.update((prev) =>
          prev.filter((item) => item.tempId !== tempId),
        );

        // Thêm vào Form State
        const isFirstImage = this.mediaList().length === 0;
        const newMedia: ProductMedia = {
          mediaId: res.id,
          url: res.url,
          alt: res.alt || file.name,
          isThumbnail: isFirstImage,
          sortOrder: this.mediaList().length,
        };

        this.formDataService.addMedia(newMedia);
      },
      error: (err) => {
        this.uploadingQueue.update((prev) =>
          prev.filter((item) => item.tempId !== tempId),
        );
        this.errorMessage.set(
          `Lỗi tải file "${file.name}": ${err.message || 'Không thể kết nối máy chủ.'}`,
        );
      },
    });
  }

  /**
   * Chọn một ảnh làm Thumbnail (Ảnh bìa)
   */
  setThumbnail(index: number): void {
    this.formDataService.setThumbnailIndex(index);
  }

  /**
   * Xóa ảnh khỏi danh sách
   */
  removeMedia(index: number): void {
    this.formDataService.removeMedia(index);
  }
}
