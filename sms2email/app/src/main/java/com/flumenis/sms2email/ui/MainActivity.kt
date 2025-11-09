package com.flumenis.sms2email.ui

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
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
import com.flumenis.sms2email.security.AntiHijackManager
import com.flumenis.sms2email.service.KeepAliveManager
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

    private lateinit var antiHijackManager: AntiHijackManager
    private lateinit var keepAliveManager: KeepAliveManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize security managers
        antiHijackManager = AntiHijackManager(this)
        keepAliveManager = KeepAliveManager(this)

        // Prevent screenshots for security (optional, can be removed if not needed)
        // Uncomment if you want to prevent screenshots
        // window.setFlags(
        //     WindowManager.LayoutParams.FLAG_SECURE,
        //     WindowManager.LayoutParams.FLAG_SECURE
        // )

        // Check for screen overlay
        antiHijackManager.checkScreenOverlay(this)

        // Verify intent source
        if (!antiHijackManager.verifyIntentSource(this)) {
            // Intent from untrusted source, finish activity
            finish()
            return
        }

        // Register activity for hijack monitoring
        antiHijackManager.registerActivity(this)

        // Setup keep-alive mechanisms
        keepAliveManager.setupKeepAlive()

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

    override fun onResume() {
        super.onResume()
        // Start monitoring for hijacking when activity is visible
        antiHijackManager.startMonitoring()
    }

    override fun onPause() {
        super.onPause()
        // Stop monitoring when activity is not visible
        antiHijackManager.stopMonitoring()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister activity
        antiHijackManager.unregisterActivity()
    }
}
