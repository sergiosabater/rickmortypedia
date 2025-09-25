package dev.sergiosabater.rickmortypedia.presentation.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
        is CharactersListUiState.Loading -> {
            // Mostrar indicador de carga
        }

        is CharactersListUiState.Success -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                // Buscador en la parte superior
                CustomSearchBar(
                    query = searchQuery,
                    onQueryChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )

                // Separación entre buscador y lista
                Spacer(modifier = Modifier.height(8.dp))

                // Lista de personajes con scroll infinito
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(characters) { character ->
                        CharacterListItem(
                            character = character,
                            onClick = onCharacterClick
                        )
                    }
                }
            }
        }

        is CharactersListUiState.Error -> {
            Log.d("APP-LOG", "SE HA PRODUCIDO UN ERROR")
        }

        is CharactersListUiState.NavigateToDetail -> TODO()
    }

}