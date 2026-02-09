import { ScrollView, Text, View, TextInput, Alert } from 'react-native';
import { Stack } from 'expo-router';
import { Card, H2, H4, YStack, XStack, Button, Separator } from 'tamagui';
import { Ionicons } from '@expo/vector-icons';
import { useState } from 'react';

const PREMIUM_FEATURES = [
  { icon: 'infinite', title: 'Unlimited SMS Forwarding', description: 'No daily limits' },
  { icon: 'call-outline', title: 'Call Log Monitoring', description: 'Track incoming/outgoing calls' },
  { icon: 'filter-outline', title: 'Advanced Filters', description: 'Regex & complex rules' },
  { icon: 'people-outline', title: 'Multiple Recipients', description: 'Send to many emails' },
  { icon: 'cloud-outline', title: 'Cloud Backup', description: 'Auto-sync configurations' },
  { icon: 'headset-outline', title: 'Priority Support', description: '24/7 email & chat support' },
];

const DEFAULT_CODES = ['ONEDAY', 'FLUMENIS', 'WELCOME2025'];

export default function PremiumScreen() {
  const [inviteCode, setInviteCode] = useState('');
  const [activating, setActivating] = useState(false);

  const activateCode = () => {
    if (!inviteCode) {
      Alert.alert('Error', 'Please enter an invite code');
      return;
    }

    setActivating(true);

    // Check if it's a default code
    if (DEFAULT_CODES.includes(inviteCode.toUpperCase())) {
      setTimeout(() => {
        setActivating(false);
        Alert.alert(
          'Success!',
          `Premium activated with code: ${inviteCode.toUpperCase()}\n\nWelcome to SMS2Email Premium!`,
          [{ text: 'OK' }]
        );
        setInviteCode('');
      }, 1500);
    } else {
      setTimeout(() => {
        setActivating(false);
        Alert.alert('Invalid Code', 'Please check your invite code and try again');
      }, 1500);
    }
  };

  return (
    <>
      <Stack.Screen options={{ title: 'Premium' }} />
      <ScrollView className="flex-1 bg-gradient-to-b from-indigo-50 to-purple-50">
        <YStack padding="$4" space="$4">
          {/* Hero */}
          <Card elevate padding="$6" backgroundColor="$purple9" alignItems="center">
            <View className="w-20 h-20 bg-white/20 rounded-full items-center justify-center mb-4">
              <Ionicons name="star" size={48} color="#ffffff" />
            </View>

            <H2 className="text-white text-center">SMS2Email Premium</H2>
            <Text className="text-base text-white/80 text-center mt-2">
              Unlock all features and enjoy unlimited SMS forwarding
            </Text>
          </Card>

          {/* Features Grid */}
          <Card elevate bordered padding="$4" backgroundColor="$background">
            <H4 className="mb-4 text-gray-900">Premium Features</H4>

            <YStack space="$3">
              {PREMIUM_FEATURES.map((feature, index) => (
                <XStack
                  key={index}
                  space="$3"
                  padding="$3"
                  backgroundColor="$gray1"
                  borderRadius="$3"
                  alignItems="flex-start"
                >
                  <View className="w-10 h-10 bg-gradient-to-br from-indigo-500 to-purple-600 rounded-full items-center justify-center">
                    <Ionicons name={feature.icon as any} size={20} color="#ffffff" />
                  </View>
                  <YStack flex={1}>
                    <Text className="text-base font-semibold text-gray-900">
                      {feature.title}
                    </Text>
                    <Text className="text-sm text-gray-600 mt-0.5">
                      {feature.description}
                    </Text>
                  </YStack>
                  <Ionicons name="checkmark-circle" size={20} color="#22c55e" />
                </XStack>
              ))}
            </YStack>
          </Card>

          {/* Activate with Invite Code */}
          <Card elevate bordered padding="$4" backgroundColor="$background">
            <H4 className="mb-3 text-gray-900">Activate Premium</H4>

            <YStack space="$3">
              <YStack space="$2">
                <Text className="text-sm font-medium text-gray-700">
                  Enter Invite Code
                </Text>
                <TextInput
                  className="px-4 py-3 bg-gray-50 border-2 border-indigo-200 rounded-lg text-base text-gray-900 font-mono"
                  placeholder="XXXXX-XXXXXXXX-XXXX"
                  value={inviteCode}
                  onChangeText={setInviteCode}
                  autoCapitalize="characters"
                />
                <Text className="text-xs text-gray-500">
                  Format: CODE-YYYYMMDD-SIGNATURE
                </Text>
              </YStack>

              <Button
                size="$5"
                theme="purple"
                icon={<Ionicons name="key-outline" size={20} />}
                onPress={activateCode}
                disabled={activating}
              >
                {activating ? 'Activating...' : 'Activate Premium'}
              </Button>

              <Separator />

              {/* Default Codes Hint */}
              <Card padding="$3" backgroundColor="$blue2">
                <XStack space="$2" alignItems="flex-start">
                  <Ionicons name="bulb-outline" size={20} color="#6366f1" />
                  <YStack flex={1}>
                    <Text className="text-sm font-medium text-gray-900">
                      Try these codes:
                    </Text>
                    <Text className="text-xs text-gray-600 mt-1 font-mono">
                      ONEDAY • FLUMENIS • WELCOME2025
                    </Text>
                  </YStack>
                </XStack>
              </Card>
            </YStack>
          </Card>

          {/* Pricing (Future) */}
          <Card elevate bordered padding="$4" backgroundColor="$background">
            <H4 className="mb-3 text-gray-900">Subscription Plans</H4>

            <YStack space="$3">
              <XStack space="$3">
                <YStack
                  flex={1}
                  padding="$4"
                  backgroundColor="$blue2"
                  borderRadius="$4"
                  borderWidth={2}
                  borderColor="$blue8"
                >
                  <Text className="text-2xl font-bold text-indigo-600">$2.99</Text>
                  <Text className="text-sm text-gray-600">per month</Text>
                  <Text className="text-xs text-gray-500 mt-1">Billed monthly</Text>
                </YStack>

                <YStack
                  flex={1}
                  padding="$4"
                  backgroundColor="$purple2"
                  borderRadius="$4"
                  borderWidth={2}
                  borderColor="$purple8"
                >
                  <XStack alignItems="baseline" space="$1">
                    <Text className="text-2xl font-bold text-purple-600">$24.99</Text>
                    <Text className="text-xs line-through text-gray-400">$35.88</Text>
                  </XStack>
                  <Text className="text-sm text-gray-600">per year</Text>
                  <Text className="text-xs text-green-600 font-semibold mt-1">
                    Save 30%
                  </Text>
                </YStack>
              </XStack>

              <Button size="$5" theme="blue" disabled>
                Coming Soon
              </Button>
            </YStack>
          </Card>

          {/* Free vs Premium */}
          <Card elevate bordered padding="$4" backgroundColor="$background">
            <H4 className="mb-3 text-gray-900">Free vs Premium</H4>

            <YStack space="$2">
              <XStack alignItems="center" justifyContent="space-between" padding="$3" backgroundColor="$gray1" borderRadius="$3">
                <Text className="text-sm text-gray-900">Daily SMS Limit</Text>
                <XStack space="$4">
                  <Text className="text-sm text-gray-600">50</Text>
                  <Text className="text-sm font-bold text-indigo-600">Unlimited</Text>
                </XStack>
              </XStack>

              <XStack alignItems="center" justifyContent="space-between" padding="$3" backgroundColor="$gray1" borderRadius="$3">
                <Text className="text-sm text-gray-900">Call Log Monitoring</Text>
                <XStack space="$4">
                  <Ionicons name="close-circle" size={16} color="#ef4444" />
                  <Ionicons name="checkmark-circle" size={16} color="#22c55e" />
                </XStack>
              </XStack>

              <XStack alignItems="center" justifyContent="space-between" padding="$3" backgroundColor="$gray1" borderRadius="$3">
                <Text className="text-sm text-gray-900">Cloud Sync</Text>
                <XStack space="$4">
                  <Ionicons name="close-circle" size={16} color="#ef4444" />
                  <Ionicons name="checkmark-circle" size={16} color="#22c55e" />
                </XStack>
              </XStack>

              <XStack alignItems="center" justifyContent="space-between" padding="$3" backgroundColor="$gray1" borderRadius="$3">
                <Text className="text-sm text-gray-900">Priority Support</Text>
                <XStack space="$4">
                  <Ionicons name="close-circle" size={16} color="#ef4444" />
                  <Ionicons name="checkmark-circle" size={16} color="#22c55e" />
                </XStack>
              </XStack>
            </YStack>
          </Card>
        </YStack>
      </ScrollView>
    </>
  );
}
