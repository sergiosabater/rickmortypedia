package dev.sergiosabater.rickmortypedia.domain.usecase

import dev.sergiosabater.rickmortypedia.data.repository.CharacterRepository
import dev.sergiosabater.rickmortypedia.domain.model.Character
import dev.sergiosabater.rickmortypedia.domain.model.CharacterStatus
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class GetCharacterByIdUseCaseTest {


    private lateinit var mockRepository: CharacterRepository
    private lateinit var getCharacterByIdUseCase: GetCharacterByIdUseCase

    private val testCharacter = Character(
        id = 1,
        name = "Rick Sanchez",
        status = CharacterStatus.ALIVE,
        species = "Human",
        type = "Genius",
        gender = "Male",
        origin = "Earth (C-137)",
        location = "Citadel of Ricks",
        image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
        episodeCount = 51,
        originUrl = "https://rickandmortyapi.com/api/location/1",
        locationUrl = "https://rickandmortyapi.com/api/location/3",
        episodeUrls = listOf(
            "https://rickandmortyapi.com/api/episode/1",
            "https://rickandmortyapi.com/api/episode/2"
        ),
        url = "https://rickandmortyapi.com/api/character/1",
        created = "2017-11-04T18:48:46.250Z"
    )

    @Before
    fun setUp() {
        mockRepository = mockk()
        getCharacterByIdUseCase = GetCharacterByIdUseCase(mockRepository)
    }

    @Test
    fun `invoke should return success with character when repository succeeds`() = runTest {
        // Given
        val characterId = 1
        coEvery { mockRepository.getCharacterById(characterId) } returns Result.success(testCharacter)

        // When
        val result = getCharacterByIdUseCase(characterId)

        // Then
        assertTrue("Result should be success", result.isSuccess)
        assertEquals("Character should match", testCharacter, result.getOrNull())
        assertEquals("Character ID should be correct", characterId, result.getOrNull()?.id)
    }

    @Test
    fun `invoke should return failure when repository fails with character not found`() = runTest {
        // Given
        val characterId = 999
        val expectedError = Exception("Character not found")
        coEvery { mockRepository.getCharacterById(characterId) } returns Result.failure(expectedError)

        // When
        val result = getCharacterByIdUseCase(characterId)

        // Then
        assertTrue("Result should be failure", result.isFailure)
        assertEquals("Error should match", expectedError, result.exceptionOrNull())
    }

    @Test
    fun `invoke should return failure when repository fails with network error`() = runTest {
        // Given
        val characterId = 1
        val expectedError = Exception("Network error")
        coEvery { mockRepository.getCharacterById(characterId) } returns Result.failure(expectedError)

        // When
        val result = getCharacterByIdUseCase(characterId)

        // Then
        assertTrue("Result should be failure", result.isFailure)
        assertEquals("Error should match", expectedError, result.exceptionOrNull())
    }

    @Test
    fun `invoke should call repository with correct character id`() = runTest {
        // Given
        val characterId = 42
        coEvery { mockRepository.getCharacterById(characterId) } returns Result.success(testCharacter)

        // When
        val result = getCharacterByIdUseCase(characterId)

        // Then
        assertTrue("Result should be success", result.isSuccess)
        // Verificamos que el repository fue llamado con el ID correcto
        // MockK ya verifica esto implícitamente a través del coEvery
    }

    @Test
    fun `invoke should preserve all character data correctly`() = runTest {
        // Given
        val characterId = 1
        coEvery { mockRepository.getCharacterById(characterId) } returns Result.success(testCharacter)

        // When
        val result = getCharacterByIdUseCase(characterId)

        // Then
        val character = result.getOrNull()
        assertNotNull("Character should not be null", character)

        assertEquals("ID should match", 1, character?.id)
        assertEquals("Name should match", "Rick Sanchez", character?.name)
        assertEquals("Status should match", CharacterStatus.ALIVE, character?.status)
        assertEquals("Species should match", "Human", character?.species)
        assertEquals("Type should match", "Genius", character?.type)
        assertEquals("Gender should match", "Male", character?.gender)
        assertEquals("Origin should match", "Earth (C-137)", character?.origin)
        assertEquals("Location should match", "Citadel of Ricks", character?.location)
        assertEquals("Episode count should match", 51, character?.episodeCount)
        assertEquals("Image URL should match", "https://rickandmortyapi.com/api/character/avatar/1.jpeg", character?.image)
    }

    @Test
    fun `invoke should handle different character ids correctly`() = runTest {
        // Given
        val mortyCharacter = testCharacter.copy(
            id = 2,
            name = "Morty Smith",
            episodeCount = 45
        )
        val characterId = 2
        coEvery { mockRepository.getCharacterById(characterId) } returns Result.success(mortyCharacter)

        // When
        val result = getCharacterByIdUseCase(characterId)

        // Then
        assertTrue("Result should be success", result.isSuccess)
        assertEquals("Character ID should be 2", 2, result.getOrNull()?.id)
        assertEquals("Character name should be Morty Smith", "Morty Smith", result.getOrNull()?.name)
        assertEquals("Episode count should be 45", 45, result.getOrNull()?.episodeCount)
    }

    @Test
    fun `invoke should handle edge case with minimum character id`() = runTest {
        // Given
        val characterId = 1 // ID mínimo válido
        coEvery { mockRepository.getCharacterById(characterId) } returns Result.success(testCharacter)

        // When
        val result = getCharacterByIdUseCase(characterId)

        // Then
        assertTrue("Result should be success", result.isSuccess)
        assertEquals("Character ID should be 1", 1, result.getOrNull()?.id)
    }

    @Test
    fun `invoke should handle edge case with large character id`() = runTest {
        // Given
        val characterId = 1000 // ID grande
        val largeIdCharacter = testCharacter.copy(id = characterId, name = "Unknown Character")
        coEvery { mockRepository.getCharacterById(characterId) } returns Result.success(largeIdCharacter)

        // When
        val result = getCharacterByIdUseCase(characterId)

        // Then
        assertTrue("Result should be success", result.isSuccess)
        assertEquals("Character ID should be 1000", 1000, result.getOrNull()?.id)
        assertEquals("Character name should be Unknown Character", "Unknown Character", result.getOrNull()?.name)
    }
}