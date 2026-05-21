import { Link } from 'react-router-dom'
import { Heart } from 'lucide-react'
import type { Flight } from '../types'
import { useAuth } from '../context/AuthContext'
import { useFavorites } from '../context/FavoritesContext'

function formatDuration(minutes: number | null): string {
  if (!minutes) return ''
  const h = Math.floor(minutes / 60)
  const m = minutes % 60
  return h > 0 ? `${h}h${m > 0 ? ` ${m}m` : ''}` : `${m}m`
}

interface Props {
  flight: Flight
  featured?: boolean
}

export default function FlightCard({ flight, featured = false }: Props) {
  const { user } = useAuth()
  const { isFavorite, toggle } = useFavorites()
  const fav = isFavorite(flight.id)

  const cover =
    flight.coverImageUrl ??
    flight.images?.find(i => i.cover)?.url ??
    flight.images?.[0]?.url ??
    'https://images.unsplash.com/photo-1436491865332-7a61a109cc05?w=800'

  const stars = Math.round(flight.rating ?? 0)

  return (
    <Link to={`/flights/${flight.id}`} className="flight-card">

      {/* Imagen de fondo — llena todo */}
      <div className="absolute inset-0">
        <img src={cover} alt={flight.name} className="fc-img" loading="lazy" />
      </div>

      {/* Overlay degradado */}
      <div className="fc-overlay" />

      {/* Badges superiores */}
      <div className="absolute top-3 left-3 right-3 flex items-start justify-between z-10">
        <div className="flex flex-wrap gap-1.5">
          {flight.category && (
            <span
              className="text-[10px] font-bold uppercase tracking-wider px-2.5 py-1 rounded-full leading-none"
              style={{
                background: 'rgba(212,175,55,0.18)',
                border: '1px solid rgba(212,175,55,0.45)',
                color: '#F5D576',
              }}
            >
              {flight.category.name}
            </span>
          )}
          {flight.flightNumber && (
            <span
              className="text-[10px] font-mono px-2 py-1 rounded-full leading-none"
              style={{
                background: 'rgba(255,255,255,0.10)',
                border: '1px solid rgba(255,255,255,0.18)',
                color: 'rgba(203,213,225,0.85)',
              }}
            >
              {flight.flightNumber}
            </span>
          )}
        </div>
        <div className="flex items-center gap-1.5">
          {!flight.active && (
            <span className="text-[10px] font-bold uppercase px-2.5 py-1 rounded-full bg-red-600/85 text-white leading-none">
              No disponible
            </span>
          )}
          {user && (
            <button
              type="button"
              onClick={e => { e.preventDefault(); e.stopPropagation(); toggle(flight) }}
              className="w-8 h-8 rounded-full flex items-center justify-center transition-all duration-200"
              style={{
                background: fav ? 'rgba(239,68,68,0.85)' : 'rgba(0,0,0,0.35)',
                backdropFilter: 'blur(4px)',
              }}
              aria-label={fav ? 'Quitar de favoritos' : 'Agregar a favoritos'}
            >
              <Heart
                className="w-4 h-4 transition-all"
                fill={fav ? '#fff' : 'none'}
                stroke={fav ? '#fff' : 'rgba(255,255,255,0.85)'}
                strokeWidth={2}
              />
            </button>
          )}
        </div>
      </div>

      {/* Info inferior — superpuesta sobre el degradado */}
      <div className="absolute bottom-0 left-0 right-0 p-4 z-10">

        {/* Nombre destino */}
        <h3
          className="font-bold text-white leading-snug mb-1"
          style={{ fontSize: featured ? '1.25rem' : '1rem' }}
        >
          {flight.name}
        </h3>

        {/* Ruta IATA + duración */}
        <div className="flex items-center gap-1.5 mb-2">
          <span className="text-[11px] font-mono font-bold text-gold-400 tracking-wider">
            {flight.origin?.iataCode}
          </span>
          <svg className="w-3 h-3 text-gold-500/70 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M17 8l4 4m0 0l-4 4m4-4H3" />
          </svg>
          <span className="text-[11px] font-mono font-bold text-gold-400 tracking-wider">
            {flight.destination?.iataCode}
          </span>
          {flight.durationMinutes && (
            <span className="text-[10px] text-white/40 ml-0.5">
              · {formatDuration(flight.durationMinutes)}
            </span>
          )}
        </div>

        {/* Precio + estrellas */}
        <div className="flex items-center justify-between">
          <div>
            <p className="text-[9px] text-white/45 uppercase tracking-wider leading-none mb-0.5">desde</p>
            <p
              className="font-bold text-white leading-none"
              style={{ fontSize: featured ? '1.35rem' : '1.1rem' }}
            >
              <span className="text-gold-400" style={{ fontSize: '0.75em' }}>$</span>
              {flight.price.toLocaleString('es-AR')}
            </p>
          </div>

          <div className="flex flex-col items-end gap-0.5">
            <div className="flex gap-px">
              {Array.from({ length: 5 }).map((_, i) => (
                <svg
                  key={i}
                  className={`w-3 h-3 flex-shrink-0 ${i < stars ? 'text-gold-400' : 'text-white/15'}`}
                  fill="currentColor"
                  viewBox="0 0 20 20"
                >
                  <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.286 3.957a1 1 0 00.95.69h4.162c.969 0 1.372 1.24.588 1.81l-3.368 2.448a1 1 0 00-.364 1.118l1.287 3.957c.3.921-.755 1.688-1.54 1.118L10 14.347l-3.952 2.878c-.784.57-1.838-.197-1.539-1.118l1.286-3.957a1 1 0 00-.364-1.118L2.063 9.384c-.784-.57-.38-1.81.588-1.81h4.162a1 1 0 00.95-.69l1.286-3.957z" />
                </svg>
              ))}
            </div>
            {flight.reviewCount > 0 && (
              <span className="text-[9px] text-white/40">{flight.reviewCount} reseñas</span>
            )}
          </div>
        </div>

        {/* Reservar — emerge en hover */}
        <div className="fc-cta mt-3 space-y-1.5">
          <span className="btn-gold w-full py-2.5 text-sm flex items-center justify-center gap-2">
            Reservar ahora
            <svg className="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M17 8l4 4m0 0l-4 4m4-4H3" />
            </svg>
          </span>
          <p className="text-center text-white/45 text-[11px]">Descubrir</p>
        </div>

      </div>
    </Link>
  )
}
