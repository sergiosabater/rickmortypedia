package dev.sergiosabater.rickmortypedia.presentation.navigation

import androidx.navigation.NavController
import javax.inject.Inject

class AppCoordinator @Inject constructor(
    private val navController: NavController
) : AppRouter {
    override fun navigateToCharacterList() {
        navController.navigate("character_list") {
            popUpTo("splash") { inclusive = true }
        }
    }
}