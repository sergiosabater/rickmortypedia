package dev.sergiosabater.rickmortypedia.presentation.ui

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dev.sergiosabater.rickmortypedia.presentation.viewmodel.CharacterDetailViewModel
import dev.sergiosabater.rickmortypedia.presentation.viewmodel.CharactersListViewModel
import dev.sergiosabater.rickmortypedia.presentation.viewmodel.SplashViewModel
import dev.sergiosabater.rickmortypedia.presentation.viewmodel.ThemeViewModel

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object CharacterList : Screen("character_list")
    object CharacterDetail : Screen("character_detail")
    object Error : Screen("error")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            val viewModel: SplashViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()

            SplashScreen(
                uiState = uiState,
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
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()
            val characters by viewModel.filteredCharacters.collectAsState()
            val searchQuery by viewModel.searchQuery.collectAsState()
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
            val selectedSpecies by viewModel.selectedSpecies.collectAsState()

            LaunchedEffect(Unit) {
                viewModel.navigationEvent.collect { characterId ->
                    navController.navigate("${Screen.CharacterDetail.route}/$characterId")
                }
            }

            CharactersListScreen(
                characters = characters,
                uiState = uiState,
                searchQuery = searchQuery,
                onSearchQueryChange = viewModel::onSearchQueryChange,
                onCharacterClick = viewModel::onCharacterClick,
                isDarkTheme = isDarkTheme,
                onThemeToggle = { themeViewModel.toggleTheme() },
                selectedSpecies = selectedSpecies,
                onSpeciesSelected = viewModel::onSpeciesFilterChange
            )
        }

        composable(Screen.Error.route) {
            val context = LocalContext.current
            val activity = context as? Activity
            ErrorScreen(
                onRetry = {
                    navController.navigate(Screen.Splash.route) {
                        popUpTo(Screen.Error.route) { inclusive = true }
                    }
                },
                onExit = {
                    activity?.finishAffinity()
                }
            )
        }

        composable(
            route = "${Screen.CharacterDetail.route}/{characterId}",
            arguments = listOf(
                navArgument("characterId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val viewModel: CharacterDetailViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()

            CharacterDetailScreen(
                uiState = uiState,
                onBackClick = { navController.popBackStack() },
                onRetry = {
                    viewModel.loadCharacter(
                        backStackEntry.arguments?.getInt("characterId") ?: 0
                    )
                }
            )
        }
    }
}