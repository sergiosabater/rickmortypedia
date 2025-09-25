package dev.sergiosabater.rickmortypedia.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.sergiosabater.rickmortypedia.presentation.viewmodel.CharactersListViewModel

// Routes.kt
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object CharacterList : Screen("character_list")
    object CharacterDetail : Screen("character_detail")
    object Error : Screen("error")
}

// AppNavigation.kt
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onLoadingComplete = {
                    navController.navigate(Screen.CharacterList.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onError = {
                    navController.navigate(Screen.Error.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.CharacterList.route) {
            val viewModel: CharactersListViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()
            val characters by viewModel.filteredCharacters.collectAsState()
            val searchQuery by viewModel.searchQuery.collectAsState()

            CharactersListScreen(
                characters = characters,
                searchQuery = searchQuery,
                onSearchQueryChange = viewModel::onSearchQueryChange,
                onCharacterClick = viewModel::onCharacterClick,
                uiState = uiState
            )
        }

        composable(Screen.Error.route) {
            ErrorScreen(
                onRetry = {
                    navController.navigate(Screen.Splash.route) {
                        popUpTo(Screen.Error.route) { inclusive = true }
                    }
                },
                onExit = {
                    // Cerrar la app
                    // (En una app real, usarías finish() de la Activity)
                }
            )
        }
    }
}