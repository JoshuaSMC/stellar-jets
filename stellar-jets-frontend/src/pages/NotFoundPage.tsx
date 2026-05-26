import { useNavigate } from 'react-router-dom'
import { Plane, Home } from 'lucide-react'

export default function NotFoundPage() {
  const navigate = useNavigate()

  return (
    <main
      className="pt-[68px] min-h-screen flex items-center justify-center px-4"
      style={{ background: '#F4F7FB' }}
    >
      <div className="text-center max-w-md">
        <Plane className="w-16 h-16 mx-auto mb-6 text-gold-500 opacity-60" />
        <h1
          className="text-6xl font-extrabold mb-2"
          style={{ color: '#0A1428', fontFamily: "'Cinzel', serif" }}
        >
          404
        </h1>
        <p className="text-lg font-semibold text-gray-700 mb-2">
          Página no encontrada
        </p>
        <p className="text-sm text-gray-500 mb-8">
          La página que buscás no existe o fue movida.
        </p>
        <button
          onClick={() => navigate('/')}
          className="btn-gold px-6 py-3 inline-flex items-center gap-2"
        >
          <Home className="w-4 h-4" />
          Volver al inicio
        </button>
      </div>
    </main>
  )
}
