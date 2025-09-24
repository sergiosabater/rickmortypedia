package dev.sergiosabater.rickmortypedia.data.repository

import dev.sergiosabater.rickmortypedia.domain.model.Character

interface CharacterRepository {
    suspend fun getCharacters(page: Int? = null): Result<List<Character>>
    suspend fun getCharacterById(id: Int): Result<Character>
    suspend fun searchCharacters(
        name: String? = null,
        status: String? = null,
        species: String? = null
    ): Result<List<Character>>
}