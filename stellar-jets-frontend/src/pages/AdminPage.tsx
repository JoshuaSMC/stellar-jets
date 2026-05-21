import { useEffect, useState, useCallback } from 'react'
import type React from 'react'
import { Link } from 'react-router-dom'
import {
  Plane, Plus, FolderOpen, Star, Users, X, Pencil, Trash2,
  Lock, ShieldOff, Monitor, AlertTriangle,
} from 'lucide-react'
import type { LucideProps } from 'lucide-react'
import CharIcon, { CHAR_ICON_MAP } from '../components/CharIcon'
import {
  adminGetAllFlights, adminCreateFlight, adminUpdateFlight,
  adminDeleteFlight, adminToggleFlight,
  getCategories, adminCreateCategory, adminUpdateCategory, adminDeleteCategory,
  getCharacteristics, adminCreateCharacteristic, adminUpdateCharacteristic, adminDeleteCharacteristic,
  adminGetUsers, adminToggleUserRole,
} from '../api/flightApi'
import type {
  AdminUser, AirportRequest, Category, Characteristic,
  Flight, FlightRequest, PagedResponse,
} from '../types'
import Pagination from '../components/Pagination'
import { useAuth } from '../context/AuthContext'

// ─── types & constants ────────────────────────────────────────────────────────

type View = 'vuelos' | 'categorias' | 'caracteristicas' | 'usuarios'

const EMPTY_AIRPORT: AirportRequest = { city: '', country: '', iataCode: '' }

const EMPTY_FLIGHT: FlightRequest = {
  flightNumber: '',
  name: '', description: '', price: 0,
  origin: { ...EMPTY_AIRPORT },
  destination: { ...EMPTY_AIRPORT },
  availableSeats: 1, durationMinutes: undefined,
  rating: 4.5, categoryId: null, characteristicIds: [], imageUrls: [],
}

// ─── component ────────────────────────────────────────────────────────────────

