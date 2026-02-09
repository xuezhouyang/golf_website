import { ScrollView, Text, TextInput, Pressable } from 'react-native';
import { Stack } from 'expo-router';
import { Card, H3, H4, YStack, XStack, Button } from 'tamagui';
import { Ionicons } from '@expo/vector-icons';
import { useSettingsStore } from '@/store/settings';
import { useState } from 'react';

const TEMPLATE_VARIABLES = [
  { name: '{{sender}}', description: 'Sender phone number' },
  { name: '{{message}}', description: 'SMS message content' },
  { name: '{{timestamp}}', description: 'Full date and time' },
  { name: '{{sim_slot}}', description: 'SIM card slot (1 or 2)' },
  { name: '{{carrier_name}}', description: 'Carrier/operator name' },
  { name: '{{date}}', description: 'Date only (YYYY-MM-DD)' },
  { name: '{{time}}', description: 'Time only (HH:mm:ss)' },
];

export default function TemplateScreen() {
  const { emailConfig, updateEmailConfig } = useSettingsStore();
  const [subject, setSubject] = useState(emailConfig.subjectTemplate);
  const [body, setBody] = useState(emailConfig.bodyTemplate);

  const handleSave = () => {
    updateEmailConfig({
      subjectTemplate: subject,
      bodyTemplate: body,
    });
  };

  const insertVariable = (variable: string, isSubject: boolean) => {
    if (isSubject) {
      setSubject(subject + variable);
    } else {
      setBody(body + variable);
    }
  };

  return (
    <>
      <Stack.Screen options={{ title: 'Email Template' }} />
      <ScrollView className="flex-1 bg-gray-50">
        <YStack padding="$4" space="$4">
          {/* Subject Template */}
          <Card elevate bordered padding="$4" backgroundColor="$background">
            <H3 className="mb-3 text-gray-900">Subject Template</H3>

            <YStack space="$3">
              <TextInput
                className="px-4 py-3 bg-gray-50 border border-gray-300 rounded-lg text-base text-gray-900"
                placeholder="Email subject..."
                value={subject}
                onChangeText={setSubject}
                multiline
              />

              <Text className="text-xs text-gray-500">
                Preview: SMS from +1234567890 (SIM 1)
              </Text>
            </YStack>
          </Card>

          {/* Body Template */}
          <Card elevate bordered padding="$4" backgroundColor="$background">
            <H4 className="mb-3 text-gray-900">Body Template</H4>

            <YStack space="$3">
              <TextInput
                className="px-4 py-3 bg-gray-50 border border-gray-300 rounded-lg text-base text-gray-900"
                placeholder="Email body..."
                value={body}
                onChangeText={setBody}
                multiline
                numberOfLines={8}
                style={{ minHeight: 160 }}
                textAlignVertical="top"
              />

              <Card padding="$3" backgroundColor="$blue1">
                <Text className="text-xs text-gray-600 font-mono">
                  From: +1234567890{'\n'}
                  SIM Slot: SIM 1{'\n'}
                  Time: 2025-11-09 14:30:00{'\n'}
                  {'\n'}
                  Message:{'\n'}
                  Hello, this is a test message!
                </Text>
              </Card>
            </YStack>
          </Card>

          {/* Available Variables */}
          <Card elevate bordered padding="$4" backgroundColor="$background">
            <XStack alignItems="center" justifyContent="space-between" marginBottom="$3">
              <H4 className="text-gray-900">Available Variables</H4>
              <Ionicons name="information-circle-outline" size={20} color="#6b7280" />
            </XStack>

            <YStack space="$2">
              {TEMPLATE_VARIABLES.map((variable) => (
                <Pressable
                  key={variable.name}
                  className="flex-row items-center justify-between p-3 bg-gray-50 border border-gray-200 rounded-lg active:bg-gray-100"
                  onPress={() => insertVariable(variable.name, false)}
                >
                  <YStack flex={1}>
                    <Text className="text-sm font-mono font-medium text-indigo-600">
                      {variable.name}
                    </Text>
                    <Text className="text-xs text-gray-600 mt-0.5">
                      {variable.description}
                    </Text>
                  </YStack>
                  <Ionicons name="add-circle-outline" size={20} color="#6366f1" />
                </Pressable>
              ))}
            </YStack>
          </Card>

          {/* HTML Support */}
          <Card padding="$4" backgroundColor="$orange2">
            <XStack space="$2" alignItems="flex-start">
              <Ionicons name="code-slash" size={20} color="#f97316" />
              <YStack flex={1}>
                <Text className="text-sm font-medium text-gray-900">HTML Support</Text>
                <Text className="text-xs text-gray-600 mt-1">
                  Use <Text className="font-mono">&lt;b&gt;</Text>, <Text className="font-mono">&lt;i&gt;</Text>, <Text className="font-mono">&lt;br&gt;</Text> tags for formatting
                </Text>
              </YStack>
            </XStack>
          </Card>

          {/* Actions */}
          <Button
            size="$5"
            theme="blue"
            icon={<Ionicons name="save-outline" size={20} />}
            onPress={handleSave}
          >
            Save Template
          </Button>
        </YStack>
      </ScrollView>
    </>
  );
}
