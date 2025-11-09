package com.flumenis.sms2email.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.flumenis.sms2email.ui.MainViewModel
import com.flumenis.sms2email.ui.components.AnimationConfig
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
    object LogViewer : Screen("log_viewer")
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
                onNavigateToPermissions = { navController.navigate(Screen.Permissions.route) },
                onNavigateToLogViewer = { navController.navigate(Screen.LogViewer.route) }
            )
        }

        composable(
            route = Screen.Settings.route,
            enterTransition = { AnimationConfig.enterTransition() },
            exitTransition = { AnimationConfig.exitTransition() },
            popEnterTransition = { AnimationConfig.popEnterTransition() },
            popExitTransition = { AnimationConfig.popExitTransition() }
        ) {
            SettingsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Template.route,
            enterTransition = { AnimationConfig.enterTransition() },
            exitTransition = { AnimationConfig.exitTransition() },
            popEnterTransition = { AnimationConfig.popEnterTransition() },
            popExitTransition = { AnimationConfig.popExitTransition() }
        ) {
            TemplateScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.About.route,
            enterTransition = { AnimationConfig.enterTransition() },
            exitTransition = { AnimationConfig.exitTransition() },
            popEnterTransition = { AnimationConfig.popEnterTransition() },
            popExitTransition = { AnimationConfig.popExitTransition() }
        ) {
            AboutScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Premium.route,
            enterTransition = { AnimationConfig.enterTransition() },
            exitTransition = { AnimationConfig.exitTransition() },
            popEnterTransition = { AnimationConfig.popEnterTransition() },
            popExitTransition = { AnimationConfig.popExitTransition() }
        ) {
            PremiumScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.CloudSync.route,
            enterTransition = { AnimationConfig.enterTransition() },
            exitTransition = { AnimationConfig.exitTransition() },
            popEnterTransition = { AnimationConfig.popEnterTransition() },
            popExitTransition = { AnimationConfig.popExitTransition() }
        ) {
            CloudSyncScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.SmsForwarding.route,
            enterTransition = { AnimationConfig.enterTransition() },
            exitTransition = { AnimationConfig.exitTransition() },
            popEnterTransition = { AnimationConfig.popEnterTransition() },
            popExitTransition = { AnimationConfig.popExitTransition() }
        ) {
            SmsForwardingScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Theme.route,
            enterTransition = { AnimationConfig.enterTransition() },
            exitTransition = { AnimationConfig.exitTransition() },
            popEnterTransition = { AnimationConfig.popEnterTransition() },
            popExitTransition = { AnimationConfig.popExitTransition() }
        ) {
            ThemeScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Permissions.route,
            enterTransition = { AnimationConfig.enterTransition() },
            exitTransition = { AnimationConfig.exitTransition() },
            popEnterTransition = { AnimationConfig.popEnterTransition() },
            popExitTransition = { AnimationConfig.popExitTransition() }
        ) {
            PermissionSettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.LogViewer.route,
            enterTransition = { AnimationConfig.enterTransition() },
            exitTransition = { AnimationConfig.exitTransition() },
            popEnterTransition = { AnimationConfig.popEnterTransition() },
            popExitTransition = { AnimationConfig.popExitTransition() }
        ) {
            LogViewerScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
