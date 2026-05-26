import { useState } from 'react'
import { Link, useNavigate, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { getErrorMessage } from '../utils/errorUtils'

const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

export default function LoginPage() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const from = (location.state as { from?: string; requiresAuth?: boolean } | null)?.from ?? '/'
  const requiresAuth = (location.state as { requiresAuth?: boolean } | null)?.requiresAuth ?? false

  const [form, setForm] = useState({ email: '', password: '' })
  const [fieldErrors, setFieldErrors] = useState<{ email?: string; password?: string }>({})
  const [serverError, setServerError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)

  const validateField = (name: 'email' | 'password', value: string) => {
    if (name === 'email') {
      if (!value.trim()) return 'El correo es obligatorio.'
      if (!emailRegex.test(value.trim())) return 'El correo electrónico no es válido.'
    }
    if (name === 'password') {
      if (!value) return 'La contraseña es obligatoria.'
    }
    return ''
  }

  const handleBlur = (name: 'email' | 'password') => {
    const msg = validateField(name, form[name])
    setFieldErrors(prev => ({ ...prev, [name]: msg }))
  }

  const handleChange = (name: 'email' | 'password', value: string) => {
    setForm(f => ({ ...f, [name]: value }))
    // Limpiar error del campo mientras escribe
    if (fieldErrors[name]) {
      setFieldErrors(prev => ({ ...prev, [name]: '' }))
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    // Validar todos los campos antes de enviar
    const emailErr = validateField('email', form.email)
    const passwordErr = validateField('password', form.password)
    if (emailErr || passwordErr) {
      setFieldErrors({ email: emailErr, password: passwordErr })
      return
    }
    setServerError(null)
    setLoading(true)
    try {
      await login(form)
      navigate(from, { replace: true })
    } catch (err: unknown) {
      setServerError(getErrorMessage(err, 'Credenciales inválidas. Revisá tu correo y contraseña.'))
    } finally {
      setLoading(false)
    }
  }

  const inputClass = (field: 'email' | 'password') =>
    `input ${fieldErrors[field] ? 'border-red-400 focus:ring-red-400' : ''}`

  return (
    <main className="pt-[68px] min-h-screen flex items-center justify-center px-4" style={{ background: '#F4F7FB' }}>
      <div className="w-full max-w-md">

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
          <form onSubmit={handleSubmit} className="px-8 py-7 space-y-4" noValidate>

            {/* Error del servidor */}
            {serverError && (
              <div className="px-4 py-3 rounded-xl bg-red-50 border border-red-200 text-red-700 text-sm">
                {serverError}
              </div>
            )}

            {/* Email */}
            <div>
              <label className="block text-xs font-medium text-gray-600 mb-1.5">
                Correo electrónico
              </label>
              <input
                type="email"
                className={inputClass('email')}
                placeholder="tu@correo.com"
                value={form.email}
                onChange={e => handleChange('email', e.target.value)}
                onBlur={() => handleBlur('email')}
              />
              {fieldErrors.email && (
                <p className="text-red-500 text-xs mt-1">{fieldErrors.email}</p>
              )}
            </div>

            {/* Contraseña */}
            <div>
              <label className="block text-xs font-medium text-gray-600 mb-1.5">
                Contraseña
              </label>
              <input
                type="password"
                className={inputClass('password')}
                placeholder="••••••••"
                value={form.password}
                onChange={e => handleChange('password', e.target.value)}
                onBlur={() => handleBlur('password')}
              />
              {fieldErrors.password && (
                <p className="text-red-500 text-xs mt-1">{fieldErrors.password}</p>
              )}
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
