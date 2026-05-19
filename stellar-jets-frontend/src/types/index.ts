export interface Airport {
  city: string
  country: string
  iataCode: string
}

export interface Category {
  id: number
  name: string
  description: string
  iconName: string
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
  active: boolean
  category: Category | null
  images: FlightImage[]
  coverImageUrl: string | null
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
  imageUrls: string[]
}
