import { useEffect, useState, useCallback, useRef } from 'react'
import type React from 'react'
import type { DateRange } from 'react-day-picker'
import { X, MapPin, CalendarDays, Plane } from 'lucide-react'
import { getActiveCategories, getRecommended, searchFlights } from '../api/flightApi'
import type { Category, Flight, PagedResponse } from '../types'
import FlightCard from '../components/FlightCard'
import Pagination from '../components/Pagination'
import DateRangePicker from '../components/DateRangePicker'

function toIso(d: Date | undefined): string | undefined {
  if (!d) return undefined
  return d.toISOString().split('T')[0]
}

export default function HomePage() {
  const [categories, setCategories]     = useState<Category[]>([])
  const [recommended, setRecommended]   = useState<Flight[]>([])
  const [paged, setPaged]               = useState<PagedResponse<Flight> | null>(null)
  const [loading, setLoading]           = useState(false)
  const [query, setQuery]               = useState('')
  const [searchInput, setSearchInput]   = useState('')
  const [categoryIds, setCategoryIds]   = useState<number[]>([])
  const [dateRange, setDateRange]       = useState<DateRange | undefined>()
  const [page, setPage]                 = useState(0)

  // Autocomplete
  const [suggestions, setSuggestions]   = useState<Flight[]>([])
  const [showSuggestions, setShowSuggestions] = useState(false)
  const debounceRef                     = useRef<ReturnType<typeof setTimeout> | null>(null)
  const searchRef                       = useRef<HTMLInputElement>(null)

  useEffect(() => {
    getActiveCategories().then(setCategories).catch(console.error)
    getRecommended().then(setRecommended).catch(console.error)
  }, [])

  const fetchFlights = useCallback(async (
    q: string, cats: number[], p: number, range: DateRange | undefined
  ) => {
    setLoading(true)
    try {
      const data = await searchFlights(
        q || undefined,
        cats.length ? cats : undefined,
        p,
        10,
        toIso(range?.from),
        toIso(range?.to),
      )
      setPaged(data)
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    fetchFlights(query, categoryIds, page, dateRange)
  }, [fetchFlights, query, categoryIds, page, dateRange])

  // Autocomplete: busca mientras escribe
  const handleInputChange = (val: string) => {
    setSearchInput(val)
    if (debounceRef.current) clearTimeout(debounceRef.current)
    if (val.trim().length < 2) { setSuggestions([]); setShowSuggestions(false); return }
    debounceRef.current = setTimeout(async () => {
      try {
        const data = await searchFlights(val.trim(), undefined, 0, 5)
        setSuggestions(data.content)
        setShowSuggestions(data.content.length > 0)
      } catch { /* ignore */ }
    }, 300)
  }

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault()
    setShowSuggestions(false)
    setQuery(searchInput.trim())
    setCategoryIds([])
    setPage(0)
  }

  const applySuggestion = (f: Flight) => {
    setSearchInput(f.destination?.city ?? f.name)
    setQuery(f.destination?.city ?? f.name)
    setCategoryIds([])
    setPage(0)
    setShowSuggestions(false)
  }

  const clearSearch = () => {
    setSearchInput('')
    setQuery('')
    setCategoryIds([])
    setDateRange(undefined)
    setPage(0)
    setShowSuggestions(false)
  }

  const toggleCategory = (id: number) => {
    setQuery('')
    setSearchInput('')
    setPage(0)
    setDateRange(undefined)
    setCategoryIds(prev =>
      prev.includes(id) ? prev.filter(x => x !== id) : [...prev, id]
    )
  }

  const hasFilters = query.length > 0 || categoryIds.length > 0 || !!dateRange?.from

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

        <div className="relative z-10 w-full max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-16">

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

          {/* Stats rápidas */}
          <div className="flex flex-wrap gap-6">
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

      {/* ===== SEARCH BAR — Qatar style, solapa hero y contenido ===== */}
      <div className="relative z-20 max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 -mt-24">
        <div className="search-bar">

          {/* Encabezado del buscador */}
          <div className="px-6 pt-5 pb-4 border-b border-gray-100">
            <h2 className="text-navy-900 font-bold text-lg leading-snug">
              Encontrá tu próximo vuelo
            </h2>
            <p className="text-gray-400 text-sm mt-1">
              Ingresá un destino y seleccioná las fechas para ver vuelos disponibles.
            </p>
          </div>

          {/* Campos */}
          <form
            onSubmit={handleSearch}
            className="flex flex-col sm:flex-row items-stretch"
          >
            {/* Destino */}
            <div className="search-bar-field relative">
              <label className="search-bar-label">
                <MapPin className="w-3 h-3 inline-block mr-1 text-gold-500" />
                Destino
              </label>
              <div className="flex items-center gap-2">
                <input
                  ref={searchRef}
                  type="text"
                  value={searchInput}
                  onChange={(e: React.ChangeEvent<HTMLInputElement>) => handleInputChange(e.target.value)}
                  onFocus={() => suggestions.length > 0 && setShowSuggestions(true)}
                  onBlur={() => setTimeout(() => setShowSuggestions(false), 150)}
                  placeholder="¿A dónde querés volar?"
                  autoComplete="off"
                  className="search-bar-input"
                />
                {searchInput && (
                  <button
                    type="button"
                    onClick={() => { setSearchInput(''); setSuggestions([]); setShowSuggestions(false) }}
                  >
                    <X className="w-3.5 h-3.5 text-gray-300 hover:text-gray-500 transition" />
                  </button>
                )}
              </div>

              {/* Dropdown sugerencias */}
              {showSuggestions && suggestions.length > 0 && (
                <div className="absolute top-full left-0 right-0 mt-1 z-50 bg-white rounded-xl shadow-2xl border border-gray-100 overflow-hidden">
                  {suggestions.map(f => (
                    <button
                      key={f.id}
                      type="button"
                      onMouseDown={() => applySuggestion(f)}
                      className="w-full flex items-center gap-3 px-4 py-3 text-left hover:bg-gray-50 transition border-b border-gray-50 last:border-0"
                    >
                      <span className="text-gold-500 text-xs font-mono font-bold w-10 flex-shrink-0">
                        {f.destination?.iataCode}
                      </span>
                      <span className="text-navy-900 text-sm flex-1 truncate font-medium">{f.destination?.city}</span>
                      <span className="text-gray-400 text-xs truncate hidden sm:block">{f.name}</span>
                    </button>
                  ))}
                </div>
              )}
            </div>

            {/* Fechas */}
            <div className="search-bar-field">
              <label className="search-bar-label">
                <CalendarDays className="w-3 h-3 inline-block mr-1 text-gold-500" />
                Fechas de viaje
              </label>
              <DateRangePicker range={dateRange} onChange={setDateRange} light />
            </div>

            {/* Botón buscar */}
            <div className="flex items-center gap-3 px-5 py-4 border-t sm:border-t-0 border-gray-100">
              <button type="submit" className="btn-gold px-8 py-3 text-sm font-bold whitespace-nowrap">
                Realizar búsqueda
              </button>
              {hasFilters && (
                <button
                  type="button"
                  onClick={clearSearch}
                  className="flex items-center gap-1.5 text-xs text-gray-400 hover:text-gray-700 transition whitespace-nowrap"
                >
                  <X className="w-3.5 h-3.5" /> Limpiar
                </button>
              )}
            </div>
          </form>

          {/* Indicador de fechas activas */}
          {dateRange?.from && dateRange?.to && (
            <div className="px-6 pb-3 text-xs text-gold-600 font-medium border-t border-gray-50 pt-2">
              Mostrando vuelos disponibles del{' '}
              {dateRange.from.toLocaleDateString('es-AR', { day: 'numeric', month: 'long' })}
              {' '}al{' '}
              {dateRange.to.toLocaleDateString('es-AR', { day: 'numeric', month: 'long', year: 'numeric' })}
            </div>
          )}
        </div>
      </div>

      {/* ===== CONTENIDO ===== */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">

        {/* CATEGORÍAS */}
        {categories.length > 0 && (
          <section className="mt-10">
            <div className="flex items-center justify-between mb-5">
              <p className="text-[#64748B] text-xs uppercase tracking-[0.2em]">
                Explorar por tipo de viaje
              </p>
              {hasFilters && !query && !dateRange?.from && (
                <button
                  onClick={clearSearch}
                  className="flex items-center gap-1.5 text-xs text-gold-500 hover:text-gold-400 border border-gold-500/30 hover:border-gold-500 px-3 py-1.5 rounded-lg transition-all"
                >
                  Eliminar filtros <X className="w-3 h-3" />
                </button>
              )}
            </div>

            <div className="flex gap-3 overflow-x-auto pb-3 sm:flex-wrap pt-2 pl-1" style={{ scrollbarWidth: 'none' }}>

              {/* Card "Todos" */}
              <button
                onClick={clearSearch}
                className="group relative flex-shrink-0 w-40 h-28 sm:w-44 sm:h-32 rounded-2xl overflow-hidden transition-all duration-200 focus:outline-none"
                style={{ boxShadow: !hasFilters ? '0 0 0 2.5px #D4AF37' : '0 2px 12px rgba(0,0,0,0.10)' }}
              >
                <div className="absolute inset-0" style={{ background: 'linear-gradient(135deg, #0A1428 0%, #1a2f55 100%)' }} />
                <div className="absolute inset-0 flex flex-col items-center justify-center gap-1.5">
                  <svg className="w-6 h-6 text-gold-400" fill="currentColor" viewBox="0 0 24 24">
                    <path d="M21 16v-2l-8-5V3.5c0-.83-.67-1.5-1.5-1.5S10 2.67 10 3.5V9l-8 5v2l8-2.5V19l-2 1.5V22l3.5-1 3.5 1v-1.5L13 19v-5.5l8 2.5z"/>
                  </svg>
                  <span className="text-white text-xs font-semibold tracking-wide">Todos</span>
                  {!hasFilters && <div className="w-4 h-0.5 bg-gold-400 rounded-full" />}
                </div>
              </button>

              {/* Cards de categoría */}
              {categories.map(cat => {
                const selected = categoryIds.includes(cat.id)
                return (
                  <button
                    key={cat.id}
                    onClick={() => toggleCategory(cat.id)}
                    className="group relative flex-shrink-0 w-40 h-28 sm:w-44 sm:h-32 rounded-2xl overflow-hidden transition-all duration-200 focus:outline-none"
                    style={{ boxShadow: selected ? '0 0 0 2.5px #D4AF37' : '0 2px 12px rgba(0,0,0,0.10)' }}
                  >
                    {cat.imageUrl ? (
                      <img
                        src={cat.imageUrl}
                        alt={cat.name}
                        className="absolute inset-0 w-full h-full object-cover transition-transform duration-500 group-hover:scale-105"
                      />
                    ) : (
                      <div className="absolute inset-0 bg-gradient-to-br from-navy-800 to-navy-600" />
                    )}
                    <div
                      className="absolute inset-0 transition-opacity duration-200"
                      style={{
                        background: selected
                          ? 'linear-gradient(to top, rgba(6,14,26,0.85) 0%, rgba(212,175,55,0.15) 100%)'
                          : 'linear-gradient(to top, rgba(6,14,26,0.80) 0%, rgba(6,14,26,0.20) 100%)',
                      }}
                    />
                    <div className="absolute inset-x-0 bottom-0 px-3 pb-3">
                      <p className="text-white text-xs font-semibold leading-tight truncate">{cat.name}</p>
                      <p className="text-white/55 text-[10px] mt-0.5">{cat.flightCount} vuelos</p>
                    </div>
                    {selected && (
                      <div className="absolute top-2 right-2 w-5 h-5 rounded-full bg-gold-400 flex items-center justify-center">
                        <svg className="w-3 h-3 text-navy-900" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={3}>
                          <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
                        </svg>
                      </div>
                    )}
                  </button>
                )
              })}
            </div>
          </section>
        )}

        {/* RECOMENDADOS */}
        {!hasFilters && recommended.length > 0 && (
          <section className="mt-16">
            <div className="flex items-end justify-between mb-8">
              <div>
                <p className="text-gold-500 text-xs uppercase tracking-[0.2em] mb-2">Selección editorial</p>
                <h2 className="section-title">Destinos recomendados</h2>
              </div>
              <span className="text-[#64748B] text-sm hidden sm:block">Ordenados por rating</span>
            </div>
            <div className="hidden lg:grid gap-4" style={{ gridTemplateColumns: '2fr 1fr', gridTemplateRows: 'repeat(2, 255px)' }}>
              <div style={{ gridRow: '1 / 3', height: '100%' }}>
                <FlightCard flight={recommended[0]} featured />
              </div>
              {recommended.slice(1, 3).map(f => (
                <div key={f.id} style={{ height: '100%' }}><FlightCard flight={f} /></div>
              ))}
            </div>
            <div className="grid lg:hidden grid-cols-1 sm:grid-cols-2 gap-4">
              {recommended.slice(0, 3).map((f, i) => (
                <div key={f.id} style={{ height: '320px' }} className={i === 0 ? 'sm:col-span-2' : ''}>
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
              {hasFilters && (
                <p className="text-gold-500 text-xs uppercase tracking-[0.2em] mb-2">
                  {dateRange?.from ? 'Disponibilidad' : query ? 'Búsqueda' : 'Filtrado por categoría'}
                </p>
              )}
              <h2 className="section-title">
                {query
                  ? `Resultados para "${query}"`
                  : dateRange?.from && dateRange?.to
                  ? `Vuelos disponibles · ${dateRange.from.toLocaleDateString('es-AR', { day: 'numeric', month: 'short' })} – ${dateRange.to.toLocaleDateString('es-AR', { day: 'numeric', month: 'short' })}`
                  : categoryIds.length === 1
                  ? `Vuelos de ${categories.find(c => c.id === categoryIds[0])?.name}`
                  : categoryIds.length > 1
                  ? `${categoryIds.length} categorías seleccionadas`
                  : 'Todos los vuelos'}
              </h2>
            </div>
            <div className="flex items-center gap-3">
              {paged && (
                <span className="text-[#64748B] text-sm">{paged.totalElements} vuelos</span>
              )}
              {hasFilters && (
                <button
                  onClick={clearSearch}
                  className="flex items-center gap-1.5 text-xs text-gold-500 hover:text-gold-400 border border-gold-500/30 hover:border-gold-500 px-3 py-1.5 rounded-lg transition-all"
                >
                  Eliminar filtros <X className="w-3 h-3" />
                </button>
              )}
            </div>
          </div>

          {loading ? (
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-5">
              {Array.from({ length: 8 }).map((_, i) => (
                <div key={i} className="skeleton" style={{ height: '360px', borderRadius: '14px' }} />
              ))}
            </div>
          ) : paged?.content.length === 0 ? (
            <div className="text-center py-24">
              <Plane className="w-16 h-16 text-gold-500 mx-auto mb-4" />
              <p className="text-xl font-semibold text-[#0A1428] mb-2">Sin resultados</p>
              <p className="text-[#64748B] text-sm mb-6">
                {dateRange?.from
                  ? 'No encontramos vuelos disponibles para esas fechas.'
                  : 'No encontramos vuelos para tu búsqueda.'}
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
