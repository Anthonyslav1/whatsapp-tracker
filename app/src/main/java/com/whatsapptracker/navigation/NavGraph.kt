package com.whatsapptracker.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.whatsapptracker.ui.screens.DashboardScreen
import com.whatsapptracker.ui.screens.SetupScreen
import com.whatsapptracker.ui.screens.YearlyReportScreen
import kotlinx.serialization.Serializable

sealed class Routes {
    @Serializable data object Setup : Routes()
    @Serializable data object Dashboard : Routes()
    @Serializable data object Settings : Routes()
    @Serializable data object YearlyReport : Routes()
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    isAccessibilityEnabled: Boolean
) {
    NavHost(
        navController = navController,
        startDestination = if (isAccessibilityEnabled) Routes.Dashboard else Routes.Setup
    ) {
        composable<Routes.Setup> {
            SetupScreen(
                onPermissionGranted = {
                    navController.navigate(Routes.Dashboard) {
                        popUpTo(Routes.Setup) { inclusive = true }
                    }
                }
            )
        }

        composable<Routes.Dashboard> {
            DashboardScreen(
                onNavigateToWrapped = {
                    navController.navigate(Routes.YearlyReport)
                },
                onNavigateToSetup = {
                    navController.navigate(Routes.Setup)
                },
                onNavigateToSettings = {
                    navController.navigate(Routes.Settings)
                }
            )
        }

        composable<Routes.YearlyReport> {
            YearlyReportScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<Routes.Settings> {
            com.whatsapptracker.ui.screens.SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
