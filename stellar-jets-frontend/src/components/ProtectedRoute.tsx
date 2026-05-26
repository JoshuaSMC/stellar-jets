import { Navigate, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

interface Props {
  children: React.ReactNode
  requireRole?: 'USER' | 'ADMIN'
}

export default function ProtectedRoute({ children, requireRole }: Props) {
  const { user } = useAuth()
  const location = useLocation()

  if (!user) {
    return (
      <Navigate
        to="/login"
        state={{ from: location.pathname, requiresAuth: true }}
        replace
      />
    )
  }

  if (requireRole === 'ADMIN' && user.role !== 'ADMIN') {
    return <Navigate to="/" replace />
  }

  return <>{children}</>
}
