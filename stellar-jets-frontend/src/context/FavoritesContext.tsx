import { createContext, useContext, useState, useEffect, useCallback, type ReactNode } from 'react'
import { getFavorites, addFavorite, removeFavorite } from '../api/flightApi'
import type { Flight } from '../types'
import { useAuth } from './AuthContext'

interface FavoritesContextType {
  favorites: Flight[]
  favoriteIds: Set<number>
  toggle: (flight: Flight) => Promise<void>
  isFavorite: (flightId: number) => boolean
  loading: boolean
}

const FavoritesContext = createContext<FavoritesContextType | null>(null)

export function FavoritesProvider({ children }: { children: ReactNode }) {
  const { user } = useAuth()
  const [favorites, setFavorites] = useState<Flight[]>([])
  const [loading, setLoading] = useState(false)

  const favoriteIds = new Set(favorites.map(f => f.id))

  const load = useCallback(async () => {
    if (!user) { setFavorites([]); return }
    setLoading(true)
    try {
      const data = await getFavorites()
      setFavorites(data)
    } catch {
      setFavorites([])
    } finally {
      setLoading(false)
    }
  }, [user])

  useEffect(() => { load() }, [load])

  const toggle = useCallback(async (flight: Flight) => {
    if (!user) return
    const isFav = favoriteIds.has(flight.id)
    // Optimistic update
    setFavorites(prev =>
      isFav ? prev.filter(f => f.id !== flight.id) : [...prev, flight]
    )
    try {
      if (isFav) await removeFavorite(flight.id)
      else await addFavorite(flight.id)
    } catch {
      // Revert on error
      setFavorites(prev =>
        isFav ? [...prev, flight] : prev.filter(f => f.id !== flight.id)
      )
    }
  }, [user, favoriteIds])

  const isFavorite = useCallback((flightId: number) => favoriteIds.has(flightId), [favoriteIds])

  return (
    <FavoritesContext.Provider value={{ favorites, favoriteIds, toggle, isFavorite, loading }}>
      {children}
    </FavoritesContext.Provider>
  )
}

export function useFavorites() {
  const ctx = useContext(FavoritesContext)
  if (!ctx) throw new Error('useFavorites must be used within FavoritesProvider')
  return ctx
}
