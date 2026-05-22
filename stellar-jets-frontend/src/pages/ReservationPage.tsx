import { useEffect, useState } from 'react'
import { useParams, useNavigate, Link } from 'react-router-dom'
import {
  CheckCircle, ArrowLeft, CalendarDays, User, Mail,
  MapPin, Tag, Users, Timer, Star,
} from 'lucide-react'
import { DayPicker } from 'react-day-picker'
import type { DateRange } from 'react-day-picker'
import { es } from 'date-fns/locale'
import 'react-day-picker/style.css'
import CharIcon from '../components/CharIcon'
import { getFlightById, getOccupiedDates, createReservation } from '../api/flightApi'
import type { Flight, OccupiedDateRange, ReservationResponse } from '../types'
import { useAuth } from '../context/AuthContext'

function parseOccupied(ranges: OccupiedDateRange[]) {
  return ranges.map(r => ({
    from: new Date(r.checkIn + 'T00:00:00'),
    to: new Date(r.checkOut + 'T00:00:00'),
  }))
}

function fmt(d: Date) {
  return d.toLocaleDateString('es-AR', { day: '2-digit', month: 'long', year: 'numeric' })
}

function formatDuration(minutes: number | null) {
  if (!minutes) return null
  const h = Math.floor(minutes / 60)
  const m = minutes % 60
  return h > 0 ? `${h}h${m > 0 ? ` ${m}m` : ''}` : `${m}m`
}

