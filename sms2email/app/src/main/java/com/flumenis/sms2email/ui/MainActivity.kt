package com.flumenis.sms2email.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flumenis.sms2email.ui.navigation.AppNavigation
import com.flumenis.sms2email.ui.theme.PostaFideTheme
import com.flumenis.sms2email.ui.theme.ThemeMode

/**
 * Main Activity - PostaFide
 *
 * Note: Permissions are managed through PermissionSettingsScreen.
 * We do NOT automatically request permissions on startup.
 * Users must grant permissions manually through Settings → Permissions.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: MainViewModel = viewModel()

            // Collect theme settings from ViewModel
            val themeMode by viewModel.themeModeFlow.collectAsState(initial = ThemeMode.AUTO)
            val useDynamicColor by viewModel.useDynamicColorFlow.collectAsState(initial = true)

            PostaFideTheme(
                darkTheme = when (themeMode) {
                    ThemeMode.LIGHT -> false
                    ThemeMode.DARK -> true
                    ThemeMode.AUTO -> isSystemInDarkTheme()
                },
                dynamicColor = useDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(viewModel = viewModel)
                }
            }
        }
    }
}
