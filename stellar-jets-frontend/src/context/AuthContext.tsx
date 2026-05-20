import { createContext, useContext, useState, useCallback, type ReactNode } from 'react'
import { authLogin, authRegister } from '../api/flightApi'
import type { AuthUser, LoginRequest, RegisterRequest } from '../types'

interface AuthContextType {
  user: AuthUser | null
  login: (data: LoginRequest) => Promise<void>
  register: (data: RegisterRequest) => Promise<void>
  logout: () => void
}

const AuthContext = createContext<AuthContextType | null>(null)

const TOKEN_KEY = 'sj_token'
const USER_KEY  = 'sj_user'

function loadStoredUser(): AuthUser | null {
  try {
    const raw = localStorage.getItem(USER_KEY)
    return raw ? (JSON.parse(raw) as AuthUser) : null
  } catch {
    return null
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(loadStoredUser)

  const persist = useCallback((u: AuthUser) => {
    localStorage.setItem(TOKEN_KEY, u.token)
    localStorage.setItem(USER_KEY, JSON.stringify(u))
    setUser(u)
  }, [])

  const login = useCallback(async (data: LoginRequest) => {
    const u = await authLogin(data)
    persist(u)
  }, [persist])

  const register = useCallback(async (data: RegisterRequest) => {
    const u = await authRegister(data)
    persist(u)
  }, [persist])

  const logout = useCallback(() => {
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
    setUser(null)
  }, [])

  return (
    <AuthContext.Provider value={{ user, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}
