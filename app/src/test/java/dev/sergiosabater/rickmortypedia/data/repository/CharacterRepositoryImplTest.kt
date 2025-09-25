package dev.sergiosabater.rickmortypedia.data.repository

import dev.sergiosabater.rickmortypedia.data.datasource.local.dao.CharacterDao
import dev.sergiosabater.rickmortypedia.data.datasource.local.dao.PaginationInfoDao
import dev.sergiosabater.rickmortypedia.data.datasource.local.entity.CharacterEntity
import dev.sergiosabater.rickmortypedia.data.datasource.local.mapper.CharacterEntityMapper
import dev.sergiosabater.rickmortypedia.data.datasource.remote.api.RickAndMortyApiService
import dev.sergiosabater.rickmortypedia.data.datasource.remote.dto.CharacterDto
import dev.sergiosabater.rickmortypedia.data.datasource.remote.mapper.CharacterMapper
import dev.sergiosabater.rickmortypedia.domain.model.Character
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test


class CharacterRepositoryImplTest {

    private val apiService = mockk<RickAndMortyApiService>()
    private val mapper = mockk<CharacterMapper>()
    private val characterDao = mockk<CharacterDao>()
    private val paginationInfoDao = mockk<PaginationInfoDao>()
    private val entityMapper = mockk<CharacterEntityMapper>()

    private lateinit var repository: CharacterRepositoryImpl

    private val mockCharacter = mockk<Character>()
    private val mockCharacterEntity = mockk<CharacterEntity>()
    private val mockCharacterDto = mockk<CharacterDto>()

    @Before
    fun setUp() {
        repository = CharacterRepositoryImpl(
            apiService, mapper, characterDao, paginationInfoDao, entityMapper
        )
    }

    @Test
    fun `when getCharacterById with cached data, should return character from local`() = runTest {
        // Given
        val characterId = 1
        coEvery { characterDao.getCharacterById(characterId) } returns mockCharacterEntity
        coEvery { entityMapper.toDomain(mockCharacterEntity) } returns mockCharacter

        // When
        val result = repository.getCharacterById(characterId)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(mockCharacter, result.getOrNull())
    }

    @Test
    fun `when getCharacterById with no cached data, should fetch from API and cache`() = runTest {
        // Given
        val characterId = 1
        coEvery { characterDao.getCharacterById(characterId) } returns null
        coEvery { apiService.getCharacterById(characterId) } returns mockCharacterDto
        coEvery { mapper.toCharacter(mockCharacterDto) } returns mockCharacter
        coEvery { entityMapper.toEntity(mockCharacter, 1) } returns mockCharacterEntity
        coEvery { characterDao.insertCharacter(mockCharacterEntity) } returns Unit

        // When
        val result = repository.getCharacterById(characterId)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(mockCharacter, result.getOrNull())
    }

    @Test
    fun `when getCharacterById fails from API, should return failure`() = runTest {
        // Given
        val characterId = 1
        coEvery { characterDao.getCharacterById(characterId) } returns null
        coEvery { apiService.getCharacterById(characterId) } throws retrofit2.HttpException(
            retrofit2.Response.error<Any>(404, mockk(relaxed = true))
        )

        // When
        val result = repository.getCharacterById(characterId)

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun `when hasCachedData with characters in database, should return true`() = runTest {
        // Given
        coEvery { characterDao.getCharacterCount() } returns 5

        // When
        val result = repository.hasCachedData()

        // Then
        assertEquals(true, result)
    }

    @Test
    fun `when hasCachedData with empty database, should return false`() = runTest {
        // Given
        coEvery { characterDao.getCharacterCount() } returns 0

        // When
        val result = repository.hasCachedData()

        // Then
        assertEquals(false, result)
    }

    @Test
    fun `when clearCache is called, should delete all data and return success`() = runTest {
        // Given
        coEvery { characterDao.deleteAllCharacters() } returns Unit
        coEvery { paginationInfoDao.deletePaginationInfo() } returns Unit

        // When
        val result = repository.clearCache()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(true, result.getOrNull())
    }
}