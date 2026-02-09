import { ScrollView, View, Text, Switch, Pressable } from 'react-native';
import { Link } from 'expo-router';
import { Button, Card, H2, H4, Paragraph, YStack, XStack, Separator } from 'tamagui';
import { Ionicons } from '@expo/vector-icons';
import { useSettingsStore } from '@/store/settings';

export default function HomeScreen() {
  const { emailConfig, updateEmailConfig } = useSettingsStore();
  const isEnabled = emailConfig.enabled;

  const toggleService = () => {
    updateEmailConfig({ enabled: !isEnabled });
  };

  return (
    <ScrollView className="flex-1 bg-gray-50">
      <YStack padding="$4" space="$4">
        {/* Status Card */}
        <Card
          elevate
          bordered
          padding="$4"
          backgroundColor="$background"
          className="shadow-sm"
        >
          <YStack space="$3">
            <XStack alignItems="center" justifyContent="space-between">
              <View>
                <H2 className="text-gray-900">Service Status</H2>
                <Paragraph className="text-gray-600 mt-1">
                  {isEnabled ? 'SMS forwarding is active' : 'SMS forwarding is disabled'}
                </Paragraph>
              </View>
              <View className={`w-16 h-16 rounded-full items-center justify-center ${isEnabled ? 'bg-green-100' : 'bg-gray-100'}`}>
                <Ionicons
                  name={isEnabled ? 'checkmark-circle' : 'close-circle'}
                  size={40}
                  color={isEnabled ? '#22c55e' : '#9ca3af'}
                />
              </View>
            </XStack>

            <Separator />

            <XStack alignItems="center" justifyContent="space-between">
              <Text className="text-base font-medium text-gray-700">
                Enable SMS Forwarding
              </Text>
              <Switch
                value={isEnabled}
                onValueChange={toggleService}
                trackColor={{ false: '#d1d5db', true: '#6366f1' }}
                thumbColor={isEnabled ? '#ffffff' : '#f3f4f6'}
              />
            </XStack>
          </YStack>
        </Card>

        {/* Quick Actions */}
        <Card
          elevate
          bordered
          padding="$4"
          backgroundColor="$background"
        >
          <H4 className="mb-3 text-gray-900">Quick Actions</H4>

          <YStack space="$2">
            <Link href="/settings" asChild>
              <Pressable className="flex-row items-center justify-between p-4 bg-white border border-gray-200 rounded-lg active:bg-gray-50">
                <XStack space="$3" alignItems="center">
                  <View className="w-10 h-10 bg-indigo-100 rounded-full items-center justify-center">
                    <Ionicons name="settings-outline" size={20} color="#6366f1" />
                  </View>
                  <Text className="text-base font-medium text-gray-900">
                    SMTP Settings
                  </Text>
                </XStack>
                <Ionicons name="chevron-forward" size={20} color="#9ca3af" />
              </Pressable>
            </Link>

            <Link href="/template" asChild>
              <Pressable className="flex-row items-center justify-between p-4 bg-white border border-gray-200 rounded-lg active:bg-gray-50">
                <XStack space="$3" alignItems="center">
                  <View className="w-10 h-10 bg-purple-100 rounded-full items-center justify-center">
                    <Ionicons name="document-text-outline" size={20} color="#a855f7" />
                  </View>
                  <Text className="text-base font-medium text-gray-900">
                    Email Template
                  </Text>
                </XStack>
                <Ionicons name="chevron-forward" size={20} color="#9ca3af" />
              </Pressable>
            </Link>

            <Link href="/premium" asChild>
              <Pressable className="flex-row items-center justify-between p-4 bg-gradient-to-r from-indigo-500 to-purple-600 rounded-lg active:opacity-90">
                <XStack space="$3" alignItems="center">
                  <View className="w-10 h-10 bg-white/20 rounded-full items-center justify-center">
                    <Ionicons name="star" size={20} color="#ffffff" />
                  </View>
                  <Text className="text-base font-bold text-white">
                    Upgrade to Premium
                  </Text>
                </XStack>
                <Ionicons name="chevron-forward" size={20} color="#ffffff" />
              </Pressable>
            </Link>
          </YStack>
        </Card>

        {/* Stats Card */}
        <Card
          elevate
          bordered
          padding="$4"
          backgroundColor="$background"
        >
          <H4 className="mb-3 text-gray-900">Statistics</H4>

          <XStack space="$4">
            <YStack flex={1} backgroundColor="$blue2" padding="$3" borderRadius="$4">
              <Text className="text-2xl font-bold text-indigo-600">0</Text>
              <Text className="text-sm text-gray-600">Messages Today</Text>
            </YStack>

            <YStack flex={1} backgroundColor="$green2" padding="$3" borderRadius="$4">
              <Text className="text-2xl font-bold text-green-600">0</Text>
              <Text className="text-sm text-gray-600">This Week</Text>
            </YStack>
          </XStack>
        </Card>

        {/* Configuration Export/Import */}
        <Card
          elevate
          bordered
          padding="$4"
          backgroundColor="$background"
        >
          <H4 className="mb-3 text-gray-900">Configuration</H4>

          <XStack space="$2">
            <Button flex={1} theme="blue" icon={<Ionicons name="download-outline" size={18} />}>
              Export
            </Button>
            <Button flex={1} theme="green" icon={<Ionicons name="cloud-upload-outline" size={18} />}>
              Import
            </Button>
          </XStack>
        </Card>

        {/* Info */}
        <Card padding="$4" backgroundColor="$blue2">
          <XStack space="$3" alignItems="flex-start">
            <Ionicons name="information-circle" size={24} color="#6366f1" />
            <YStack flex={1}>
              <Text className="text-sm font-medium text-gray-900">
                SMS2Email v2.0.0
              </Text>
              <Text className="text-xs text-gray-600 mt-1">
                Developed by Flumenis LLC, Delaware
              </Text>
            </YStack>
          </XStack>
        </Card>
      </YStack>
    </ScrollView>
  );
}
