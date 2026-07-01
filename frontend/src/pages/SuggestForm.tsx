import { useState } from 'react'
import type { SuggestionRequest } from '../api/nutrients'
import styles from './NutrientForm.module.css'

interface SuggestFormProps {
  onSave: (request: SuggestionRequest) => Promise<void>
  onCancel: () => void
}

export function SuggestForm({ onSave, onCancel }: SuggestFormProps) {
  const [name, setName] = useState('')
  const [kcalPerGram, setKcalPerGram] = useState('')
  const [defaultUnit, setDefaultUnit] = useState<'GRAM' | 'MILLIGRAM'>('GRAM')
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!name.trim()) {
      setError('Name is required')
      return
    }

    setSaving(true)
    setError(null)

    try {
      await onSave({
        name: name.trim(),
        kcalPerGram: kcalPerGram === '' || kcalPerGram === null ? null : kcalPerGram,
        defaultUnit,
      })
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to submit')
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className={styles.overlay}>
      <form className={styles.form} onSubmit={handleSubmit}>
        <h2 className={styles.title}>Suggest a Nutrient</h2>

        {error && <p className={styles.error}>{error}</p>}

        <label className={styles.label}>
          Name
          <input
            className={styles.input}
            value={name}
            onChange={(e) => setName(e.target.value)}
            required
            autoFocus
          />
        </label>

        <label className={styles.label}>
          kcal per gram
          <input
            className={styles.input}
            type="number"
            step="any"
            value={kcalPerGram}
            onChange={(e) => setKcalPerGram(e.target.value)}
            placeholder="Optional"
          />
        </label>

        <label className={styles.label}>
          Default unit
          <select className={styles.input} value={defaultUnit} onChange={(e) => setDefaultUnit(e.target.value as 'GRAM' | 'MILLIGRAM')}>
            <option value="GRAM">Gram</option>
            <option value="MILLIGRAM">Milligram</option>
          </select>
        </label>

        <div className={styles.actions}>
          <button type="button" className={styles.cancelButton} onClick={onCancel} disabled={saving}>
            Cancel
          </button>
          <button type="submit" className={styles.saveButton} disabled={saving}>
            {saving ? 'Submitting...' : 'Submit'}
          </button>
        </div>
      </form>
    </div>
  )
}
