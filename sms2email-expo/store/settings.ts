import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';
import AsyncStorage from '@react-native-async-storage/async-storage';

export interface EmailConfig {
  smtpHost: string;
  smtpPort: number;
  smtpUsername: string;
  smtpPassword: string;
  smtpUseTls: boolean;
  smtpUseSsl: boolean;
  fromEmail: string;
  fromName: string;
  toEmail: string;
  subjectTemplate: string;
  bodyTemplate: string;
  enabled: boolean;
}

export interface AppConfig {
  version: number;
  emailConfig: EmailConfig;
  filterEnabled: boolean;
  allowedSenders: string[];
  blockedSenders: string[];
  keywordFilters: string[];
  createdAt: number;
  company: string;
}

interface SettingsState {
  emailConfig: EmailConfig;
  appConfig: AppConfig;
  updateEmailConfig: (config: Partial<EmailConfig>) => void;
  updateAppConfig: (config: Partial<AppConfig>) => void;
  resetConfig: () => void;
}

const defaultEmailConfig: EmailConfig = {
  smtpHost: '',
  smtpPort: 587,
  smtpUsername: '',
  smtpPassword: '',
  smtpUseTls: true,
  smtpUseSsl: false,
  fromEmail: '',
  fromName: 'SMS2Email',
  toEmail: '',
  subjectTemplate: 'SMS from {{sender}} ({{sim_slot}})',
  bodyTemplate: `From: {{sender}}
SIM Slot: {{sim_slot}}
Time: {{timestamp}}

Message:
{{message}}`,
  enabled: false,
};

const defaultAppConfig: AppConfig = {
  version: 1,
  emailConfig: defaultEmailConfig,
  filterEnabled: false,
  allowedSenders: [],
  blockedSenders: [],
  keywordFilters: [],
  createdAt: Date.now(),
  company: 'Flumenis LLC, Delaware',
};

export const useSettingsStore = create<SettingsState>()(
  persist(
    (set) => ({
      emailConfig: defaultEmailConfig,
      appConfig: defaultAppConfig,
      updateEmailConfig: (config) =>
        set((state) => ({
          emailConfig: { ...state.emailConfig, ...config },
        })),
      updateAppConfig: (config) =>
        set((state) => ({
          appConfig: { ...state.appConfig, ...config },
        })),
      resetConfig: () =>
        set({
          emailConfig: defaultEmailConfig,
          appConfig: defaultAppConfig,
        }),
    }),
    {
      name: 'sms2email-storage',
      storage: createJSONStorage(() => AsyncStorage),
    }
  )
);
