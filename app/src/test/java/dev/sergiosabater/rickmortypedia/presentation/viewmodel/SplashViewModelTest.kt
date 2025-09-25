package dev.sergiosabater.rickmortypedia.presentation.viewmodel

import app.cash.turbine.test
import dev.sergiosabater.rickmortypedia.domain.error.DomainError
import dev.sergiosabater.rickmortypedia.domain.model.Character
import dev.sergiosabater.rickmortypedia.domain.usecase.GetCharactersUseCase
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {

    private val getCharactersUseCase = mockk<GetCharactersUseCase>()
    private lateinit var viewModel: SplashViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when use case returns characters, should emit Success state`() = runTest {
        // Given
        val characters = listOf(mockk<Character>(), mockk<Character>())
        coEvery { getCharactersUseCase() } returns Result.success(characters)

        // When
        viewModel = SplashViewModel(getCharactersUseCase)

        // Then
        viewModel.uiState.test {
            assertEquals(SplashUiState.Loading, awaitItem())
            assertEquals(SplashUiState.Success, awaitItem())
        }
    }

    @Test
    fun `when use case returns empty list, should emit Error state`() = runTest {
        // Given
        coEvery { getCharactersUseCase() } returns Result.success(emptyList())

        // When
        viewModel = SplashViewModel(getCharactersUseCase)

        // Then
        viewModel.uiState.test {
            assertEquals(SplashUiState.Loading, awaitItem())
            val errorState = awaitItem()

            assertTrue(errorState is SplashUiState.Error)
            assertEquals("No characters were found", (errorState as SplashUiState.Error).message)
        }
    }

    @Test
    fun `when use case fails with DomainError, should emit Error state with domain message`() =
        runTest {
            // Given
            val domainError = mockk<DomainError> {
                every { message } returns "Domain specific error"
            }
            coEvery { getCharactersUseCase() } returns Result.failure(domainError)

            // When
            viewModel = SplashViewModel(getCharactersUseCase)

            // Then
            viewModel.uiState.test {
                assertEquals(SplashUiState.Loading, awaitItem())
                val errorState = awaitItem()
                assertTrue(errorState is SplashUiState.Error)
                assertEquals("Domain specific error", (errorState as SplashUiState.Error).message)
            }
        }

    @Test
    fun `when use case fails with generic exception, should emit Error state with exception message`() =
        runTest {
            // Given
            val exception = RuntimeException("Network error")
            coEvery { getCharactersUseCase() } returns Result.failure(exception)

            // When
            viewModel = SplashViewModel(getCharactersUseCase)

            // Then
            viewModel.uiState.test {
                assertEquals(SplashUiState.Loading, awaitItem())
                val errorState = awaitItem()
                assertTrue(errorState is SplashUiState.Error)
                assertEquals("Network error", (errorState as SplashUiState.Error).message)
            }
        }
}
