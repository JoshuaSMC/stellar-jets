import { useEffect, useState, useCallback } from 'react'
import type React from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import {
  Plane, ArrowLeft, ArrowRight, Users, Timer, Tag,
  CheckCircle, XCircle, Armchair, RefreshCw, Share2, Star,
  ShieldCheck, Luggage, Clock, HeartPulse,
} from 'lucide-react'
import { DayPicker } from 'react-day-picker'
import { es } from 'date-fns/locale'
import 'react-day-picker/style.css'
import CharIcon from '../components/CharIcon'
import ShareModal from '../components/ShareModal'
import { getFlightById, getOccupiedDates, getReviews, submitReview } from '../api/flightApi'
import type { Flight, OccupiedDateRange, Review } from '../types'
import ImageGallery from '../components/ImageGallery'
import { useAuth } from '../context/AuthContext'

function formatDuration(minutes: number | null): string {
  if (!minutes) return ''
  const h = Math.floor(minutes / 60)
  const m = minutes % 60
  return h > 0 ? `${h}h${m > 0 ? ` ${m}m` : ''}` : `${m}m`
}

export default function FlightDetailPage() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const { user } = useAuth()
  const [flight, setFlight] = useState<Flight | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(false)
  const [occupiedRanges, setOccupiedRanges] = useState<OccupiedDateRange[]>([])
  const [calendarError, setCalendarError] = useState(false)

  // US#27 — Compartir
  const [showShare, setShowShare] = useState(false)

  // US#28 — Reseñas
  const [reviews, setReviews] = useState<Review[]>([])
  const [reviewStars, setReviewStars] = useState(0)
  const [hoverStar, setHoverStar] = useState(0)
  const [reviewComment, setReviewComment] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [reviewError, setReviewError] = useState('')

  useEffect(() => {
    if (!id) return
    getFlightById(Number(id))
      .then(setFlight)
      .catch(() => setError(true))
      .finally(() => setLoading(false))
  }, [id])

  const fetchOccupiedDates = useCallback(() => {
    if (!id) return
    setCalendarError(false)
    getOccupiedDates(Number(id))
      .then(setOccupiedRanges)
      .catch(() => setCalendarError(true))
  }, [id])

  useEffect(() => { fetchOccupiedDates() }, [fetchOccupiedDates])

  useEffect(() => {
    if (!id) return
    getReviews(Number(id)).then(setReviews).catch(() => {})
  }, [id])

  const handleReviewSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (reviewStars === 0) { setReviewError('Seleccioná una puntuación.'); return }
    setSubmitting(true)
    setReviewError('')
    try {
      const saved = await submitReview(Number(id), { stars: reviewStars, comment: reviewComment })
      setReviews(prev => {
        const exists = prev.find(r => r.id === saved.id)
        return exists ? prev.map(r => r.id === saved.id ? saved : r) : [saved, ...prev]
      })
      if (flight) {
        const total = reviews.length + (reviews.find(r => r.firstName === user?.firstName) ? 0 : 1)
        const avg = (reviews.reduce((s, r) => s + r.stars, 0) + saved.stars) / total
        setFlight({ ...flight, rating: Math.round(avg * 10) / 10, reviewCount: total })
      }
      setReviewComment('')
      setReviewStars(0)
    } catch {
      setReviewError('No se pudo guardar la reseña. Intentá de nuevo.')
    } finally {
      setSubmitting(false)
    }
  }

  if (loading) {
    return (
      <div className="pt-[68px] min-h-screen flex items-center justify-center" style={{ background: '#F4F7FB' }}>
        <div className="flex flex-col items-center gap-3">
          <div className="w-10 h-10 rounded-full border-2 border-gold-500/30 border-t-gold-500 animate-spin" />
          <p className="text-xs text-gray-400 tracking-widest uppercase">Cargando</p>
        </div>
      </div>
    )
  }

  if (error || !flight) {
    return (
      <div className="pt-[68px] min-h-screen flex flex-col items-center justify-center gap-4" style={{ background: '#F4F7FB' }}>
        <Plane className="w-16 h-16 text-navy-300" />
        <p className="text-2xl font-bold text-gray-900">Vuelo no encontrado</p>
        <Link to="/" className="btn-gold mt-2 px-8 py-3">Volver al inicio</Link>
      </div>
    )
  }

  const stars = Math.round(flight.rating ?? 0)

  const occupiedModifier = occupiedRanges.map(r => ({
    from: new Date(r.checkIn + 'T00:00:00'),
    to:   new Date(r.checkOut + 'T00:00:00'),
  }))
  const cover =
    flight.coverImageUrl ??
    flight.images?.find(i => i.cover)?.url ??
    flight.images?.[0]?.url ??
    'https://images.unsplash.com/photo-1436491865332-7a61a109cc05?w=1800'

  const secondImage = flight.images?.find((img, i) => i > 0 && !img.cover)?.url ?? cover

  return (
    <main style={{ background: '#F4F7FB' }}>

      {/* ══════════════════════════════════════════
          HERO — imagen full-width con título Qatar-style
      ══════════════════════════════════════════ */}
      <div className="relative w-full overflow-hidden" style={{ height: '58vh', minHeight: '400px' }}>
        <img
          src={cover}
          alt={flight.name}
          className="absolute inset-0 w-full h-full object-cover"
        />
        <div
          className="absolute inset-0"
          style={{
            background:
              'linear-gradient(to bottom, rgba(6,14,26,0.38) 0%, rgba(6,14,26,0.10) 35%, rgba(6,14,26,0.70) 100%)',
          }}
        />

        {/* Breadcrumb + flecha volver alineada a la derecha (US#5) */}
        <div className="absolute top-0 left-0 right-0 pt-[82px]">
          <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 flex items-center justify-between">
            <nav className="flex items-center gap-1.5 text-[11px] text-white/55">
              <Link to="/" className="hover:text-white transition-colors">Inicio</Link>
              <span>/</span>
              <span className="text-white/90">{flight.name}</span>
            </nav>
            <button
              onClick={() => navigate(-1)}
              className="flex items-center gap-1.5 text-xs text-white/60 hover:text-white transition-colors"
            >
              <ArrowLeft className="w-4 h-4" />
              Volver
            </button>
          </div>
        </div>

        {/* Título */}
        <div className="absolute bottom-0 left-0 right-0 pb-9">
          <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
            {flight.category && (
              <p className="text-[10px] font-bold uppercase tracking-[0.22em] text-gold-400 mb-2">
                {flight.category.name}
              </p>
            )}
            <h1
              className="font-bold text-white leading-tight mb-2"
              style={{ fontSize: 'clamp(1.8rem, 4vw, 3rem)', letterSpacing: '-0.02em' }}
            >
              {flight.active
                ? `Vuelos a ${flight.destination?.city} (${flight.destination?.iataCode})`
                : flight.name}
            </h1>
            <div className="flex items-center gap-3 text-white/70 text-sm">
              <span className="font-mono font-semibold text-gold-400">{flight.origin?.iataCode}</span>
              <svg className="w-4 h-4 text-gold-500/70" fill="currentColor" viewBox="0 0 24 24">
                <path d="M21 16v-2l-8-5V3.5c0-.83-.67-1.5-1.5-1.5S10 2.67 10 3.5V9l-8 5v2l8-2.5V19l-2 1.5V22l3.5-1 3.5 1v-1.5L13 19v-5.5l8 2.5z"/>
              </svg>
              <span className="font-mono font-semibold text-gold-400">{flight.destination?.iataCode}</span>
              {flight.durationMinutes && (
                <span className="text-white/45 text-xs">· {formatDuration(flight.durationMinutes)}</span>
              )}
              {flight.flightNumber && (
                <span className="text-white/40 text-xs font-mono">· {flight.flightNumber}</span>
              )}
            </div>
          </div>
        </div>
      </div>

      {/* ══════════════════════════════════════════
          CUERPO PRINCIPAL
      ══════════════════════════════════════════ */}
      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8 items-start">

          {/* ── COLUMNA CONTENIDO (2/3) ── */}
          <div className="lg:col-span-2 space-y-6">

            {/* DESCRIPCIÓN + imagen lateral — Qatar style */}
            {flight.description && (
              <section className="bg-white rounded-2xl overflow-hidden shadow-sm border border-gray-100">
                <div className="grid grid-cols-1 sm:grid-cols-5">
                  {/* Texto */}
                  <div className="sm:col-span-3 px-6 py-6">
                    <p className="section-eyebrow mb-3 text-[11px]">Sobre este vuelo</p>
                    <h2
                      className="font-bold text-navy-900 mb-3 leading-snug"
                      style={{ fontSize: '1.25rem' }}
                    >
                      {flight.name}: {flight.destination?.city}
                    </h2>
                    <p className="text-gray-600 text-[0.9rem] leading-relaxed">{flight.description}</p>
                  </div>
                  {/* Imagen lateral */}
                  <div className="sm:col-span-2 min-h-[180px] sm:min-h-0">
                    <img
                      src={secondImage}
                      alt={flight.destination?.city}
                      className="w-full h-full object-cover"
                      style={{ minHeight: '180px' }}
                    />
                  </div>
                </div>
              </section>
            )}

            {/* RUTA — card blanca limpia */}
            <section className="bg-white rounded-2xl shadow-sm border border-gray-100 px-6 py-5">
              <p className="section-eyebrow mb-5 text-[11px]">Itinerario del vuelo</p>
              <div className="flex items-center justify-between gap-4">

                {/* Origen */}
                <div className="text-center min-w-[90px]">
                  <p
                    className="font-extrabold text-navy-900 font-mono tracking-widest"
                    style={{ fontSize: 'clamp(1.6rem, 3vw, 2.4rem)' }}
                  >
                    {flight.origin?.iataCode}
                  </p>
                  <p className="text-sm font-semibold text-gray-800 mt-0.5">{flight.origin?.city}</p>
                  <p className="text-xs text-gray-400">{flight.origin?.country}</p>
                </div>

                {/* Línea */}
                <div className="flex-1 flex flex-col items-center px-2 sm:px-6 min-w-0">
                  <div className="flex items-center w-full">
                    <div className="w-2.5 h-2.5 rounded-full border-2 border-gold-500 flex-shrink-0" />
                    <div
                      className="flex-1 h-px mx-1"
                      style={{
                        background:
                          'repeating-linear-gradient(90deg,#D4AF37 0,#D4AF37 5px,transparent 5px,transparent 12px)',
                      }}
                    />
                    <svg className="w-6 h-6 text-gold-500 flex-shrink-0" fill="currentColor" viewBox="0 0 24 24">
                      <path d="M21 16v-2l-8-5V3.5c0-.83-.67-1.5-1.5-1.5S10 2.67 10 3.5V9l-8 5v2l8-2.5V19l-2 1.5V22l3.5-1 3.5 1v-1.5L13 19v-5.5l8 2.5z"/>
                    </svg>
                    <div
                      className="flex-1 h-px mx-1"
                      style={{
                        background:
                          'repeating-linear-gradient(90deg,#D4AF37 0,#D4AF37 5px,transparent 5px,transparent 12px)',
                      }}
                    />
                    <div className="w-2.5 h-2.5 rounded-full bg-gold-500 flex-shrink-0" />
                  </div>
                  {flight.durationMinutes && (
                    <p className="text-xs text-gray-400 mt-2">{formatDuration(flight.durationMinutes)}</p>
                  )}
                </div>

                {/* Destino */}
                <div className="text-center min-w-[90px]">
                  <p
                    className="font-extrabold text-navy-900 font-mono tracking-widest"
                    style={{ fontSize: 'clamp(1.6rem, 3vw, 2.4rem)' }}
                  >
                    {flight.destination?.iataCode}
                  </p>
                  <p className="text-sm font-semibold text-gray-800 mt-0.5">{flight.destination?.city}</p>
                  <p className="text-xs text-gray-400">{flight.destination?.country}</p>
                </div>

              </div>
            </section>

            {/* GALERÍA */}
            <section className="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
              <p className="section-eyebrow mb-4 text-[11px]">Galería de imágenes</p>
              <ImageGallery images={flight.images} flightName={flight.name} />
            </section>

            {/* CARACTERÍSTICAS — US#18 */}
            {flight.characteristics && flight.characteristics.length > 0 && (
              <section className="bg-white rounded-2xl shadow-sm border border-gray-100 px-6 py-5">
                <p className="section-eyebrow mb-4 text-[11px]">Características</p>
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
                  {flight.characteristics.map(c => (
                    <div
                      key={c.id}
                      className="flex items-center gap-3 rounded-xl px-4 py-3 border border-gray-100"
                    >
                      <div className="flex-shrink-0 w-9 h-9 rounded-xl bg-navy-50 flex items-center justify-center">
                        <CharIcon iconName={c.iconName} className="w-5 h-5 text-navy-600" />
                      </div>
                      <span className="text-sm font-medium text-gray-800">{c.name}</span>
                    </div>
                  ))}
                </div>
              </section>
            )}

            {/* DISPONIBILIDAD — US#23 */}
            <section className="bg-white rounded-2xl shadow-sm border border-gray-100 px-6 py-5">
              <p className="section-eyebrow mb-1 text-[11px]">Disponibilidad</p>
              <p className="text-xs text-gray-400 mb-4">Las fechas marcadas en rojo ya tienen reservas asignadas.</p>

              {calendarError ? (
                <div className="flex flex-col items-center gap-3 py-8 text-center">
                  <p className="text-sm text-gray-500">No se pudo cargar el calendario de disponibilidad.</p>
                  <button
                    type="button"
                    onClick={fetchOccupiedDates}
                    className="flex items-center gap-2 text-sm text-navy-700 hover:text-gold-600 transition-colors font-medium"
                  >
                    <RefreshCw className="w-4 h-4" />
                    Reintentar
                  </button>
                </div>
              ) : (
                <>
                  <div className="overflow-x-auto">
                    <DayPicker
                      mode="multiple"
                      numberOfMonths={2}
                      locale={es}
                      showOutsideDays
                      disabled={{ before: new Date() }}
                      modifiers={{ occupied: occupiedModifier }}
                      modifiersClassNames={{ occupied: 'rdp-day--occupied' }}
                      onDayClick={() => {}}
                    />
                  </div>

                  {/* Leyenda */}
                  <div className="flex items-center gap-5 pt-3 mt-1 border-t border-gray-100">
                    <div className="flex items-center gap-2">
                      <span className="text-sm font-semibold text-gray-700">15</span>
                      <span className="text-xs text-gray-500">Disponible</span>
                    </div>
                    <div className="flex items-center gap-2">
                      <span className="text-sm font-semibold text-red-500">15</span>
                      <span className="text-xs text-gray-500">Ocupado</span>
                    </div>
                  </div>
                </>
              )}
            </section>

            {/* DETALLES — 4 chips */}
            <section className="bg-white rounded-2xl shadow-sm border border-gray-100 px-6 py-5">
              <p className="section-eyebrow mb-4 text-[11px]">Detalles del vuelo</p>
              <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
                {([
                  {
                    icon: <Armchair className="w-5 h-5 text-navy-600" />,
                    label: 'Asientos',
                    value: `${flight.availableSeats} disponibles`,
                    highlight: flight.availableSeats < 10,
                  },
                  {
                    icon: <Timer className="w-5 h-5 text-navy-600" />,
                    label: 'Duración',
                    value: flight.durationMinutes ? formatDuration(flight.durationMinutes) : '—',
                  },
                  {
                    icon: <Tag className="w-5 h-5 text-navy-600" />,
                    label: 'Categoría',
                    value: flight.category?.name ?? '—',
                  },
                  {
                    icon: flight.active
                      ? <CheckCircle className="w-5 h-5 text-green-500" />
                      : <XCircle className="w-5 h-5 text-red-400" />,
                    label: 'Estado',
                    value: flight.active ? 'Disponible' : 'No disponible',
                    valueClass: flight.active ? 'text-green-600' : 'text-red-500',
                  },
                ] as { icon: React.ReactNode; label: string; value: string; highlight?: boolean; valueClass?: string }[]).map(item => (
                  <div
                    key={item.label}
                    className="rounded-xl p-3.5 text-center flex flex-col items-center"
                    style={{ background: '#F4F7FB' }}
                  >
                    <div className="mb-1.5">{item.icon}</div>
                    <p className="text-[9px] uppercase tracking-wider text-gray-400 mb-0.5">{item.label}</p>
                    <p className={`font-semibold text-xs ${item.valueClass ?? 'text-navy-900'} ${item.highlight ? 'text-amber-600' : ''}`}>
                      {item.value}
                    </p>
                  </div>
                ))}
              </div>
            </section>

            {/* POLÍTICAS — US#26 */}
            <section className="w-full bg-white rounded-2xl shadow-sm border border-gray-100 px-6 py-6">
              <h2
                className="text-navy-900 font-bold text-lg mb-1"
                style={{ textDecoration: 'underline', textDecorationColor: '#D4AF37', textUnderlineOffset: '4px' }}
              >
                Políticas del vuelo
              </h2>
              <p className="text-gray-400 text-sm mb-5">Información importante antes de reservar.</p>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-5">
                {[
                  {
                    icon: <ShieldCheck className="w-5 h-5 text-gold-500" />,
                    title: 'Cancelación y reembolsos',
                    desc: 'Cancelaciones hasta 72 hs antes del vuelo reciben reembolso completo. Cancelaciones con menos de 72 hs tienen un cargo del 30% del valor del pasaje.',
                  },
                  {
                    icon: <Luggage className="w-5 h-5 text-gold-500" />,
                    title: 'Equipaje',
                    desc: 'Se permite una valija de hasta 23 kg y un equipaje de mano de hasta 10 kg por pasajero. Excesos de equipaje tienen cargo adicional.',
                  },
                  {
                    icon: <Clock className="w-5 h-5 text-gold-500" />,
                    title: 'Check-in',
                    desc: 'El check-in online abre 48 hs antes del vuelo. Presentarse en el aeropuerto con al menos 2 horas de anticipación. Pasaportes y documentos vigentes son obligatorios.',
                  },
                  {
                    icon: <HeartPulse className="w-5 h-5 text-gold-500" />,
                    title: 'Salud y seguridad',
                    desc: 'Pasajeros con condiciones médicas deben presentar certificado médico. Stellar Jets cumple con todos los protocolos de seguridad aérea internacionales.',
                  },
                ].map(p => (
                  <div key={p.title} className="flex gap-3">
                    <div className="flex-shrink-0 w-9 h-9 rounded-xl bg-gold-50 flex items-center justify-center">
                      {p.icon}
                    </div>
                    <div>
                      <p className="font-semibold text-navy-900 text-sm mb-1">{p.title}</p>
                      <p className="text-gray-500 text-xs leading-relaxed">{p.desc}</p>
                    </div>
                  </div>
                ))}
              </div>
            </section>

            {/* RESEÑAS — US#28 */}
            <section id="resenas" className="bg-white rounded-2xl shadow-sm border border-gray-100 px-6 py-5">
              <div className="flex items-end justify-between mb-1">
                <p className="section-eyebrow text-[11px]">Valoraciones</p>
                {flight.reviewCount > 0 && (
                  <span className="text-xs text-gray-400">{flight.reviewCount} {flight.reviewCount === 1 ? 'reseña' : 'reseñas'}</span>
                )}
              </div>

              {/* Rating promedio grande */}
              {flight.reviewCount > 0 && (
                <div className="flex items-center gap-3 mb-5 pb-4 border-b border-gray-100">
                  <span className="text-4xl font-extrabold text-navy-900">{Number(flight.rating).toFixed(1)}</span>
                  <div>
                    <div className="flex gap-0.5 mb-0.5">
                      {Array.from({ length: 5 }).map((_, i) => (
                        <Star key={i} className={`w-4 h-4 ${i < Math.round(flight.rating) ? 'text-gold-400' : 'text-gray-200'}`} fill={i < Math.round(flight.rating) ? '#D4AF37' : '#e5e7eb'} />
                      ))}
                    </div>
                    <p className="text-xs text-gray-400">Puntuación media</p>
                  </div>
                </div>
              )}

              {/* Formulario nueva reseña */}
              {user ? (
                <form onSubmit={handleReviewSubmit} className="mb-6 pb-6 border-b border-gray-100">
                  <p className="text-sm font-semibold text-navy-900 mb-3">Tu valoración</p>
                  <div className="flex gap-1 mb-3">
                    {Array.from({ length: 5 }).map((_, i) => (
                      <button
                        key={i}
                        type="button"
                        onMouseEnter={() => setHoverStar(i + 1)}
                        onMouseLeave={() => setHoverStar(0)}
                        onClick={() => setReviewStars(i + 1)}
                      >
                        <Star
                          className="w-7 h-7 transition-colors"
                          fill={(hoverStar || reviewStars) > i ? '#D4AF37' : '#e5e7eb'}
                          stroke="none"
                        />
                      </button>
                    ))}
                  </div>
                  <textarea
                    value={reviewComment}
                    onChange={e => setReviewComment(e.target.value)}
                    rows={3}
                    placeholder="Contá tu experiencia (opcional)..."
                    className="w-full text-sm border border-gray-200 rounded-xl px-3 py-2.5 outline-none focus:border-gold-400 resize-none transition text-gray-800"
                  />
                  {reviewError && <p className="text-xs text-red-500 mt-1">{reviewError}</p>}
                  <button
                    type="submit"
                    disabled={submitting}
                    className="btn-gold mt-3 px-6 py-2.5 text-sm font-bold"
                  >
                    {submitting ? 'Guardando...' : 'Publicar reseña'}
                  </button>
                </form>
              ) : (
                <div className="mb-5 pb-5 border-b border-gray-100 text-center">
                  <p className="text-sm text-gray-500 mb-2">Iniciá sesión para dejar tu reseña.</p>
                  <Link to="/login" className="btn-gold px-5 py-2 text-sm">Iniciar sesión</Link>
                </div>
              )}

              {/* Lista de reseñas */}
              {reviews.length === 0 ? (
                <p className="text-sm text-gray-400 text-center py-4">Todavía no hay reseñas. ¡Sé el primero!</p>
              ) : (
                <div className="space-y-4">
                  {reviews.map(r => (
                    <div key={r.id} className="flex gap-3">
                      <div className="w-9 h-9 rounded-full bg-navy-100 flex items-center justify-center text-xs font-bold text-navy-700 flex-shrink-0">
                        {r.firstName[0]}{r.lastName[0]}
                      </div>
                      <div className="flex-1">
                        <div className="flex items-center justify-between gap-2">
                          <p className="text-sm font-semibold text-navy-900">{r.firstName} {r.lastName}</p>
                          <p className="text-[11px] text-gray-400">{r.createdAt}</p>
                        </div>
                        <div className="flex gap-0.5 my-1">
                          {Array.from({ length: 5 }).map((_, i) => (
                            <Star key={i} className="w-3.5 h-3.5" fill={i < r.stars ? '#D4AF37' : '#e5e7eb'} stroke="none" />
                          ))}
                        </div>
                        {r.comment && <p className="text-sm text-gray-600 leading-relaxed">{r.comment}</p>}
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </section>

          </div>

          {/* ── SIDEBAR RESERVA (1/3) ── */}
          <div className="lg:col-span-1">
            <div className="sticky top-24 space-y-4">

              {/* Card precio + reserva */}
              <div className="bg-white rounded-2xl shadow-md border border-gray-100 overflow-hidden">

                {/* Header precio */}
                <div className="px-6 py-5" style={{ background: '#0A1428' }}>
                  <p className="text-[10px] uppercase tracking-[0.2em] text-white/45 mb-1.5">
                    Precio por persona
                  </p>
                  <div className="flex items-end gap-1">
                    <span className="text-gold-400 text-xl font-semibold mb-0.5">$</span>
                    <span className="text-4xl font-extrabold text-white leading-none">
                      {flight.price.toLocaleString('es-AR')}
                    </span>
                  </div>

                  {/* Estrellas */}
                  <div className="flex items-center gap-1.5 mt-3">
                    <div className="flex gap-0.5">
                      {Array.from({ length: 5 }).map((_, i) => (
                        <svg
                          key={i}
                          className={`w-3.5 h-3.5 ${i < stars ? 'text-gold-400' : 'text-white/15'}`}
                          fill="currentColor"
                          viewBox="0 0 20 20"
                        >
                          <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.286 3.957a1 1 0 00.95.69h4.162c.969 0 1.372 1.24.588 1.81l-3.368 2.448a1 1 0 00-.364 1.118l1.287 3.957c.3.921-.755 1.688-1.54 1.118L10 14.347l-3.952 2.878c-.784.57-1.838-.197-1.539-1.118l1.286-3.957a1 1 0 00-.364-1.118L2.063 9.384c-.784-.57-.38-1.81.588-1.81h4.162a1 1 0 00.95-.69l1.286-3.957z" />
                        </svg>
                      ))}
                    </div>
                    <span className="text-white/50 text-xs">{flight.rating?.toFixed(1)} / 5.0</span>
                  </div>
                  <a
                    href="#resenas"
                    className="inline-block mt-2 text-[11px] text-gold-400/70 hover:text-gold-400 transition underline underline-offset-2"
                  >
                    {flight.reviewCount > 0 ? `Ver ${flight.reviewCount} reseñas` : 'Dejá tu reseña'}
                  </a>
                </div>

                <div className="px-5 py-5">

                  {/* Asientos */}
                  <div className="flex items-center gap-3 rounded-xl px-4 py-3 mb-4" style={{ background: '#F4F7FB' }}>
                    <Users className="w-5 h-5 text-gold-500 flex-shrink-0" />
                    <div>
                      <p className="text-[10px] text-gray-400 leading-none mb-0.5">Asientos disponibles</p>
                      <p className="text-sm font-bold text-gray-900">{flight.availableSeats} plazas</p>
                    </div>
                  </div>

                  {/* CTA — US#30 */}
                  <button
                    className="btn-gold w-full py-4 text-[0.95rem] font-bold"
                    disabled={!flight.active}
                    style={!flight.active ? { opacity: 0.4, cursor: 'not-allowed' } : undefined}
                    onClick={() => {
                      if (!flight.active) return
                      if (user) {
                        navigate(`/reservations/${flight.id}`)
                      } else {
                        navigate('/login', { state: { from: `/reservations/${flight.id}`, requiresAuth: true } })
                      }
                    }}
                  >
                    {flight.active ? 'Reservar ahora' : 'No disponible'}
                    {flight.active && <ArrowRight className="w-4 h-4" />}
                  </button>

                  <button
                    type="button"
                    onClick={() => setShowShare(true)}
                    className="w-full mt-2.5 py-2.5 text-sm text-gray-500 hover:text-navy-800 border border-gray-200 hover:border-gray-300 rounded-xl transition-colors flex items-center justify-center gap-2"
                  >
                    <Share2 className="w-4 h-4" />
                    Compartir vuelo
                  </button>

                  <button
                    onClick={() => navigate(-1)}
                    className="w-full mt-2 py-2.5 text-sm text-gray-400 hover:text-gray-700 transition-colors flex items-center justify-center gap-2"
                  >
                    <ArrowLeft className="w-4 h-4" />
                    Volver a resultados
                  </button>
                </div>
              </div>

              {/* Info extra */}
              <div
                className="rounded-2xl px-5 py-4 border border-gold-500/20"
                style={{ background: 'rgba(212,175,55,0.04)' }}
              >
                <p className="text-xs font-semibold text-gray-700 mb-1">¿Necesitás ayuda?</p>
                <p className="text-xs text-gray-500 leading-relaxed">
                  Nuestro equipo está disponible 24/7 para asistirte con tu reserva.
                </p>
              </div>

            </div>
          </div>

        </div>
      </div>

      {/* Modal compartir — US#27 */}
      {showShare && flight && (
        <ShareModal flight={flight} onClose={() => setShowShare(false)} />
      )}
    </main>
  )
}
