import { apiFetch } from './client'

const NUTRIENTS_PATH = '/v1/nutrients'
const SUGGESTIONS_PATH = '/v1/nutrient-suggestions'

export interface NutrientRequest {
  name: string
  kcalPerGram: string | null
  defaultUnit: 'GRAM' | 'MILLIGRAM'
}

export interface NutrientResponse {
  id: string
  name: string
  kcalPerGram: string | null
  defaultUnit: string
  status: string
  source: string
  authorId: string | null
  createdAt: string
  _links: Record<string, { href: string; method?: string }>
}

export interface SuggestionRequest {
  name: string
  kcalPerGram: string | null
  defaultUnit: 'GRAM' | 'MILLIGRAM'
}

export interface SuggestionResponse {
  id: string
  name: string
  kcalPerGram: string | null
  defaultUnit: string
  status: string
  source: string
  authorId: string | null
  voteCount: number
  createdAt: string
  _links: Record<string, { href: string; method?: string }>
}

export interface PageMetadata {
  size: number
  totalElements: number
  totalPages: number
  number: number
}

export interface HalPage<T> {
  _embedded: Record<string, T[]>
  page: PageMetadata
  _links: Record<string, { href: string }>
}

function hasLink(item: { _links?: Record<string, unknown> }, rel: string): boolean {
  if (!item._links) return false
  return rel in item._links
}

export async function fetchNutrients(token: string | undefined, page: number, pageSize = 20): Promise<HalPage<NutrientResponse>> {
  return apiFetch<HalPage<NutrientResponse>>(
    `${NUTRIENTS_PATH}?page=${page}&size=${pageSize}`,
    token,
  )
}

export async function fetchNutrient(token: string | undefined, id: string): Promise<NutrientResponse> {
  return apiFetch<NutrientResponse>(`${NUTRIENTS_PATH}/${id}`, token)
}

export async function createNutrient(token: string | undefined, request: NutrientRequest): Promise<NutrientResponse> {
  return apiFetch<NutrientResponse>(NUTRIENTS_PATH, token, {
    method: 'POST',
    body: JSON.stringify(request),
  })
}

export async function updateNutrient(token: string | undefined, id: string, request: NutrientRequest): Promise<NutrientResponse> {
  return apiFetch<NutrientResponse>(`${NUTRIENTS_PATH}/${id}`, token, {
    method: 'PUT',
    body: JSON.stringify(request),
  })
}

export async function deleteNutrient(token: string | undefined, id: string): Promise<void> {
  await apiFetch<void>(`${NUTRIENTS_PATH}/${id}`, token, { method: 'DELETE' })
}

export async function fetchSuggestions(token: string | undefined, page: number, pageSize = 20): Promise<HalPage<SuggestionResponse>> {
  return apiFetch<HalPage<SuggestionResponse>>(
    `${SUGGESTIONS_PATH}?page=${page}&size=${pageSize}`,
    token,
  )
}

export async function suggestNutrient(token: string | undefined, request: SuggestionRequest): Promise<SuggestionResponse> {
  return apiFetch<SuggestionResponse>(SUGGESTIONS_PATH, token, {
    method: 'POST',
    body: JSON.stringify(request),
  })
}

export async function voteOnSuggestion(token: string | undefined, id: string): Promise<SuggestionResponse> {
  return apiFetch<SuggestionResponse>(`${SUGGESTIONS_PATH}/${id}/votes`, token, { method: 'POST' })
}

export async function approveSuggestion(token: string | undefined, id: string): Promise<SuggestionResponse> {
  return apiFetch<SuggestionResponse>(`${SUGGESTIONS_PATH}/${id}/approve`, token, { method: 'POST' })
}

export { hasLink }
