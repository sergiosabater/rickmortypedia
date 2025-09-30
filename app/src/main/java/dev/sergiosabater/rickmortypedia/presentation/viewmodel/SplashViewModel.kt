package dev.sergiosabater.rickmortypedia.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sergiosabater.rickmortypedia.domain.error.DomainError
import dev.sergiosabater.rickmortypedia.domain.usecase.GetCharactersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getCharactersUseCase: GetCharactersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {

            val result = getCharactersUseCase()

            when {
                result.isSuccess -> {
                    val characters = result.getOrNull() ?: emptyList()
                    if (characters.isNotEmpty()) {
                        _uiState.value = SplashUiState.Success
                    } else {
                        _uiState.value = SplashUiState.Error("No characters were found")
                    }
                }

                else -> {
                    val exception = result.exceptionOrNull()
                    val errorMessage = if (exception is DomainError) {
                        exception.message
                    } else {
                        exception?.message ?: "Unknown error occurred"
                    }
                    _uiState.value = SplashUiState.Error(errorMessage)
                }
            }
        }
    }
}

sealed class SplashUiState {
    object Loading : SplashUiState()
    object Success : SplashUiState()
    data class Error(val message: String?) : SplashUiState()
}