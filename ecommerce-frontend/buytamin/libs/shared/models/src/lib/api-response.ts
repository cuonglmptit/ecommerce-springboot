export interface ApiResponse<T> {
  success: boolean;
  code: string;
  userMessage: string;
  data: T;
  devMessage?: string | null;
  timestamp?: string;
  requestId?: string | null;
  extra?: Record<string, unknown>;
}
