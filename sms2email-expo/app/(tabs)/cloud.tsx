import { ScrollView, Text, Pressable, View, Alert } from 'react-native';
import { Card, H3, H4, YStack, XStack, Button } from 'tamagui';
import { Ionicons } from '@expo/vector-icons';
import { useState } from 'react';

const CLOUD_PROVIDERS = [
  {
    id: 'google',
    name: 'Google Drive',
    icon: 'logo-google',
    color: '#4285F4',
    description: 'Sync with Google Drive',
  },
  {
    id: 'microsoft',
    name: 'OneDrive',
    icon: 'logo-microsoft',
    color: '#0078D4',
    description: 'Sync with Microsoft OneDrive',
  },
  {
    id: 'github',
    name: 'GitHub Gist',
    icon: 'logo-github',
    color: '#24292e',
    description: 'Sync with GitHub Gist',
  },
];

export default function CloudScreen() {
  const [selectedProvider, setSelectedProvider] = useState<string | null>(null);
  const [connected, setConnected] = useState(false);

  const handleConnect = (providerId: string) => {
    setSelectedProvider(providerId);
    // Simulate OAuth flow
    setTimeout(() => {
      setConnected(true);
      Alert.alert('Success', 'Connected successfully!');
    }, 1500);
  };

  const handleSync = () => {
    Alert.alert('Syncing', 'Configuration backed up successfully!');
  };

  const handleRestore = () => {
    Alert.alert('Restoring', 'Configuration restored successfully!');
  };

  return (
    <ScrollView className="flex-1 bg-gray-50">
      <YStack padding="$4" space="$4">
        {/* Premium Notice */}
        <Card padding="$4" backgroundColor="$purple2">
          <XStack space="$3" alignItems="flex-start">
            <View className="w-12 h-12 bg-purple-500 rounded-full items-center justify-center">
              <Ionicons name="star" size={24} color="#ffffff" />
            </View>
            <YStack flex={1}>
              <Text className="text-base font-bold text-gray-900">Premium Feature</Text>
              <Text className="text-sm text-gray-600 mt-1">
                Cloud sync requires premium subscription
              </Text>
            </YStack>
          </XStack>
        </Card>

        {/* Cloud Providers */}
        <Card elevate bordered padding="$4" backgroundColor="$background">
          <H3 className="mb-4 text-gray-900">Select Cloud Provider</H3>

          <YStack space="$3">
            {CLOUD_PROVIDERS.map((provider) => (
              <Pressable
                key={provider.id}
                className={`p-4 border-2 rounded-xl ${
                  selectedProvider === provider.id
                    ? 'border-indigo-500 bg-indigo-50'
                    : 'border-gray-200 bg-white'
                }`}
                onPress={() => setSelectedProvider(provider.id)}
              >
                <XStack alignItems="center" justifyContent="space-between">
                  <XStack space="$3" alignItems="center">
                    <View
                      className="w-12 h-12 rounded-xl items-center justify-center"
                      style={{ backgroundColor: provider.color + '20' }}
                    >
                      <Ionicons
                        name={provider.icon as any}
                        size={24}
                        color={provider.color}
                      />
                    </View>
                    <YStack>
                      <Text className="text-base font-semibold text-gray-900">
                        {provider.name}
                      </Text>
                      <Text className="text-sm text-gray-600">
                        {provider.description}
                      </Text>
                    </YStack>
                  </XStack>

                  {selectedProvider === provider.id && (
                    <View className="w-6 h-6 bg-indigo-500 rounded-full items-center justify-center">
                      <Ionicons name="checkmark" size={16} color="#ffffff" />
                    </View>
                  )}
                </XStack>
              </Pressable>
            ))}
          </YStack>

          {selectedProvider && !connected && (
            <Button
              marginTop="$3"
              size="$4"
              theme="blue"
              icon={<Ionicons name="link-outline" size={18} />}
              onPress={() => handleConnect(selectedProvider)}
            >
              Connect to {CLOUD_PROVIDERS.find(p => p.id === selectedProvider)?.name}
            </Button>
          )}
        </Card>

        {/* Connection Status */}
        {connected && (
          <Card elevate bordered padding="$4" backgroundColor="$green2">
            <XStack space="$3" alignItems="center">
              <View className="w-12 h-12 bg-green-500 rounded-full items-center justify-center">
                <Ionicons name="checkmark-circle" size={28} color="#ffffff" />
              </View>
              <YStack flex={1}>
                <Text className="text-base font-bold text-gray-900">Connected</Text>
                <Text className="text-sm text-gray-600">
                  {CLOUD_PROVIDERS.find(p => p.id === selectedProvider)?.name}
                </Text>
              </YStack>
            </XStack>
          </Card>
        )}

        {/* Sync Actions */}
        {connected && (
          <Card elevate bordered padding="$4" backgroundColor="$background">
            <H4 className="mb-3 text-gray-900">Sync Actions</H4>

            <YStack space="$2">
              <Button
                size="$4"
                theme="blue"
                icon={<Ionicons name="cloud-upload-outline" size={18} />}
                onPress={handleSync}
              >
                Backup Configuration
              </Button>

              <Button
                size="$4"
                theme="green"
                icon={<Ionicons name="cloud-download-outline" size={18} />}
                onPress={handleRestore}
              >
                Restore Configuration
              </Button>

              <Button
                size="$4"
                theme="red"
                icon={<Ionicons name="unlink-outline" size={18} />}
                onPress={() => {
                  setConnected(false);
                  setSelectedProvider(null);
                }}
              >
                Disconnect
              </Button>
            </YStack>
          </Card>
        )}

        {/* Sync History */}
        <Card elevate bordered padding="$4" backgroundColor="$background">
          <H4 className="mb-3 text-gray-900">Recent Activity</H4>

          <YStack space="$3">
            <XStack alignItems="center" justifyContent="space-between" padding="$3" backgroundColor="$gray2" borderRadius="$3">
              <YStack flex={1}>
                <Text className="text-sm font-medium text-gray-900">Last Backup</Text>
                <Text className="text-xs text-gray-600">Never</Text>
              </YStack>
              <Ionicons name="time-outline" size={20} color="#9ca3af" />
            </XStack>

            <XStack alignItems="center" justifyContent="space-between" padding="$3" backgroundColor="$gray2" borderRadius="$3">
              <YStack flex={1}>
                <Text className="text-sm font-medium text-gray-900">Last Restore</Text>
                <Text className="text-xs text-gray-600">Never</Text>
              </YStack>
              <Ionicons name="time-outline" size={20} color="#9ca3af" />
            </XStack>
          </YStack>
        </Card>

        {/* Auto Sync */}
        <Card elevate bordered padding="$4" backgroundColor="$background">
          <XStack alignItems="center" justifyContent="space-between">
            <YStack flex={1}>
              <Text className="text-base font-medium text-gray-900">Auto Backup</Text>
              <Text className="text-sm text-gray-600 mt-1">
                Automatically backup every week
              </Text>
            </YStack>
            <Ionicons name="toggle-outline" size={32} color="#6366f1" />
          </XStack>
        </Card>
      </YStack>
    </ScrollView>
  );
}
