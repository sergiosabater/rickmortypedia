package dev.sergiosabater.rickmortypedia.data.repository

import dev.sergiosabater.rickmortypedia.data.datasource.local.dao.CharacterDao
import dev.sergiosabater.rickmortypedia.data.datasource.local.dao.PaginationInfoDao
import dev.sergiosabater.rickmortypedia.data.datasource.local.entity.PaginationInfoEntity
import dev.sergiosabater.rickmortypedia.data.datasource.local.mapper.CharacterEntityMapper
import dev.sergiosabater.rickmortypedia.data.datasource.remote.api.RickAndMortyApiService
import dev.sergiosabater.rickmortypedia.data.datasource.remote.dto.PageInfoDto
import dev.sergiosabater.rickmortypedia.data.datasource.remote.mapper.CharacterMapper
import dev.sergiosabater.rickmortypedia.domain.error.DomainError
import dev.sergiosabater.rickmortypedia.domain.model.Character
import javax.inject.Inject

class CharacterRepositoryImpl @Inject constructor(
    private val apiService: RickAndMortyApiService,
    private val mapper: CharacterMapper,
    private val characterDao: CharacterDao,
    private val paginationInfoDao: PaginationInfoDao,
    private val entityMapper: CharacterEntityMapper
) : CharacterRepository {

    override suspend fun getCharacters(page: Int?): Result<List<Character>> {
        return try {
            // 1. First check if data in the cache
            if (hasCachedData()) {
                // 2. Get data from the local database
                val localCharacters = getCharactersFromLocal(page)
                if (localCharacters.isNotEmpty()) {
                    Result.success(localCharacters)
                } else {
                    Result.failure(DomainError.NoCachedData)
                }
            } else {
                // 3. If no cache, synchronize with API
                syncWithApi(page ?: 1)

                // 4. Get data after synchronization
                val refreshedCharacters = getCharactersFromLocal(page)
                if (refreshedCharacters.isNotEmpty()) {
                    Result.success(refreshedCharacters)
                } else {
                    Result.failure(DomainError.NoCharactersFound)
                }
            }
        } catch (e: DomainError) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(DomainError.DatabaseError)
        }
    }

    override suspend fun getCharacterById(id: Int): Result<Character> {
        return try {
            // 1. First check if data in the cache
            val localCharacter = getCharacterFromLocal(id)
            if (localCharacter != null) {
                Result.success(localCharacter)
            } else {
                // 2. If no cache, connecting with API
                val apiResult = safeApiCall {
                    val dto = apiService.getCharacterById(id)
                    mapper.toCharacter(dto)
                }

                if (apiResult.isSuccess) {
                    val character = apiResult.getOrNull()
                    if (character != null) {
                        // 3. Save in cache for future references
                        saveCharacterToLocal(character)
                        Result.success(character)
                    } else {
                        Result.failure(DomainError.CharacterNotFound(id))
                    }
                } else {
                    val exception = apiResult.exceptionOrNull()
                    Result.failure(
                        exception as? DomainError ?: DomainError.UnknownError(exception?.message)
                    )
                }
            }
        } catch (e: Exception) {
            Result.failure(DomainError.DatabaseError)
        }
    }

    override suspend fun searchCharacters(
        name: String?,
        status: String?,
        species: String?
    ): Result<List<Character>> {
        return try {
            // Always search local cache for searches
            if (!hasCachedData()) {
                return Result.failure(DomainError.NoCachedData)
            }

            val searchResults = when {
                !name.isNullOrBlank() -> {
                    characterDao.searchCharacters(name).map { entityMapper.toDomain(it) }
                }

                else -> {
                    // Only search by name
                    // In the future is possible advanced search as well
                    getCharactersFromLocal()
                }
            }

            if (searchResults.isNotEmpty()) {
                Result.success(searchResults)
            } else {
                Result.failure(DomainError.NoCharactersFound)
            }
        } catch (e: Exception) {
            Result.failure(DomainError.DatabaseError)
        }
    }

    override suspend fun hasCachedData(): Boolean {
        return try {
            characterDao.getCharacterCount() > 0
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun clearCache(): Result<Boolean> {
        return try {
            characterDao.deleteAllCharacters()
            paginationInfoDao.deletePaginationInfo()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(DomainError.DatabaseError)
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

    private suspend inline fun <T> safeApiCall(apiCall: suspend () -> T): Result<T> {
        return safeApiCall(null, apiCall)
    }

    private suspend fun getCharactersFromLocal(page: Int? = null): List<Character> {
        return try {
            val entities = if (page != null) {
                characterDao.getCharactersByPage(page)
            } else {
                // Get all characters
                characterDao.getCharacters(Int.MAX_VALUE, 0)
            }
            entities.map { entityMapper.toDomain(it) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private suspend fun getCharacterFromLocal(id: Int): Character? {
        return try {
            val entity = characterDao.getCharacterById(id)
            entity?.let { entityMapper.toDomain(it) }
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun saveCharactersToLocal(
        characters: List<Character>,
        page: Int = 1,
        pageInfo: PageInfoDto? = null
    ) {
        try {
            val entities = characters.map { entityMapper.toEntity(it, page) }
            characterDao.insertCharacters(entities)

            pageInfo?.let { info ->
                val paginationInfo = PaginationInfoEntity(
                    id = 1,
                    totalPages = info.pages,
                    totalCharacters = info.count,
                    lastSyncTimestamp = System.currentTimeMillis()
                )
                paginationInfoDao.insertPaginationInfo(paginationInfo)
            }
        } catch (e: Exception) {
            throw DomainError.DatabaseError
        }
    }

    private suspend fun saveCharacterToLocal(character: Character) {
        try {
            val entity = entityMapper.toEntity(character, 1)
            characterDao.insertCharacter(entity)
        } catch (e: Exception) {
            throw DomainError.DatabaseError
        }
    }

    private suspend fun syncWithApi(page: Int = 1) {
        val apiResult = safeApiCall {
            val response = apiService.getCharacters(page = page)
            response to response.results.map { dto -> mapper.toCharacter(dto) }
        }

        when {
            apiResult.isSuccess -> {
                val (apiResponse, characters) = apiResult.getOrNull()!!
                if (characters.isNotEmpty()) {
                    saveCharactersToLocal(characters, page, apiResponse.info)
                } else {
                    throw DomainError.NoCharactersFound
                }
            }
            else -> {
                val originalError = apiResult.exceptionOrNull() as? DomainError ?: DomainError.SyncFailed
                throw originalError
            }
        }
    }
}