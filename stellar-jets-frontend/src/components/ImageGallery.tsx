import { useState } from 'react'
import type { FlightImage } from '../types'

interface Props {
  images: FlightImage[]
  flightName: string
}

export default function ImageGallery({ images, flightName }: Props) {
  const [lightbox, setLightbox] = useState(false)
  const [lightboxIndex, setLightboxIndex] = useState(0)

  if (!images || images.length === 0) {
    return (
      <div className="w-full h-64 bg-gray-100 rounded-xl flex items-center justify-center text-gray-400 text-sm">
        Sin imágenes disponibles
      </div>
    )
  }

  const main   = images[0]
  const grid   = images.slice(1, 5)        // hasta 4 para la grilla 2×2
  const total  = images.length

  const openLightbox = (i: number) => { setLightboxIndex(i); setLightbox(true) }

  return (
    <>
      {/* ── BLOQUE PRINCIPAL: imagen izquierda + grilla 2×2 derecha ── */}
      <div className="w-full flex flex-col sm:flex-row gap-1.5 rounded-xl overflow-hidden" style={{ height: '340px' }}>

        {/* Imagen principal — mitad izquierda */}
        <div
          className="relative flex-1 cursor-zoom-in overflow-hidden"
          onClick={() => openLightbox(0)}
        >
          <img
            src={main.url}
            alt={main.altText ?? flightName}
            className="w-full h-full object-cover hover:scale-[1.03] transition-transform duration-500"
          />
        </div>

        {/* Grilla 2×2 — mitad derecha */}
        {grid.length > 0 && (
          <div className="relative grid grid-cols-2 gap-1.5 flex-1">
            {Array.from({ length: 4 }).map((_, i) => {
              const img = grid[i]
              return img ? (
                <div
                  key={img.id}
                  className="relative overflow-hidden cursor-zoom-in"
                  onClick={() => openLightbox(i + 1)}
                >
                  <img
                    src={img.url}
                    alt={img.altText ?? flightName}
                    className="w-full h-full object-cover hover:scale-[1.05] transition-transform duration-500"
                  />
                  {/* "Ver más" — sobre la última celda si hay más de 5 imágenes */}
                  {i === 3 && total > 5 && (
                    <div
                      className="absolute inset-0 bg-black/55 flex flex-col items-center justify-center cursor-pointer"
                      onClick={e => { e.stopPropagation(); openLightbox(4) }}
                    >
                      <span className="text-white font-semibold text-sm">Ver más</span>
                      <span className="text-white/70 text-xs mt-0.5">+{total - 5} fotos</span>
                    </div>
                  )}
                </div>
              ) : (
                <div key={i} className="bg-gray-100" />
              )
            })}

            {/* "Ver más" en la esquina inferior derecha si hay exactamente 5 */}
            {total > 1 && (
              <button
                onClick={() => openLightbox(0)}
                className="absolute bottom-3 right-3 flex items-center gap-1.5 text-xs font-semibold text-white
                           bg-black/50 hover:bg-black/70 backdrop-blur-sm px-3 py-1.5 rounded-full transition-colors"
              >
                <svg className="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                    d="M4 8V4m0 0h4M4 4l5 5m11-1V4m0 0h-4m4 0l-5 5M4 16v4m0 0h4m-4 0l5-5m11 5l-5-5m5 5v-4m0 4h-4" />
                </svg>
                Ver más ({total})
              </button>
            )}
          </div>
        )}
      </div>

      {/* ── LIGHTBOX ── */}
      {lightbox && (
        <div
          className="fixed inset-0 z-50 bg-black/92 flex items-center justify-center p-4"
          onClick={() => setLightbox(false)}
        >
          {/* Cerrar */}
          <button
            className="absolute top-4 right-4 w-11 h-11 flex items-center justify-center rounded-full bg-black/70 hover:bg-black/90 text-white text-lg font-bold transition"
            onClick={() => setLightbox(false)}
          >
            ✕
          </button>

          {/* Contador */}
          <span className="absolute top-5 left-1/2 -translate-x-1/2 text-white text-sm font-medium bg-black/50 px-3 py-1 rounded-full">
            {lightboxIndex + 1} / {total}
          </span>

          {/* Flecha izquierda */}
          <button
            className="absolute left-4 w-14 h-14 flex items-center justify-center rounded-full bg-black/70 hover:bg-black/90 text-white text-3xl font-light transition shadow-lg"
            onClick={e => { e.stopPropagation(); setLightboxIndex(i => (i - 1 + total) % total) }}
          >
            ←
          </button>

          <img
            src={images[lightboxIndex].url}
            alt={images[lightboxIndex].altText ?? flightName}
            className="max-w-full max-h-[88vh] rounded-lg shadow-2xl object-contain"
            onClick={e => e.stopPropagation()}
          />

          {/* Flecha derecha */}
          <button
            className="absolute right-4 w-14 h-14 flex items-center justify-center rounded-full bg-black/70 hover:bg-black/90 text-white text-3xl font-light transition shadow-lg"
            onClick={e => { e.stopPropagation(); setLightboxIndex(i => (i + 1) % total) }}
          >
            →
          </button>
        </div>
      )}
    </>
  )
}
