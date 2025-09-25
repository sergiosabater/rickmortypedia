package dev.sergiosabater.rickmortypedia.domain.usecase

import dev.sergiosabater.rickmortypedia.data.repository.CharacterRepository
import dev.sergiosabater.rickmortypedia.domain.model.Character
import dev.sergiosabater.rickmortypedia.domain.model.CharacterStatus
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetCharactersUseCaseTest {

    private lateinit var mockRepository: CharacterRepository
    private lateinit var getCharactersUseCase: GetCharactersUseCase

    private val testCharacters = listOf(
        Character(
            id = 1,
            name = "Rick Sanchez",
            status = CharacterStatus.ALIVE,
            species = "Human",
            type = "",
            gender = "Male",
            origin = "Earth (C-137)",
            location = "Citadel of Ricks",
            image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
            episodeCount = 51
        ),
        Character(
            id = 2,
            name = "Morty Smith",
            status = CharacterStatus.ALIVE,
            species = "Human",
            type = "",
            gender = "Male",
            origin = "Earth (C-137)",
            location = "Citadel of Ricks",
            image = "https://rickandmortyapi.com/api/character/avatar/2.jpeg",
            episodeCount = 45
        )
    )

    @Before
    fun setUp() {
        mockRepository = mockk()
        getCharactersUseCase = GetCharactersUseCase(mockRepository)
    }

    @Test
    fun `invoke should return success with characters when repository succeeds`() = runTest {
        // Given (Arrange)
        coEvery { mockRepository.getCharacters(null) } returns Result.success(testCharacters)

        // When (Act)
        val result = getCharactersUseCase()

        // Then (Assert)
        assertTrue(result.isSuccess)
        assertEquals(testCharacters, result.getOrNull())
        assertEquals(2, result.getOrNull()?.size)
        assertEquals("Rick Sanchez", result.getOrNull()?.first()?.name)
    }

    @Test
    fun `invoke should return success with empty list when repository returns empty list`() =
        runTest {
            // Given
            coEvery { mockRepository.getCharacters(null) } returns Result.success(emptyList())

            // When
            val result = getCharactersUseCase()

            // Then
            assertTrue(result.isSuccess)
            assertTrue(result.getOrNull()?.isEmpty() == true)
        }

    @Test
    fun `invoke should return failure when repository fails`() = runTest {
        // Given
        val expectedError = Exception("Network error")
        coEvery { mockRepository.getCharacters(null) } returns Result.failure(expectedError)

        // When
        val result = getCharactersUseCase()

        // Then
        assertTrue(result.isFailure)
        assertEquals(expectedError, result.exceptionOrNull())
    }

    @Test
    fun `invoke with page parameter should call repository with correct page`() = runTest {
        // Given
        val testPage = 2
        coEvery { mockRepository.getCharacters(testPage) } returns Result.success(testCharacters)

        // When
        val result = getCharactersUseCase(page = testPage)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(testCharacters, result.getOrNull())
    }

    @Test
    fun `invoke should preserve character data integrity`() = runTest {
        // Given
        coEvery { mockRepository.getCharacters(null) } returns Result.success(testCharacters)

        // When
        val result = getCharactersUseCase()

        // Then
        val character = result.getOrNull()?.first()
        assertEquals(1, character?.id)
        assertEquals("Rick Sanchez", character?.name)
        assertEquals(CharacterStatus.ALIVE, character?.status)
        assertEquals("Human", character?.species)
        assertEquals(51, character?.episodeCount)
    }
}