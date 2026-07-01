import { useResourceList } from './useResourceList'
import { ResourceTable, type ColumnConfig } from './ResourceTable'
import styles from './ResourceListPage.module.css'

interface ResourceItem {
  id: string
  name: string
}

interface ResourceListPageProps {
  resourcePath: string
  title: string
}

const columns: ColumnConfig<ResourceItem>[] = [
  { key: 'id', header: 'ID' },
  { key: 'name', header: 'Name' },
]

export function ResourceListPage({ resourcePath, title }: ResourceListPageProps) {
  const { items, page, totalPages, totalElements, loading, error, goToPage, retry } =
    useResourceList<ResourceItem>(resourcePath)

  return (
    <div>
      <h1 className={styles.title}>{title}</h1>
      <ResourceTable
        columns={columns}
        items={items}
        page={page}
        totalPages={totalPages}
        totalElements={totalElements}
        loading={loading}
        error={error}
        onGoToPage={goToPage}
        onRetry={retry}
      />
    </div>
  )
}
