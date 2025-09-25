package dev.sergiosabater.rickmortypedia.presentation.viewmodel


import app.cash.turbine.test
import dev.sergiosabater.rickmortypedia.domain.usecase.GetCharactersUseCase
import dev.sergiosabater.rickmortypedia.domain.usecase.SearchCharactersUseCase
import io.mockk.coEvery
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
import org.junit.Before
import org.junit.Test
import dev.sergiosabater.rickmortypedia.domain.model.Character


@OptIn(ExperimentalCoroutinesApi::class)
class CharactersListViewModelTest {

    private val getCharactersUseCase = mockk<GetCharactersUseCase>()
    private val searchCharactersUseCase = mockk<SearchCharactersUseCase>()
    private lateinit var viewModel: CharactersListViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val testCharacters = listOf(
        mockk<Character>(),
        mockk<Character>()
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when characters are loaded successfully, should emit Success state and update characters`() = runTest {
        // Given
        coEvery { getCharactersUseCase() } returns Result.success(testCharacters)

        // When
        viewModel = CharactersListViewModel(getCharactersUseCase, searchCharactersUseCase)
        testScheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            assertEquals(CharactersListUiState.Success, awaitItem())
        }

        viewModel.characters.test {
            assertEquals(testCharacters, awaitItem())
        }
    }

    @Test
    fun `when characters loading fails, should emit Error state`() = runTest {
        // Given
        coEvery { getCharactersUseCase() } returns Result.failure(RuntimeException("Network error"))

        // When
        viewModel = CharactersListViewModel(getCharactersUseCase, searchCharactersUseCase)
        testScheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            assertEquals(CharactersListUiState.Error, awaitItem())
        }
    }

    @Test
    fun `when search query changes, should update search query state`() = runTest {
        // Given
        coEvery { getCharactersUseCase() } returns Result.success(testCharacters)
        coEvery { searchCharactersUseCase(name = "Rick") } returns Result.success(emptyList())
        viewModel = CharactersListViewModel(getCharactersUseCase, searchCharactersUseCase)
        testScheduler.advanceUntilIdle()

        // When
        viewModel.searchQuery.test {
            assertEquals("", awaitItem()) // Valor inicial

            viewModel.onSearchQueryChange("Rick")

            assertEquals("Rick", awaitItem()) // Nuevo valor
        }
    }

    @Test
    fun `when character is clicked, should emit navigation event`() = runTest {
        // Given
        val testCharacter = mockk<Character> {
            every { id } returns 1
        }
        coEvery { getCharactersUseCase() } returns Result.success(testCharacters)
        viewModel = CharactersListViewModel(getCharactersUseCase, searchCharactersUseCase)
        testScheduler.advanceUntilIdle()

        // When & Then
        viewModel.navigationEvent.test {
            viewModel.onCharacterClick(testCharacter)
            testScheduler.advanceUntilIdle()

            assertEquals(1, awaitItem())
        }
    }
}