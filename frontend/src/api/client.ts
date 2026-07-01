interface RequestOptions {
  method?: string
  headers?: Record<string, string>
  body?: string
  signal?: AbortSignal
}

export async function apiFetch<T>(path: string, token: string | undefined, options: RequestOptions = {}): Promise<T> {
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...options.headers,
  }

  if (token) {
    headers['Authorization'] = `Bearer ${token}`
  }

  const response = await fetch(path, {
    method: options.method ?? 'GET',
    headers,
    body: options.body,
    signal: options.signal,
  })

  const body = await response.text()

  if (!response.ok) {
    throw new Error(`API error: ${response.status}${body ? ` — ${body}` : ''}`)
  }

  return body ? (JSON.parse(body) as T) : (undefined as T)
}
