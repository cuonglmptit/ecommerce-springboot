import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // 1. Lấy chuỗi JWT Access Token từ LocalStorage (hoặc inject từ AuthStore)
  // const accessToken = localStorage.getItem('access_token');
  const accessToken =
    'eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIzIiwiYXVkIjoiY2xpZW50IiwibmJmIjoxNzg1NzMxMzI1LCJzY29wZSI6WyJvcGVuaWQiXSwicm9sZXMiOlsiQ1VTVE9NRVIiXSwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwIiwiZXhwIjoxNzg1NzM3MzI1LCJpYXQiOjE3ODU3MzEzMjUsImp0aSI6IjU5YWI0NmM1LTljZTMtNGIwZC1hOGNlLWFjMDQzNDlkMTU4MCIsImVtYWlsIjoiY3Vvbmdjb2RlcnZpcHBybzIwMHhAZ21haWwuY29tIiwidXNlcm5hbWUiOiJjdW9uZyJ9.I-_H4RKzutaZelJ0BIT2sXUIa6XwQFyUtr7HWKnkohYf9SrFmNQgiaM_g-cNEL2XQZnqb5uWiN1fMVmO9VaJPoyJJbJeKwcS-vUspPIJkIs1lRA5EsraEPAv_gokelEb5xyF2zL4gLhWXRcMOFm-IQX1oDLeYRKY85Mb2FGcyEr0h9TTlNC2Y4jwg2G14zFWZOjPdYRgZTwjbhGysnwNJfJkjpedgL8Dgfx0u2GwOI4KawO7Lf6lVw79FuJ_rgd3nC860X-3tIIAev3yR6moEBIwaGsbzi7hzmZ2JM_xrj4VlghCrAw-_QD9RpCpvgvBLko63Kjz-LrB8rbzFx0yCg\n';
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
