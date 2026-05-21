import { useState } from 'react'
import { Link, useNavigate, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function LoginPage() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const from = (location.state as { from?: string; requiresAuth?: boolean } | null)?.from ?? '/'
  const requiresAuth = (location.state as { requiresAuth?: boolean } | null)?.requiresAuth ?? false

  const [form, setForm] = useState({ email: '', password: '' })
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
      await login(form)
      navigate(from, { replace: true })
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { message?: string } } }).response?.data?.message
      setError(msg ?? 'Credenciales inválidas. Revisá tu correo y contraseña.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className="pt-[68px] min-h-screen flex items-center justify-center px-4" style={{ background: '#F4F7FB' }}>
      <div className="w-full max-w-md">

        {/* Card */}
        <div className="bg-white rounded-2xl shadow-md border border-gray-100 overflow-hidden">

          {/* Header */}
          <div className="px-8 py-7" style={{ background: '#060E1A' }}>
            <p className="text-[10px] uppercase tracking-[0.25em] text-white/40 mb-2">Bienvenido a</p>
            <h1
              className="text-gold-400"
              style={{ fontFamily: "'Cinzel', serif", fontWeight: 700, fontSize: '1.3rem', letterSpacing: '0.18em' }}
            >
              STELLAR JETS
            </h1>
            <p className="text-white/50 text-sm mt-2">Iniciá sesión en tu cuenta</p>
          </div>

          {/* Banner login obligatorio — US#30 */}
          {requiresAuth && (
            <div className="px-8 py-4 border-b border-amber-100" style={{ background: '#fffbeb' }}>
              <p className="text-sm font-semibold text-amber-800 mb-0.5">Iniciá sesión para continuar</p>
              <p className="text-xs text-amber-700">
                Para completar tu reserva es necesario estar registrado.{' '}
                <Link to="/register" state={{ from }} className="underline font-medium hover:text-amber-900">
                  ¿No tenés cuenta? Registrate aquí.
                </Link>
              </p>
            </div>
          )}

          {/* Form */}
          <form onSubmit={handleSubmit} className="px-8 py-7 space-y-4">

            {error && (
              <div className="px-4 py-3 rounded-xl bg-red-50 border border-red-200 text-red-700 text-sm">
                {error}
              </div>
            )}

            <div>
              <label className="block text-xs font-medium text-gray-600 mb-1.5">Correo electrónico</label>
              <input
                type="email"
                className="input"
                placeholder="tu@correo.com"
                value={form.email}
                onChange={e => setForm(f => ({ ...f, email: e.target.value }))}
                required
              />
            </div>

            <div>
              <label className="block text-xs font-medium text-gray-600 mb-1.5">Contraseña</label>
              <input
                type="password"
                className="input"
                placeholder="••••••••"
                value={form.password}
                onChange={e => setForm(f => ({ ...f, password: e.target.value }))}
                required
              />
            </div>

            <button
              type="submit"
              disabled={loading}
              className="btn-gold w-full py-3.5 text-sm font-semibold mt-2 disabled:opacity-60"
            >
              {loading ? 'Iniciando sesión...' : 'Iniciar sesión'}
            </button>

            <p className="text-center text-sm text-gray-500 pt-1">
              ¿No tenés cuenta?{' '}
              <Link to="/register" className="text-navy-600 font-medium hover:underline">
                Crear cuenta
              </Link>
            </p>
          </form>
        </div>
      </div>
    </main>
  )
}
