import { useEffect, useState, useCallback, useRef } from 'react'
import { getActiveCategories, getRecommended, searchFlights } from '../api/flightApi'
import type { Category, Flight, PagedResponse } from '../types'
import FlightCard from '../components/FlightCard'
import Pagination from '../components/Pagination'


export default function HomePage() {
  const [categories, setCategories]     = useState<Category[]>([])
  const [recommended, setRecommended]   = useState<Flight[]>([])
  const [paged, setPaged]               = useState<PagedResponse<Flight> | null>(null)
  const [loading, setLoading]           = useState(false)
  const [query, setQuery]               = useState('')
  const [searchInput, setSearchInput]   = useState('')
  const [categoryId, setCategoryId]     = useState<number | null>(null)
  const [page, setPage]                 = useState(0)
  const searchRef                       = useRef<HTMLInputElement>(null)

  useEffect(() => {
    getActiveCategories().then(setCategories).catch(console.error)
    getRecommended().then(setRecommended).catch(console.error)
  }, [])

  const fetchFlights = useCallback(async (q: string, cat: number | null, p: number) => {
    setLoading(true)
    try {
      const data = await searchFlights(q || undefined, cat, p, 10)
      setPaged(data)
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    fetchFlights(query, categoryId, page)
  }, [fetchFlights, query, categoryId, page])

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault()
    setQuery(searchInput.trim())
    setCategoryId(null)
    setPage(0)
  }

  const clearSearch = () => {
    setSearchInput('')
    setQuery('')
    setCategoryId(null)
    setPage(0)
  }

  const handleCategory = (id: number | null) => {
    setCategoryId(id)
    setQuery('')
    setSearchInput('')
    setPage(0)
  }

  const activeCategoryName = categories.find(c => c.id === categoryId)?.name

  return (
    <main>
      {/* ===== HERO ===== */}
      <section className="hero-section pt-[68px]">
        <img
          src="https://images.unsplash.com/photo-1464037866556-6812c9d1c72e?w=1800&q=85&fit=crop"
          alt="Stellar Jets — aviación premium"
          className="hero-bg"
          loading="eager"
        />
        <div className="hero-overlay" />

        <div className="relative z-10 w-full max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-20">

          {/* Eyebrow */}
          <div className="flex items-center gap-2 mb-5">
            <div className="w-6 h-px bg-gold-500" />
            <span className="text-gold-400 text-xs font-semibold uppercase tracking-[0.2em]">
              Aviación Premium
            </span>
          </div>

          {/* Heading */}
          <h1 className="text-5xl sm:text-6xl lg:text-7xl font-bold text-white leading-[1.08] mb-6">
            Donde las estrellas<br />
            <span
              className="font-extrabold"
              style={{
                background: 'linear-gradient(105deg, #D4AF37 0%, #F5D576 50%, #D4AF37 100%)',
                WebkitBackgroundClip: 'text',
                WebkitTextFillColor: 'transparent',
                backgroundClip: 'text',
              }}
            >
              guían tu destino
            </span>
          </h1>

          <p className="text-[#CBD5E1] text-lg max-w-xl mb-10 leading-relaxed">
            Descubre vuelos exclusivos a los destinos más increíbles del mundo.
            Lujo, confort y precisión en cada trayecto.
          </p>

          {/* Search widget */}
          <form onSubmit={handleSearch} className="search-widget p-4 sm:p-5 max-w-2xl">
            <div className="relative mb-4">
              <svg
                className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 pointer-events-none"
                style={{ color: '#D4AF37' }}
                fill="none" viewBox="0 0 24 24" stroke="currentColor"
              >
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                  d="M21 21l-4.35-4.35M17 11A6 6 0 1 1 5 11a6 6 0 0 1 12 0z" />
              </svg>
              <input
                ref={searchRef}
                type="text"
                value={searchInput}
                onChange={e => setSearchInput(e.target.value)}
                placeholder="Destino, aeropuerto o nombre del vuelo..."
              />
              {searchInput && (
                <button
                  type="button"
                  onClick={clearSearch}
                  className="absolute right-4 top-1/2 -translate-y-1/2 text-[#64748B] hover:text-white transition"
                >
                  ✕
                </button>
              )}
            </div>
            <div className="flex justify-end">
              <button type="submit" className="btn-gold px-8 py-3">
                Buscar vuelos
              </button>
            </div>
          </form>

          {/* Stats rápidas */}
          <div className="flex flex-wrap gap-6 mt-8">
            {[
              { label: 'Destinos', value: '50+' },
              { label: 'Aerolíneas', value: '12' },
              { label: 'Clientes', value: '10K+' },
            ].map(s => (
              <div key={s.label} className="flex items-center gap-2">
                <span className="text-xl font-bold text-gold-400">{s.value}</span>
                <span className="text-[#CBD5E1]/60 text-sm">{s.label}</span>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* ===== CONTENIDO ===== */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">

        {/* CATEGORÍAS */}
        {categories.length > 0 && (
          <section className="mt-14">
            <p className="text-[#64748B] text-xs uppercase tracking-[0.2em] mb-4">
              Explorar por tipo de viaje
            </p>
            <div className="flex flex-wrap gap-2.5">
              <button
                onClick={() => handleCategory(null)}
                className={`cat-pill ${categoryId === null && !query ? 'active' : ''}`}
              >
                Todos los vuelos
              </button>
              {categories.map(cat => (
                <button
                  key={cat.id}
                  onClick={() => handleCategory(cat.id)}
                  className={`cat-pill ${categoryId === cat.id ? 'active' : ''}`}
                >
                  {cat.name}
                  <span className="opacity-50">({cat.flightCount})</span>
                </button>
              ))}
            </div>
          </section>
        )}

        {/* RECOMENDADOS */}
        {!query && !categoryId && recommended.length > 0 && (
          <section className="mt-16">
            <div className="flex items-end justify-between mb-8">
              <div>
                <p className="text-gold-500 text-xs uppercase tracking-[0.2em] mb-2">
                  Selección editorial
                </p>
                <h2 className="section-title">Destinos recomendados</h2>
              </div>
              <span className="text-[#64748B] text-sm hidden sm:block">
                Ordenados por rating
              </span>
            </div>

            {/* Desktop: editorial 2fr + 1fr, featured cubre 2 filas */}
            <div
              className="hidden lg:grid gap-4"
              style={{
                gridTemplateColumns: '2fr 1fr',
                gridTemplateRows: 'repeat(2, 255px)',
              }}
            >
              <div style={{ gridRow: '1 / 3', height: '100%' }}>
                <FlightCard flight={recommended[0]} featured />
              </div>
              {recommended.slice(1, 3).map(f => (
                <div key={f.id} style={{ height: '100%' }}>
                  <FlightCard flight={f} />
                </div>
              ))}
            </div>

            {/* Mobile / tablet */}
            <div className="grid lg:hidden grid-cols-1 sm:grid-cols-2 gap-4">
              {recommended.slice(0, 3).map((f, i) => (
                <div key={f.id} style={{ height: '320px' }}
                  className={i === 0 ? 'sm:col-span-2' : ''}>
                  <FlightCard flight={f} featured={i === 0} />
                </div>
              ))}
            </div>
          </section>
        )}

        {/* TODOS LOS VUELOS */}
        <section className="mt-16 pb-16">
          <div className="flex items-end justify-between mb-8">
            <div>
              {(query || categoryId) && (
                <p className="text-gold-500 text-xs uppercase tracking-[0.2em] mb-2">
                  {query ? `Búsqueda` : `Categoría`}
                </p>
              )}
              <h2 className="section-title">
                {query
                  ? `Resultados para "${query}"`
                  : categoryId
                  ? `Vuelos de ${activeCategoryName}`
                  : 'Todos los vuelos'}
              </h2>
            </div>
            <div className="flex items-center gap-3">
              {paged && (
                <span className="text-[#64748B] text-sm">
                  {paged.totalElements} vuelos
                </span>
              )}
              {(query || categoryId) && (
                <button
                  onClick={clearSearch}
                  className="text-xs text-gold-500 hover:text-gold-400 border border-gold-500/30 hover:border-gold-500 px-3 py-1.5 rounded-lg transition-all"
                >
                  Limpiar filtro ✕
                </button>
              )}
            </div>
          </div>

          {loading ? (
            /* Skeleton */
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-5">
              {Array.from({ length: 8 }).map((_, i) => (
                <div key={i} className="skeleton" style={{ height: '360px', borderRadius: '14px' }} />
              ))}
            </div>
          ) : paged?.content.length === 0 ? (
            <div className="text-center py-24">
              <p className="text-5xl mb-4">✈️</p>
              <p className="text-xl font-semibold text-white mb-2">
                Sin resultados
              </p>
              <p className="text-[#64748B] text-sm mb-6">
                No encontramos vuelos para tu búsqueda.
              </p>
              <button onClick={clearSearch} className="btn-gold px-6 py-3">
                Ver todos los vuelos
              </button>
            </div>
          ) : (
            <>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-5">
                {paged?.content.map(f => (
                  <div key={f.id} style={{ height: '360px' }}>
                    <FlightCard flight={f} />
                  </div>
                ))}
              </div>
              {paged && (
                <Pagination
                  currentPage={paged.currentPage}
                  totalPages={paged.totalPages}
                  onPageChange={setPage}
                />
              )}
            </>
          )}
        </section>
      </div>
    </main>
  )
}
