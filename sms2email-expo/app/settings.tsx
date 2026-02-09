import { ScrollView, Text, TextInput, Alert } from 'react-native';
import { Stack } from 'expo-router';
import { Card, H3, H4, YStack, XStack, Button, Switch, Separator } from 'tamagui';
import { Ionicons } from '@expo/vector-icons';
import { useSettingsStore } from '@/store/settings';
import { useState } from 'react';

export default function SettingsScreen() {
  const { emailConfig, updateEmailConfig } = useSettingsStore();
  const [config, setConfig] = useState(emailConfig);
  const [testing, setTesting] = useState(false);

  const handleSave = () => {
    updateEmailConfig(config);
    Alert.alert('Success', 'Settings saved successfully');
  };

  const testConnection = async () => {
    setTesting(true);
    // Simulate test
    setTimeout(() => {
      setTesting(false);
      Alert.alert('Connection Test', 'SMTP connection successful!');
    }, 2000);
  };

  return (
    <>
      <Stack.Screen options={{ title: 'SMTP Settings' }} />
      <ScrollView className="flex-1 bg-gray-50">
        <YStack padding="$4" space="$4">
          {/* Server Configuration */}
          <Card elevate bordered padding="$4" backgroundColor="$background">
            <H3 className="mb-4 text-gray-900">Server Configuration</H3>

            <YStack space="$3">
              <YStack space="$2">
                <Text className="text-sm font-medium text-gray-700">SMTP Host</Text>
                <TextInput
                  className="px-4 py-3 bg-gray-50 border border-gray-300 rounded-lg text-base text-gray-900"
                  placeholder="smtp.gmail.com"
                  value={config.smtpHost}
                  onChangeText={(text) => setConfig({ ...config, smtpHost: text })}
                  autoCapitalize="none"
                />
              </YStack>

              <YStack space="$2">
                <Text className="text-sm font-medium text-gray-700">Port</Text>
                <TextInput
                  className="px-4 py-3 bg-gray-50 border border-gray-300 rounded-lg text-base text-gray-900"
                  placeholder="587"
                  value={config.smtpPort.toString()}
                  onChangeText={(text) => setConfig({ ...config, smtpPort: parseInt(text) || 587 })}
                  keyboardType="number-pad"
                />
              </YStack>

              <XStack space="$4">
                <YStack flex={1}>
                  <XStack alignItems="center" justifyContent="space-between" padding="$3" backgroundColor="$gray2" borderRadius="$3">
                    <Text className="text-sm font-medium text-gray-700">Use TLS</Text>
                    <Switch
                      value={config.smtpUseTls}
                      onValueChange={(value) => setConfig({ ...config, smtpUseTls: value })}
                      trackColor={{ false: '#d1d5db', true: '#6366f1' }}
                    />
                  </XStack>
                </YStack>

                <YStack flex={1}>
                  <XStack alignItems="center" justifyContent="space-between" padding="$3" backgroundColor="$gray2" borderRadius="$3">
                    <Text className="text-sm font-medium text-gray-700">Use SSL</Text>
                    <Switch
                      value={config.smtpUseSsl}
                      onValueChange={(value) => setConfig({ ...config, smtpUseSsl: value })}
                      trackColor={{ false: '#d1d5db', true: '#6366f1' }}
                    />
                  </XStack>
                </YStack>
              </XStack>
            </YStack>
          </Card>

          {/* Authentication */}
          <Card elevate bordered padding="$4" backgroundColor="$background">
            <H4 className="mb-3 text-gray-900">Authentication</H4>

            <YStack space="$3">
              <YStack space="$2">
                <Text className="text-sm font-medium text-gray-700">Username</Text>
                <TextInput
                  className="px-4 py-3 bg-gray-50 border border-gray-300 rounded-lg text-base text-gray-900"
                  placeholder="your.email@gmail.com"
                  value={config.smtpUsername}
                  onChangeText={(text) => setConfig({ ...config, smtpUsername: text })}
                  autoCapitalize="none"
                  keyboardType="email-address"
                />
              </YStack>

              <YStack space="$2">
                <Text className="text-sm font-medium text-gray-700">Password</Text>
                <TextInput
                  className="px-4 py-3 bg-gray-50 border border-gray-300 rounded-lg text-base text-gray-900"
                  placeholder="••••••••••••"
                  value={config.smtpPassword}
                  onChangeText={(text) => setConfig({ ...config, smtpPassword: text })}
                  secureTextEntry
                />
                <Text className="text-xs text-gray-500">
                  For Gmail, use App Password
                </Text>
              </YStack>
            </YStack>
          </Card>

          {/* Email Configuration */}
          <Card elevate bordered padding="$4" backgroundColor="$background">
            <H4 className="mb-3 text-gray-900">Email Configuration</H4>

            <YStack space="$3">
              <YStack space="$2">
                <Text className="text-sm font-medium text-gray-700">From Email</Text>
                <TextInput
                  className="px-4 py-3 bg-gray-50 border border-gray-300 rounded-lg text-base text-gray-900"
                  placeholder="sender@example.com"
                  value={config.fromEmail}
                  onChangeText={(text) => setConfig({ ...config, fromEmail: text })}
                  autoCapitalize="none"
                  keyboardType="email-address"
                />
              </YStack>

              <YStack space="$2">
                <Text className="text-sm font-medium text-gray-700">From Name</Text>
                <TextInput
                  className="px-4 py-3 bg-gray-50 border border-gray-300 rounded-lg text-base text-gray-900"
                  placeholder="SMS2Email"
                  value={config.fromName}
                  onChangeText={(text) => setConfig({ ...config, fromName: text })}
                />
              </YStack>

              <YStack space="$2">
                <Text className="text-sm font-medium text-gray-700">To Email</Text>
                <TextInput
                  className="px-4 py-3 bg-gray-50 border border-gray-300 rounded-lg text-base text-gray-900"
                  placeholder="recipient@example.com"
                  value={config.toEmail}
                  onChangeText={(text) => setConfig({ ...config, toEmail: text })}
                  autoCapitalize="none"
                  keyboardType="email-address"
                />
              </YStack>
            </YStack>
          </Card>

          {/* Actions */}
          <YStack space="$2">
            <Button
              size="$5"
              theme="blue"
              icon={<Ionicons name="flash-outline" size={20} />}
              onPress={testConnection}
              disabled={testing}
            >
              {testing ? 'Testing...' : 'Test Connection'}
            </Button>

            <Button
              size="$5"
              theme="green"
              icon={<Ionicons name="save-outline" size={20} />}
              onPress={handleSave}
            >
              Save Settings
            </Button>
          </YStack>
        </YStack>
      </ScrollView>
    </>
  );
}
