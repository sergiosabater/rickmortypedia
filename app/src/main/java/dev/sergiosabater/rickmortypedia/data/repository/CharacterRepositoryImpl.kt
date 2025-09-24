package dev.sergiosabater.rickmortypedia.data.repository

import dev.sergiosabater.rickmortypedia.data.mapper.CharacterMapper
import dev.sergiosabater.rickmortypedia.data.remote.api.RickAndMortyApiService
import dev.sergiosabater.rickmortypedia.domain.error.DomainError
import dev.sergiosabater.rickmortypedia.domain.model.Character
import javax.inject.Inject

class CharacterRepositoryImpl @Inject constructor(
    private val apiService: RickAndMortyApiService,
    private val mapper: CharacterMapper
) : CharacterRepository {

    override suspend fun getCharacters(page: Int?): Result<List<Character>> {
        return safeApiCall {
            val response = apiService.getCharacters(page = page)
            val characters = response.results.map { dto -> mapper.toCharacter(dto) }

            if (characters.isEmpty()) {
                throw DomainError.NoCharactersFound
            }

            characters
        }
    }

    override suspend fun getCharacterById(id: Int): Result<Character> {
        return safeApiCall(characterId = id) {
            val dto = apiService.getCharacterById(id)
            mapper.toCharacter(dto)
        }
    }

    override suspend fun searchCharacters(
        name: String?,
        status: String?,
        species: String?
    ): Result<List<Character>> {
        return safeApiCall {
            val response = apiService.getCharacters(
                name = name,
                status = status,
                species = species
            )
            val characters = response.results.map { dto -> mapper.toCharacter(dto) }

            if (characters.isEmpty()) {
                throw DomainError.NoCharactersFound
            }

            characters
        }
    }

    // Helper function to handle API errors
    private suspend inline fun <T> safeApiCall(
        characterId: Int? = null,
        apiCall: suspend () -> T
    ): Result<T> {
        return try {
            val result = apiCall()
            Result.success(result)
        } catch (domainError: DomainError) {
            Result.failure(domainError)
        } catch (e: retrofit2.HttpException) {
            val error = when (e.code()) {
                404 -> characterId?.let { DomainError.CharacterNotFound(it) }
                    ?: DomainError.NoCharactersFound

                500, 502, 503 -> DomainError.ServerError
                else -> DomainError.NetworkError
            }
            Result.failure(error)
        } catch (e: java.net.UnknownHostException) {
            Result.failure(DomainError.NetworkError)
        } catch (e: java.net.SocketTimeoutException) {
            Result.failure(DomainError.NetworkError)
        } catch (e: java.io.IOException) {
            Result.failure(DomainError.NetworkError)
        } catch (e: Exception) {
            Result.failure(DomainError.UnknownError(e.message))
        }
    }
}