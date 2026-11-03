export interface ApiError {
  timestamp?: string;
  status: number;
  error?: string;
  message: string;
  path?: string;
  validationErrors?: Record<string, string>;
}

export async function request<T>(url: string, options?: RequestInit): Promise<T> {
  const headers = {
    'Content-Type': 'application/json',
    ...(options?.headers || {})
  };

  try {
    const res = await fetch(url, {
      ...options,
      headers
    });

    if (!res.ok) {
      let errorPayload: ApiError;
      try {
        errorPayload = await res.json();
      } catch {
        errorPayload = {
          status: res.status,
          message: res.statusText || 'An unexpected HTTP error occurred'
        };
      }
      throw errorPayload;
    }

    if (res.status === 204) {
      return {} as T;
    }

    return await res.json();
  } catch (err: unknown) {
    if ((err as ApiError)?.status) {
      throw err;
    }
    // Network or connectivity error
    const netErr: ApiError = {
      status: 0,
      message: (err as Error)?.message || 'Network connection failed. Verify backend services are running.'
    };
    throw netErr;
  }
}
