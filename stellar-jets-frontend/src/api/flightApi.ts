import axios from 'axios'
import type { Category, Flight, FlightRequest, PagedResponse } from '../types'

const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
})

// ---- Vuelos públicos ----

export const getFlights = (page = 0, size = 10): Promise<PagedResponse<Flight>> =>
  api.get('/flights', { params: { page, size } }).then(r => r.data)

export const searchFlights = (
  query?: string,
  categoryId?: number | null,
  page = 0,
  size = 10,
): Promise<PagedResponse<Flight>> =>
  api.get('/flights/search', { params: { query, categoryId, page, size } }).then(r => r.data)

export const getRecommended = (): Promise<Flight[]> =>
  api.get('/flights/recommended').then(r => r.data)

export const getFlightById = (id: number): Promise<Flight> =>
  api.get(`/flights/${id}`).then(r => r.data)

// ---- Categorías ----

export const getCategories = (): Promise<Category[]> =>
  api.get('/categories').then(r => r.data)

export const getActiveCategories = (): Promise<Category[]> =>
  api.get('/categories/active').then(r => r.data)

// ---- Admin ----

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
