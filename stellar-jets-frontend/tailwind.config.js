/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{js,ts,jsx,tsx}"],
  theme: {
    extend: {
      colors: {
        navy: {
          50:  '#EEF2FA',
          100: '#D5DFF2',
          200: '#A8BCE5',
          300: '#6B91CF',
          400: '#3E6BBF',
          500: '#1E4DA8',
          600: '#1E3A8A',   // deep blue principal
          700: '#152B6B',
          800: '#0D1B40',
          900: '#0A1428',   // muy oscuro — header / footer
          950: '#060E1A',
        },
        gold: {
          100: '#FDF6DC',
          200: '#FAE8A0',
          300: '#F5D576',   // light hover
          400: '#E8C227',
          500: '#D4AF37',   // dorado premium
          600: '#B8960E',
          700: '#8B6F0A',
        },
        page: '#F4F7FB',    // fondo general (azul suave)
      },
      fontFamily: {
        sans: ['Inter', 'ui-sans-serif', 'system-ui'],
      },
      keyframes: {
        shimmer: {
          '0%':   { backgroundPosition: '-600% 0' },
          '100%': { backgroundPosition: '600% 0' },
        },
        fadeUp: {
          from: { opacity: '0', transform: 'translateY(12px)' },
          to:   { opacity: '1', transform: 'translateY(0)' },
        },
      },
      animation: {
        shimmer: 'shimmer 2.5s linear infinite',
        fadeUp:  'fadeUp 0.35s ease forwards',
      },
    },
  },
  plugins: [],
}
