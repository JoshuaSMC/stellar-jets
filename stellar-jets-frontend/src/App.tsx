import { BrowserRouter, Routes, Route, useLocation } from 'react-router-dom'
import { useEffect } from 'react'
import { AuthProvider } from './context/AuthContext'
import { FavoritesProvider } from './context/FavoritesContext'
import { ToastProvider } from './context/ToastContext'
import Header from './components/Header'
import Footer from './components/Footer'
import ProtectedRoute from './components/ProtectedRoute'
import HomePage from './pages/HomePage'
import FlightDetailPage from './pages/FlightDetailPage'
import FavoritesPage from './pages/FavoritesPage'
import AdminPage from './pages/AdminPage'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import ReservationPage from './pages/ReservationPage'
import MyReservationsPage from './pages/MyReservationsPage'
import NotFoundPage from './pages/NotFoundPage'
import WhatsAppButton from './components/WhatsAppButton'

function ScrollToTop() {
  const { pathname } = useLocation()
  useEffect(() => { window.scrollTo(0, 0) }, [pathname])
  return null
}

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <ToastProvider>
        <FavoritesProvider>
          <ScrollToTop />
          <Header />
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/flights/:id" element={<FlightDetailPage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />

            {/* Rutas protegidas — requieren autenticación */}
            <Route path="/favoritos" element={
              <ProtectedRoute><FavoritesPage /></ProtectedRoute>
            } />
            <Route path="/reservations/:id" element={
              <ProtectedRoute><ReservationPage /></ProtectedRoute>
            } />
            <Route path="/mis-reservas" element={
              <ProtectedRoute><MyReservationsPage /></ProtectedRoute>
            } />

            {/* Rutas protegidas — requieren rol ADMIN */}
            <Route path="/administracion" element={
              <ProtectedRoute requireRole="ADMIN"><AdminPage /></ProtectedRoute>
            } />
            <Route path="/admin" element={
              <ProtectedRoute requireRole="ADMIN"><AdminPage /></ProtectedRoute>
            } />

            {/* 404 */}
            <Route path="*" element={<NotFoundPage />} />
          </Routes>
          <Footer />
          <WhatsAppButton />
        </FavoritesProvider>
        </ToastProvider>
      </AuthProvider>
    </BrowserRouter>
  )
}
