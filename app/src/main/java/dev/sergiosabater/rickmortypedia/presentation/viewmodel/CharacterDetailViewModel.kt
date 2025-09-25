package dev.sergiosabater.rickmortypedia.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sergiosabater.rickmortypedia.domain.model.Character
import dev.sergiosabater.rickmortypedia.domain.usecase.GetCharacterByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharacterDetailViewModel @Inject constructor(
    private val getCharacterByIdUseCase: GetCharacterByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CharacterDetailUiState>(CharacterDetailUiState.Loading)
    val uiState: StateFlow<CharacterDetailUiState> = _uiState.asStateFlow()

    fun loadCharacter(characterId: Int) {
        viewModelScope.launch {
            _uiState.value = CharacterDetailUiState.Loading
            val result = getCharacterByIdUseCase(characterId)

            when {
                result.isSuccess -> {
                    val character = result.getOrNull()
                    if (character != null) {
                        _uiState.value = CharacterDetailUiState.Success(character)
                    } else {
                        _uiState.value = CharacterDetailUiState.Error("Personaje no encontrado")
                    }
                }

                else -> {
                    val errorMessage = result.exceptionOrNull()?.message ?: "Error desconocido"
                    _uiState.value = CharacterDetailUiState.Error(errorMessage)
                }
            }
        }
    }
}

sealed class CharacterDetailUiState {
    object Loading : CharacterDetailUiState()
    data class Success(val character: Character) : CharacterDetailUiState()
    data class Error(val message: String) : CharacterDetailUiState()
}