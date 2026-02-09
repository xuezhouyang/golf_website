import { ScrollView, Text, Linking, Pressable } from 'react-native';
import { Card, H2, H4, YStack, XStack, Separator, Button } from 'tamagui';
import { Ionicons } from '@expo/vector-icons';

const FEATURES = [
  { icon: 'phone-portrait-outline', title: 'Dual SIM Support', description: 'Automatically detect both SIM cards' },
  { icon: 'mail-outline', title: 'Custom SMTP', description: 'Use your own email server' },
  { icon: 'document-text-outline', title: 'Template System', description: 'Customizable email templates' },
  { icon: 'cloud-upload-outline', title: 'Cloud Sync', description: 'Backup and restore configurations' },
  { icon: 'shield-checkmark-outline', title: 'Secure', description: 'No third-party servers' },
  { icon: 'infinite-outline', title: 'Unlimited', description: 'No message limits' },
];

export default function AboutScreen() {
  const openURL = (url: string) => {
    Linking.openURL(url);
  };

  return (
    <ScrollView className="flex-1 bg-gray-50">
      <YStack padding="$4" space="$4">
        {/* App Info */}
        <Card elevate bordered padding="$4" backgroundColor="$background" alignItems="center">
          <View className="w-24 h-24 bg-indigo-500 rounded-3xl items-center justify-center mb-4">
            <Ionicons name="mail" size={48} color="#ffffff" />
          </View>

          <H2 className="text-gray-900">SMS2Email</H2>
          <Text className="text-base text-gray-600 mt-1">Version 2.0.0</Text>
          <Text className="text-sm text-gray-500 mt-2">
            Forward SMS messages to your email
          </Text>
        </Card>

        {/* Features */}
        <Card elevate bordered padding="$4" backgroundColor="$background">
          <H4 className="mb-3 text-gray-900">Features</H4>

          <YStack space="$3">
            {FEATURES.map((feature, index) => (
              <XStack key={index} space="$3" alignItems="flex-start">
                <View className="w-10 h-10 bg-indigo-100 rounded-full items-center justify-center">
                  <Ionicons name={feature.icon as any} size={20} color="#6366f1" />
                </View>
                <YStack flex={1}>
                  <Text className="text-base font-medium text-gray-900">
                    {feature.title}
                  </Text>
                  <Text className="text-sm text-gray-600 mt-0.5">
                    {feature.description}
                  </Text>
                </YStack>
              </XStack>
            ))}
          </YStack>
        </Card>

        {/* Company Info */}
        <Card elevate bordered padding="$4" backgroundColor="$background">
          <H4 className="mb-3 text-gray-900">Developer</H4>

          <YStack space="$3">
            <XStack alignItems="center" justifyContent="space-between">
              <Text className="text-base text-gray-700">Company</Text>
              <Text className="text-base font-medium text-gray-900">Flumenis LLC</Text>
            </XStack>

            <Separator />

            <XStack alignItems="center" justifyContent="space-between">
              <Text className="text-base text-gray-700">Location</Text>
              <Text className="text-base font-medium text-gray-900">Delaware, USA</Text>
            </XStack>

            <Separator />

            <XStack alignItems="center" justifyContent="space-between">
              <Text className="text-base text-gray-700">License</Text>
              <Text className="text-base font-medium text-gray-900">Proprietary</Text>
            </XStack>
          </YStack>
        </Card>

        {/* Links */}
        <Card elevate bordered padding="$4" backgroundColor="$background">
          <H4 className="mb-3 text-gray-900">Support & Links</H4>

          <YStack space="$2">
            <Pressable
              className="flex-row items-center justify-between p-3 bg-gray-50 border border-gray-200 rounded-lg active:bg-gray-100"
              onPress={() => openURL('mailto:support@flumenis.com')}
            >
              <XStack space="$3" alignItems="center">
                <Ionicons name="mail-outline" size={20} color="#6366f1" />
                <Text className="text-base text-gray-900">support@flumenis.com</Text>
              </XStack>
              <Ionicons name="open-outline" size={16} color="#9ca3af" />
            </Pressable>

            <Pressable
              className="flex-row items-center justify-between p-3 bg-gray-50 border border-gray-200 rounded-lg active:bg-gray-100"
              onPress={() => openURL('https://flumenis.com')}
            >
              <XStack space="$3" alignItems="center">
                <Ionicons name="globe-outline" size={20} color="#6366f1" />
                <Text className="text-base text-gray-900">flumenis.com</Text>
              </XStack>
              <Ionicons name="open-outline" size={16} color="#9ca3af" />
            </Pressable>

            <Pressable
              className="flex-row items-center justify-between p-3 bg-gray-50 border border-gray-200 rounded-lg active:bg-gray-100"
              onPress={() => openURL('https://github.com/flumenis')}
            >
              <XStack space="$3" alignItems="center">
                <Ionicons name="logo-github" size={20} color="#6366f1" />
                <Text className="text-base text-gray-900">GitHub</Text>
              </XStack>
              <Ionicons name="open-outline" size={16} color="#9ca3af" />
            </Pressable>
          </YStack>
        </Card>

        {/* Legal */}
        <Card padding="$4" backgroundColor="$gray2">
          <Text className="text-xs text-gray-600 text-center">
            © 2025 Flumenis LLC, Delaware{'\n'}
            All rights reserved.{'\n\n'}
            This software is proprietary and confidential.
          </Text>
        </Card>
      </YStack>
    </ScrollView>
  );
}
