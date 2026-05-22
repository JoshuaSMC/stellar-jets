import { BrowserRouter, Routes, Route, useLocation } from 'react-router-dom'
import { useEffect } from 'react'
import { AuthProvider } from './context/AuthContext'
import { FavoritesProvider } from './context/FavoritesContext'
import Header from './components/Header'
import Footer from './components/Footer'
import HomePage from './pages/HomePage'
import FlightDetailPage from './pages/FlightDetailPage'
import FavoritesPage from './pages/FavoritesPage'
import AdminPage from './pages/AdminPage'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import ReservationPage from './pages/ReservationPage'
import MyReservationsPage from './pages/MyReservationsPage'
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
        <FavoritesProvider>
          <ScrollToTop />
          <Header />
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/flights/:id" element={<FlightDetailPage />} />
            <Route path="/favoritos" element={<FavoritesPage />} />
            <Route path="/administracion" element={<AdminPage />} />
            <Route path="/admin" element={<AdminPage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/reservations/:id" element={<ReservationPage />} />
            <Route path="/mis-reservas" element={<MyReservationsPage />} />
          </Routes>
          <Footer />
          <WhatsAppButton />
        </FavoritesProvider>
      </AuthProvider>
    </BrowserRouter>
  )
}
