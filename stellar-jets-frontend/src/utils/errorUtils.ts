/**
 * Extrae el mensaje de error de una respuesta de Axios/backend.
 * Normaliza el patrón repetido en todas las páginas.
 */
export function getErrorMessage(err: unknown, fallback = 'Ocurrió un error. Intentá de nuevo.'): string {
  const response = (err as { response?: { data?: { message?: string } } })?.response
  return response?.data?.message ?? fallback
}
