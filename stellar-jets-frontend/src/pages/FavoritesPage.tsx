import { Link } from 'react-router-dom'
import { Heart } from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { useFavorites } from '../context/FavoritesContext'
import FlightCard from '../components/FlightCard'

export default function FavoritesPage() {
  const { user } = useAuth()
  const { favorites, loading } = useFavorites()

  if (!user) {
    return (
      <div className="pt-[68px] min-h-screen flex flex-col items-center justify-center gap-5" style={{ background: '#F4F7FB' }}>
        <Heart className="w-14 h-14 text-gray-300" />
        <p className="text-xl font-bold text-gray-800">Iniciá sesión para ver tus favoritos</p>
        <p className="text-gray-400 text-sm">Guardá los vuelos que más te gustan para encontrarlos fácilmente.</p>
        <Link to="/login" className="btn-gold px-8 py-3 mt-2">Iniciar sesión</Link>
      </div>
    )
  }

  return (
    <main className="pt-[68px] min-h-screen" style={{ background: '#F4F7FB' }}>
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">

        {/* Encabezado */}
        <div className="mb-8">
          <p className="text-gold-500 text-xs uppercase tracking-[0.2em] mb-2">Mi cuenta</p>
          <div className="flex items-center gap-3">
            <Heart className="w-6 h-6 text-red-400" fill="#f87171" />
            <h1 className="section-title">Mis favoritos</h1>
          </div>
          {!loading && (
            <p className="text-gray-400 text-sm mt-1">
              {favorites.length === 0
                ? 'Todavía no marcaste ningún vuelo como favorito.'
                : `${favorites.length} ${favorites.length === 1 ? 'vuelo guardado' : 'vuelos guardados'}`}
            </p>
          )}
        </div>

        {/* Contenido */}
        {loading ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
            {Array.from({ length: 3 }).map((_, i) => (
              <div key={i} className="skeleton" style={{ height: '360px', borderRadius: '14px' }} />
            ))}
          </div>
        ) : favorites.length === 0 ? (
          <div className="flex flex-col items-center gap-5 py-24 text-center">
            <Heart className="w-16 h-16 text-gray-200" />
            <p className="text-gray-500 text-base max-w-sm">
              Explorá los vuelos disponibles y tocá el corazón para guardar los que más te interesan.
            </p>
            <Link to="/" className="btn-gold px-8 py-3">Explorar vuelos</Link>
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
            {favorites.map(f => (
              <div key={f.id} style={{ height: '360px' }}>
                <FlightCard flight={f} />
              </div>
            ))}
          </div>
        )}
      </div>
    </main>
  )
}
