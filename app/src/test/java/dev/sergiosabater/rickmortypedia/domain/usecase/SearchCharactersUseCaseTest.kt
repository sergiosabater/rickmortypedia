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

class SearchCharactersUseCaseTest {

    private lateinit var mockRepository: CharacterRepository
    private lateinit var searchCharactersUseCase: SearchCharactersUseCase


    private val testCharacters = listOf(
        Character(
            id = 1,
            name = "Rick Sanchez",
            status = CharacterStatus.ALIVE,
            species = "Human",
            type = "Genius",
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
        ),
        Character(
            id = 3,
            name = "Summer Smith",
            status = CharacterStatus.ALIVE,
            species = "Human",
            type = "",
            gender = "Female",
            origin = "Earth (Replacement Dimension)",
            location = "Earth (Replacement Dimension)",
            image = "https://rickandmortyapi.com/api/character/avatar/3.jpeg",
            episodeCount = 32
        ),
        Character(
            id = 4,
            name = "Alien Rick",
            status = CharacterStatus.UNKNOWN,
            species = "Alien",
            type = "",
            gender = "Male",
            origin = "Unknown",
            location = "Citadel of Ricks",
            image = "https://rickandmortyapi.com/api/character/avatar/4.jpeg",
            episodeCount = 10
        )
    )

    @Before
    fun setUp() {
        mockRepository = mockk()
        searchCharactersUseCase = SearchCharactersUseCase(mockRepository)
    }

    @Test
    fun `invoke should return success with characters when searching by name`() = runTest {
        // Given
        val searchName = "Rick"
        val expectedResults =
            listOf(testCharacters[0], testCharacters[3]) // Rick Sanchez y Alien Rick
        coEvery {
            mockRepository.searchCharacters(name = searchName, status = null, species = null)
        } returns Result.success(expectedResults)

        // When
        val result = searchCharactersUseCase(name = searchName)

        // Then
        assertTrue("Result should be success", result.isSuccess)
        assertEquals("Should return 2 characters with 'Rick' in name", 2, result.getOrNull()?.size)
        assertTrue("Should contain Rick Sanchez", result.getOrNull()?.any { it.id == 1 } == true)
        assertTrue("Should contain Alien Rick", result.getOrNull()?.any { it.id == 4 } == true)
    }

    @Test
    fun `invoke should return empty list when no characters match name search`() = runTest {
        // Given
        val searchName = "NonExistentCharacter"
        coEvery {
            mockRepository.searchCharacters(name = searchName, status = null, species = null)
        } returns Result.success(emptyList())

        // When
        val result = searchCharactersUseCase(name = searchName)

        // Then
        assertTrue("Result should be success", result.isSuccess)
        assertTrue("List should be empty", result.getOrNull()?.isEmpty() == true)
    }

    @Test
    fun `invoke should return success when searching by status`() = runTest {
        // Given
        val searchStatus = "alive"
        val expectedResults =
            listOf(testCharacters[0], testCharacters[1], testCharacters[2]) // Todos vivos
        coEvery {
            mockRepository.searchCharacters(name = null, status = searchStatus, species = null)
        } returns Result.success(expectedResults)

        // When
        val result = searchCharactersUseCase(status = searchStatus)

        // Then
        assertTrue("Result should be success", result.isSuccess)
        assertEquals("Should return 3 alive characters", 3, result.getOrNull()?.size)
        assertTrue(
            "All characters should be alive",
            result.getOrNull()?.all { it.status == CharacterStatus.ALIVE } == true)
    }

    @Test
    fun `invoke should return success when searching by species`() = runTest {
        // Given
        val searchSpecies = "Alien"
        val expectedResults = listOf(testCharacters[3]) // Solo Alien Rick
        coEvery {
            mockRepository.searchCharacters(name = null, status = null, species = searchSpecies)
        } returns Result.success(expectedResults)

        // When
        val result = searchCharactersUseCase(species = searchSpecies)

        // Then
        assertTrue("Result should be success", result.isSuccess)
        assertEquals("Should return 1 alien character", 1, result.getOrNull()?.size)
        assertEquals("Should be Alien Rick", "Alien Rick", result.getOrNull()?.first()?.name)
    }

