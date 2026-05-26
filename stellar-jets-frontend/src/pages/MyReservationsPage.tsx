import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { CalendarDays, MapPin, Plane, Clock } from 'lucide-react'
import { getMyReservations } from '../api/flightApi'
import type { ReservationResponse } from '../types'
import { useAuth } from '../context/AuthContext'

function fmt(d: string) {
  return new Date(d + 'T00:00:00').toLocaleDateString('es-AR', {
    day: '2-digit', month: 'long', year: 'numeric',
  })
}

function statusBadge(checkOut: string) {
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  const out = new Date(checkOut + 'T00:00:00')
  return out >= today ? 'upcoming' : 'past'
}

export default function MyReservationsPage() {
  const { user } = useAuth()
  const navigate = useNavigate()
  const [reservations, setReservations] = useState<ReservationResponse[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    getMyReservations()
      .then(setReservations)
      .finally(() => setLoading(false))
  }, [user, navigate])

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

  return (
    <main className="pt-[68px] min-h-screen px-4 py-10" style={{ background: '#F4F7FB' }}>
      <div className="max-w-3xl mx-auto">

        {/* Título */}
        <div className="mb-8">
          <p className="text-[10px] uppercase tracking-[0.25em] text-gray-400 mb-1">Mi cuenta</p>
          <h1 className="text-2xl font-bold text-navy-900">Mis reservas</h1>
          <p className="text-sm text-gray-500 mt-1">
            {user?.firstName} {user?.lastName} · {user?.email}
          </p>
        </div>

        {/* Sin reservas */}
        {reservations.length === 0 ? (
          <div className="bg-white rounded-2xl shadow-sm border border-gray-100 px-8 py-16 text-center">
            <Plane className="w-12 h-12 mx-auto mb-4 text-gray-200" />
            <p className="text-gray-500 font-medium mb-1">Todavía no tenés reservas</p>
            <p className="text-sm text-gray-400 mb-6">Explorá nuestros vuelos y hacé tu primera reserva.</p>
            <Link to="/" className="btn-gold px-8 py-3 text-sm font-semibold">
              Explorar vuelos
            </Link>
          </div>
        ) : (
          <div className="space-y-4">
            {reservations.map(r => {
              const status = statusBadge(r.checkOut)
              return (
                <div
                  key={r.id}
                  className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden flex flex-col sm:flex-row"
                >
                  {/* Imagen */}
                  {r.coverImageUrl ? (
                    <div className="sm:w-48 h-40 sm:h-auto flex-shrink-0">
                      <img
                        src={r.coverImageUrl}
                        alt={r.flightName}
                        className="w-full h-full object-cover"
                      />
                    </div>
                  ) : (
                    <div className="sm:w-48 h-40 sm:h-auto flex-shrink-0 flex items-center justify-center" style={{ background: '#0A1428' }}>
                      <Plane className="w-8 h-8 text-gold-400" />
                    </div>
                  )}

                  {/* Info */}
                  <div className="flex-1 px-5 py-5 flex flex-col justify-between">
                    <div>
                      {/* Badge estado */}
                      <div className="flex items-center justify-between mb-2">
                        <span
                          className="text-[10px] font-bold uppercase tracking-wider px-2.5 py-1 rounded-full"
                          style={status === 'upcoming'
                            ? { background: '#ecfdf5', color: '#065f46' }
                            : { background: '#f3f4f6', color: '#6b7280' }
                          }
                        >
                          {status === 'upcoming' ? 'Próxima' : 'Completada'}
                        </span>
                        <span className="text-[11px] text-gray-400">Reserva #{r.id}</span>
                      </div>

                      <h2 className="font-bold text-navy-900 text-base mb-1">{r.flightName}</h2>

                      {r.flightOrigin && r.flightDestination && (
                        <div className="flex items-center gap-1.5 text-sm text-gray-500 mb-3">
                          <MapPin className="w-3.5 h-3.5 flex-shrink-0" />
                          <span>{r.flightOrigin} → {r.flightDestination}</span>
                        </div>
                      )}

                      <div className="space-y-1.5">
                        <div className="flex items-center gap-2 text-sm text-gray-600">
                          <CalendarDays className="w-4 h-4 flex-shrink-0" style={{ color: '#D4AF37' }} />
                          <span>
                            <strong>{fmt(r.checkIn)}</strong> → <strong>{fmt(r.checkOut)}</strong>
                          </span>
                        </div>
                        {r.createdAt && (
                          <div className="flex items-center gap-2 text-xs text-gray-400">
                            <Clock className="w-3.5 h-3.5 flex-shrink-0" />
                            <span>Reservado el {fmt(r.createdAt)}</span>
                          </div>
                        )}
                      </div>
                    </div>

                    <div className="mt-4">
                      <Link
                        to={`/flights/${r.flightId}`}
                        className="text-sm font-medium hover:underline"
                        style={{ color: '#D4AF37' }}
                      >
                        Ver detalle del vuelo →
                      </Link>
                    </div>
                  </div>
                </div>
              )
            })}
          </div>
        )}
      </div>
    </main>
  )
}
