import { ScrollView, Text, Pressable, View } from 'react-native';
import { Card, H3, H4, YStack, XStack } from 'tamagui';
import { Ionicons } from '@expo/vector-icons';
import { useState } from 'react';

const THEME_MODES = [
  { id: 'light', name: 'Light', icon: 'sunny', description: 'Always use light theme' },
  { id: 'dark', name: 'Dark', icon: 'moon', description: 'Always use dark theme' },
  { id: 'auto', name: 'Auto', icon: 'contrast', description: 'Follow system theme' },
];

const ACCENT_COLORS = [
  { id: 'indigo', name: 'Indigo', color: '#6366f1' },
  { id: 'purple', name: 'Purple', color: '#a855f7' },
  { id: 'pink', name: 'Pink', color: '#ec4899' },
  { id: 'blue', name: 'Blue', color: '#3b82f6' },
  { id: 'green', name: 'Green', color: '#22c55e' },
  { id: 'orange', name: 'Orange', color: '#f97316' },
];

export default function ThemeScreen() {
  const [themeMode, setThemeMode] = useState('auto');
  const [accentColor, setAccentColor] = useState('indigo');
  const [dynamicColor, setDynamicColor] = useState(true);

  return (
    <ScrollView className="flex-1 bg-gray-50">
      <YStack padding="$4" space="$4">
        {/* Theme Mode */}
        <Card elevate bordered padding="$4" backgroundColor="$background">
          <H3 className="mb-4 text-gray-900">Theme Mode</H3>

          <YStack space="$3">
            {THEME_MODES.map((mode) => (
              <Pressable
                key={mode.id}
                className={`p-4 border-2 rounded-xl ${
                  themeMode === mode.id
                    ? 'border-indigo-500 bg-indigo-50'
                    : 'border-gray-200 bg-white'
                }`}
                onPress={() => setThemeMode(mode.id)}
              >
                <XStack alignItems="center" justifyContent="space-between">
                  <XStack space="$3" alignItems="center">
                    <View
                      className={`w-12 h-12 rounded-full items-center justify-center ${
                        themeMode === mode.id ? 'bg-indigo-500' : 'bg-gray-200'
                      }`}
                    >
                      <Ionicons
                        name={mode.icon as any}
                        size={24}
                        color={themeMode === mode.id ? '#ffffff' : '#6b7280'}
                      />
                    </View>
                    <YStack>
                      <Text className="text-base font-semibold text-gray-900">
                        {mode.name}
                      </Text>
                      <Text className="text-sm text-gray-600">
                        {mode.description}
                      </Text>
                    </YStack>
                  </XStack>

                  {themeMode === mode.id && (
                    <View className="w-6 h-6 bg-indigo-500 rounded-full items-center justify-center">
                      <Ionicons name="checkmark" size={16} color="#ffffff" />
                    </View>
                  )}
                </XStack>
              </Pressable>
            ))}
          </YStack>
        </Card>

        {/* Accent Color */}
        <Card elevate bordered padding="$4" backgroundColor="$background">
          <H4 className="mb-4 text-gray-900">Accent Color</H4>

          <YStack space="$4">
            {/* Dynamic Color Toggle */}
            <XStack alignItems="center" justifyContent="space-between" padding="$3" backgroundColor="$gray2" borderRadius="$3">
              <YStack flex={1}>
                <Text className="text-base font-medium text-gray-900">
                  Dynamic Color
                </Text>
                <Text className="text-sm text-gray-600">
                  Use wallpaper colors (Android 12+)
                </Text>
              </YStack>
              <Pressable onPress={() => setDynamicColor(!dynamicColor)}>
                <Ionicons
                  name={dynamicColor ? 'toggle' : 'toggle-outline'}
                  size={32}
                  color={dynamicColor ? '#6366f1' : '#9ca3af'}
                />
              </Pressable>
            </XStack>

            {/* Color Grid */}
            {!dynamicColor && (
              <View className="flex-row flex-wrap gap-3">
                {ACCENT_COLORS.map((color) => (
                  <Pressable
                    key={color.id}
                    className={`flex-1 min-w-[30%] p-4 rounded-xl border-2 ${
                      accentColor === color.id
                        ? 'border-gray-900'
                        : 'border-gray-200'
                    }`}
                    style={{ backgroundColor: color.color + '20' }}
                    onPress={() => setAccentColor(color.id)}
                  >
                    <View className="items-center">
                      <View
                        className="w-12 h-12 rounded-full mb-2"
                        style={{ backgroundColor: color.color }}
                      />
                      <Text className="text-sm font-medium text-gray-900">
                        {color.name}
                      </Text>
                      {accentColor === color.id && (
                        <View className="mt-1">
                          <Ionicons name="checkmark-circle" size={16} color={color.color} />
                        </View>
                      )}
                    </View>
                  </Pressable>
                ))}
              </View>
            )}
          </YStack>
        </Card>

        {/* Preview */}
        <Card elevate bordered padding="$4" backgroundColor="$background">
          <H4 className="mb-3 text-gray-900">Preview</H4>

          <YStack space="$3">
            <View className="p-4 bg-indigo-500 rounded-xl">
              <Text className="text-base font-semibold text-white">Primary Button</Text>
            </View>

            <View className="p-4 border-2 border-indigo-500 rounded-xl">
              <Text className="text-base font-semibold text-indigo-600">Outlined Button</Text>
            </View>

            <XStack space="$3">
              <View className="flex-1 p-3 bg-indigo-100 rounded-lg items-center">
                <Ionicons name="mail" size={24} color="#6366f1" />
                <Text className="text-xs text-gray-900 mt-1">Icon</Text>
              </View>
              <View className="flex-1 p-3 bg-indigo-100 rounded-lg items-center">
                <Ionicons name="settings" size={24} color="#6366f1" />
                <Text className="text-xs text-gray-900 mt-1">Icon</Text>
              </View>
              <View className="flex-1 p-3 bg-indigo-100 rounded-lg items-center">
                <Ionicons name="star" size={24} color="#6366f1" />
                <Text className="text-xs text-gray-900 mt-1">Icon</Text>
              </View>
            </XStack>
          </YStack>
        </Card>

        {/* Info */}
        <Card padding="$4" backgroundColor="$blue2">
          <XStack space="$3" alignItems="flex-start">
            <Ionicons name="information-circle" size={24} color="#6366f1" />
            <YStack flex={1}>
              <Text className="text-sm font-medium text-gray-900">
                Theme changes apply instantly
              </Text>
              <Text className="text-xs text-gray-600 mt-1">
                Your preferences are saved automatically
              </Text>
            </YStack>
          </XStack>
        </Card>
      </YStack>
    </ScrollView>
  );
}
