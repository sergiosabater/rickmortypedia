package dev.sergiosabater.rickmortypedia.presentation.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.sergiosabater.rickmortypedia.R
import dev.sergiosabater.rickmortypedia.presentation.ui.theme.RickMortyPediaTheme
import dev.sergiosabater.rickmortypedia.domain.model.Character
import dev.sergiosabater.rickmortypedia.domain.model.CharacterStatus

@Composable
fun CharacterListItem(
    character: Character,
    onClick: (Character) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(character) }
            .padding(horizontal = 16.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(character.image)
                    .crossfade(true) // Animación suave al cargar
                    .build(),
                contentDescription = "Imagen de ${character.name}",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.ic_placeholder),
                error = painterResource(R.drawable.ic_error)
            )

            // Nombre del personaje
            Text(
                text = character.name,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            // Ícono opcional para indicar que es clickable
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Ver detalle",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// Versión alternativa
@Composable
fun CharacterListItemSimple(
    character: Character,
    onClick: (Character) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(character) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Imagen circular del personaje usando Coil
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(character.image)
                .crossfade(true)
                .build(),
            contentDescription = "Imagen de ${character.name}",
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        // Nombre del personaje
        Text(
            text = character.name,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
    }
}

// Modelo de datos de ejemplo
data class CharacterModel(
    val id: Int,
    val name: String,
    val image: String, // URL de la imagen
    val status: String? = null,
    val species: String? = null,
    val gender: String? = null
)

// Preview para desarrollo
@Preview(name = "Character List Item", showBackground = true)
@Composable
fun CharacterListItemPreview() {
    RickMortyPediaTheme {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Preview con Card
            CharacterListItem(
                character = Character(
                    id = 1,
                    name = "Rick Sanchez",
                    status = CharacterStatus.ALIVE,
                    species = "Human",
                    type = "",
                    gender = "",
                    origin = "",
                    location = "",
                    image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
                    episodeCount = 10,
                    originUrl = "",
                    locationUrl = "",
                    episodeUrls = listOf(),
                    url = "",
                    created = "",
                ),
                onClick = { /* Handle click */ }
            )

            CharacterListItem(
                character = Character(
                    id = 1,
                    name = "Rick Sanchez",
                    status = CharacterStatus.ALIVE,
                    species = "Human",
                    type = "",
                    gender = "",
                    origin = "",
                    location = "",
                    image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
                    episodeCount = 10,
                    originUrl = "",
                    locationUrl = "",
                    episodeUrls = listOf(),
                    url = "",
                    created = "",
                ),
                onClick = { /* Handle click */ }
            )

            // Preview versión simple
            CharacterListItemSimple(
                character = Character(
                    id = 1,
                    name = "Rick Sanchez",
                    status = CharacterStatus.ALIVE,
                    species = "Human",
                    type = "",
                    gender = "",
                    origin = "",
                    location = "",
                    image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
                    episodeCount = 10,
                    originUrl = "",
                    locationUrl = "",
                    episodeUrls = listOf(),
                    url = "",
                    created = ""
                ),
                onClick = { /* Handle click */ }
            )
        }
    }
}

// Preview alternativa con placeholder (sin URLs reales)
@Preview(name = "Character List Item - Placeholder", showBackground = true)
@Composable
fun CharacterListItemPlaceholderPreview() {
    RickMortyPediaTheme {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CharacterListItem(
                character = Character(
                    id = 1,
                    name = "Rick Sanchez",
                    status = CharacterStatus.ALIVE,
                    species = "Human",
                    type = "",
                    gender = "",
                    origin = "",
                    location = "",
                    image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
                    episodeCount = 10,
                    originUrl = "",
                    locationUrl = "",
                    episodeUrls = listOf(),
                    url = "",
                    created = "",
                ),
                onClick = { }
            )

            CharacterListItem(
                character = Character(
                    id = 1,
                    name = "Rick Sanchez",
                    status = CharacterStatus.ALIVE,
                    species = "Human",
                    type = "",
                    gender = "",
                    origin = "",
                    location = "",
                    image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
                    episodeCount = 10,
                    originUrl = "",
                    locationUrl = "",
                    episodeUrls = listOf(),
                    url = "",
                    created = "",
                ),
                onClick = { }
            )
        }
    }
}
