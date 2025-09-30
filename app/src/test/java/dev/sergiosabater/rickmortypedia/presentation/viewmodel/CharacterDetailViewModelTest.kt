package dev.sergiosabater.rickmortypedia.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import dev.sergiosabater.rickmortypedia.domain.model.Character
import dev.sergiosabater.rickmortypedia.domain.usecase.GetCharacterByIdUseCase
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
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
    private val savedStateHandle = mockk<SavedStateHandle>(relaxed = true)
    private lateinit var viewModel: CharacterDetailViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        every { savedStateHandle.get<Int>("characterId") } returns 1

        coEvery {
            getCharacterByIdUseCase.invoke(any())
        } returns Result.success(mockk(relaxed = true))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `when loadCharacter is called with valid id, should emit Success state`() = runTest {
        // Given
        val characterId = 1
        val mockCharacter = mockk<Character>()

        coEvery {
            getCharacterByIdUseCase.invoke(characterId)
        } returns Result.success(mockCharacter)

        viewModel = CharacterDetailViewModel(savedStateHandle, getCharacterByIdUseCase)

        testScheduler.advanceUntilIdle()

        // When & Then
        viewModel.uiState.test {

            val currentState = awaitItem()
            assertTrue(currentState is CharacterDetailUiState.Success)
            assertEquals(mockCharacter, (currentState as CharacterDetailUiState.Success).character)

            cancelAndIgnoreRemainingEvents()
        }

        coVerify { getCharacterByIdUseCase.invoke(characterId) }
    }

    @Test
    fun `when use case fails with exception, should emit Error state with exception message`() =
        runTest {
            // Given
            val characterId = 1
            val exception = RuntimeException("Network error")

            coEvery {
                getCharacterByIdUseCase.invoke(characterId)
            } returns Result.failure(exception)

            viewModel = CharacterDetailViewModel(savedStateHandle, getCharacterByIdUseCase)

            testScheduler.advanceUntilIdle()

            // When & Then
            viewModel.uiState.test {
                val errorState = awaitItem()
                assertTrue(errorState is CharacterDetailUiState.Error)
                assertEquals("Network error", (errorState as CharacterDetailUiState.Error).message)

                cancelAndIgnoreRemainingEvents()
            }

            coVerify { getCharacterByIdUseCase.invoke(characterId) }
        }

    @Test
    fun `when use case fails without message, should emit Error state with default message`() =
        runTest {
            // Given
            val characterId = 1
            val exception = RuntimeException()

            coEvery {
                getCharacterByIdUseCase.invoke(characterId)
            } returns Result.failure(exception)

            viewModel = CharacterDetailViewModel(savedStateHandle, getCharacterByIdUseCase)
            testScheduler.advanceUntilIdle()

            // When & Then
            viewModel.uiState.test {
                val errorState = awaitItem()
                assertTrue(errorState is CharacterDetailUiState.Error)
                assertEquals(
                    "Error desconocido",
                    (errorState as CharacterDetailUiState.Error).message
                )

                cancelAndIgnoreRemainingEvents()
            }

            coVerify { getCharacterByIdUseCase.invoke(characterId) }
        }

    @Test
    fun `when calling loadCharacter manually after init, should emit Loading then Success`() =
        runTest {
            // Given
            val characterId = 2
            val mockCharacter = mockk<Character>()

            coEvery {
                getCharacterByIdUseCase.invoke(1)
            } returns Result.success(mockk(relaxed = true))

            coEvery {
                getCharacterByIdUseCase.invoke(characterId)
            } returns Result.success(mockCharacter)

            viewModel = CharacterDetailViewModel(savedStateHandle, getCharacterByIdUseCase)
            testScheduler.advanceUntilIdle()

            // When & Then
            viewModel.uiState.test {

                skipItems(1)
                viewModel.loadCharacter(characterId)
                assertEquals(CharacterDetailUiState.Loading, awaitItem())
                testScheduler.advanceUntilIdle()

                val successState = awaitItem()
                assertTrue(successState is CharacterDetailUiState.Success)
                assertEquals(
                    mockCharacter,
                    (successState as CharacterDetailUiState.Success).character
                )

                cancelAndIgnoreRemainingEvents()
            }

            coVerify(exactly = 1) { getCharacterByIdUseCase.invoke(1) }
            coVerify(exactly = 1) { getCharacterByIdUseCase.invoke(characterId) }
        }
}