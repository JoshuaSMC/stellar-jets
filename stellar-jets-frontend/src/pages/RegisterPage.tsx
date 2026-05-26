import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { CheckCircle, RefreshCw } from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { authResendConfirmation } from '../api/flightApi'
import { getErrorMessage } from '../utils/errorUtils'

const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

type FormFields = 'firstName' | 'lastName' | 'email' | 'password' | 'confirm'
type FieldErrors = Partial<Record<FormFields, string>>

export default function RegisterPage() {
  const { register } = useAuth()
  const navigate = useNavigate()

  const [form, setForm] = useState<Record<FormFields, string>>({
    firstName: '', lastName: '', email: '', password: '', confirm: '',
  })
  const [fieldErrors, setFieldErrors] = useState<FieldErrors>({})
  const [serverError, setServerError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)
  const [registered, setRegistered] = useState<{ firstName: string; email: string } | null>(null)
  const [resendLoading, setResendLoading] = useState(false)
  const [resendMsg, setResendMsg] = useState<string | null>(null)

  const onlyLetters = (value: string) => value.replace(/[^a-záéíóúüñA-ZÁÉÍÓÚÜÑ\s'-]/g, '')

  const validateField = (name: FormFields, value: string): string => {
    switch (name) {
      case 'firstName':
        if (!value.trim()) return 'El nombre es obligatorio.'
        if (value.trim().length < 2) return 'El nombre debe tener al menos 2 caracteres.'
        return ''
      case 'lastName':
        if (!value.trim()) return 'El apellido es obligatorio.'
        if (value.trim().length < 2) return 'El apellido debe tener al menos 2 caracteres.'
        return ''
      case 'email':
        if (!value.trim()) return 'El correo es obligatorio.'
        if (!emailRegex.test(value.trim())) return 'El correo electrónico no es válido.'
        return ''
      case 'password':
        if (!value) return 'La contraseña es obligatoria.'
        if (value.length < 6) return 'La contraseña debe tener al menos 6 caracteres.'
        return ''
      case 'confirm':
        if (!value) return 'Confirmá tu contraseña.'
        if (value !== form.password) return 'Las contraseñas no coinciden.'
        return ''
      default:
        return ''
    }
  }

  const handleBlur = (name: FormFields) => {
    const msg = validateField(name, form[name])
    setFieldErrors(prev => ({ ...prev, [name]: msg }))
  }

  const handleChange = (name: FormFields, value: string) => {
    const processed = (name === 'firstName' || name === 'lastName') ? onlyLetters(value) : value
    setForm(f => ({ ...f, [name]: processed }))
    if (fieldErrors[name]) {
      setFieldErrors(prev => ({ ...prev, [name]: '' }))
    }
    // Revalidar confirm si cambia la contraseña
    if (name === 'password' && fieldErrors.confirm) {
      const confirmErr = value !== form.confirm ? 'Las contraseñas no coinciden.' : ''
      setFieldErrors(prev => ({ ...prev, confirm: confirmErr }))
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    const fields: FormFields[] = ['firstName', 'lastName', 'email', 'password', 'confirm']
    const errors: FieldErrors = {}
    fields.forEach(f => {
      const msg = validateField(f, form[f])
      if (msg) errors[f] = msg
    })
    if (Object.keys(errors).length > 0) {
      setFieldErrors(errors)
      return
    }
    setServerError(null)
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
      setServerError(getErrorMessage(err, 'Error al registrar. Revisá los datos e intentá de nuevo.'))
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

  const inputClass = (name: FormFields) =>
    `input ${fieldErrors[name] ? 'border-red-400 focus:ring-red-400' : ''}`

  const FieldError = ({ name }: { name: FormFields }) =>
    fieldErrors[name] ? <p className="text-red-500 text-xs mt-1">{fieldErrors[name]}</p> : null

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
              <div className="bg-gray-50 rounded-xl px-5 py-4 text-left text-sm space-y-1 border border-gray-100">
                <p className="text-[10px] uppercase tracking-wider text-gray-400 mb-2">Tu información</p>
                <p className="text-gray-700"><span className="font-medium">Email:</span> {registered.email}</p>
              </div>
              <p className="text-sm text-gray-500">
                Te enviamos un correo de confirmación a <strong>{registered.email}</strong>.<br />
                Revisá tu bandeja de entrada.
              </p>
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

          <form onSubmit={handleSubmit} className="px-8 py-7 space-y-4" noValidate>

            {/* Error del servidor */}
            {serverError && (
              <div className="px-4 py-3 rounded-xl bg-red-50 border border-red-200 text-red-700 text-sm">
                {serverError}
              </div>
            )}

            {/* Nombre y Apellido */}
            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1.5">Nombre *</label>
                <input
                  type="text"
                  className={inputClass('firstName')}
                  placeholder="Juan"
                  value={form.firstName}
                  onChange={e => handleChange('firstName', e.target.value)}
                  onBlur={() => handleBlur('firstName')}
                />
                <FieldError name="firstName" />
              </div>
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1.5">Apellido *</label>
                <input
                  type="text"
                  className={inputClass('lastName')}
                  placeholder="García"
                  value={form.lastName}
                  onChange={e => handleChange('lastName', e.target.value)}
                  onBlur={() => handleBlur('lastName')}
                />
                <FieldError name="lastName" />
              </div>
            </div>

            {/* Email */}
            <div>
              <label className="block text-xs font-medium text-gray-600 mb-1.5">Correo electrónico *</label>
              <input
                type="email"
                className={inputClass('email')}
                placeholder="tu@correo.com"
                value={form.email}
                onChange={e => handleChange('email', e.target.value)}
                onBlur={() => handleBlur('email')}
              />
              <FieldError name="email" />
            </div>

            {/* Contraseña */}
            <div>
              <label className="block text-xs font-medium text-gray-600 mb-1.5">Contraseña * (mín. 6 caracteres)</label>
              <input
                type="password"
                className={inputClass('password')}
                placeholder="••••••••"
                value={form.password}
                onChange={e => handleChange('password', e.target.value)}
                onBlur={() => handleBlur('password')}
              />
              <FieldError name="password" />
            </div>

            {/* Confirmar contraseña */}
            <div>
              <label className="block text-xs font-medium text-gray-600 mb-1.5">Confirmar contraseña *</label>
              <input
                type="password"
                className={inputClass('confirm')}
                placeholder="••••••••"
                value={form.confirm}
                onChange={e => handleChange('confirm', e.target.value)}
                onBlur={() => handleBlur('confirm')}
              />
              <FieldError name="confirm" />
            </div>

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
