import { useEffect, useState } from 'react'
import { useParams, useNavigate, Link } from 'react-router-dom'
import type { DateRange } from 'react-day-picker'
import { CheckCircle, ArrowLeft, CalendarDays } from 'lucide-react'
import DateRangePicker from '../components/DateRangePicker'
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

export default function ReservationPage() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const { user } = useAuth()

  const [flight, setFlight] = useState<Flight | null>(null)
  const [occupiedRanges, setOccupiedRanges] = useState<OccupiedDateRange[]>([])
  const [range, setRange] = useState<DateRange | undefined>()
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
    return occupied.some(occ =>
      r.from! <= occ.to && r.to! >= occ.from
    )
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    setError('')
    if (!range?.from || !range?.to) {
      setError('Seleccioná un rango de fechas para continuar.')
      return
    }
    if (rangeOverlapsOccupied(range)) {
      setError('El rango seleccionado incluye fechas no disponibles. Elegí otras fechas.')
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
      const msg = (err as { response?: { data?: { message?: string } } }).response?.data?.message
      setError(msg ?? 'No se pudo crear la reserva. Intentá con otras fechas.')
    } finally {
      setSubmitting(false)
    }
  }

  if (loading) {
    return (
      <main className="pt-[68px] min-h-screen flex items-center justify-center" style={{ background: '#F4F7FB' }}>
        <p className="text-gray-500">Cargando...</p>
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

  // ── Pantalla de confirmación ──────────────────────────────────────────────
  if (confirmed) {
    return (
      <main className="pt-[68px] min-h-screen flex items-center justify-center px-4" style={{ background: '#F4F7FB' }}>
        <div className="bg-white rounded-2xl shadow-md border border-gray-100 max-w-md w-full overflow-hidden">
          <div className="px-8 py-7 text-center" style={{ background: '#060E1A' }}>
            <CheckCircle className="w-12 h-12 mx-auto mb-3" style={{ color: '#D4AF37' }} />
            <h1 className="text-white text-xl font-semibold">¡Reserva confirmada!</h1>
          </div>
          <div className="px-8 py-7 space-y-4">
            <div className="rounded-xl p-4 space-y-2" style={{ background: '#F4F7FB' }}>
              <p className="font-semibold text-navy-900">{confirmed.flightName}</p>
              <div className="flex items-center gap-2 text-sm text-gray-600">
                <CalendarDays className="w-4 h-4" style={{ color: '#D4AF37' }} />
                <span>{fmt(new Date(confirmed.checkIn + 'T00:00:00'))} → {fmt(new Date(confirmed.checkOut + 'T00:00:00'))}</span>
              </div>
              <p className="text-xs text-gray-400">{confirmed.userEmail}</p>
            </div>
            <Link
              to="/"
              className="btn-gold w-full py-3 text-sm font-semibold text-center block"
            >
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

  // ── Formulario de reserva ─────────────────────────────────────────────────
  return (
    <main className="pt-[68px] min-h-screen px-4 py-10" style={{ background: '#F4F7FB' }}>
      <div className="max-w-xl mx-auto">

        {/* Volver */}
        <Link
          to={`/flights/${flight.id}`}
          className="inline-flex items-center gap-2 text-sm text-gray-500 hover:text-navy-700 mb-6 transition"
        >
          <ArrowLeft className="w-4 h-4" /> Volver al detalle
        </Link>

        <div className="bg-white rounded-2xl shadow-md border border-gray-100">

          {/* Header */}
          <div className="px-8 py-7 rounded-t-2xl" style={{ background: '#060E1A' }}>
            <p className="text-[10px] uppercase tracking-[0.25em] text-white/40 mb-1">Reservá tu vuelo</p>
            <h1
              className="text-white text-lg font-semibold leading-tight"
              style={{ fontFamily: "'Cinzel', serif" }}
            >
              {flight.name}
            </h1>
            <p className="text-white/50 text-sm mt-1">
              {flight.origin.city} ({flight.origin.iataCode}) → {flight.destination.city} ({flight.destination.iataCode})
            </p>
          </div>

          {/* Form */}
          <form onSubmit={handleSubmit} className="px-8 py-7 space-y-6">

            {/* Selector de fechas */}
            <div>
              <label className="block text-xs font-medium text-gray-600 mb-2">
                Seleccioná las fechas de tu viaje
              </label>
              <div
                className="rounded-xl px-4 py-3 border"
                style={{ borderColor: '#e5e7eb', background: '#fafafa' }}
              >
                <DateRangePicker
                  range={range}
                  onChange={r => { setRange(r); setError('') }}
                  light
                  disabledRanges={occupied}
                />
              </div>
              {range?.from && (
                <div className="flex items-center gap-2 mt-3 px-1">
                  <CalendarDays className="w-4 h-4 flex-shrink-0" style={{ color: '#D4AF37' }} />
                  <p className="text-sm text-gray-700">
                    <strong>{fmt(range.from)}</strong>
                    {range.to && <> → <strong>{fmt(range.to)}</strong></>}
                    {!range.to && <span className="text-gray-400"> → seleccioná la fecha de salida</span>}
                  </p>
                </div>
              )}
            </div>

            {/* Fechas ocupadas */}
            {occupiedRanges.length > 0 && (
              <div className="rounded-xl p-4 border border-amber-100" style={{ background: '#fffbeb' }}>
                <p className="text-xs font-medium text-amber-700 mb-2">Fechas no disponibles:</p>
                <ul className="space-y-1">
                  {occupiedRanges.map((r, i) => (
                    <li key={i} className="text-xs text-amber-600">
                      {fmt(new Date(r.checkIn + 'T00:00:00'))} → {fmt(new Date(r.checkOut + 'T00:00:00'))}
                    </li>
                  ))}
                </ul>
              </div>
            )}

            {error && (
              <div className="px-4 py-3 rounded-xl bg-red-50 border border-red-200 text-red-700 text-sm">
                {error}
              </div>
            )}

            <button
              type="submit"
              disabled={submitting || !range?.from || !range?.to}
              className="btn-gold w-full py-3.5 text-sm font-semibold disabled:opacity-50"
            >
              {submitting ? 'Procesando...' : 'Confirmar reserva'}
            </button>
          </form>
        </div>
      </div>
    </main>
  )
}
