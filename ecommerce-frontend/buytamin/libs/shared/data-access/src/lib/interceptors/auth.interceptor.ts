import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const accessToken = localStorage.getItem('access_token');

  // 1. Chỉ đính kèm Bearer Token cho các API nội bộ của Backend (/api/v1/...)
  const isInternalApi =
    req.url.startsWith('/api') || req.url.includes('/api/v1');

  if (accessToken && isInternalApi) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${accessToken}`,
      },
    });
  }

  return next(req);
};
