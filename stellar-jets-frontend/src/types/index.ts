export interface Airport {
  city: string
  country: string
  iataCode: string
}

export interface Category {
  id: number
  name: string
  description: string
  imageUrl?: string
  flightCount: number
}

export interface FlightImage {
  id: number
  url: string
  altText: string
  cover: boolean
}

export interface Flight {
  id: number
  flightNumber: string | null
  name: string
  description: string
  price: number
  origin: Airport
  destination: Airport
  availableSeats: number
  durationMinutes: number | null
  rating: number
  reviewCount: number
  active: boolean
  category: Category | null
  images: FlightImage[]
  characteristics: Characteristic[]
  coverImageUrl: string | null
}

export interface Review {
  id: number
  firstName: string
  lastName: string
  stars: number
  comment: string | null
  createdAt: string
}

export interface ReviewRequest {
  stars: number
  comment: string
}

export interface PagedResponse<T> {
  content: T[]
  currentPage: number
  totalPages: number
  totalElements: number
  pageSize: number
  first: boolean
  last: boolean
}

export interface Characteristic {
  id: number
  name: string
  iconName: string
}

export interface AirportRequest {
  city: string
  country: string
  iataCode: string
}

export interface FlightRequest {
  flightNumber?: string
  name: string
  description: string
  price: number
  origin: AirportRequest
  destination: AirportRequest
  availableSeats: number
  durationMinutes?: number
  rating: number
  categoryId: number | null
  characteristicIds: number[]
  imageUrls: string[]
}

export interface AuthUser {
  id: number
  firstName: string
  lastName: string
  email: string
  role: 'USER' | 'ADMIN'
  token: string
}

export interface RegisterRequest {
  firstName: string
  lastName: string
  email: string
  password: string
}

export interface LoginRequest {
  email: string
  password: string
}

export interface AdminUser {
  id: number
  firstName: string
  lastName: string
  email: string
  role: string
  active: boolean
}

export interface OccupiedDateRange {
  checkIn: string
  checkOut: string
}

export interface ReservationRequest {
  checkIn: string
  checkOut: string
}

export interface ReservationResponse {
  id: number
  flightId: number
  flightName: string
  checkIn: string
  checkOut: string
  userEmail: string
}
