import { render, screen } from '@testing-library/react'
import { describe, it, expect, vi } from 'vitest'
import { ResourceTable } from '../ResourceTable'

const columns = [
  { key: 'id' as const, header: 'ID' },
  { key: 'name' as const, header: 'Name' },
]

const defaultProps = {
  columns,
  items: [] as Array<{ id: string; name: string }>,
  page: 0,
  totalPages: 0,
  totalElements: 0,
  loading: false,
  error: null as string | null,
  onGoToPage: vi.fn(),
  onRetry: vi.fn(),
}

describe('ResourceTable', () => {
  it('shows loading state', () => {
    render(<ResourceTable {...defaultProps} loading={true} />)
    expect(screen.getByText('Loading...')).toBeInTheDocument()
  })

  it('shows error state with retry button', () => {
    render(<ResourceTable {...defaultProps} error="Something went wrong" />)
    expect(screen.getByText('Something went wrong')).toBeInTheDocument()
    expect(screen.getByText('Retry')).toBeInTheDocument()
  })

  it('shows empty state when no items', () => {
    render(<ResourceTable {...defaultProps} items={[]} />)
    expect(screen.getByText('No items found')).toBeInTheDocument()
  })

  it('renders table with items', () => {
    const items = [{ id: '1', name: 'test' }]
    render(<ResourceTable {...defaultProps} items={items} totalPages={1} totalElements={1} />)
    expect(screen.getByText('test')).toBeInTheDocument()
    expect(screen.getByText('Page 1 of 1 (1 items)')).toBeInTheDocument()
  })

  it('disables previous on first page', () => {
    const items = [{ id: '1', name: 'test' }]
    render(<ResourceTable {...defaultProps} items={items} totalPages={2} totalElements={2} />)
    expect(screen.getByText('Previous')).toBeDisabled()
    expect(screen.getByText('Next')).not.toBeDisabled()
  })

  it('disables next on last page', () => {
    const items = [{ id: '1', name: 'test' }]
    render(<ResourceTable {...defaultProps} items={items} page={1} totalPages={2} totalElements={2} />)
    expect(screen.getByText('Next')).toBeDisabled()
  })
})
