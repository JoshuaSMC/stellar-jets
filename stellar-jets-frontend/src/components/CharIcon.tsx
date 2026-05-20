import {
  Wifi, Armchair, Utensils, Tv, Briefcase,
  Wine, Star, Plane, HelpCircle,
} from 'lucide-react'
import type { LucideProps } from 'lucide-react'

export const CHAR_ICON_MAP: Record<string, React.ComponentType<LucideProps>> = {
  wifi: Wifi,
  armchair: Armchair,
  utensils: Utensils,
  tv: Tv,
  briefcase: Briefcase,
  wine: Wine,
  star: Star,
  plane: Plane,
}

export default function CharIcon({
  iconName,
  className = 'w-5 h-5 text-navy-600',
}: {
  iconName: string
  className?: string
}) {
  const Icon = CHAR_ICON_MAP[iconName?.toLowerCase()] ?? HelpCircle
  return <Icon className={className} />
}
