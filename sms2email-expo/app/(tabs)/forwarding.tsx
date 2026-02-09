import { ScrollView, Text, View, TextInput } from 'react-native';
import { Card, H3, H4, YStack, XStack, Switch, Button, Separator } from 'tamagui';
import { Ionicons } from '@expo/vector-icons';
import { useState } from 'react';

export default function ForwardingScreen() {
  const [singleMode, setSingleMode] = useState(true);
  const [phoneNumbers, setPhoneNumbers] = useState<string[]>(['']);
  const [segmentEnabled, setSegmentEnabled] = useState(true);
  const [maxSegments, setMaxSegments] = useState('5');

  const addPhoneNumber = () => {
    setPhoneNumbers([...phoneNumbers, '']);
  };

  return (
    <ScrollView className="flex-1 bg-gray-50">
      <YStack padding="$4" space="$4">
        {/* SMS Forwarding Mode */}
        <Card elevate bordered padding="$4" backgroundColor="$background">
          <H3 className="mb-4 text-gray-900">SMS Forwarding Mode</H3>

          <YStack space="$3">
            <XStack
              padding="$3"
              backgroundColor={singleMode ? '$blue2' : '$background'}
              borderRadius="$3"
              borderWidth={1}
              borderColor={singleMode ? '$blue8' : '$borderColor'}
              alignItems="center"
              justifyContent="space-between"
              pressStyle={{ opacity: 0.8 }}
              onPress={() => setSingleMode(true)}
            >
              <XStack space="$3" alignItems="center">
                <View className={`w-12 h-12 rounded-full items-center justify-center ${singleMode ? 'bg-indigo-500' : 'bg-gray-200'}`}>
                  <Ionicons name="person" size={24} color={singleMode ? '#ffffff' : '#6b7280'} />
                </View>
                <YStack>
                  <Text className="text-base font-semibold text-gray-900">Single Recipient</Text>
                  <Text className="text-sm text-gray-600">Forward to one email</Text>
                </YStack>
              </XStack>
              <View className={`w-6 h-6 rounded-full border-2 items-center justify-center ${singleMode ? 'border-indigo-500' : 'border-gray-300'}`}>
                {singleMode && <View className="w-3 h-3 rounded-full bg-indigo-500" />}
              </View>
            </XStack>

            <XStack
              padding="$3"
              backgroundColor={!singleMode ? '$blue2' : '$background'}
              borderRadius="$3"
              borderWidth={1}
              borderColor={!singleMode ? '$blue8' : '$borderColor'}
              alignItems="center"
              justifyContent="space-between"
              pressStyle={{ opacity: 0.8 }}
              onPress={() => setSingleMode(false)}
            >
              <XStack space="$3" alignItems="center">
                <View className={`w-12 h-12 rounded-full items-center justify-center ${!singleMode ? 'bg-purple-500' : 'bg-gray-200'}`}>
                  <Ionicons name="people" size={24} color={!singleMode ? '#ffffff' : '#6b7280'} />
                </View>
                <YStack>
                  <Text className="text-base font-semibold text-gray-900">Multiple Recipients</Text>
                  <Text className="text-sm text-gray-600">Forward to multiple emails</Text>
                </YStack>
              </XStack>
              <View className={`w-6 h-6 rounded-full border-2 items-center justify-center ${!singleMode ? 'border-purple-500' : 'border-gray-300'}`}>
                {!singleMode && <View className="w-3 h-3 rounded-full bg-purple-500" />}
              </View>
            </XStack>
          </YStack>
        </Card>

        {/* Phone Numbers */}
        {!singleMode && (
          <Card elevate bordered padding="$4" backgroundColor="$background">
            <XStack alignItems="center" justifyContent="space-between" marginBottom="$3">
              <H4 className="text-gray-900">Recipient Emails</H4>
              <Button
                size="$3"
                theme="blue"
                icon={<Ionicons name="add" size={16} />}
                onPress={addPhoneNumber}
              >
                Add
              </Button>
            </XStack>

            <YStack space="$2">
              {phoneNumbers.map((number, index) => (
                <XStack key={index} space="$2" alignItems="center">
                  <TextInput
                    className="flex-1 px-4 py-3 bg-gray-50 border border-gray-300 rounded-lg text-base text-gray-900"
                    placeholder="email@example.com"
                    placeholderTextColor="#9ca3af"
                    value={number}
                    onChangeText={(text) => {
                      const newNumbers = [...phoneNumbers];
                      newNumbers[index] = text;
                      setPhoneNumbers(newNumbers);
                    }}
                    keyboardType="email-address"
                    autoCapitalize="none"
                  />
                  {phoneNumbers.length > 1 && (
                    <Button
                      size="$3"
                      theme="red"
                      circular
                      icon={<Ionicons name="trash-outline" size={16} />}
                      onPress={() => {
                        setPhoneNumbers(phoneNumbers.filter((_, i) => i !== index));
                      }}
                    />
                  )}
                </XStack>
              ))}
            </YStack>
          </Card>
        )}

        {/* SMS Segmentation */}
        <Card elevate bordered padding="$4" backgroundColor="$background">
          <H4 className="mb-3 text-gray-900">SMS Segmentation</H4>

          <YStack space="$3">
            <XStack alignItems="center" justifyContent="space-between">
              <YStack flex={1}>
                <Text className="text-base font-medium text-gray-900">Smart Segmentation</Text>
                <Text className="text-sm text-gray-600 mt-1">
                  Split long messages intelligently
                </Text>
              </YStack>
              <Switch
                value={segmentEnabled}
                onValueChange={setSegmentEnabled}
                trackColor={{ false: '#d1d5db', true: '#6366f1' }}
                thumbColor={segmentEnabled ? '#ffffff' : '#f3f4f6'}
              />
            </XStack>

            {segmentEnabled && (
              <>
                <Separator />
                <YStack space="$2">
                  <Text className="text-sm font-medium text-gray-700">
                    Maximum Segments per Email
                  </Text>
                  <TextInput
                    className="px-4 py-3 bg-gray-50 border border-gray-300 rounded-lg text-base text-gray-900"
                    placeholder="5"
                    value={maxSegments}
                    onChangeText={setMaxSegments}
                    keyboardType="number-pad"
                  />
                  <Text className="text-xs text-gray-500">
                    Recommended: 3-10 segments
                  </Text>
                </YStack>
              </>
            )}
          </YStack>
        </Card>

        {/* Retry Policy */}
        <Card elevate bordered padding="$4" backgroundColor="$background">
          <H4 className="mb-3 text-gray-900">Retry Policy</H4>

          <YStack space="$3">
            <XStack alignItems="center" justifyContent="space-between" padding="$3" backgroundColor="$gray2" borderRadius="$3">
              <Text className="text-base text-gray-900">Max Retry Attempts</Text>
              <Text className="text-base font-bold text-indigo-600">3</Text>
            </XStack>

            <XStack alignItems="center" justifyContent="space-between" padding="$3" backgroundColor="$gray2" borderRadius="$3">
              <Text className="text-base text-gray-900">Retry Interval</Text>
              <Text className="text-base font-bold text-indigo-600">5 min</Text>
            </XStack>
          </YStack>
        </Card>

        {/* Save Button */}
        <Button
          size="$5"
          theme="blue"
          icon={<Ionicons name="save-outline" size={20} />}
        >
          Save Settings
        </Button>
      </YStack>
    </ScrollView>
  );
}
