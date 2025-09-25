package dev.sergiosabater.rickmortypedia.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sergiosabater.rickmortypedia.R
import dev.sergiosabater.rickmortypedia.domain.model.Character
import dev.sergiosabater.rickmortypedia.presentation.ui.component.CharacterListItem
import dev.sergiosabater.rickmortypedia.presentation.ui.component.CustomSearchBar
import dev.sergiosabater.rickmortypedia.presentation.viewmodel.CharactersListUiState


@Composable
fun CharactersListScreen(
    characters: List<Character>,
    uiState: CharactersListUiState,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onCharacterClick: (Character) -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {

        CharactersListUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF00FF00))
            }
        }

        CharactersListUiState.Success -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 64.dp)
                        .size(96.dp)
                        .wrapContentSize(Alignment.Center)
                )

                CustomSearchBar(
                    query = searchQuery,
                    onQueryChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(
                        items = characters,
                        key = { it.id }
                    ) { character ->
                        CharacterListItem(
                            character = character,
                            onClick = onCharacterClick
                        )
                    }

                    if (characters.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (searchQuery.isNotEmpty())
                                        "No se encontraron personajes"
                                    else
                                        "No hay personajes disponibles",
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        CharactersListUiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Error al cargar los personajes",
                        color = Color.Red,
                        fontSize = 18.sp
                    )
                    Button(
                        onClick = { /* Añadir retry */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF00))
                    ) {
                        Text("Reintentar")
                    }
                }
            }
        }
    }
}