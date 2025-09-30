package dev.sergiosabater.rickmortypedia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import dev.sergiosabater.rickmortypedia.presentation.ui.AppNavigation
import dev.sergiosabater.rickmortypedia.presentation.ui.theme.RickMortyPediaTheme
import dev.sergiosabater.rickmortypedia.presentation.viewmodel.ThemeViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
            val systemDarkTheme = isSystemInDarkTheme()
            val useDarkTheme = isDarkTheme ?: systemDarkTheme

            RickMortyPediaTheme(darkTheme = useDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AppNavigation(themeViewModel = themeViewModel)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RickMortyPediaTheme {
    }
}
