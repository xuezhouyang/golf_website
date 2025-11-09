package com.flumenis.sms2email.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.flumenis.sms2email.ui.MainViewModel
import com.flumenis.sms2email.ui.screens.*

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Settings : Screen("settings")
    object Template : Screen("template")
    object About : Screen("about")
    object Premium : Screen("premium")
    object CloudSync : Screen("cloud_sync")
    object SmsForwarding : Screen("sms_forwarding")
    object Theme : Screen("theme")
    object Permissions : Screen("permissions")
}

@Composable
fun AppNavigation(viewModel: MainViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToTemplate = { navController.navigate(Screen.Template.route) },
                onNavigateToAbout = { navController.navigate(Screen.About.route) },
                onNavigateToPremium = { navController.navigate(Screen.Premium.route) },
                onNavigateToCloudSync = { navController.navigate(Screen.CloudSync.route) },
                onNavigateToSmsForwarding = { navController.navigate(Screen.SmsForwarding.route) },
                onNavigateToTheme = { navController.navigate(Screen.Theme.route) },
                onNavigateToPermissions = { navController.navigate(Screen.Permissions.route) }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Template.route) {
            TemplateScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.About.route) {
            AboutScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Premium.route) {
            PremiumScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.CloudSync.route) {
            CloudSyncScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.SmsForwarding.route) {
            SmsForwardingScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Theme.route) {
            ThemeScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Permissions.route) {
            PermissionSettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
