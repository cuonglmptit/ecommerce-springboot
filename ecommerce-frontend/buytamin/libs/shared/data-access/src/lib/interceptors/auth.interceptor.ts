import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // 1. Lấy chuỗi JWT Access Token từ LocalStorage (hoặc inject từ AuthStore)
  const accessToken = localStorage.getItem('access_token');
  // 2. Nếu tồn tại token, tiến hành clone request và đính kèm Bearer Header
  if (accessToken) {
    req = req.clone({
      setHeaders: {
        // Authorization: `Bearer ${accessToken}`,
        Authorization: `Bearer ${accessToken}`,
      },
    });
  }

  // 3. Chuyển tiếp request đã được xử lý sang interceptor kế tiếp hoặc backend
  return next(req);
};
