package com.gfreeman.takeme

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gfreeman.takeme.home.view.HomeScreen
import com.gfreeman.takeme.home.view.map.ArrivedToDestinationScreen
import com.gfreeman.takeme.home.viewmodel.map.ArrivedToDestinationViewModel
import com.gfreeman.takeme.login.view.LoginScreen
import com.gfreeman.takeme.login.view.SignupScreen
import com.gfreeman.takeme.login.viewmodel.LoginViewModel
import com.gfreeman.takeme.login.viewmodel.SignupViewModel
import com.gfreeman.takeme.ui.theme.TakeMeTheme
import com.gfreeman.takeme.weather_forecast.view.WeatherReportScreen
import dagger.hilt.android.AndroidEntryPoint

/**
 * MainActivity - The single entry point Activity for the entire application (Single Activity Architecture).
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TakeMeTheme {
                TakeMeNavHost()
            }
        }
    }
}

/**
 * Top-level Compose NavHost routing all application screens.
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun TakeMeNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        // 1. Login Screen Route
        composable("login") {
            val loginViewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onSkipClick = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onSignUpClick = {
                    navController.navigate("signup")
                }
            )
        }

        // 2. Signup Screen Route
        composable("signup") {
            val signupViewModel: SignupViewModel = hiltViewModel()
            SignupScreen(
                viewModel = signupViewModel,
                onSignupSuccess = {
                    navController.navigate("home") {
                        popUpTo("signup") { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.popBackStack()
                }
            )
        }

        // 3. Main Home Screen Route (Hosts Favorites, Map, Profile tabs)
        composable("home") {
            HomeScreen(
                onNavigateToWeather = { point ->
                    navController.navigate("weather/${point.latitude}/${point.longitude}")
                },
                onLogoutClick = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        // 4. Weather Forecast Screen Route
        composable(
            route = "weather/{lat}/{long}",
            arguments = listOf(
                navArgument("lat") { type = NavType.StringType },
                navArgument("long") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val lat = backStackEntry.arguments?.getString("lat")
            val long = backStackEntry.arguments?.getString("long")
            WeatherReportScreen(
                lat = lat,
                long = long,
                onBackClick = { navController.popBackStack() }
            )
        }

        // 5. Arrived to Destination Screen Route
        composable("arrived") {
            val arrivedViewModel: ArrivedToDestinationViewModel = hiltViewModel()
            ArrivedToDestinationScreen(
                viewModel = arrivedViewModel
            )
        }
    }
}
