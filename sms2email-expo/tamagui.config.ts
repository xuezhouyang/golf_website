import { config as configBase } from '@tamagui/config/v3'
import { createTamagui } from 'tamagui'

const config = createTamagui({
  ...configBase,
  themes: {
    ...configBase.themes,
    light: {
      ...configBase.themes.light,
      primary: '#6366f1',
      primaryHover: '#4f46e5',
      secondary: '#22c55e',
      background: '#ffffff',
      backgroundHover: '#f9fafb',
      backgroundPress: '#f3f4f6',
      backgroundFocus: '#f3f4f6',
      borderColor: '#e5e7eb',
      borderColorHover: '#d1d5db',
      color: '#111827',
      colorHover: '#374151',
      colorPress: '#1f2937',
      colorFocus: '#1f2937',
    },
    dark: {
      ...configBase.themes.dark,
      primary: '#818cf8',
      primaryHover: '#6366f1',
      secondary: '#4ade80',
      background: '#111827',
      backgroundHover: '#1f2937',
      backgroundPress: '#374151',
      backgroundFocus: '#374151',
      borderColor: '#374151',
      borderColorHover: '#4b5563',
      color: '#f9fafb',
      colorHover: '#f3f4f6',
      colorPress: '#e5e7eb',
      colorFocus: '#e5e7eb',
    },
  },
})

export type AppConfig = typeof config

declare module 'tamagui' {
  interface TamaguiCustomConfig extends AppConfig {}
}

export default config
