package dev.sergiosabater.rickmortypedia.data.repository

import dev.sergiosabater.rickmortypedia.data.mapper.CharacterMapper
import dev.sergiosabater.rickmortypedia.data.remote.api.RickAndMortyApiService
import dev.sergiosabater.rickmortypedia.domain.model.Character
import javax.inject.Inject

class CharacterRepositoryImpl @Inject constructor(
    private val apiService: RickAndMortyApiService,
    private val mapper: CharacterMapper
) : CharacterRepository {

    override suspend fun getCharacters(page: Int?): List<Character> {
        val response = apiService.getCharacters(page = page)
        return response.results.map { dto ->
            mapper.toCharacter(dto)
        }
    }

    override suspend fun getCharacterById(id: Int): Character {
        val dto = apiService.getCharacterById(id)
        return mapper.toCharacter(dto)
    }

    override suspend fun searchCharacters(
        name: String?,
        status: String?,
        species: String?
    ): List<Character> {
        val response = apiService.getCharacters(
            name = name,
            status = status,
            species = species
        )
        return response.results.map { dto ->
            mapper.toCharacter(dto)
        }
    }

}