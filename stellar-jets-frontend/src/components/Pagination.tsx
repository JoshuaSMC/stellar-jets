interface Props {
  currentPage: number
  totalPages: number
  onPageChange: (page: number) => void
}

export default function Pagination({ currentPage, totalPages, onPageChange }: Props) {
  if (totalPages <= 1) return null

  const window = 2
  const start = Math.max(0, currentPage - window)
  const end   = Math.min(totalPages - 1, currentPage + window)
  const visible = Array.from({ length: end - start + 1 }, (_, i) => start + i)

  return (
    <div className="flex items-center justify-center gap-1.5 mt-12">

      {/* Inicio */}
      <button
        className="page-btn text-xs px-3"
        onClick={() => onPageChange(0)}
        disabled={currentPage === 0}
        title="Primera página"
      >
        «
      </button>

      {/* Anterior */}
      <button
        className="page-btn"
        onClick={() => onPageChange(currentPage - 1)}
        disabled={currentPage === 0}
        title="Página anterior"
      >
        ←
      </button>

      {start > 0 && (
        <>
          <button className={`page-btn ${currentPage === 0 ? 'active' : ''}`} onClick={() => onPageChange(0)}>1</button>
          {start > 1 && <span className="text-gray-400 px-1 text-sm">…</span>}
        </>
      )}

      {visible.map(p => (
        <button
          key={p}
          className={`page-btn ${p === currentPage ? 'active' : ''}`}
          onClick={() => onPageChange(p)}
        >
          {p + 1}
        </button>
      ))}

      {end < totalPages - 1 && (
        <>
          {end < totalPages - 2 && <span className="text-gray-400 px-1 text-sm">…</span>}
          <button
            className={`page-btn ${currentPage === totalPages - 1 ? 'active' : ''}`}
            onClick={() => onPageChange(totalPages - 1)}
          >
            {totalPages}
          </button>
        </>
      )}

      {/* Siguiente */}
      <button
        className="page-btn"
        onClick={() => onPageChange(currentPage + 1)}
        disabled={currentPage === totalPages - 1}
        title="Página siguiente"
      >
        →
      </button>

    </div>
  )
}
