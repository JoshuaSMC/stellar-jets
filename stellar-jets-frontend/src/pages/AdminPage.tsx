import { useEffect, useState, useCallback } from 'react'
import {
  adminGetAllFlights, adminCreateFlight, adminUpdateFlight,
  adminDeleteFlight, adminToggleFlight, getCategories,
} from '../api/flightApi'
import type { AirportRequest, Category, Flight, FlightRequest, PagedResponse } from '../types'
import Pagination from '../components/Pagination'

const EMPTY_AIRPORT: AirportRequest = { city: '', country: '', iataCode: '' }

const EMPTY_FORM: FlightRequest = {
  flightNumber: '',
  name: '', description: '', price: 0,
  origin: { ...EMPTY_AIRPORT },
  destination: { ...EMPTY_AIRPORT },
  availableSeats: 1, durationMinutes: undefined,
  rating: 4.5, categoryId: null, imageUrls: [],
}

export default function AdminPage() {
  const [paged, setPaged] = useState<PagedResponse<Flight> | null>(null)
  const [categories, setCategories] = useState<Category[]>([])
  const [page, setPage] = useState(0)
  const [loading, setLoading] = useState(false)
  const [showForm, setShowForm] = useState(false)
  const [editingId, setEditingId] = useState<number | null>(null)
  const [form, setForm] = useState<FlightRequest>(EMPTY_FORM)
  const [imageInput, setImageInput] = useState('')
  const [saving, setSaving] = useState(false)
  const [formError, setFormError] = useState<string | null>(null)
  const [confirm, setConfirm] = useState<number | null>(null)
  const [view, setView] = useState<'lista' | 'agregar'>('lista')

  const fetchFlights = useCallback(async (p: number) => {
    setLoading(true)
    try { setPaged(await adminGetAllFlights(p, 10)) }
    finally { setLoading(false) }
  }, [])

  useEffect(() => {
    fetchFlights(page)
    getCategories().then(setCategories).catch(console.error)
  }, [fetchFlights, page])

  const openCreate = () => {
    setEditingId(null)
    setForm(EMPTY_FORM)
    setImageInput('')
    setFormError(null)
    setShowForm(true)
    setView('agregar')
  }

  const openEdit = (f: Flight) => {
    setEditingId(f.id)
    setFormError(null)
    setForm({
      flightNumber: f.flightNumber ?? '',
      name: f.name, description: f.description ?? '',
      price: f.price,
      origin: f.origin ?? { ...EMPTY_AIRPORT },
      destination: f.destination ?? { ...EMPTY_AIRPORT },
      availableSeats: f.availableSeats,
      durationMinutes: f.durationMinutes ?? undefined,
      rating: f.rating ?? 4,
      categoryId: f.category?.id ?? null,
      imageUrls: f.images.map(i => i.url),
    })
    setImageInput('')
    setShowForm(true)
  }

  const setOrigin = (field: keyof AirportRequest, value: string) =>
    setForm(f => ({ ...f, origin: { ...f.origin, [field]: value } }))

  const setDestination = (field: keyof AirportRequest, value: string) =>
    setForm(f => ({ ...f, destination: { ...f.destination, [field]: value } }))

  const handleSave = async () => {
    if (!form.name.trim() || !form.origin.city.trim() || !form.destination.city.trim()) return
    setSaving(true)
    setFormError(null)
    try {
      editingId
        ? await adminUpdateFlight(editingId, form)
        : await adminCreateFlight(form)
      setShowForm(false)
      fetchFlights(page)
    } catch (err: unknown) {
      const res = (err as { response?: { data?: { message?: string } } }).response
      setFormError(res?.data?.message ?? 'Error al guardar. Revisá los datos e intentá de nuevo.')
    } finally { setSaving(false) }
  }

  const handleDelete = async (id: number) => {
    await adminDeleteFlight(id)
    setConfirm(null)
    fetchFlights(page)
  }

  const handleToggle = async (id: number) => {
    await adminToggleFlight(id)
    fetchFlights(page)
  }

  const addImage = () => {
    if (imageInput.trim()) {
      setForm(f => ({ ...f, imageUrls: [...f.imageUrls, imageInput.trim()] }))
      setImageInput('')
    }
  }

  const removeImage = (i: number) =>
    setForm(f => ({ ...f, imageUrls: f.imageUrls.filter((_, idx) => idx !== i) }))

  return (
    <main className="pt-16 pb-20 min-h-screen bg-gray-50">

      {/* US#9: Panel NO disponible en mobile */}
      <div className="md:hidden flex flex-col items-center justify-center min-h-[70vh] px-8 text-center">
        <div className="text-5xl mb-4">🖥️</div>
        <h2 className="text-xl font-bold text-gray-900 mb-2">Panel no disponible</h2>
        <p className="text-gray-500 text-sm leading-relaxed">
          El panel de administración no está disponible en dispositivos móviles.<br />
          Por favor, ingresá desde una computadora de escritorio.
        </p>
      </div>

      <div className="hidden md:block max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">

        <div className="mt-8 mb-6">
          <h1 className="text-2xl font-bold text-gray-900 mb-4">Panel de Administración</h1>
          {/* Menú US#9 */}
          <div className="flex gap-3 border-b border-gray-200 pb-4">
            <button
              onClick={() => { setView('lista'); setShowForm(false) }}
              className={view === 'lista' ? 'btn-primary' : 'btn-secondary'}
            >
              Lista de productos
            </button>
            <button
              onClick={openCreate}
              className={view === 'agregar' ? 'btn-primary' : 'btn-secondary'}
            >
              + Agregar producto
            </button>
          </div>
        </div>

        {/* Vista: Lista de productos */}
        {view === 'lista' && (
        <>
        <div id="tabla-productos" className="card overflow-x-auto">
          {loading ? (
            <div className="py-16 text-center text-gray-400">Cargando...</div>
          ) : (
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-gray-100 text-left text-xs uppercase tracking-wider text-gray-500">
                  <th className="px-4 py-3">Id</th>
                  <th className="px-4 py-3">Nombre</th>
                  <th className="px-4 py-3">Ruta</th>
                  <th className="px-4 py-3">Duración</th>
                  <th className="px-4 py-3">Categoría</th>
                  <th className="px-4 py-3">Precio</th>
                  <th className="px-4 py-3">Asientos</th>
                  <th className="px-4 py-3">Estado</th>
                  <th className="px-4 py-3 text-right">Acciones</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-50">
                {paged?.content.map(f => (
                  <tr key={f.id} className="hover:bg-gray-50 transition-colors">
                    <td className="px-4 py-3 text-gray-500 text-xs font-mono">{f.id}</td>
                    <td className="px-4 py-3">
                      <div className="flex items-center gap-3">
                        {f.coverImageUrl && (
                          <img src={f.coverImageUrl} alt="" className="w-10 h-10 rounded-lg object-cover flex-shrink-0" />
                        )}
                        <div>
                          <p className="font-medium text-gray-900 max-w-[130px] truncate">{f.name}</p>
                          {f.flightNumber && (
                            <p className="text-xs font-mono text-gray-400">{f.flightNumber}</p>
                          )}
                        </div>
                      </div>
                    </td>
                    <td className="px-4 py-3 text-gray-600">
                      <span className="font-mono font-semibold">{f.origin?.iataCode}</span>
                      <span className="mx-1 text-gray-300">→</span>
                      <span className="font-mono font-semibold">{f.destination?.iataCode}</span>
                      <p className="text-xs text-gray-400">{f.origin?.city} → {f.destination?.city}</p>
                    </td>
                    <td className="px-4 py-3 text-gray-500 text-xs">
                      {f.durationMinutes ? `${Math.floor(f.durationMinutes / 60)}h ${f.durationMinutes % 60}m` : '—'}
                    </td>
                    <td className="px-4 py-3">
                      {f.category
                        ? <span className="badge bg-navy-50 text-navy-700">{f.category.name}</span>
                        : <span className="text-gray-300">—</span>}
                    </td>
                    <td className="px-4 py-3 font-semibold text-navy-600">
                      ${f.price.toLocaleString('es-AR')}
                    </td>
                    <td className="px-4 py-3 text-gray-600">{f.availableSeats}</td>
                    <td className="px-4 py-3">
                      <button
                        onClick={() => handleToggle(f.id)}
                        className={`badge cursor-pointer transition-colors ${
                          f.active ? 'bg-green-100 text-green-700 hover:bg-green-200' : 'bg-red-100 text-red-600 hover:bg-red-200'
                        }`}
                      >
                        {f.active ? 'Activo' : 'Inactivo'}
                      </button>
                    </td>
                    <td className="px-4 py-3">
                      <div className="flex items-center justify-end gap-2">
                        <button onClick={() => openEdit(f)} className="text-xs text-navy-600 hover:underline font-medium">Editar</button>
                        <button onClick={() => setConfirm(f.id)} className="text-xs text-red-500 hover:underline font-medium">Eliminar</button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>

        {paged && (
          <Pagination currentPage={paged.currentPage} totalPages={paged.totalPages} onPageChange={setPage} />
        )}
        </>
        )}
      </div>

      {/* ---- MODAL FORMULARIO ---- */}
      {showForm && (
        <div className="fixed inset-0 z-50 bg-black/50 flex items-center justify-center p-4 overflow-y-auto">
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-2xl my-4">
            <div className="flex items-center justify-between px-6 py-4 border-b border-gray-100">
              <h2 className="text-lg font-semibold text-gray-900">
                {editingId ? 'Editar vuelo' : 'Nuevo vuelo'}
              </h2>
              <button onClick={() => setShowForm(false)} className="text-gray-400 hover:text-gray-600 text-xl">✕</button>
            </div>

            <div className="px-6 py-5 grid grid-cols-1 sm:grid-cols-2 gap-4 overflow-y-auto max-h-[70vh]">

              {/* Nombre + código vuelo */}
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1">Nombre *</label>
                <input className="input" value={form.name}
                  onChange={e => setForm(f => ({ ...f, name: e.target.value }))} />
              </div>
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1">Código de vuelo (ej: SJ-012)</label>
                <input className="input font-mono" placeholder="SJ-012" value={form.flightNumber ?? ''}
                  onChange={e => setForm(f => ({ ...f, flightNumber: e.target.value }))} />
              </div>

              {/* Origen */}
              <div className="sm:col-span-2">
                <p className="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-2">Origen *</p>
                <div className="grid grid-cols-3 gap-2">
                  <div>
                    <label className="block text-xs text-gray-500 mb-1">Ciudad</label>
                    <input className="input" placeholder="Buenos Aires" value={form.origin.city}
                      onChange={e => setOrigin('city', e.target.value)} />
                  </div>
                  <div>
                    <label className="block text-xs text-gray-500 mb-1">País</label>
                    <input className="input" placeholder="Argentina" value={form.origin.country}
                      onChange={e => setOrigin('country', e.target.value)} />
                  </div>
                  <div>
                    <label className="block text-xs text-gray-500 mb-1">IATA</label>
                    <input className="input font-mono uppercase" placeholder="EZE" maxLength={3}
                      value={form.origin.iataCode}
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
                    <input className="input" placeholder="Madrid" value={form.destination.city}
                      onChange={e => setDestination('city', e.target.value)} />
                  </div>
                  <div>
                    <label className="block text-xs text-gray-500 mb-1">País</label>
                    <input className="input" placeholder="España" value={form.destination.country}
                      onChange={e => setDestination('country', e.target.value)} />
                  </div>
                  <div>
                    <label className="block text-xs text-gray-500 mb-1">IATA</label>
                    <input className="input font-mono uppercase" placeholder="MAD" maxLength={3}
                      value={form.destination.iataCode}
                      onChange={e => setDestination('iataCode', e.target.value.toUpperCase())} />
                  </div>
                </div>
              </div>

              {/* Precio / Duración */}
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1">Precio (USD) *</label>
                <input type="number" className="input" min={1} value={form.price}
                  onChange={e => setForm(f => ({ ...f, price: Number(e.target.value) }))} />
              </div>
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1">Duración (minutos)</label>
                <input type="number" className="input" min={1} placeholder="ej: 180"
                  value={form.durationMinutes ?? ''}
                  onChange={e => setForm(f => ({ ...f, durationMinutes: e.target.value ? Number(e.target.value) : undefined }))} />
              </div>

              {/* Asientos / Rating */}
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1">Asientos disponibles</label>
                <input type="number" className="input" min={1} value={form.availableSeats}
                  onChange={e => setForm(f => ({ ...f, availableSeats: Number(e.target.value) }))} />
              </div>
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1">Rating (1-5)</label>
                <input type="number" className="input" min={1} max={5} step={0.1} value={form.rating}
                  onChange={e => setForm(f => ({ ...f, rating: Number(e.target.value) }))} />
              </div>

              {/* Categoría */}
              <div className="sm:col-span-2">
                <label className="block text-xs font-medium text-gray-600 mb-1">Categoría</label>
                <select className="input" value={form.categoryId ?? ''}
                  onChange={e => setForm(f => ({ ...f, categoryId: e.target.value ? Number(e.target.value) : null }))}>
                  <option value="">Sin categoría</option>
                  {categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
                </select>
              </div>

              {/* Descripción */}
              <div className="sm:col-span-2">
                <label className="block text-xs font-medium text-gray-600 mb-1">Descripción</label>
                <textarea className="input h-20 resize-none" value={form.description}
                  onChange={e => setForm(f => ({ ...f, description: e.target.value }))} />
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
                {form.imageUrls.length > 0 && (
                  <div className="flex flex-wrap gap-2 mt-2">
                    {form.imageUrls.map((url, i) => (
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

            {formError && (
              <div className="mx-6 mb-2 px-4 py-3 rounded-lg bg-red-50 border border-red-200 text-red-700 text-sm">
                {formError}
              </div>
            )}
            <div className="flex justify-end gap-3 px-6 py-4 border-t border-gray-100">
              <button onClick={() => { setShowForm(false); setFormError(null) }} className="btn-secondary">Cancelar</button>
              <button onClick={handleSave} disabled={saving} className="btn-primary disabled:opacity-60">
                {saving ? 'Guardando...' : editingId ? 'Guardar cambios' : 'Crear vuelo'}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* ---- MODAL CONFIRMACIÓN ---- */}
      {confirm !== null && (
        <div className="fixed inset-0 z-50 bg-black/50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-sm p-6 text-center">
            <p className="text-4xl mb-3">⚠️</p>
            <h3 className="text-lg font-semibold text-gray-900 mb-2">¿Confirmar eliminación?</h3>
            <p className="text-gray-500 text-sm mb-6">Esta acción no se puede deshacer.</p>
            <div className="flex justify-center gap-3">
              <button onClick={() => setConfirm(null)} className="btn-secondary">Cancelar</button>
              <button onClick={() => handleDelete(confirm)} className="btn-danger">Eliminar</button>
            </div>
          </div>
        </div>
      )}
    </main>
  )
}
