import { Directive, output, signal } from '@angular/core';

@Directive({
  selector: '[buytmFileDropzoneDirective]',
  host: {
    '[class.drag-over]': 'isHovered()',
    '(dragover)': 'onDragOver($event)',
    '(dragenter)': 'onDragEnter($event)',
    '(dragleave)': 'onDragLeave($event)',
    '(drop)': 'onDrop($event)',
  },
})
export class FileDropzoneDirective {
  /**
   * Signal Output phát ra danh sách File khi người dùng thả file thành công
   */
  readonly filesDropped = output<File[]>();

  /**
   * Signal Output thông báo trạng thái hover
   */
  readonly fileHovered = output<boolean>();

  protected readonly isHovered = signal(false);
  private dragCounter = 0;

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();

    if (event.dataTransfer?.types.includes('Files')) {
      event.dataTransfer.dropEffect = 'copy';
      if (!this.isHovered()) {
        this.isHovered.set(true);
        this.fileHovered.emit(true);
      }
    }
  }

  onDragEnter(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();

    if (event.dataTransfer?.types.includes('Files')) {
      this.dragCounter++;
    }
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();

    this.dragCounter--;
    if (this.dragCounter <= 0) {
      this.dragCounter = 0;
      this.isHovered.set(false);
      this.fileHovered.emit(false);
    }
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();

    this.dragCounter = 0;
    this.isHovered.set(false);
    this.fileHovered.emit(false);

    const files = event.dataTransfer?.files;
    if (files && files.length > 0) {
      this.filesDropped.emit(Array.from(files));
    }
  }
}
