package com.whatsapptracker.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.whatsapptracker.ui.screens.DashboardScreen
import com.whatsapptracker.ui.screens.SetupScreen
import com.whatsapptracker.ui.screens.YearlyReportScreen

object Routes {
    const val SETUP = "setup"
    const val DASHBOARD = "dashboard"
    const val YEARLY_REPORT = "yearly_report"
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    isAccessibilityEnabled: Boolean
) {
    NavHost(
        navController = navController,
        startDestination = if (isAccessibilityEnabled) Routes.DASHBOARD else Routes.SETUP
    ) {
        composable(Routes.SETUP) {
            SetupScreen(
                onPermissionGranted = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.SETUP) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.DASHBOARD) {
            DashboardScreen(
                onNavigateToWrapped = {
                    navController.navigate(Routes.YEARLY_REPORT)
                },
                onNavigateToSetup = {
                    navController.navigate(Routes.SETUP)
                }
            )
        }

        composable(Routes.YEARLY_REPORT) {
            YearlyReportScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
