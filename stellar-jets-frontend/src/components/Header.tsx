import { useState, useEffect, useRef } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { Heart } from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { useFavorites } from '../context/FavoritesContext'

export default function Header() {
  const [menuOpen, setMenuOpen] = useState(false)
  const [avatarOpen, setAvatarOpen] = useState(false)
  const [scrolled, setScrolled] = useState(false)
  const { pathname } = useLocation()
  const navigate = useNavigate()
  const { user, logout } = useAuth()
  const { favorites } = useFavorites()
  const dropdownRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 40)
    window.addEventListener('scroll', onScroll, { passive: true })
    return () => window.removeEventListener('scroll', onScroll)
  }, [])

  // Close avatar dropdown on outside click
  useEffect(() => {
    const handler = (e: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target as Node)) {
        setAvatarOpen(false)
      }
    }
    document.addEventListener('mousedown', handler)
    return () => document.removeEventListener('mousedown', handler)
  }, [])

  const navLink = (to: string, label: string) => {
    const active = pathname === to
    return (
      <Link
        to={to}
        onClick={() => setMenuOpen(false)}
        className={`relative px-4 py-2 text-sm font-medium transition-all duration-200 ${
          active ? 'text-gold-400' : 'text-[#CBD5E1] hover:text-white'
        }`}
      >
        {label}
        {active && (
          <span className="absolute bottom-0 left-1/2 -translate-x-1/2 w-4 h-0.5 bg-gold-500 rounded-full" />
        )}
      </Link>
    )
  }

  const initials = user ? `${user.firstName[0]}${user.lastName[0]}`.toUpperCase() : ''

  const handleLogout = () => {
    logout()
    setAvatarOpen(false)
    setMenuOpen(false)
    navigate('/')
  }

  return (
    <header
      className={`fixed top-0 left-0 right-0 z-50 transition-all duration-300 ${
        scrolled ? 'header-scrolled' : ''
      }`}
      style={!scrolled ? { background: 'rgba(6,14,26,0.95)' } : undefined}
    >
      {/* Gold accent bar */}
      <div className="h-px w-full bg-gradient-to-r from-transparent via-gold-500/60 to-transparent" />

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-[68px]">

          {/* Logo */}
          <Link to="/" className="flex flex-col leading-none flex-shrink-0 group">
            <span
              className="text-gold-400 group-hover:text-gold-300 transition-colors duration-200"
              style={{
                fontFamily: "'Cinzel', serif",
                fontWeight: 700,
                fontSize: '1.18rem',
                letterSpacing: '0.18em',
              }}
            >
              STELLAR JETS
            </span>
            <span
              className="text-white/35 group-hover:text-white/50 transition-colors duration-200 mt-[3px]"
              style={{
                fontFamily: "'Cinzel', serif",
                fontWeight: 400,
                fontSize: '0.52rem',
                letterSpacing: '0.30em',
              }}
            >
              PREMIUM AVIATION
            </span>
          </Link>

          {/* Nav desktop */}
          <nav className="hidden md:flex items-center gap-1">
            {navLink('/', 'Inicio')}
            {user?.role === 'ADMIN' && navLink('/administracion', 'Administración')}

            <div className="w-px h-5 bg-white/10 mx-3" />

            {user ? (
              /* Avatar + dropdown */
              <div className="relative" ref={dropdownRef}>
                <button
                  onClick={() => setAvatarOpen(o => !o)}
                  className="flex items-center gap-2.5 px-3 py-1.5 rounded-full hover:bg-white/10 transition-colors"
                >
                  <div className="w-8 h-8 rounded-full bg-gold-500 flex items-center justify-center text-xs font-bold text-navy-900 flex-shrink-0">
                    {initials}
                  </div>
                  <span className="text-sm text-white/90 font-medium">
                    {user.firstName}
                  </span>
                  <svg className={`w-3.5 h-3.5 text-white/50 transition-transform ${avatarOpen ? 'rotate-180' : ''}`} fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                  </svg>
                </button>

                {avatarOpen && (
                  <div className="absolute right-0 top-full mt-2 w-52 bg-white rounded-xl shadow-xl border border-gray-100 overflow-hidden z-50 animate-fadeUp">
                    <div className="px-4 py-3 border-b border-gray-50">
                      <p className="text-sm font-semibold text-gray-900">{user.firstName} {user.lastName}</p>
                      <p className="text-xs text-gray-400 truncate">{user.email}</p>
                      <span className={`inline-block mt-1 text-[10px] font-bold px-2 py-0.5 rounded-full ${
                        user.role === 'ADMIN' ? 'bg-gold-100 text-gold-700' : 'bg-gray-100 text-gray-500'
                      }`}>
                        {user.role === 'ADMIN' ? 'Administrador' : 'Usuario'}
                      </span>
                    </div>
                    <Link
                      to="/favoritos"
                      onClick={() => setAvatarOpen(false)}
                      className="w-full text-left px-4 py-3 text-sm text-gray-700 hover:bg-gray-50 transition-colors flex items-center justify-between"
                    >
                      <span className="flex items-center gap-2">
                        <Heart className="w-4 h-4 text-red-400" fill="#f87171" />
                        Mis favoritos
                      </span>
                      {favorites.length > 0 && (
                        <span className="text-[10px] font-bold bg-red-100 text-red-500 px-1.5 py-0.5 rounded-full">
                          {favorites.length}
                        </span>
                      )}
                    </Link>
                    <button
                      onClick={handleLogout}
                      className="w-full text-left px-4 py-3 text-sm text-red-500 hover:bg-red-50 transition-colors flex items-center gap-2"
                    >
                      <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                      </svg>
                      Cerrar sesión
                    </button>
                  </div>
                )}
              </div>
            ) : (
              <>
                <button
                  onClick={() => navigate('/login')}
                  className="btn-ghost text-sm py-2 px-4"
                >
                  Iniciar sesión
                </button>
                <button
                  onClick={() => navigate('/register')}
                  className="btn-gold text-sm py-2 px-5 ml-2"
                >
                  Crear cuenta
                </button>
              </>
            )}
          </nav>

          {/* Hamburger mobile */}
          <button
            className="md:hidden text-white p-2 rounded-lg hover:bg-white/10 transition"
            onClick={() => setMenuOpen(o => !o)}
            aria-label="Menú"
          >
            <svg className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              {menuOpen
                ? <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                : <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
              }
            </svg>
          </button>
        </div>

        {/* Nav mobile */}
        {menuOpen && (
          <div className="md:hidden pb-5 pt-3 border-t border-white/10 flex flex-col gap-1 animate-fadeUp">
            {navLink('/', 'Inicio')}
            {user?.role === 'ADMIN' && navLink('/administracion', 'Administración')}

            {user ? (
              <div className="pt-3 flex flex-col gap-2 border-t border-white/10 mt-2">
                <div className="flex items-center gap-3 px-4 py-2">
                  <div className="w-8 h-8 rounded-full bg-gold-500 flex items-center justify-center text-xs font-bold text-navy-900">
                    {initials}
                  </div>
                  <div>
                    <p className="text-sm text-white font-medium">{user.firstName} {user.lastName}</p>
                    <p className="text-xs text-white/50">{user.email}</p>
                  </div>
                </div>
                {navLink('/favoritos', '❤ Mis favoritos')}
                <button
                  onClick={handleLogout}
                  className="mx-4 btn-ghost text-sm text-center text-red-400"
                >
                  Cerrar sesión
                </button>
              </div>
            ) : (
              <div className="pt-3 flex flex-col gap-2">
                <button onClick={() => { navigate('/login'); setMenuOpen(false) }} className="btn-ghost text-sm text-center">Iniciar sesión</button>
                <button onClick={() => { navigate('/register'); setMenuOpen(false) }} className="btn-gold text-sm text-center">Crear cuenta</button>
              </div>
            )}
          </div>
        )}
      </div>
    </header>
  )
}