    @Test
    fun `invoke should return success when searching with multiple parameters`() = runTest {
        // Given
        val searchName = "Rick"
        val searchStatus = "alive"
        val searchSpecies = "Human"
        val expectedResults = listOf(testCharacters[0]) // Solo Rick Sanchez (vivo, humano)
        coEvery {
            mockRepository.searchCharacters(
                name = searchName,
                status = searchStatus,
                species = searchSpecies
            )
        } returns Result.success(expectedResults)

        // When
        val result = searchCharactersUseCase(
            name = searchName,
            status = searchStatus,
            species = searchSpecies
        )

        // Then
        assertTrue("Result should be success", result.isSuccess)
        assertEquals("Should return 1 character", 1, result.getOrNull()?.size)
        assertEquals("Should be Rick Sanchez", "Rick Sanchez", result.getOrNull()?.first()?.name)
    }

    @Test
    fun `invoke should return success with all characters when no search parameters provided`() =
        runTest {
            // Given
            coEvery {
                mockRepository.searchCharacters(name = null, status = null, species = null)
            } returns Result.success(testCharacters)

            // When
            val result = searchCharactersUseCase()

            // Then
            assertTrue("Result should be success", result.isSuccess)
            assertEquals("Should return all 4 characters", 4, result.getOrNull()?.size)
        }

    @Test
    fun `invoke should return failure when repository fails`() = runTest {
        // Given
        val expectedError = Exception("Search failed")
        coEvery {
            mockRepository.searchCharacters(name = "Rick", status = null, species = null)
        } returns Result.failure(expectedError)

        // When
        val result = searchCharactersUseCase(name = "Rick")

        // Then
        assertTrue("Result should be failure", result.isFailure)
        assertEquals("Error should match", expectedError, result.exceptionOrNull())
    }

    @Test
    fun `invoke should handle case insensitive search`() = runTest {
        // Given
        val searchName = "rick" // minúsculas
        val expectedResults =
            listOf(testCharacters[0], testCharacters[3]) // Rick Sanchez y Alien Rick
        coEvery {
            mockRepository.searchCharacters(name = searchName, status = null, species = null)
        } returns Result.success(expectedResults)

        // When
        val result = searchCharactersUseCase(name = searchName)

        // Then
        assertTrue("Result should be success", result.isSuccess)
        assertEquals("Should return 2 characters", 2, result.getOrNull()?.size)
    }

    @Test
    fun `invoke should handle partial name matches`() = runTest {
        // Given
        val searchName = "Smith"
        val expectedResults =
            listOf(testCharacters[1], testCharacters[2]) // Morty Smith y Summer Smith
        coEvery {
            mockRepository.searchCharacters(name = searchName, status = null, species = null)
        } returns Result.success(expectedResults)

        // When
        val result = searchCharactersUseCase(name = searchName)

        // Then
        assertTrue("Result should be success", result.isSuccess)
        assertEquals("Should return 2 Smith characters", 2, result.getOrNull()?.size)
        assertTrue("Should contain Morty Smith", result.getOrNull()?.any { it.id == 2 } == true)
        assertTrue("Should contain Summer Smith", result.getOrNull()?.any { it.id == 3 } == true)
    }

    @Test
    fun `invoke should handle empty string parameters`() = runTest {
        // Given
        coEvery {
            mockRepository.searchCharacters(name = "", status = "", species = "")
        } returns Result.success(testCharacters)

        // When
        val result = searchCharactersUseCase(name = "", status = "", species = "")

        // Then
        assertTrue("Result should be success", result.isSuccess)
        assertEquals("Should return all characters", 4, result.getOrNull()?.size)
    }

    @Test
    fun `invoke should handle special characters in search query`() = runTest {
        // Given
        val searchName = "Rick-117"
        val specialCharacter = testCharacters[0].copy(id = 5, name = "Rick-117")
        val expectedResults = listOf(specialCharacter)
        coEvery {
            mockRepository.searchCharacters(name = searchName, status = null, species = null)
        } returns Result.success(expectedResults)

        // When
        val result = searchCharactersUseCase(name = searchName)

        // Then
        assertTrue("Result should be success", result.isSuccess)
        assertEquals("Should return 1 character", 1, result.getOrNull()?.size)
        assertEquals("Should be Rick-117", "Rick-117", result.getOrNull()?.first()?.name)
    }
}