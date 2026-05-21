import type { Category } from '../types'

interface Props {
  categories: Category[]
  selected: number | null
  onSelect: (id: number | null) => void
}

export default function CategoryFilter({ categories, selected, onSelect }: Props) {
  return (
    <div className="flex flex-wrap gap-2">
      <button
        onClick={() => onSelect(null)}
        className={`flex items-center gap-1.5 px-4 py-2 rounded-full text-sm font-medium transition-all border ${
          selected === null
            ? 'bg-navy-600 text-white border-navy-600 shadow-sm'
            : 'bg-white text-gray-600 border-gray-200 hover:border-navy-400 hover:text-navy-600'
        }`}
      >
        ✈️ Todos
      </button>

      {categories.map(cat => (
        <button
          key={cat.id}
          onClick={() => onSelect(cat.id)}
          className={`flex items-center gap-1.5 px-4 py-2 rounded-full text-sm font-medium transition-all border ${
            selected === cat.id
              ? 'bg-navy-600 text-white border-navy-600 shadow-sm'
              : 'bg-white text-gray-600 border-gray-200 hover:border-navy-400 hover:text-navy-600'
          }`}
        >
          {cat.name}
          <span className="ml-1 text-xs opacity-60">({cat.flightCount})</span>
        </button>
      ))}
    </div>
  )
}