export default function ReservationPage() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const { user } = useAuth()

  const [flight, setFlight] = useState<Flight | null>(null)
  const [occupiedRanges, setOccupiedRanges] = useState<OccupiedDateRange[]>([])
  const [range, setRange] = useState<DateRange | undefined>()
  const [notes, setNotes] = useState('')
  const [showUserDetail, setShowUserDetail] = useState(false)
  const [loading, setLoading] = useState(true)
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState('')
  const [confirmed, setConfirmed] = useState<ReservationResponse | null>(null)

  useEffect(() => {
    if (!user) {
      navigate('/login', { state: { from: `/reservations/${id}`, requiresAuth: true } })
      return
    }
    if (!id) return
    Promise.all([
      getFlightById(Number(id)),
      getOccupiedDates(Number(id)),
    ]).then(([f, occ]) => {
      setFlight(f)
      setOccupiedRanges(occ)
    }).finally(() => setLoading(false))
  }, [id, user, navigate])

  const occupied = parseOccupied(occupiedRanges)

  function rangeOverlapsOccupied(r: DateRange) {
    if (!r.from || !r.to) return false
    return occupied.some(occ => r.from! <= occ.to && r.to! >= occ.from)
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    setError('')
    if (!range?.from || !range?.to) {
      setError('Seleccioná un rango de fechas para continuar.')
      return
    }
    if (rangeOverlapsOccupied(range)) {
      setError('El rango seleccionado incluye fechas no disponibles. Por favor elegí otras fechas.')
      return
    }
    setSubmitting(true)
    try {
      const res = await createReservation(Number(id), {
        checkIn: range.from.toISOString().split('T')[0],
        checkOut: range.to.toISOString().split('T')[0],
      })
      setConfirmed(res)
    } catch (err: unknown) {
      const status = (err as { response?: { status?: number; data?: { message?: string } } }).response?.status
      const msg = (err as { response?: { data?: { message?: string } } }).response?.data?.message
      if (status === 409) {
        setError('Las fechas seleccionadas se solapan con una reserva existente. Por favor elegí otras fechas.')
      } else if (status === 400) {
        setError('Las fechas ingresadas no son válidas. La fecha de salida debe ser posterior a la de entrada.')
      } else if (status === 404) {
        setError('El vuelo seleccionado no existe.')
      } else {
        setError(msg ?? 'Ocurrió un error al procesar la reserva. Intentá de nuevo más tarde.')
      }
    } finally {
      setSubmitting(false)
    }
  }

  if (loading) {
    return (
      <main className="pt-[68px] min-h-screen flex items-center justify-center" style={{ background: '#F4F7FB' }}>
        <div className="flex flex-col items-center gap-3">
          <div className="w-10 h-10 rounded-full border-2 border-gold-500/30 border-t-gold-500 animate-spin" />
          <p className="text-xs text-gray-400 tracking-widest uppercase">Cargando</p>
        </div>
      </main>
    )
  }

  if (!flight) {
    return (
      <main className="pt-[68px] min-h-screen flex items-center justify-center" style={{ background: '#F4F7FB' }}>
        <p className="text-gray-500">Vuelo no encontrado.</p>
      </main>
    )
  }

  const cover =
    flight.coverImageUrl ??
    flight.images?.find(i => i.cover)?.url ??
    flight.images?.[0]?.url ??
    'https://images.unsplash.com/photo-1436491865332-7a61a109cc05?w=1200'

  // ── Pantalla de confirmación ────────────────────────────────────────────────
  if (confirmed) {
    return (
      <main className="pt-[68px] min-h-screen flex items-center justify-center px-4 py-10" style={{ background: '#F4F7FB' }}>
        <div className="bg-white rounded-2xl shadow-md border border-gray-100 max-w-lg w-full overflow-hidden">
          <div className="px-8 py-8 text-center" style={{ background: '#060E1A' }}>
            <CheckCircle className="w-14 h-14 mx-auto mb-3" style={{ color: '#D4AF37' }} />
            <h1 className="text-white text-2xl font-bold" style={{ fontFamily: "'Cinzel', serif" }}>
              ¡Reserva confirmada!
            </h1>
            <p className="text-white/50 text-sm mt-2">Tu reserva fue procesada con éxito.</p>
          </div>

          <div className="px-8 py-7 space-y-5">
            {/* Imagen del vuelo */}
            <div className="rounded-xl overflow-hidden h-40">
              <img src={cover} alt={confirmed.flightName} className="w-full h-full object-cover" />
            </div>

            {/* Datos de la reserva */}
            <div className="rounded-xl p-5 space-y-3" style={{ background: '#F4F7FB' }}>
              <p className="font-bold text-navy-900 text-base">{confirmed.flightName}</p>
              <div className="flex items-center gap-2 text-sm text-gray-600">
                <CalendarDays className="w-4 h-4 flex-shrink-0" style={{ color: '#D4AF37' }} />
                <span>
                  {fmt(new Date(confirmed.checkIn + 'T00:00:00'))}
                  {' → '}
                  {fmt(new Date(confirmed.checkOut + 'T00:00:00'))}
                </span>
              </div>
              <div className="flex items-center gap-2 text-sm text-gray-600">
                <User className="w-4 h-4 flex-shrink-0" style={{ color: '#D4AF37' }} />
                <span>{user?.firstName} {user?.lastName}</span>
              </div>
              <div className="flex items-center gap-2 text-sm text-gray-600">
                <Mail className="w-4 h-4 flex-shrink-0" style={{ color: '#D4AF37' }} />
                <span>{confirmed.userEmail}</span>
              </div>
              {notes && (
                <p className="text-xs text-gray-500 pt-1 border-t border-gray-200">
                  <span className="font-medium">Notas:</span> {notes}
                </p>
              )}
            </div>

            <Link to="/" className="btn-gold w-full py-3 text-sm font-semibold text-center block">
              Volver al inicio
            </Link>
            <Link
              to={`/flights/${confirmed.flightId}`}
              className="block text-center text-sm text-navy-600 hover:underline"
            >
              Ver detalle del vuelo
            </Link>
          </div>
        </div>
      </main>
    )
  }

  // ── Formulario de reserva ───────────────────────────────────────────────────
  return (
    <main className="pt-[68px] min-h-screen px-4 py-10" style={{ background: '#F4F7FB' }}>
      <div className="max-w-6xl mx-auto">

        {/* Volver */}
        <Link
          to={`/flights/${flight.id}`}
          className="inline-flex items-center gap-2 text-sm text-gray-500 hover:text-navy-700 mb-6 transition"
        >
          <ArrowLeft className="w-4 h-4" /> Volver al detalle
        </Link>

        <div className="grid grid-cols-1 lg:grid-cols-5 gap-8 items-start">

          {/* ── COLUMNA IZQUIERDA — Detalle del producto (3/5) ── */}
          <div className="lg:col-span-3 space-y-5">

            {/* Imagen hero */}
            <div className="rounded-2xl overflow-hidden shadow-sm" style={{ height: '320px' }}>
              <img src={cover} alt={flight.name} className="w-full h-full object-cover" />
            </div>

            {/* Info principal */}
            <div className="bg-white rounded-2xl shadow-sm border border-gray-100 px-6 py-5">
              {flight.category && (
                <p className="text-[10px] font-bold uppercase tracking-[0.2em] mb-1" style={{ color: '#D4AF37' }}>
                  {flight.category.name}
                </p>
              )}
              <h1 className="text-xl font-bold text-navy-900 mb-2">{flight.name}</h1>
              {flight.description && (
                <p className="text-sm text-gray-600 leading-relaxed">{flight.description}</p>
              )}
            </div>

            {/* Ruta */}
            <div className="bg-white rounded-2xl shadow-sm border border-gray-100 px-6 py-5">
              <p className="text-[10px] uppercase tracking-wider text-gray-400 mb-4">Itinerario</p>
              <div className="flex items-center justify-between gap-4">
                <div className="text-center">
                  <p className="text-2xl font-extrabold text-navy-900 font-mono">{flight.origin.iataCode}</p>
                  <p className="text-sm font-semibold text-gray-700">{flight.origin.city}</p>
                  <p className="text-xs text-gray-400">{flight.origin.country}</p>
                </div>
                <div className="flex-1 flex flex-col items-center">
                  <div className="flex items-center w-full">
                    <div className="w-2 h-2 rounded-full border-2 flex-shrink-0" style={{ borderColor: '#D4AF37' }} />
                    <div className="flex-1 h-px mx-1" style={{ background: 'repeating-linear-gradient(90deg,#D4AF37 0,#D4AF37 5px,transparent 5px,transparent 12px)' }} />
                    <svg className="w-5 h-5 flex-shrink-0" fill="#D4AF37" viewBox="0 0 24 24">
                      <path d="M21 16v-2l-8-5V3.5c0-.83-.67-1.5-1.5-1.5S10 2.67 10 3.5V9l-8 5v2l8-2.5V19l-2 1.5V22l3.5-1 3.5 1v-1.5L13 19v-5.5l8 2.5z"/>
                    </svg>
                    <div className="flex-1 h-px mx-1" style={{ background: 'repeating-linear-gradient(90deg,#D4AF37 0,#D4AF37 5px,transparent 5px,transparent 12px)' }} />
                    <div className="w-2 h-2 rounded-full flex-shrink-0" style={{ background: '#D4AF37' }} />
                  </div>
                  {flight.durationMinutes && (
                    <p className="text-xs text-gray-400 mt-1">{formatDuration(flight.durationMinutes)}</p>
                  )}
                </div>
                <div className="text-center">
                  <p className="text-2xl font-extrabold text-navy-900 font-mono">{flight.destination.iataCode}</p>
                  <p className="text-sm font-semibold text-gray-700">{flight.destination.city}</p>
                  <p className="text-xs text-gray-400">{flight.destination.country}</p>
                </div>
              </div>
            </div>

            {/* Chips de detalles */}
            <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
              {[
                { icon: <Tag className="w-4 h-4 text-navy-600" />, label: 'Categoría', value: flight.category?.name ?? '—' },
                { icon: <Users className="w-4 h-4 text-navy-600" />, label: 'Asientos', value: `${flight.availableSeats} disp.` },
                { icon: <Timer className="w-4 h-4 text-navy-600" />, label: 'Duración', value: formatDuration(flight.durationMinutes) ?? '—' },
                { icon: <Star className="w-4 h-4 text-gold-500" />, label: 'Rating', value: `${flight.rating?.toFixed(1)} / 5` },
              ].map(item => (
                <div key={item.label} className="bg-white rounded-xl p-3 text-center border border-gray-100 shadow-sm">
                  <div className="flex justify-center mb-1">{item.icon}</div>
                  <p className="text-[9px] uppercase tracking-wider text-gray-400">{item.label}</p>
                  <p className="text-xs font-semibold text-navy-900 mt-0.5">{item.value}</p>
                </div>
              ))}
            </div>

            {/* Características */}
            {flight.characteristics?.length > 0 && (
              <div className="bg-white rounded-2xl shadow-sm border border-gray-100 px-6 py-5">
                <p className="text-[10px] uppercase tracking-wider text-gray-400 mb-3">Características</p>
                <div className="grid grid-cols-2 gap-2">
                  {flight.characteristics.map(c => (
                    <div key={c.id} className="flex items-center gap-2 rounded-xl px-3 py-2 border border-gray-100">
                      <CharIcon iconName={c.iconName} className="w-4 h-4 text-navy-600" />
                      <span className="text-xs font-medium text-gray-700">{c.name}</span>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {/* Precio */}
            <div className="rounded-2xl px-6 py-4 flex items-center justify-between" style={{ background: '#0A1428' }}>
              <p className="text-white/60 text-sm">Precio por persona</p>
              <div className="flex items-end gap-1">
                <span className="text-lg font-semibold" style={{ color: '#D4AF37' }}>$</span>
                <span className="text-3xl font-extrabold text-white">{flight.price.toLocaleString('es-AR')}</span>
              </div>
            </div>

          </div>

          {/* ── COLUMNA DERECHA — Formulario (2/5) ── */}
          <div className="lg:col-span-2">
            <div className="sticky top-24">
              <form onSubmit={handleSubmit} className="bg-white rounded-2xl shadow-md border border-gray-100 overflow-hidden">

                <div className="px-6 py-5 rounded-t-2xl" style={{ background: '#060E1A' }}>
                  <p className="text-[10px] uppercase tracking-[0.2em] text-white/40 mb-1">Completá tu reserva</p>
                  <p className="text-white font-semibold">{flight.name}</p>
                  <p className="text-white/50 text-xs mt-0.5">
                    <MapPin className="w-3 h-3 inline mr-1" />
                    {flight.origin.city} → {flight.destination.city}
                  </p>
                </div>

                <div className="px-6 py-6 space-y-5">

                  {/* Datos del usuario */}
                  <div className="rounded-xl border border-gray-100 overflow-hidden">
                    <button
                      type="button"
                      onClick={() => setShowUserDetail(v => !v)}
                      className="w-full flex items-center justify-between px-4 py-3 hover:bg-gray-50 transition"
                    >
                      <div className="flex items-center gap-3">
                        <div className="w-8 h-8 rounded-full bg-navy-100 flex items-center justify-center text-xs font-bold text-navy-700">
                          {user?.firstName?.[0]}{user?.lastName?.[0]}
                        </div>
                        <div className="text-left">
                          <p className="text-sm font-semibold text-navy-900">{user?.firstName} {user?.lastName}</p>
                          <p className="text-xs text-gray-400">{user?.email}</p>
                        </div>
                      </div>
                      <span className="text-xs text-gray-400">{showUserDetail ? '▲' : '▼'}</span>
                    </button>

                    {showUserDetail && (
                      <div className="px-4 py-3 border-t border-gray-100 space-y-2" style={{ background: '#fafafa' }}>
                        <div className="flex items-center gap-2 text-sm text-gray-600">
                          <User className="w-3.5 h-3.5 text-gray-400" />
                          <span>{user?.firstName} {user?.lastName}</span>
                        </div>
                        <div className="flex items-center gap-2 text-sm text-gray-600">
                          <Mail className="w-3.5 h-3.5 text-gray-400" />
                          <span>{user?.email}</span>
                        </div>
                      </div>
                    )}
                  </div>

                  {/* Selector de fechas — calendario embebido */}
                  <div>
                    <label className="block text-xs font-medium text-gray-600 mb-2">
                      Fechas de viaje <span className="text-red-400">*</span>
                    </label>
                    <div className="rounded-xl border flex justify-center overflow-x-auto" style={{ borderColor: '#e5e7eb', background: '#fafafa' }}>
                      <DayPicker
                        mode="range"
                        selected={range}
                        onSelect={r => { setRange(r); setError('') }}
                        numberOfMonths={1}
                        locale={es}
                        disabled={{ before: new Date() }}
                        modifiers={{ occupied }}
                        modifiersClassNames={{ occupied: 'rdp-day--occupied' }}
                        showOutsideDays
                      />
                    </div>
                    {range?.from && (
                      <div className="flex items-center gap-2 mt-2 px-1">
                        <CalendarDays className="w-3.5 h-3.5 flex-shrink-0" style={{ color: '#D4AF37' }} />
                        <p className="text-xs text-gray-600">
                          <strong>{fmt(range.from)}</strong>
                          {range.to
                            ? <> → <strong>{fmt(range.to)}</strong></>
                            : <span className="text-gray-400"> → seleccioná la salida</span>
                          }
                        </p>
                      </div>
                    )}
                  </div>

                  {/* Fechas no disponibles */}
                  {occupiedRanges.length > 0 && (
                    <div className="rounded-xl p-3 border border-amber-100 text-xs" style={{ background: '#fffbeb' }}>
                      <p className="font-medium text-amber-700 mb-1.5">Fechas no disponibles:</p>
                      <ul className="space-y-0.5">
                        {occupiedRanges.map((r, i) => (
                          <li key={i} className="text-amber-600">
                            {fmt(new Date(r.checkIn + 'T00:00:00'))} → {fmt(new Date(r.checkOut + 'T00:00:00'))}
                          </li>
                        ))}
                      </ul>
                    </div>
                  )}

                  {/* Notas opcionales */}
                  <div>
                    <label className="block text-xs font-medium text-gray-600 mb-2">
                      Notas adicionales <span className="text-gray-400">(opcional)</span>
                    </label>
                    <textarea
                      value={notes}
                      onChange={e => setNotes(e.target.value)}
                      rows={3}
                      placeholder="Preferencias especiales, requerimientos, etc."
                      className="w-full text-sm border border-gray-200 rounded-xl px-3 py-2.5 outline-none focus:border-gold-400 resize-none transition text-gray-800"
                    />
                  </div>

                  {/* Error */}
                  {error && (
                    <div className="px-4 py-3 rounded-xl bg-red-50 border border-red-200 text-red-700 text-sm">
                      {error}
                    </div>
                  )}

                  {/* Submit */}
                  <button
                    type="submit"
                    disabled={submitting || !range?.from || !range?.to}
                    className="btn-gold w-full py-3.5 text-sm font-semibold disabled:opacity-50"
                  >
                    {submitting ? 'Procesando...' : 'Confirmar reserva'}
                  </button>

                  <p className="text-center text-xs text-gray-400">
                    Al confirmar aceptás los términos y políticas del vuelo.
                  </p>
                </div>
              </form>
            </div>
          </div>

        </div>
      </div>
    </main>
  )
}
