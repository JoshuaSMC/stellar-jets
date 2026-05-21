import { useRef, useEffect, useState } from 'react'
import { DayPicker } from 'react-day-picker'
import type { DateRange } from 'react-day-picker'
import { es } from 'date-fns/locale'
import { Calendar, X } from 'lucide-react'
import 'react-day-picker/style.css'

function fmt(date: Date | undefined): string {
  if (!date) return ''
  return date.toLocaleDateString('es-AR', { day: '2-digit', month: '2-digit', year: 'numeric' })
}

interface Props {
  range: DateRange | undefined
  onChange: (range: DateRange | undefined) => void
  light?: boolean
  disabledRanges?: { from: Date; to: Date }[]
}

export default function DateRangePicker({ range, onChange, light = false, disabledRanges = [] }: Props) {
  const [open, setOpen] = useState(false)
  const ref = useRef<HTMLDivElement>(null)

  useEffect(() => {
    const handleClick = (e: MouseEvent) => {
      if (ref.current && !ref.current.contains(e.target as Node)) setOpen(false)
    }
    const handleScroll = () => setOpen(false)
    document.addEventListener('mousedown', handleClick)
    window.addEventListener('scroll', handleScroll, true)
    return () => {
      document.removeEventListener('mousedown', handleClick)
      window.removeEventListener('scroll', handleScroll, true)
    }
  }, [])


  const clear = (e: React.MouseEvent) => {
    e.stopPropagation()
    onChange(undefined)
  }

  return (
    <div ref={ref} className="relative w-full">
      {/* Trigger */}
      <button
        type="button"
        onClick={() => setOpen(v => !v)}
        className={`w-full flex items-center gap-3 text-left transition ${
          light
            ? 'rounded-none py-0'
            : 'rounded-xl px-4 py-3'
        }`}
        style={light ? undefined : { background: 'rgba(255,255,255,0.10)', border: '1px solid rgba(255,255,255,0.18)' }}
      >
        <Calendar className="w-4 h-4 flex-shrink-0" style={{ color: '#D4AF37' }} />
        <div className="flex-1 min-w-0">
          {range?.from ? (
            <span className={`text-sm font-medium ${light ? 'text-navy-900' : 'text-white'}`}>
              {fmt(range.from)}
              {range.to && <> &rarr; {fmt(range.to)}</>}
            </span>
          ) : (
            <span className={`text-sm ${light ? 'text-gray-400' : 'text-white/40'}`}>
              {light ? 'Seleccioná las fechas' : 'Seleccioná las fechas de viaje'}
            </span>
          )}
        </div>
        {range?.from && (
          <span onClick={clear} className={`transition cursor-pointer ${light ? 'text-gray-400 hover:text-gray-700' : 'text-white/40 hover:text-white'}`}>
            <X className="w-4 h-4" />
          </span>
        )}
      </button>

      {/* Popup calendar */}
      {open && (
        <div
          className="absolute top-full left-0 mt-2 z-50 rounded-2xl shadow-2xl p-4"
          style={{ background: '#fff', border: '1px solid #e5e7eb', minWidth: 'max-content' }}
        >
          <DayPicker
            mode="range"
            selected={range}
            onSelect={onChange}
            numberOfMonths={2}
            locale={es}
            disabled={[{ before: new Date() }, ...disabledRanges]}
            showOutsideDays
          />
          {range?.from && range?.to && (
            <div className="flex justify-end pt-2" style={{ borderTop: '1px solid #f3f4f6' }}>
              <button
                type="button"
                onClick={() => setOpen(false)}
                className="btn-gold px-6 py-2 text-sm"
              >
                Confirmar fechas
              </button>
            </div>
          )}
        </div>
      )}
    </div>
  )
}
