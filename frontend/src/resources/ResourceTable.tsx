import type { ReactNode } from 'react'
import styles from './ResourceTable.module.css'

export interface ColumnConfig<T> {
  key: keyof T & string
  header: string
  render?: (item: T) => ReactNode
}

interface ResourceTableProps<T> {
  columns: ColumnConfig<T>[]
  items: T[]
  page: number
  totalPages: number
  totalElements: number
  loading: boolean
  error: string | null
  onGoToPage: (page: number) => void
  onRetry: () => void
}

export function ResourceTable<T extends { id: string }>({
  columns,
  items,
  page,
  totalPages,
  totalElements,
  loading,
  error,
  onGoToPage,
  onRetry,
}: ResourceTableProps<T>) {
  if (loading) {
    return (
      <div className={styles.stateMessage}>
        <div className={styles.spinner} />
        <span>Loading...</span>
      </div>
    )
  }

  if (error) {
    return (
      <div className={styles.stateMessage}>
        <p className={styles.errorText}>{error}</p>
        <button onClick={onRetry} className={styles.retryButton}>
          Retry
        </button>
      </div>
    )
  }

  if (items.length === 0) {
    return <div className={styles.stateMessage}>No items found</div>
  }

  return (
    <div>
      <table className={styles.table}>
        <thead>
          <tr>
            {columns.map((col) => (
              <th key={col.key} className={styles.th}>{col.header}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {items.map((item, idx) => (
            <tr key={item.id ?? String(idx)} className={styles.tr}>
              {columns.map((col) => (
                <td key={col.key} className={styles.td}>
                  {col.render
                    ? col.render(item)
                    : String((item as Record<string, unknown>)[col.key] ?? '')}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>

      <div className={styles.pagination}>
        <button
          onClick={() => onGoToPage(page - 1)}
          disabled={page === 0}
          className={styles.pageButton}
        >
          Previous
        </button>
        <span className={styles.pageInfo}>
          Page {page + 1} of {totalPages} ({totalElements} items)
        </span>
        <button
          onClick={() => onGoToPage(page + 1)}
          disabled={page >= totalPages - 1}
          className={styles.pageButton}
        >
          Next
        </button>
      </div>
    </div>
  )
}
