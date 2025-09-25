package dev.sergiosabater.rickmortypedia.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.sergiosabater.rickmortypedia.R
import dev.sergiosabater.rickmortypedia.presentation.viewmodel.SplashUiState
import dev.sergiosabater.rickmortypedia.presentation.viewmodel.SplashViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    onLoadingComplete: () -> Unit,
    onError: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        when (uiState) {
            is SplashUiState.Success -> {
                delay(500) // Pequeña pausa para mostrar la pantalla completa
                onLoadingComplete()
            }

            is SplashUiState.Error -> {
                onError()
            }

            else -> { /* Loading - no hacer nada */
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), // Fondo blanco
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Imagen de Rick adaptativa al ancho de pantalla (x2 más grande)
            Image(
                painter = painterResource(R.drawable.loading),
                contentDescription = "Rick and Morty",
                modifier = Modifier
                    .fillMaxWidth(1f) // Ocupa el 120% del ancho de pantalla (x2 más grande que 0.6f)
                    .aspectRatio(1f), // Mantiene proporción cuadrada (1:1)
                contentScale = ContentScale.Fit
            )

            // Progress Bar circular
            CircularProgressIndicator(
                modifier = Modifier.size(51.dp),
                color = Color(0xFF00FF00), // Verde Rick and Morty
                strokeWidth = 4.dp
            )

            // Texto de loading
            Text(
                text = "Loading...",
                color = Color.Black, // Texto negro para fondo blanco
                fontSize = 32.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

