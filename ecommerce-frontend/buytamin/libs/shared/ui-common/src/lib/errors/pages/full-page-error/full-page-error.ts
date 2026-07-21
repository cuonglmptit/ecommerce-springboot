import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'buytm-full-page-error',
  imports: [RouterLink],
  templateUrl: './full-page-error.html',
  styleUrl: './full-page-error.scss',
})
export class FullPageError {
  imageUrl = input('assets/images/default-404.png');
  message = input('Trang này không có sẵn. Mong bạn thông cảm.');
  homeLink = input('/');
  homeButtonText = input('Quay lại trang chủ');
}
