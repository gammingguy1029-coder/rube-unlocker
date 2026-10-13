package com.rubecoder.rubeaurachecker.core.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rubecoder.rubeaurachecker.presentation.history.HistoryScreen
import com.rubecoder.rubeaurachecker.presentation.home.HomeScreen
import com.rubecoder.rubeaurachecker.presentation.owner.OwnerScreen
import com.rubecoder.rubeaurachecker.presentation.ownermode.OwnerModeScreen
import com.rubecoder.rubeaurachecker.presentation.registration.RegistrationScreen
import com.rubecoder.rubeaurachecker.presentation.result.ResultScreen
import com.rubecoder.rubeaurachecker.presentation.scanner.ScannerScreen
import com.rubecoder.rubeaurachecker.presentation.settings.SettingsScreen

@Composable
fun RubeAuraNavHost(
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "registration",
        modifier = modifier
    ) {
        composable("registration") {
            RegistrationScreen(onRegistrationComplete = { navController.navigate("home") })
        }
        composable("home") {
            HomeScreen(
                onStartScan = { navController.navigate("scanner") },
                onOpenHistory = { navController.navigate("history") },
                onOpenSettings = { navController.navigate("settings") },
                onOpenOwner = { navController.navigate("owner_pin") },
            )
        }
        composable("scanner") {
            ScannerScreen(
                onScanComplete = { score -> navController.navigate("result?score=$score") },
                onGoBack = { navController.popBackStack() }
            )
        }
        composable("result") {
            val score = it.navBackStackEntry.arguments?.getInt("score") ?: 850
            ResultScreen(
                score = score,
                onScanAgain = { navController.navigate("scanner") },
                onHome = { navController.navigate("home") }
            )
        }
        composable("history") {
            HistoryScreen(
                navigateBack = { navController.popBackStack() },
                navigateToResultWithScore = { score -> navController.navigate("result?score=$score") }
            )
        }
        composable("settings") { SettingsScreen(navigateBack = { navController.popBackStack() }) }
        composable("owner_pin") {
            OwnerScreen(
                onOwnerAuthenticated = { token ->
                    navController.navigate("owner_mode?token=$token")
                }
            )
        }
        composable(
            route = "owner_mode?token={token}",
            arguments = listOf(navArgument("token") { type = NavType.StringType })
        ) { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token") ?: ""
            OwnerModeScreen(
                navigateBack = { navController.popBackStack() },
                initialToken = token
            )
        }
    }
}