import { useCallback, useEffect, useRef, useState } from 'react'
import { useAuth } from '../auth/useAuth'
import {
  fetchNutrients,
  fetchSuggestions,
  createNutrient,
  updateNutrient,
  deleteNutrient,
  suggestNutrient,
  voteOnSuggestion,
  approveSuggestion,
  hasLink,
} from '../api/nutrients'
import type { NutrientResponse, NutrientRequest, SuggestionResponse, SuggestionRequest } from '../api/nutrients'
import { NutrientForm } from './NutrientForm'
import { SuggestForm } from './SuggestForm'
import styles from './NutrientsPage.module.css'

export function NutrientsPage() {
  const { getToken } = useAuth()
  const [activeTab, setActiveTab] = useState<'active' | 'suggestions'>('active')

  const [nutrients, setNutrients] = useState<NutrientResponse[]>([])
  const [nPage, setNPage] = useState({ number: 0, totalPages: 0, totalElements: 0 })
  const [nLinks, setNLinks] = useState<Record<string, unknown>>({})
  const [nLoading, setNLoading] = useState(true)
  const [nError, setNError] = useState<string | null>(null)
  const nAbortRef = useRef<AbortController | null>(null)

  const [suggestions, setSuggestions] = useState<SuggestionResponse[]>([])
  const [sPage, setSPage] = useState({ number: 0, totalPages: 0, totalElements: 0 })
  const [sLinks, setSLinks] = useState<Record<string, unknown>>({})
  const [sLoading, setSLoading] = useState(true)
  const [sError, setSError] = useState<string | null>(null)
  const sAbortRef = useRef<AbortController | null>(null)

  const [editing, setEditing] = useState<NutrientResponse | null>(null)
  const [creating, setCreating] = useState(false)
  const [showSuggestForm, setShowSuggestForm] = useState(false)

  const loadNutrients = useCallback(async (pageNumber: number) => {
    nAbortRef.current?.abort()
    const controller = new AbortController()
    nAbortRef.current = controller
    setNLoading(true)
    setNError(null)
    try {
      const data = await fetchNutrients(getToken(), pageNumber)
      if (!controller.signal.aborted) {
        setNutrients(data._embedded?.nutrients ?? [])
        setNPage({ number: data.page.number, totalPages: data.page.totalPages, totalElements: data.page.totalElements })
        setNLinks(data._links ?? {})
      }
    } catch (err) {
      if (err instanceof Error && err.name === 'AbortError') return
      setNError('Failed to load nutrients')
    } finally {
      if (!controller.signal.aborted) setNLoading(false)
    }
  }, [getToken])

  const loadSuggestions = useCallback(async (pageNumber: number) => {
    sAbortRef.current?.abort()
    const controller = new AbortController()
    sAbortRef.current = controller
    setSLoading(true)
    setSError(null)
    try {
      const data = await fetchSuggestions(getToken(), pageNumber)
      if (!controller.signal.aborted) {
        setSuggestions(data._embedded?.suggestions ?? [])
        setSPage({ number: data.page.number, totalPages: data.page.totalPages, totalElements: data.page.totalElements })
        setSLinks(data._links ?? {})
      }
    } catch (err) {
      if (err instanceof Error && err.name === 'AbortError') return
      setSError('Failed to load suggestions')
    } finally {
      if (!controller.signal.aborted) setSLoading(false)
    }
  }, [getToken])

  useEffect(() => {
    loadNutrients(0)
    return () => nAbortRef.current?.abort()
  }, [loadNutrients])

  useEffect(() => {
    loadSuggestions(0)
    return () => sAbortRef.current?.abort()
  }, [loadSuggestions])

  const handleCreate = async (request: NutrientRequest) => {
    await createNutrient(getToken(), request)
    setCreating(false)
    loadNutrients(nPage.number)
  }

  const handleUpdate = async (request: NutrientRequest) => {
    await updateNutrient(getToken(), editing!.id, request)
    setEditing(null)
    loadNutrients(nPage.number)
  }

  const handleDelete = async (id: string) => {
    if (!window.confirm('Delete this nutrient?')) return
    await deleteNutrient(getToken(), id)
    loadNutrients(nPage.number)
  }

  const handleSuggest = async (request: SuggestionRequest) => {
    await suggestNutrient(getToken(), request)
    setShowSuggestForm(false)
    loadSuggestions(sPage.number)
  }

  const handleVote = async (id: string) => {
    await voteOnSuggestion(getToken(), id)
    loadSuggestions(sPage.number)
  }

  const handleApprove = async (id: string) => {
    await approveSuggestion(getToken(), id)
    loadSuggestions(sPage.number)
    loadNutrients(nPage.number)
  }

  const token = getToken()
  const hasCreateLink = token && 'create-nutrient' in nLinks
  const hasSuggestLink = token && 'suggest-nutrient' in sLinks

  return (
    <div>
      <div className={styles.header}>
        <h1 className={styles.title}>Nutrients</h1>
        <div className={styles.tabs}>
          <button
            className={activeTab === 'active' ? styles.tabActive : styles.tab}
            onClick={() => setActiveTab('active')}
          >
            Active
          </button>
          <button
            className={activeTab === 'suggestions' ? styles.tabActive : styles.tab}
            onClick={() => setActiveTab('suggestions')}
          >
            Suggestions
          </button>
        </div>
        <div className={styles.headerActions}>
          {activeTab === 'active' && hasCreateLink && (
            <button className={styles.createButton} onClick={() => setCreating(true)}>
              + Create
            </button>
          )}
          {activeTab === 'suggestions' && hasSuggestLink && (
            <button className={styles.createButton} onClick={() => setShowSuggestForm(true)}>
              + Suggest
            </button>
          )}
        </div>
      </div>

      {activeTab === 'active' && (
        <>
          {nLoading && (
            <div className={styles.stateMessage}>
              <div className={styles.spinner} />
              <span>Loading...</span>
            </div>
          )}
          {nError && (
            <div className={styles.stateMessage}>
              <p className={styles.errorText}>{nError}</p>
              <button className={styles.retryButton} onClick={() => loadNutrients(nPage.number)}>Retry</button>
            </div>
          )}
          {!nLoading && !nError && nutrients.length === 0 && (
            <div className={styles.stateMessage}>No nutrients found</div>
          )}
          {!nLoading && !nError && nutrients.length > 0 && (
            <>
              <table className={styles.table}>
                <thead>
                  <tr>
                    <th className={styles.th}>Name</th>
                    <th className={styles.th}>kcal / g</th>
                    <th className={styles.th}>Unit</th>
                    {token && <th className={styles.th}>Actions</th>}
                  </tr>
                </thead>
                <tbody>
                  {nutrients.map((n) => (
                    <tr key={n.id} className={styles.tr}>
                      <td className={styles.td}>{n.name}</td>
                      <td className={styles.td}>{n.kcalPerGram ?? '—'}</td>
                      <td className={styles.td}>{n.defaultUnit}</td>
                      {token && (
                        <td className={styles.td}>
                          {hasLink(n, 'edit') && (
                            <button className={styles.actionButton} onClick={() => setEditing(n)}>Edit</button>
                          )}
                          {hasLink(n, 'delete') && (
                            <button className={styles.actionButtonDanger} onClick={() => handleDelete(n.id)}>Delete</button>
                          )}
                        </td>
                      )}
                    </tr>
                  ))}
                </tbody>
              </table>
              <div className={styles.pagination}>
                <button className={styles.pageButton} disabled={nPage.number === 0} onClick={() => loadNutrients(nPage.number - 1)}>
                  Previous
                </button>
                <span className={styles.pageInfo}>
                  Page {nPage.number + 1} of {nPage.totalPages} ({nPage.totalElements} items)
                </span>
                <button className={styles.pageButton} disabled={nPage.number >= nPage.totalPages - 1} onClick={() => loadNutrients(nPage.number + 1)}>
                  Next
                </button>
              </div>
            </>
          )}
        </>
      )}

      {activeTab === 'suggestions' && (
        <>
          {sLoading && (
            <div className={styles.stateMessage}>
              <div className={styles.spinner} />
              <span>Loading...</span>
            </div>
          )}
          {sError && (
            <div className={styles.stateMessage}>
              <p className={styles.errorText}>{sError}</p>
              <button className={styles.retryButton} onClick={() => loadSuggestions(sPage.number)}>Retry</button>
            </div>
          )}
          {!sLoading && !sError && suggestions.length === 0 && (
            <div className={styles.stateMessage}>No suggestions yet</div>
          )}
          {!sLoading && !sError && suggestions.length > 0 && (
            <>
              <table className={styles.table}>
                <thead>
                  <tr>
                    <th className={styles.th}>Name</th>
                    <th className={styles.th}>kcal / g</th>
                    <th className={styles.th}>Unit</th>
                    <th className={styles.th}>Votes</th>
                    <th className={styles.th}>Author</th>
                    {token && <th className={styles.th}>Actions</th>}
                  </tr>
                </thead>
                <tbody>
                  {suggestions.map((s) => (
                    <tr key={s.id} className={styles.tr}>
                      <td className={styles.td}>{s.name}</td>
                      <td className={styles.td}>{s.kcalPerGram ?? '—'}</td>
                      <td className={styles.td}>{s.defaultUnit}</td>
                      <td className={styles.td}>{s.voteCount}</td>
                      <td className={styles.td}>{s.authorId ?? '—'}</td>
                      {token && (
                        <td className={styles.td}>
                          {hasLink(s, 'vote') && (
                            <button className={styles.actionButton} onClick={() => handleVote(s.id)}>Vote</button>
                          )}
                          {hasLink(s, 'approve') && (
                            <button className={styles.actionButton} onClick={() => handleApprove(s.id)}>Approve</button>
                          )}
                        </td>
                      )}
                    </tr>
                  ))}
                </tbody>
              </table>
              <div className={styles.pagination}>
                <button className={styles.pageButton} disabled={sPage.number === 0} onClick={() => loadSuggestions(sPage.number - 1)}>
                  Previous
                </button>
                <span className={styles.pageInfo}>
                  Page {sPage.number + 1} of {sPage.totalPages} ({sPage.totalElements} items)
                </span>
                <button className={styles.pageButton} disabled={sPage.number >= sPage.totalPages - 1} onClick={() => loadSuggestions(sPage.number + 1)}>
                  Next
                </button>
              </div>
            </>
          )}
        </>
      )}

      {(creating || editing) && (
        <NutrientForm
          initial={editing ?? undefined}
          onSave={editing ? handleUpdate : handleCreate}
          onCancel={() => { setCreating(false); setEditing(null) }}
        />
      )}

      {showSuggestForm && (
        <SuggestForm
          onSave={handleSuggest}
          onCancel={() => setShowSuggestForm(false)}
        />
      )}
    </div>
  )
}
