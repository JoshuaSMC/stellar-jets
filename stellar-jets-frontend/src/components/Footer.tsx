import { Link } from 'react-router-dom'

export default function Footer() {
  return (
    <footer style={{ background: '#060E1A' }}>
      {/* Gold accent line */}
      <div className="h-px w-full bg-gradient-to-r from-transparent via-gold-500/50 to-transparent" />

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-14">
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-10">

          {/* Marca — ocupa 2 columnas */}
          <div className="lg:col-span-2">
            <Link to="/" className="flex flex-col leading-none mb-4 w-fit group">
              <span
                className="text-gold-400 group-hover:text-gold-300 transition-colors duration-200"
                style={{
                  fontFamily: "'Cinzel', serif",
                  fontWeight: 700,
                  fontSize: '1.15rem',
                  letterSpacing: '0.18em',
                }}
              >
                STELLAR JETS
              </span>
              <span
                className="text-white/30 mt-[3px]"
                style={{
                  fontFamily: "'Cinzel', serif",
                  fontWeight: 400,
                  fontSize: '0.50rem',
                  letterSpacing: '0.30em',
                }}
              >
                PREMIUM AVIATION
              </span>
            </Link>
            <p className="text-white/70 text-sm leading-relaxed max-w-xs">
              La plataforma de reservas de vuelos más elegante.
              Donde las estrellas guían tu destino.
            </p>
            {/* Año y copyright — en el mismo bloque que el logo (US#7) */}
            <p className="text-white/50 text-xs mt-5">
              © {new Date().getFullYear()} Stellar Jets. Todos los derechos reservados.
            </p>
          </div>

          {/* Plataforma */}
          <div>
            <h3 className="text-[11px] font-bold uppercase tracking-[0.18em] text-white/70 mb-4">
              Plataforma
            </h3>
            <ul className="space-y-2.5">
              {[
                { to: '/', label: 'Inicio' },
                { to: '/administracion', label: 'Administración' },
                { to: '#', label: 'Mis reservas' },
                { to: '#', label: 'Mis vuelos' },
              ].map(link => (
                <li key={link.label}>
                  <Link
                    to={link.to}
                    className="text-sm text-white/70 hover:text-gold-400 transition-colors duration-200"
                  >
                    {link.label}
                  </Link>
                </li>
              ))}
            </ul>
          </div>

          {/* Soporte */}
          <div>
            <h3 className="text-[11px] font-bold uppercase tracking-[0.18em] text-white/70 mb-4">
              Soporte
            </h3>
            <ul className="space-y-2.5">
              {['Centro de ayuda', 'Contacto', 'Política de privacidad', 'Términos de uso'].map(item => (
                <li key={item}>
                  <a href="#" className="text-sm text-white/70 hover:text-gold-400 transition-colors duration-200">
                    {item}
                  </a>
                </li>
              ))}
            </ul>
          </div>

          {/* Newsletter */}
          <div>
            <h3 className="text-[11px] font-bold uppercase tracking-[0.18em] text-white/70 mb-4">
              Novedades
            </h3>
            <p className="text-white/60 text-xs mb-3 leading-relaxed">
              Las mejores ofertas, directo a tu casilla.
            </p>
            <div className="flex flex-col gap-2">
              <input
                type="email"
                placeholder="tu@email.com"
                className="px-3 py-2.5 rounded-lg text-sm text-white placeholder-white/30 focus:outline-none focus:ring-1 focus:ring-gold-500 transition"
                style={{
                  background: 'rgba(255,255,255,0.06)',
                  border: '1px solid rgba(255,255,255,0.12)',
                }}
              />
              <button className="btn-gold py-2.5 text-sm">
                Suscribirme
              </button>
            </div>
          </div>
        </div>

        {/* Bottom bar */}
        <div className="mt-12 pt-6 flex justify-end gap-4"
             style={{ borderTop: '1px solid rgba(255,255,255,0.07)' }}>
          {['Privacidad', 'Términos', 'Cookies'].map(item => (
            <a key={item} href="#" className="text-white/50 hover:text-gold-500/70 text-xs transition-colors">
              {item}
            </a>
          ))}
        </div>
      </div>
    </footer>
  )
}
