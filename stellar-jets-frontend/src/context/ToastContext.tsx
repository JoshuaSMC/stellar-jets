import { createContext, useContext, useState, useCallback, useRef } from 'react'
import type { ReactNode } from 'react'
import { CheckCircle, XCircle, X } from 'lucide-react'

type ToastType = 'success' | 'error'

interface Toast {
  id: number
  message: string
  type: ToastType
}

interface ToastContextValue {
  success: (message: string) => void
  error: (message: string) => void
}

const ToastContext = createContext<ToastContextValue | null>(null)

export function useToast() {
  const ctx = useContext(ToastContext)
  if (!ctx) throw new Error('useToast must be used within ToastProvider')
  return ctx
}

export function ToastProvider({ children }: { children: ReactNode }) {
  const [toasts, setToasts] = useState<Toast[]>([])
  const counter = useRef(0)

  const addToast = useCallback((message: string, type: ToastType) => {
    const id = ++counter.current
    setToasts(prev => [...prev, { id, message, type }])
    setTimeout(() => {
      setToasts(prev => prev.filter(t => t.id !== id))
    }, 4000)
  }, [])

  const remove = useCallback((id: number) => {
    setToasts(prev => prev.filter(t => t.id !== id))
  }, [])

  const value: ToastContextValue = {
    success: (msg) => addToast(msg, 'success'),
    error: (msg) => addToast(msg, 'error'),
  }

  return (
    <ToastContext.Provider value={value}>
      {children}

      {/* Contenedor de toasts */}
      <div className="fixed top-20 right-4 z-50 flex flex-col gap-2 max-w-sm w-full pointer-events-none">
        {toasts.map(toast => (
          <div
            key={toast.id}
            className={`
              flex items-start gap-3 px-4 py-3 rounded-xl shadow-lg border pointer-events-auto
              animate-[fadeInRight_0.25s_ease-out]
              ${toast.type === 'success'
                ? 'bg-white border-green-200 text-green-800'
                : 'bg-white border-red-200 text-red-700'}
            `}
          >
            {toast.type === 'success'
              ? <CheckCircle className="w-5 h-5 text-green-500 flex-shrink-0 mt-0.5" />
              : <XCircle className="w-5 h-5 text-red-500 flex-shrink-0 mt-0.5" />
            }
            <p className="text-sm flex-1 leading-snug">{toast.message}</p>
            <button
              onClick={() => remove(toast.id)}
              className="text-gray-400 hover:text-gray-600 transition flex-shrink-0"
            >
              <X className="w-4 h-4" />
            </button>
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  )
}
