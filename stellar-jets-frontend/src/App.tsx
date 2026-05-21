import { BrowserRouter, Routes, Route } from 'react-router-dom'
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

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <FavoritesProvider>
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
          </Routes>
          <Footer />
        </FavoritesProvider>
      </AuthProvider>
    </BrowserRouter>
  )
}
