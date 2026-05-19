import { useState } from 'react'

interface Props {
  onSearch: (query: string) => void
  loading?: boolean
}

export default function SearchBar({ onSearch, loading }: Props) {
  const [value, setValue] = useState('')

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    onSearch(value.trim())
  }

  return (
    <form onSubmit={handleSubmit} className="flex gap-3 w-full max-w-2xl">
      <div className="relative flex-1">
        <svg
          className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400"
          fill="none" viewBox="0 0 24 24" stroke="currentColor"
        >
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
            d="M21 21l-4.35-4.35M17 11A6 6 0 1 1 5 11a6 6 0 0 1 12 0z" />
        </svg>
        <input
          type="text"
          value={value}
          onChange={e => setValue(e.target.value)}
          placeholder="Buscar destino, origen o nombre de vuelo..."
          className="w-full pl-12 pr-4 py-3.5 rounded-xl border border-gray-200 shadow-sm text-gray-900 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-navy-500 focus:border-transparent text-sm"
        />
        {value && (
          <button
            type="button"
            onClick={() => { setValue(''); onSearch('') }}
            className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
          >
            ✕
          </button>
        )}
      </div>
      <button
        type="submit"
        disabled={loading}
        className="btn-primary px-6 py-3.5 rounded-xl text-sm disabled:opacity-60"
      >
        {loading ? 'Buscando...' : 'Buscar'}
      </button>
    </form>
  )
}
