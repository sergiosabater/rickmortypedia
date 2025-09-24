package dev.sergiosabater.rickmortypedia.data.repository

import dev.sergiosabater.rickmortypedia.domain.model.Character

interface CharacterRepository {
    suspend fun getCharacters(page: Int? = null): List<Character>
    suspend fun getCharacterById(id: Int): Character
    suspend fun searchCharacters(
        name: String? = null,
        status: String? = null,
        species: String? = null
    ): List<Character>
}