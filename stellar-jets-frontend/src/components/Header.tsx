import { useState, useEffect } from 'react'
import { Link, useLocation } from 'react-router-dom'

export default function Header() {
  const [menuOpen, setMenuOpen] = useState(false)
  const [scrolled, setScrolled] = useState(false)
  const { pathname } = useLocation()

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 40)
    window.addEventListener('scroll', onScroll, { passive: true })
    return () => window.removeEventListener('scroll', onScroll)
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
            {navLink('/administracion', 'Administración')}

            <div className="w-px h-5 bg-white/10 mx-3" />

            {/* Sprint 2: login */}
            <button className="btn-ghost text-sm py-2 px-4">
              Iniciar sesión
            </button>
            <button className="btn-gold text-sm py-2 px-5 ml-2">
              Registrarse
            </button>
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
            {navLink('/administracion', 'Administración')}
            <div className="pt-3 flex flex-col gap-2">
              <button className="btn-ghost text-sm text-center">Iniciar sesión</button>
              <button className="btn-gold text-sm text-center">Registrarse</button>
            </div>
          </div>
        )}
      </div>
    </header>
  )
}
