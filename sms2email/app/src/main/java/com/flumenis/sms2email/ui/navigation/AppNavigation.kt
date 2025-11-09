package com.flumenis.sms2email.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.flumenis.sms2email.ui.MainViewModel
import com.flumenis.sms2email.ui.screens.AboutScreen
import com.flumenis.sms2email.ui.screens.HomeScreen
import com.flumenis.sms2email.ui.screens.SettingsScreen
import com.flumenis.sms2email.ui.screens.TemplateScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Settings : Screen("settings")
    object Template : Screen("template")
    object About : Screen("about")
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
                onNavigateToAbout = { navController.navigate(Screen.About.route) }
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
    }
}
