import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { CheckCircle, RefreshCw } from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { authResendConfirmation } from '../api/flightApi'

export default function RegisterPage() {
  const { register } = useAuth()
  const navigate = useNavigate()

  const [form, setForm] = useState({
    firstName: '', lastName: '', email: '', password: '', confirm: '',
  })
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)
  const [registered, setRegistered] = useState<{ firstName: string; email: string } | null>(null)
  const [resendLoading, setResendLoading] = useState(false)
  const [resendMsg, setResendMsg] = useState<string | null>(null)

  const onlyLetters = (value: string) => value.replace(/[^a-záéíóúüñA-ZÁÉÍÓÚÜÑ\s'-]/g, '')

  const validate = () => {
    if (form.firstName.trim().length < 2)
      return 'El nombre debe tener al menos 2 caracteres.'
    if (form.lastName.trim().length < 2)
      return 'El apellido debe tener al menos 2 caracteres.'
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email.trim()))
      return 'El correo electrónico no es válido.'
    if (form.password.length < 6)
      return 'La contraseña debe tener al menos 6 caracteres.'
    if (form.password !== form.confirm)
      return 'Las contraseñas no coinciden.'
    return null
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    const validationError = validate()
    if (validationError) { setError(validationError); return }
    setError(null)
    setLoading(true)
    try {
      await register({
        firstName: form.firstName.trim(),
        lastName: form.lastName.trim(),
        email: form.email.trim(),
        password: form.password,
      })
      setRegistered({ firstName: form.firstName.trim(), email: form.email.trim() })
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { message?: string } } }).response?.data?.message
      setError(msg ?? 'Error al registrar. Revisá los datos e intentá de nuevo.')
    } finally {
      setLoading(false)
    }
  }

  const handleResend = async () => {
    if (!registered) return
    setResendLoading(true)
    setResendMsg(null)
    try {
      await authResendConfirmation(registered.email)
      setResendMsg('¡Correo reenviado! Revisá tu bandeja de entrada.')
    } catch {
      setResendMsg('No se pudo reenviar. Intentá de nuevo.')
    } finally {
      setResendLoading(false)
    }
  }

  const field = (label: string, key: keyof typeof form, type = 'text', placeholder = '') => (
    <div>
      <label className="block text-xs font-medium text-gray-600 mb-1.5">{label}</label>
      <input
        type={type}
        className="input"
        placeholder={placeholder}
        value={form[key]}
        onChange={e => setForm(f => ({ ...f, [key]: e.target.value }))}
        required
      />
    </div>
  )

  /* ── Pantalla de éxito ── */
  if (registered) {
    return (
      <main className="pt-[68px] min-h-screen flex items-center justify-center px-4 py-10" style={{ background: '#F4F7FB' }}>
        <div className="w-full max-w-md">
          <div className="bg-white rounded-2xl shadow-md border border-gray-100 overflow-hidden">

            <div className="px-8 py-7" style={{ background: '#060E1A' }}>
              <p className="text-[10px] uppercase tracking-[0.25em] text-white/40 mb-2">Bienvenido a</p>
              <h1 className="text-gold-400" style={{ fontFamily: "'Cinzel', serif", fontWeight: 700, fontSize: '1.3rem', letterSpacing: '0.18em' }}>
                STELLAR JETS
              </h1>
            </div>

            <div className="px-8 py-8 text-center space-y-5">
              <CheckCircle className="w-14 h-14 text-green-500 mx-auto" />
              <div>
                <h2 className="text-xl font-bold text-gray-900 mb-1">¡Registro exitoso!</h2>
                <p className="text-gray-500 text-sm">Hola <strong>{registered.firstName}</strong>, tu cuenta fue creada correctamente.</p>
              </div>

              {/* Info box */}
              <div className="bg-gray-50 rounded-xl px-5 py-4 text-left text-sm space-y-1 border border-gray-100">
                <p className="text-[10px] uppercase tracking-wider text-gray-400 mb-2">Tu información</p>
                <p className="text-gray-700"><span className="font-medium">Email:</span> {registered.email}</p>
              </div>

              <p className="text-sm text-gray-500">
                Te enviamos un correo de confirmación a <strong>{registered.email}</strong>.<br />
                Revisá tu bandeja de entrada.
              </p>

              {/* Reenvío */}
              <div className="border-t border-gray-100 pt-4 space-y-2">
                <p className="text-xs text-gray-400">¿No recibiste el correo?</p>
                <button
                  onClick={handleResend}
                  disabled={resendLoading}
                  className="flex items-center gap-2 mx-auto text-sm text-navy-600 font-medium hover:text-navy-800 disabled:opacity-50 transition-colors"
                >
                  <RefreshCw className={`w-4 h-4 ${resendLoading ? 'animate-spin' : ''}`} />
                  Reenviar correo de confirmación
                </button>
                {resendMsg && (
                  <p className={`text-xs ${resendMsg.includes('reenviado') ? 'text-green-600' : 'text-red-500'}`}>
                    {resendMsg}
                  </p>
                )}
              </div>

              {/* Acciones */}
              <div className="flex gap-3 pt-2">
                <button onClick={() => navigate('/')} className="btn-secondary flex-1 py-2.5 text-sm">
                  Ir al inicio
                </button>
                <button onClick={() => navigate('/login')} className="btn-gold flex-1 py-2.5 text-sm">
                  Iniciar sesión
                </button>
              </div>
            </div>
          </div>
        </div>
      </main>
    )
  }

  /* ── Formulario ── */
  return (
    <main className="pt-[68px] min-h-screen flex items-center justify-center px-4 py-10" style={{ background: '#F4F7FB' }}>
      <div className="w-full max-w-md">
        <div className="bg-white rounded-2xl shadow-md border border-gray-100 overflow-hidden">

          <div className="px-8 py-7" style={{ background: '#060E1A' }}>
            <p className="text-[10px] uppercase tracking-[0.25em] text-white/40 mb-2">Unirse a</p>
            <h1 className="text-gold-400" style={{ fontFamily: "'Cinzel', serif", fontWeight: 700, fontSize: '1.3rem', letterSpacing: '0.18em' }}>
              STELLAR JETS
            </h1>
            <p className="text-white/50 text-sm mt-2">Creá tu cuenta gratuita</p>
          </div>

          <form onSubmit={handleSubmit} className="px-8 py-7 space-y-4">

            {error && (
              <div className="px-4 py-3 rounded-xl bg-red-50 border border-red-200 text-red-700 text-sm">
                {error}
              </div>
            )}

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1.5">Nombre *</label>
                <input
                  type="text"
                  className="input"
                  placeholder="Juan"
                  value={form.firstName}
                  onChange={e => setForm(f => ({ ...f, firstName: onlyLetters(e.target.value) }))}
                  required
                />
              </div>
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1.5">Apellido *</label>
                <input
                  type="text"
                  className="input"
                  placeholder="García"
                  value={form.lastName}
                  onChange={e => setForm(f => ({ ...f, lastName: onlyLetters(e.target.value) }))}
                  required
                />
              </div>
            </div>

            {field('Correo electrónico *', 'email', 'email', 'tu@correo.com')}
            {field('Contraseña * (mín. 6 caracteres)', 'password', 'password', '••••••••')}
            {field('Confirmar contraseña *', 'confirm', 'password', '••••••••')}

            <button
              type="submit"
              disabled={loading}
              className="btn-gold w-full py-3.5 text-sm font-semibold mt-2 disabled:opacity-60"
            >
              {loading ? 'Creando cuenta...' : 'Crear cuenta'}
            </button>

            <p className="text-center text-sm text-gray-500 pt-1">
              ¿Ya tenés cuenta?{' '}
              <Link to="/login" className="text-navy-600 font-medium hover:underline">
                Iniciar sesión
              </Link>
            </p>
          </form>
        </div>
      </div>
    </main>
  )
}