export default function AdminPage() {

  // shared
  const [view, setView] = useState<View>('vuelos')

  const { user } = useAuth()

  // ── vuelos ──────────────────────────────────────────────────────────────────
  const [paged, setPaged] = useState<PagedResponse<Flight> | null>(null)
  const [fetchError, setFetchError] = useState<string | null>(null)
  const [categories, setCategories] = useState<Category[]>([])
  const [characteristics, setCharacteristics] = useState<Characteristic[]>([])
  const [page, setPage] = useState(0)
  const [loadingFlights, setLoadingFlights] = useState(false)
  const [showFlightForm, setShowFlightForm] = useState(false)
  const [editingId, setEditingId] = useState<number | null>(null)
  const [flightForm, setFlightForm] = useState<FlightRequest>(EMPTY_FLIGHT)
  const [imageInput, setImageInput] = useState('')
  const [saving, setSaving] = useState(false)
  const [flightError, setFlightError] = useState<string | null>(null)
  const [confirm, setConfirm] = useState<number | null>(null)

  // ── categorías ──────────────────────────────────────────────────────────────
  const [catForm, setCatForm] = useState<Partial<Category>>({ name: '', description: '', imageUrl: '' })
  const [editingCatId, setEditingCatId] = useState<number | null>(null)
  const [showCatForm, setShowCatForm] = useState(false)
  const [catError, setCatError] = useState<string | null>(null)
  const [savingCat, setSavingCat] = useState(false)
  const [confirmCat, setConfirmCat] = useState<Category | null>(null)

  // ── características ─────────────────────────────────────────────────────────
  const [charForm, setCharForm] = useState<Partial<Characteristic>>({ name: '', iconName: '' })
  const [editingCharId, setEditingCharId] = useState<number | null>(null)
  const [showCharForm, setShowCharForm] = useState(false)
  const [charError, setCharError] = useState<string | null>(null)
  const [savingChar, setSavingChar] = useState(false)

  // ── usuarios ────────────────────────────────────────────────────────────────
  const [users, setUsers] = useState<AdminUser[]>([])
  const [loadingUsers, setLoadingUsers] = useState(false)

  // ─── fetch helpers ───────────────────────────────────────────────────────────

  const fetchFlights = useCallback(async (p: number) => {
    setLoadingFlights(true)
    setFetchError(null)
    try {
      setPaged(await adminGetAllFlights(p, 10))
    } catch (err: unknown) {
      const status = (err as { response?: { status?: number } }).response?.status
      if (status === 401 || status === 403) {
        setFetchError('Necesitás iniciar sesión como administrador para ver los productos.')
      } else {
        setFetchError('No se pudo cargar la lista de productos.')
      }
    } finally {
      setLoadingFlights(false)
    }
  }, [])

  const refreshShared = useCallback(() => {
    getCategories().then(setCategories).catch(console.error)
    getCharacteristics().then(setCharacteristics).catch(console.error)
  }, [])

  const fetchUsers = useCallback(async () => {
    setLoadingUsers(true)
    try { setUsers(await adminGetUsers()) }
    finally { setLoadingUsers(false) }
  }, [])

  useEffect(() => {
    if (user?.role === 'ADMIN') {
      fetchFlights(page)
    }
    refreshShared()
  }, [fetchFlights, refreshShared, page, user])

  useEffect(() => {
    if (view === 'usuarios') fetchUsers()
  }, [view, fetchUsers])

  // ─── vuelos actions ──────────────────────────────────────────────────────────

  const openCreate = () => {
    setEditingId(null)
    setFlightForm(EMPTY_FLIGHT)
    setImageInput('')
    setFlightError(null)
    setShowFlightForm(true)
  }

  const openEdit = (f: Flight) => {
    setEditingId(f.id)
    setFlightError(null)
    setFlightForm({
      flightNumber: f.flightNumber ?? '',
      name: f.name, description: f.description ?? '',
      price: f.price,
      origin: f.origin ?? { ...EMPTY_AIRPORT },
      destination: f.destination ?? { ...EMPTY_AIRPORT },
      availableSeats: f.availableSeats,
      durationMinutes: f.durationMinutes ?? undefined,
      rating: f.rating ?? 4,
      categoryId: f.category?.id ?? null,
      characteristicIds: f.characteristics?.map(c => c.id) ?? [],
      imageUrls: f.images.map(i => i.url),
    })
    setImageInput('')
    setShowFlightForm(true)
  }

  const setOrigin = (field: keyof AirportRequest, value: string) =>
    setFlightForm(f => ({ ...f, origin: { ...f.origin, [field]: value } }))

  const setDestination = (field: keyof AirportRequest, value: string) =>
    setFlightForm(f => ({ ...f, destination: { ...f.destination, [field]: value } }))

  const toggleChar = (id: number) =>
    setFlightForm(f => ({
      ...f,
      characteristicIds: f.characteristicIds.includes(id)
        ? f.characteristicIds.filter(x => x !== id)
        : [...f.characteristicIds, id],
    }))

  const handleSaveFlight = async () => {
    if (!flightForm.name.trim() || !flightForm.origin.city.trim() || !flightForm.destination.city.trim()) return
    setSaving(true)
    setFlightError(null)
    try {
      editingId
        ? await adminUpdateFlight(editingId, flightForm)
        : await adminCreateFlight(flightForm)
      setShowFlightForm(false)
      fetchFlights(page)
    } catch (err: unknown) {
      const res = (err as { response?: { data?: { message?: string } } }).response
      setFlightError(res?.data?.message ?? 'Error al guardar. Revisá los datos e intentá de nuevo.')
    } finally { setSaving(false) }
  }

  const handleDeleteFlight = async (id: number) => {
    await adminDeleteFlight(id)
    setConfirm(null)
    fetchFlights(page)
  }

  const handleToggleFlight = async (id: number) => {
    await adminToggleFlight(id)
    fetchFlights(page)
  }

  const addImage = () => {
    if (imageInput.trim()) {
      setFlightForm(f => ({ ...f, imageUrls: [...f.imageUrls, imageInput.trim()] }))
      setImageInput('')
    }
  }

  const removeImage = (i: number) =>
    setFlightForm(f => ({ ...f, imageUrls: f.imageUrls.filter((_, idx) => idx !== i) }))

  // ─── categorías actions ──────────────────────────────────────────────────────

  const openCatCreate = () => {
    setEditingCatId(null)
    setCatForm({ name: '', description: '', imageUrl: '' })
    setCatError(null)
    setShowCatForm(true)
  }

  const openCatEdit = (c: Category) => {
    setEditingCatId(c.id)
    setCatForm({ name: c.name, description: c.description, imageUrl: c.imageUrl ?? '' })
    setCatError(null)
    setShowCatForm(true)
  }

  const handleSaveCat = async () => {
    if (!catForm.name?.trim()) { setCatError('El nombre es obligatorio.'); return }
    setSavingCat(true)
    setCatError(null)
    try {
      editingCatId
        ? await adminUpdateCategory(editingCatId, catForm)
        : await adminCreateCategory(catForm)
      setShowCatForm(false)
      refreshShared()
    } catch (err: unknown) {
      const res = (err as { response?: { data?: { message?: string } } }).response
      setCatError(res?.data?.message ?? 'Error al guardar.')
    } finally { setSavingCat(false) }
  }

  const handleDeleteCat = async (id: number) => {
    await adminDeleteCategory(id)
    setConfirmCat(null)
    refreshShared()
  }

  // ─── características actions ─────────────────────────────────────────────────

  const openCharCreate = () => {
    setEditingCharId(null)
    setCharForm({ name: '', iconName: '' })
    setCharError(null)
    setShowCharForm(true)
  }

  const openCharEdit = (c: Characteristic) => {
    setEditingCharId(c.id)
    setCharForm({ name: c.name, iconName: c.iconName })
    setCharError(null)
    setShowCharForm(true)
  }

  const handleSaveChar = async () => {
    if (!charForm.name?.trim()) { setCharError('El nombre es obligatorio.'); return }
    setSavingChar(true)
    setCharError(null)
    try {
      editingCharId
        ? await adminUpdateCharacteristic(editingCharId, charForm)
        : await adminCreateCharacteristic(charForm)
      setShowCharForm(false)
      refreshShared()
    } catch (err: unknown) {
      const res = (err as { response?: { data?: { message?: string } } }).response
      setCharError(res?.data?.message ?? 'Error al guardar.')
    } finally { setSavingChar(false) }
  }

  const handleDeleteChar = async (id: number) => {
    if (!window.confirm('¿Eliminar esta característica?')) return
    await adminDeleteCharacteristic(id)
    refreshShared()
  }

  // ─── render ──────────────────────────────────────────────────────────────────

  const tabBtn = (v: View, label: React.ReactNode) => (
    <button
      onClick={() => setView(v)}
      className={`flex items-center gap-1.5 px-4 py-2 text-sm font-medium rounded-lg transition-colors ${
        view === v ? 'bg-navy-900 text-white' : 'text-gray-600 hover:bg-gray-100'
      }`}
    >
      {label}
    </button>
  )

  return (
    <main className="pt-16 pb-20 min-h-screen bg-gray-50">

      {/* Mobile blocker */}
      <div className="md:hidden flex flex-col items-center justify-center min-h-[70vh] px-8 text-center">
        <Monitor className="w-14 h-14 text-navy-300 mb-4" />
        <h2 className="text-xl font-bold text-gray-900 mb-2">Panel no disponible</h2>
        <p className="text-gray-500 text-sm leading-relaxed">
          El panel de administración no está disponible en dispositivos móviles.<br />
          Por favor, ingresá desde una computadora de escritorio.
        </p>
      </div>

      <div className="hidden md:block max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">

        {/* Auth gate — must be admin */}
        {!user && (
          <div className="mt-20 flex flex-col items-center text-center gap-4">
            <Lock className="w-14 h-14 text-navy-300" />
            <h2 className="text-xl font-bold text-gray-900">Acceso restringido</h2>
            <p className="text-gray-500 text-sm">Necesitás iniciar sesión para acceder al panel de administración.</p>
            <div className="flex gap-3 mt-2">
              <Link to="/login" className="btn-primary px-6 py-2.5 text-sm">Iniciar sesión</Link>
              <Link to="/register" className="btn-secondary px-6 py-2.5 text-sm">Crear cuenta</Link>
            </div>
          </div>
        )}

        {user && user.role !== 'ADMIN' && (
          <div className="mt-20 flex flex-col items-center text-center gap-4">
            <ShieldOff className="w-14 h-14 text-navy-300" />
            <h2 className="text-xl font-bold text-gray-900">Sin permisos</h2>
            <p className="text-gray-500 text-sm">Tu cuenta no tiene permisos de administrador.</p>
            <Link to="/" className="btn-primary px-6 py-2.5 text-sm mt-2">Volver al inicio</Link>
          </div>
        )}

        {user?.role === 'ADMIN' && (
        <>
        <div className="mt-8 mb-6">
          <h1 className="text-2xl font-bold text-gray-900 mb-4">Panel de Administración</h1>

          {/* Tab menu */}
          <div className="flex gap-2 border-b border-gray-200 pb-4 flex-wrap">
            {tabBtn('vuelos', <><Plane className="w-4 h-4" /> Lista de productos</>)}
            <button
              onClick={openCreate}
              className="flex items-center gap-1.5 px-4 py-2 text-sm font-medium rounded-lg transition-colors text-gray-600 hover:bg-gray-100"
            >
              <Plus className="w-4 h-4" /> Agregar producto
            </button>
            {tabBtn('categorias', <><FolderOpen className="w-4 h-4" /> Categorías</>)}
            {tabBtn('caracteristicas', <><Star className="w-4 h-4" /> Características</>)}
            {tabBtn('usuarios', <><Users className="w-4 h-4" /> Usuarios</>)}
          </div>
        </div>

        {/* ── VUELOS ──────────────────────────────────────────────────────────── */}
        {view === 'vuelos' && (
          <>
            {/* Wrapper sin overflow-hidden para que el scroll horizontal funcione */}
            <div className="bg-white rounded-2xl shadow-sm border border-gray-100">
              {loadingFlights ? (
                <div className="py-16 text-center text-gray-400">Cargando...</div>
              ) : fetchError ? (
                <div className="py-16 text-center text-red-500 text-sm">{fetchError}</div>
              ) : (
                <div className="overflow-x-auto">
                  <table className="w-full text-sm" style={{ minWidth: '860px' }}>
                    <thead>
                      <tr className="border-b border-gray-100 text-left text-xs uppercase tracking-wider text-gray-500 bg-gray-50/60">
                        <th className="px-3 py-3 w-10">Id</th>
                        <th className="px-3 py-3">Nombre</th>
                        <th className="px-3 py-3">Ruta</th>
                        <th className="px-3 py-3">Categoría</th>
                        <th className="px-3 py-3 text-right">Precio</th>
                        <th className="px-3 py-3 text-center w-20">Estado</th>
                        <th className="px-3 py-3 text-right w-28">Acciones</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-50">
                      {paged?.content.map(f => (
                        <tr key={f.id} className="hover:bg-gray-50/70 transition-colors">
                          <td className="px-3 py-3 text-gray-400 text-xs font-mono">{f.id}</td>

                          {/* Nombre + thumbnail */}
                          <td className="px-3 py-3">
                            <div className="flex items-center gap-2.5">
                              {f.coverImageUrl && (
                                <img src={f.coverImageUrl} alt="" className="w-9 h-9 rounded-lg object-cover flex-shrink-0" />
                              )}
                              <div className="min-w-0">
                                <p className="font-medium text-gray-900 truncate max-w-[160px]">{f.name}</p>
                                <p className="text-[11px] text-gray-400 font-mono">
                                  {f.flightNumber ?? '—'}
                                  {f.durationMinutes ? ` · ${Math.floor(f.durationMinutes / 60)}h${f.durationMinutes % 60 > 0 ? `${f.durationMinutes % 60}m` : ''}` : ''}
                                </p>
                              </div>
                            </div>
                          </td>

                          {/* Ruta */}
                          <td className="px-3 py-3">
                            <p className="font-mono text-sm font-semibold text-gray-700">
                              {f.origin?.iataCode} <span className="text-gray-300">→</span> {f.destination?.iataCode}
                            </p>
                            <p className="text-[11px] text-gray-400 truncate max-w-[150px]">
                              {f.origin?.city} → {f.destination?.city}
                            </p>
                          </td>

                          {/* Categoría — US#12 */}
                          <td className="px-3 py-3">
                            {f.category
                              ? <span className="badge bg-navy-50 text-navy-700 text-[11px]">{f.category.name}</span>
                              : <span className="text-gray-300 text-xs">Sin categoría</span>}
                          </td>

                          {/* Precio + asientos */}
                          <td className="px-3 py-3 text-right">
                            <p className="font-semibold text-navy-600">${f.price.toLocaleString('es-AR')}</p>
                            <p className="text-[11px] text-gray-400">{f.availableSeats} asientos</p>
                          </td>

                          {/* Estado toggle */}
                          <td className="px-3 py-3 text-center">
                            <button
                              onClick={() => handleToggleFlight(f.id)}
                              title="Click para cambiar estado"
                              className={`badge cursor-pointer transition-colors text-[11px] ${
                                f.active ? 'bg-green-100 text-green-700 hover:bg-green-200' : 'bg-red-100 text-red-500 hover:bg-red-200'
                              }`}
                            >
                              {f.active ? 'Activo' : 'Inactivo'}
                            </button>
                          </td>

                          {/* Acciones */}
                          <td className="px-3 py-3">
                            <div className="flex items-center justify-end gap-2">
                              <button onClick={() => openEdit(f)} className="p-1.5 rounded-lg text-navy-600 hover:bg-navy-50 transition-colors" title="Editar"><Pencil className="w-4 h-4" /></button>
                              <button onClick={() => setConfirm(f.id)} className="p-1.5 rounded-lg text-red-500 hover:bg-red-50 transition-colors" title="Eliminar"><Trash2 className="w-4 h-4" /></button>
                            </div>
                          </td>
                        </tr>
                      ))}
                      {paged?.content.length === 0 && (
                        <tr>
                          <td colSpan={7} className="px-4 py-12 text-center text-gray-400 text-sm">
                            No hay vuelos cargados aún.
                          </td>
                        </tr>
                      )}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
            {paged && (
              <Pagination currentPage={paged.currentPage} totalPages={paged.totalPages} onPageChange={setPage} />
            )}
          </>
        )}

        {/* ── CATEGORÍAS ─────────────────────────────────────────────────────── */}
        {view === 'categorias' && (
          <div className="bg-white rounded-2xl shadow-sm border border-gray-100">
            <div className="flex items-center justify-between px-5 py-4 border-b border-gray-100">
              <h2 className="font-semibold text-gray-900">Categorías</h2>
              <button onClick={openCatCreate} className="btn-primary text-sm flex items-center gap-1.5"><Plus className="w-4 h-4" /> Agregar categoría</button>
            </div>
            <div className="overflow-x-auto">
              <table className="w-full text-sm" style={{ minWidth: '640px' }}>
                <thead>
                  <tr className="border-b border-gray-100 text-left text-xs uppercase tracking-wider text-gray-500 bg-gray-50/60">
                    <th className="px-4 py-3 w-10">Id</th>
                    <th className="px-4 py-3">Nombre</th>
                    <th className="px-4 py-3">Descripción</th>
                    <th className="px-4 py-3 w-20 text-center">Vuelos</th>
                    <th className="px-4 py-3 text-right w-28">Acciones</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-50">
                  {categories.map(c => (
                    <tr key={c.id} className="hover:bg-gray-50/70">
                      <td className="px-4 py-3 text-gray-400 text-xs">{c.id}</td>
                      <td className="px-4 py-3 font-medium text-gray-900">{c.name}</td>
                      <td className="px-4 py-3 text-gray-500 text-xs truncate max-w-[240px]">{c.description}</td>
                      <td className="px-4 py-3 text-gray-600 text-center">{c.flightCount}</td>
                      <td className="px-4 py-3">
                        <div className="flex items-center justify-end gap-2">
                          <button onClick={() => openCatEdit(c)} className="p-1.5 rounded-lg text-navy-600 hover:bg-navy-50 transition-colors" title="Editar"><Pencil className="w-4 h-4" /></button>
                          <button onClick={() => setConfirmCat(c)} className="p-1.5 rounded-lg text-red-500 hover:bg-red-50 transition-colors" title="Eliminar"><Trash2 className="w-4 h-4" /></button>
                        </div>
                      </td>
                    </tr>
                  ))}
                  {categories.length === 0 && (
                    <tr><td colSpan={6} className="px-4 py-10 text-center text-gray-400 text-sm">No hay categorías aún.</td></tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {/* ── CARACTERÍSTICAS ─────────────────────────────────────────────────── */}
        {view === 'caracteristicas' && (
          <div className="bg-white rounded-2xl shadow-sm border border-gray-100">
            <div className="flex items-center justify-between px-5 py-4 border-b border-gray-100">
              <h2 className="font-semibold text-gray-900">Características</h2>
              <button onClick={openCharCreate} className="btn-primary text-sm flex items-center gap-1.5">
                <Plus className="w-4 h-4" /> Nueva característica
              </button>
            </div>
            <div className="p-5 grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-3">
              {characteristics.map(c => (
                <div
                  key={c.id}
                  className="group flex items-center gap-3 rounded-xl border border-gray-100 bg-gray-50/60 px-4 py-3 hover:border-navy-200 hover:bg-navy-50/30 transition-colors"
                >
                  <div className="w-9 h-9 rounded-xl bg-white border border-gray-200 flex items-center justify-center flex-shrink-0 shadow-sm">
                    <CharIcon iconName={c.iconName} />
                  </div>
                  <span className="text-sm font-medium text-gray-800 flex-1 min-w-0 truncate">{c.name}</span>
                  <div className="hidden group-hover:flex gap-1 flex-shrink-0">
                    <button onClick={() => openCharEdit(c)} className="p-1 rounded-lg text-navy-600 hover:bg-navy-100 transition-colors" title="Editar"><Pencil className="w-3.5 h-3.5" /></button>
                    <button onClick={() => handleDeleteChar(c.id)} className="p-1 rounded-lg text-red-500 hover:bg-red-100 transition-colors" title="Eliminar"><Trash2 className="w-3.5 h-3.5" /></button>
                  </div>
                </div>
              ))}
              {characteristics.length === 0 && (
                <p className="col-span-4 text-center py-10 text-gray-400 text-sm">No hay características aún.</p>
              )}
            </div>
          </div>
        )}

        {/* ── USUARIOS ────────────────────────────────────────────────────────── */}
        {view === 'usuarios' && (
          <div className="bg-white rounded-2xl shadow-sm border border-gray-100">
            <div className="px-5 py-4 border-b border-gray-100">
              <h2 className="font-semibold text-gray-900">Gestión de usuarios</h2>
            </div>
            {loadingUsers ? (
              <div className="py-10 text-center text-gray-400">Cargando...</div>
            ) : (
              <div className="overflow-x-auto">
              <table className="w-full text-sm" style={{ minWidth: '580px' }}>
                <thead>
                  <tr className="border-b border-gray-100 text-left text-xs uppercase tracking-wider text-gray-500 bg-gray-50/60">
                    <th className="px-4 py-3 w-10">Id</th>
                    <th className="px-4 py-3">Nombre</th>
                    <th className="px-4 py-3">Email</th>
                    <th className="px-4 py-3 w-24">Rol</th>
                    <th className="px-4 py-3 text-right w-32">Acciones</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-50">
                  {users.map(u => (
                    <tr key={u.id} className="hover:bg-gray-50">
                      <td className="px-4 py-3 text-gray-400 text-xs">{u.id}</td>
                      <td className="px-4 py-3">
                        <div className="flex items-center gap-2.5">
                          <div className="w-8 h-8 rounded-full bg-navy-100 flex items-center justify-center text-xs font-bold text-navy-700">
                            {u.firstName[0]}{u.lastName[0]}
                          </div>
                          <span className="font-medium text-gray-900">{u.firstName} {u.lastName}</span>
                        </div>
                      </td>
                      <td className="px-4 py-3 text-gray-500">{u.email}</td>
                      <td className="px-4 py-3">
                        <span className={`badge ${
                          u.role === 'ADMIN'
                            ? 'bg-gold-100 text-gold-700'
                            : 'bg-gray-100 text-gray-500'
                        }`}>
                          {u.role === 'ADMIN' ? 'Admin' : 'Usuario'}
                        </span>
                      </td>
                      <td className="px-4 py-3">
                        <div className="flex justify-end">
                          <button
                            onClick={async () => {
                              const updated = await adminToggleUserRole(u.id)
                              setUsers(prev => prev.map(x => x.id === u.id ? updated : x))
                            }}
                            className={`text-xs font-medium px-3 py-1 rounded-lg transition-colors ${
                              u.role === 'ADMIN'
                                ? 'bg-red-50 text-red-600 hover:bg-red-100'
                                : 'bg-navy-50 text-navy-700 hover:bg-navy-100'
                            }`}
                          >
                            {u.role === 'ADMIN' ? 'Quitar Admin' : 'Hacer Admin'}
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
              </div>
            )}
          </div>
        )}
        </>
        )}
      </div>

      {/* ── MODAL: Vuelo ─────────────────────────────────────────────────────── */}
      {showFlightForm && (
        <div className="fixed inset-0 z-50 bg-black/50 flex items-center justify-center p-4 overflow-y-auto">
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-2xl my-4">
            <div className="flex items-center justify-between px-6 py-4 border-b border-gray-100">
              <h2 className="text-lg font-semibold text-gray-900">
                {editingId ? 'Editar vuelo' : 'Nuevo vuelo'}
              </h2>
              <button onClick={() => setShowFlightForm(false)} className="text-gray-400 hover:text-gray-600"><X className="w-5 h-5" /></button>
            </div>

            <div className="px-6 py-5 grid grid-cols-1 sm:grid-cols-2 gap-4 overflow-y-auto max-h-[70vh]">

              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1">Nombre *</label>
                <input className="input" value={flightForm.name}
                  onChange={e => setFlightForm(f => ({ ...f, name: e.target.value }))} />
              </div>
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1">Código de vuelo (ej: SJ-012)</label>
                <input className="input font-mono" placeholder="SJ-012" value={flightForm.flightNumber ?? ''}
                  onChange={e => setFlightForm(f => ({ ...f, flightNumber: e.target.value }))} />
              </div>

              {/* Origen */}
              <div className="sm:col-span-2">
                <p className="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-2">Origen *</p>
                <div className="grid grid-cols-3 gap-2">
                  <div>
                    <label className="block text-xs text-gray-500 mb-1">Ciudad</label>
                    <input className="input" placeholder="Buenos Aires" value={flightForm.origin.city}
                      onChange={e => setOrigin('city', e.target.value)} />
                  </div>
                  <div>
                    <label className="block text-xs text-gray-500 mb-1">País</label>
                    <input className="input" placeholder="Argentina" value={flightForm.origin.country}
                      onChange={e => setOrigin('country', e.target.value)} />
                  </div>
                  <div>
                    <label className="block text-xs text-gray-500 mb-1">IATA</label>
                    <input className="input font-mono uppercase" placeholder="EZE" maxLength={3}
                      value={flightForm.origin.iataCode}
                      onChange={e => setOrigin('iataCode', e.target.value.toUpperCase())} />
                  </div>
                </div>
              </div>

              {/* Destino */}
              <div className="sm:col-span-2">
                <p className="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-2">Destino *</p>
                <div className="grid grid-cols-3 gap-2">
                  <div>
                    <label className="block text-xs text-gray-500 mb-1">Ciudad</label>
                    <input className="input" placeholder="Madrid" value={flightForm.destination.city}
                      onChange={e => setDestination('city', e.target.value)} />
                  </div>
                  <div>
                    <label className="block text-xs text-gray-500 mb-1">País</label>
                    <input className="input" placeholder="España" value={flightForm.destination.country}
                      onChange={e => setDestination('country', e.target.value)} />
                  </div>
                  <div>
                    <label className="block text-xs text-gray-500 mb-1">IATA</label>
                    <input className="input font-mono uppercase" placeholder="MAD" maxLength={3}
                      value={flightForm.destination.iataCode}
                      onChange={e => setDestination('iataCode', e.target.value.toUpperCase())} />
                  </div>
                </div>
              </div>

              {/* Precio / Duración */}
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1">Precio (USD) *</label>
                <input type="number" className="input" min={1} value={flightForm.price}
                  onChange={e => setFlightForm(f => ({ ...f, price: Number(e.target.value) }))} />
              </div>
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1">Duración (minutos)</label>
                <input type="number" className="input" min={1} placeholder="ej: 180"
                  value={flightForm.durationMinutes ?? ''}
                  onChange={e => setFlightForm(f => ({ ...f, durationMinutes: e.target.value ? Number(e.target.value) : undefined }))} />
              </div>

              {/* Asientos / Rating */}
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1">Asientos disponibles</label>
                <input type="number" className="input" min={1} value={flightForm.availableSeats}
                  onChange={e => setFlightForm(f => ({ ...f, availableSeats: Number(e.target.value) }))} />
              </div>
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1">Rating (1-5)</label>
                <input type="number" className="input" min={1} max={5} step={0.1} value={flightForm.rating}
                  onChange={e => setFlightForm(f => ({ ...f, rating: Number(e.target.value) }))} />
              </div>

              {/* Categoría */}
              <div className="sm:col-span-2">
                <label className="block text-xs font-medium text-gray-600 mb-1">Categoría</label>
                <select className="input" value={flightForm.categoryId ?? ''}
                  onChange={e => setFlightForm(f => ({ ...f, categoryId: e.target.value ? Number(e.target.value) : null }))}>
                  <option value="">Sin categoría</option>
                  {categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
                </select>
              </div>

              {/* Características */}
              {characteristics.length > 0 && (
                <div className="sm:col-span-2">
                  <label className="block text-xs font-medium text-gray-600 mb-2">Características del vuelo</label>
                  <div className="flex flex-wrap gap-2">
                    {characteristics.map(c => {
                      const selected = flightForm.characteristicIds.includes(c.id)
                      return (
                        <button
                          key={c.id}
                          type="button"
                          onClick={() => toggleChar(c.id)}
                          className={`flex items-center gap-1.5 px-3 py-1.5 rounded-full text-xs border transition-colors ${
                            selected
                              ? 'bg-navy-600 text-white border-navy-600'
                              : 'bg-white text-gray-600 border-gray-200 hover:border-navy-400'
                          }`}
                        >
                          <span>{c.iconName}</span>
                          {c.name}
                        </button>
                      )
                    })}
                  </div>
                </div>
              )}

              {/* Descripción */}
              <div className="sm:col-span-2">
                <label className="block text-xs font-medium text-gray-600 mb-1">Descripción</label>
                <textarea className="input h-20 resize-none" value={flightForm.description}
                  onChange={e => setFlightForm(f => ({ ...f, description: e.target.value }))} />
              </div>

              {/* Imágenes */}
              <div className="sm:col-span-2">
                <label className="block text-xs font-medium text-gray-600 mb-1">URLs de imágenes</label>
                <div className="flex gap-2 mb-2">
                  <input className="input flex-1" placeholder="https://..." value={imageInput}
                    onChange={e => setImageInput(e.target.value)}
                    onKeyDown={e => e.key === 'Enter' && (e.preventDefault(), addImage())} />
                  <button onClick={addImage} className="btn-secondary px-3 py-2 text-sm">Agregar</button>
                </div>
                {flightForm.imageUrls.length > 0 && (
                  <div className="flex flex-wrap gap-2 mt-2">
                    {flightForm.imageUrls.map((url, i) => (
                      <div key={i} className="flex items-center gap-1 bg-gray-100 rounded-lg px-2 py-1 text-xs text-gray-600 max-w-[200px]">
                        <span className="truncate">{url.split('/').pop()}</span>
                        {i === 0 && <span className="badge bg-navy-100 text-navy-600 ml-1">portada</span>}
                        <button onClick={() => removeImage(i)} className="text-red-400 hover:text-red-600 ml-1 flex-shrink-0">✕</button>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            </div>

            {flightError && (
              <div className="mx-6 mb-2 px-4 py-3 rounded-lg bg-red-50 border border-red-200 text-red-700 text-sm">
                {flightError}
              </div>
            )}
            <div className="flex justify-end gap-3 px-6 py-4 border-t border-gray-100">
              <button onClick={() => { setShowFlightForm(false); setFlightError(null) }} className="btn-secondary">Cancelar</button>
              <button onClick={handleSaveFlight} disabled={saving} className="btn-primary disabled:opacity-60">
                {saving ? 'Guardando...' : editingId ? 'Guardar cambios' : 'Crear vuelo'}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* ── MODAL: Categoría ─────────────────────────────────────────────────── */}
      {showCatForm && (
        <div className="fixed inset-0 z-50 bg-black/50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-md">
            <div className="flex items-center justify-between px-6 py-4 border-b border-gray-100">
              <h2 className="text-lg font-semibold text-gray-900">
                {editingCatId ? 'Editar categoría' : 'Nueva categoría'}
              </h2>
              <button onClick={() => setShowCatForm(false)} className="text-gray-400 hover:text-gray-600"><X className="w-5 h-5" /></button>
            </div>
            <div className="px-6 py-5 space-y-4">
              {catError && (
                <div className="px-4 py-3 rounded-lg bg-red-50 border border-red-200 text-red-700 text-sm">{catError}</div>
              )}
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1">Nombre *</label>
                <input className="input" value={catForm.name ?? ''}
                  onChange={e => setCatForm(f => ({ ...f, name: e.target.value }))} />
              </div>
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1">Descripción</label>
                <input className="input" value={catForm.description ?? ''}
                  onChange={e => setCatForm(f => ({ ...f, description: e.target.value }))} />
              </div>
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1">URL de imagen</label>
                <input className="input" placeholder="https://..." value={catForm.imageUrl ?? ''}
                  onChange={e => setCatForm(f => ({ ...f, imageUrl: e.target.value }))} />
              </div>
            </div>
            <div className="flex justify-end gap-3 px-6 py-4 border-t border-gray-100">
              <button onClick={() => setShowCatForm(false)} className="btn-secondary">Cancelar</button>
              <button onClick={handleSaveCat} disabled={savingCat} className="btn-primary disabled:opacity-60">
                {savingCat ? 'Guardando...' : 'Guardar'}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* ── MODAL: Característica ────────────────────────────────────────────── */}
      {showCharForm && (
        <div className="fixed inset-0 z-50 bg-black/50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-sm">
            <div className="flex items-center justify-between px-6 py-4 border-b border-gray-100">
              <h2 className="text-lg font-semibold text-gray-900">
                {editingCharId ? 'Editar característica' : 'Nueva característica'}
              </h2>
              <button onClick={() => setShowCharForm(false)} className="text-gray-400 hover:text-gray-600"><X className="w-5 h-5" /></button>
            </div>
            <div className="px-6 py-5 space-y-4">
              {charError && (
                <div className="px-4 py-3 rounded-lg bg-red-50 border border-red-200 text-red-700 text-sm">{charError}</div>
              )}
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1">Nombre *</label>
                <input className="input" placeholder="ej: WiFi a bordo" value={charForm.name ?? ''}
                  onChange={e => setCharForm(f => ({ ...f, name: e.target.value }))} />
              </div>
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-2">Ícono</label>
                <div className="grid grid-cols-4 gap-2">
                  {(Object.entries(CHAR_ICON_MAP) as [string, React.ComponentType<LucideProps>][]).map(([key, Icon]) => (
                    <button
                      key={key}
                      type="button"
                      onClick={() => setCharForm(f => ({ ...f, iconName: key }))}
                      className={`flex flex-col items-center gap-1 rounded-xl p-3 border transition-colors ${
                        charForm.iconName === key
                          ? 'bg-navy-600 border-navy-600 text-white'
                          : 'border-gray-200 text-gray-500 hover:border-navy-300 hover:text-navy-600'
                      }`}
                    >
                      <Icon className="w-5 h-5" />
                      <span className="text-[10px] capitalize">{key}</span>
                    </button>
                  ))}
                </div>
              </div>
            </div>
            <div className="flex justify-end gap-3 px-6 py-4 border-t border-gray-100">
              <button onClick={() => setShowCharForm(false)} className="btn-secondary">Cancelar</button>
              <button onClick={handleSaveChar} disabled={savingChar} className="btn-primary disabled:opacity-60">
                {savingChar ? 'Guardando...' : 'Guardar'}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* ── MODAL: Confirmar eliminación vuelo ───────────────────────────────── */}
      {confirm !== null && (
        <div className="fixed inset-0 z-50 bg-black/50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-sm p-6 text-center">
            <AlertTriangle className="w-12 h-12 text-amber-500 mb-3 mx-auto" />
            <h3 className="text-lg font-semibold text-gray-900 mb-2">¿Confirmar eliminación?</h3>
            <p className="text-gray-500 text-sm mb-6">Esta acción no se puede deshacer.</p>
            <div className="flex justify-center gap-3">
              <button onClick={() => setConfirm(null)} className="btn-secondary">Cancelar</button>
              <button onClick={() => handleDeleteFlight(confirm)} className="btn-danger">Eliminar</button>
            </div>
          </div>
        </div>
      )}

      {/* ── MODAL: Confirmar eliminación categoría — US#29 ───────────────────── */}
      {confirmCat !== null && (
        <div className="fixed inset-0 z-50 bg-black/50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-sm p-6 text-center">
            <AlertTriangle className="w-12 h-12 text-amber-500 mb-3 mx-auto" />
            <h3 className="text-lg font-semibold text-gray-900 mb-2">¿Eliminar categoría?</h3>
            <p className="text-gray-700 text-sm font-medium mb-1">
              "{confirmCat.name}"
            </p>
            <p className="text-gray-500 text-sm mb-2">
              {confirmCat.flightCount > 0
                ? `Esta categoría tiene ${confirmCat.flightCount} ${confirmCat.flightCount === 1 ? 'vuelo asociado' : 'vuelos asociados'}. Al eliminarla, esos vuelos quedarán sin categoría.`
                : 'Esta categoría no tiene vuelos asociados.'}
            </p>
            <p className="text-red-500 text-xs mb-6">Esta acción no se puede deshacer.</p>
            <div className="flex justify-center gap-3">
              <button onClick={() => setConfirmCat(null)} className="btn-secondary">Cancelar</button>
              <button onClick={() => handleDeleteCat(confirmCat.id)} className="btn-danger">Confirmar eliminación</button>
            </div>
          </div>
        </div>
      )}
    </main>
  )
}
