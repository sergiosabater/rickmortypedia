package dev.sergiosabater.rickmortypedia.presentation.viewmodel

import app.cash.turbine.test
import dev.sergiosabater.rickmortypedia.domain.model.Character
import dev.sergiosabater.rickmortypedia.domain.usecase.GetCharacterByIdUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterDetailViewModelTest {

    private val getCharacterByIdUseCase = mockk<GetCharacterByIdUseCase>()
    private lateinit var viewModel: CharacterDetailViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = CharacterDetailViewModel(getCharacterByIdUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when loadCharacter is called with valid id, should emit Success state`() = runTest {
        // Given
        val characterId = 1
        val mockCharacter = mockk<Character>()
        coEvery { getCharacterByIdUseCase(characterId) } returns Result.success(mockCharacter)

        // When & Then
        viewModel.uiState.test {

            assertEquals(CharacterDetailUiState.Loading, awaitItem())

            viewModel.loadCharacter(characterId)
            testScheduler.advanceUntilIdle()

            val successState = awaitItem()
            assertTrue(successState is CharacterDetailUiState.Success)
            assertEquals(mockCharacter, (successState as CharacterDetailUiState.Success).character)
        }
    }

    @Test
    fun `when use case fails with exception, should emit Error state with exception message`() =
        runTest {
            // Given
            val characterId = 1
            val exception = RuntimeException("Network error")
            coEvery { getCharacterByIdUseCase(characterId) } returns Result.failure(exception)

            // When & Then
            viewModel.uiState.test {
                assertEquals(CharacterDetailUiState.Loading, awaitItem())

                viewModel.loadCharacter(characterId)
                testScheduler.advanceUntilIdle()

                val errorState = awaitItem()
                assertTrue(errorState is CharacterDetailUiState.Error)
                assertEquals("Network error", (errorState as CharacterDetailUiState.Error).message)
            }
        }

    @Test
    fun `when use case fails without message, should emit Error state with default message`() =
        runTest {
            // Given
            val characterId = 1
            val exception = RuntimeException()
            coEvery { getCharacterByIdUseCase(characterId) } returns Result.failure(exception)

            // When & Then
            viewModel.uiState.test {
                assertEquals(CharacterDetailUiState.Loading, awaitItem())

                viewModel.loadCharacter(characterId)
                testScheduler.advanceUntilIdle()

                val errorState = awaitItem()
                assertTrue(errorState is CharacterDetailUiState.Error)
                assertEquals(
                    "Error desconocido",
                    (errorState as CharacterDetailUiState.Error).message
                )
            }
        }

    @Test
    fun `initial state should be Loading`() = runTest {
        // Then
        viewModel.uiState.test {
            assertEquals(CharacterDetailUiState.Loading, awaitItem())
        }
    }
}