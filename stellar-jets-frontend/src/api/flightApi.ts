import axios from 'axios'
import type {
  AdminUser, AuthUser, Category, Characteristic, Flight,
  FlightRequest, LoginRequest, PagedResponse, RegisterRequest,
} from '../types'

const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
})

// Attach JWT to every request when available
api.interceptors.request.use(config => {
  const token = localStorage.getItem('sj_token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

// ---- Vuelos públicos ----

export const getFlights = (page = 0, size = 10): Promise<PagedResponse<Flight>> =>
  api.get('/flights', { params: { page, size } }).then(r => r.data)

export const searchFlights = (
  query?: string,
  categoryIds?: number[],
  page = 0,
  size = 10,
): Promise<PagedResponse<Flight>> =>
  api.get('/flights/search', {
    params: { query, categoryIds: categoryIds?.join(',') || undefined, page, size },
  }).then(r => r.data)

export const getRecommended = (): Promise<Flight[]> =>
  api.get('/flights/recommended').then(r => r.data)

export const getFlightById = (id: number): Promise<Flight> =>
  api.get(`/flights/${id}`).then(r => r.data)

// ---- Categorías ----

export const getCategories = (): Promise<Category[]> =>
  api.get('/categories').then(r => r.data)

export const getActiveCategories = (): Promise<Category[]> =>
  api.get('/categories/active').then(r => r.data)

// ---- Características ----

export const getCharacteristics = (): Promise<Characteristic[]> =>
  api.get('/characteristics').then(r => r.data)

// ---- Auth ----

export const authRegister = (data: RegisterRequest): Promise<AuthUser> =>
  api.post('/auth/register', data).then(r => r.data)

export const authLogin = (data: LoginRequest): Promise<AuthUser> =>
  api.post('/auth/login', data).then(r => r.data)

export const authResendConfirmation = (email: string): Promise<void> =>
  api.post('/auth/resend-confirmation', { email }).then(r => r.data)

// ---- Admin: Vuelos ----

export const adminGetAllFlights = (page = 0, size = 10): Promise<PagedResponse<Flight>> =>
  api.get('/admin/flights', { params: { page, size } }).then(r => r.data)

export const adminCreateFlight = (data: FlightRequest): Promise<Flight> =>
  api.post('/admin/flights', data).then(r => r.data)

export const adminUpdateFlight = (id: number, data: FlightRequest): Promise<Flight> =>
  api.put(`/admin/flights/${id}`, data).then(r => r.data)

export const adminDeleteFlight = (id: number): Promise<void> =>
  api.delete(`/admin/flights/${id}`)

export const adminToggleFlight = (id: number): Promise<Flight> =>
  api.patch(`/admin/flights/${id}/toggle`).then(r => r.data)

// ---- Admin: Categorías ----

export const adminCreateCategory = (data: Partial<Category>): Promise<Category> =>
  api.post('/admin/categories', data).then(r => r.data)

export const adminUpdateCategory = (id: number, data: Partial<Category>): Promise<Category> =>
  api.put(`/admin/categories/${id}`, data).then(r => r.data)

export const adminDeleteCategory = (id: number): Promise<void> =>
  api.delete(`/admin/categories/${id}`)

// ---- Admin: Características ----

export const adminCreateCharacteristic = (data: Partial<Characteristic>): Promise<Characteristic> =>
  api.post('/admin/characteristics', data).then(r => r.data)

export const adminUpdateCharacteristic = (id: number, data: Partial<Characteristic>): Promise<Characteristic> =>
  api.put(`/admin/characteristics/${id}`, data).then(r => r.data)

export const adminDeleteCharacteristic = (id: number): Promise<void> =>
  api.delete(`/admin/characteristics/${id}`)

// ---- Admin: Usuarios ----

export const adminGetUsers = (): Promise<AdminUser[]> =>
  api.get('/admin/users').then(r => r.data)

export const adminToggleUserRole = (id: number): Promise<AdminUser> =>
  api.patch(`/admin/users/${id}/toggle-role`).then(r => r.data)
