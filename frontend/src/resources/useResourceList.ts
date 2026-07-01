import { useCallback, useEffect, useRef, useState } from 'react'
import { apiFetch } from '../api/client'
import { useAuth } from '../auth/useAuth'

export interface Page<T> {
  content: T[]
  page: {
    size: number
    totalElements: number
    totalPages: number
    number: number
  }
}

export interface UseResourceListResult<T> {
  items: T[]
  page: number
  totalPages: number
  totalElements: number
  loading: boolean
  error: string | null
  goToPage: (page: number) => void
  retry: () => void
}

export function useResourceList<T>(resourcePath: string): UseResourceListResult<T> {
  const { getToken } = useAuth()
  const [items, setItems] = useState<T[]>([])
  const [page, setPage] = useState({ number: 0, totalPages: 0, totalElements: 0, size: 0 })
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const abortRef = useRef<AbortController | null>(null)

  const fetchPage = useCallback(async (pageNumber: number) => {
    abortRef.current?.abort()
    const controller = new AbortController()
    abortRef.current = controller

    setLoading(true)
    setError(null)

    try {
      const token = getToken()
      const data = await apiFetch<Page<T>>(
        `${resourcePath}?page=${pageNumber}&size=20`,
        token,
        { signal: controller.signal },
      )
      setItems(data.content ?? [])
      setPage(data.page ?? { number: 0, totalPages: 0, totalElements: 0, size: 0 })
    } catch (err) {
      if (err instanceof Error && err.name === 'AbortError') return
      setError('Failed to load data')
    } finally {
      if (!controller.signal.aborted) {
        setLoading(false)
      }
    }
  }, [resourcePath, getToken])

  useEffect(() => {
    fetchPage(0)
    return () => abortRef.current?.abort()
  }, [fetchPage])

  const goToPage = useCallback((newPage: number) => {
    fetchPage(newPage)
  }, [fetchPage])

  const retry = useCallback(() => {
    fetchPage(page.number)
  }, [fetchPage, page.number])

  return {
    items,
    page: page.number,
    totalPages: page.totalPages,
    totalElements: page.totalElements,
    loading,
    error,
    goToPage,
    retry,
  }
}
