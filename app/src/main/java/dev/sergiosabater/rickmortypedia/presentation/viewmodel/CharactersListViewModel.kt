package dev.sergiosabater.rickmortypedia.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sergiosabater.rickmortypedia.domain.model.Character
import dev.sergiosabater.rickmortypedia.domain.usecase.GetCharactersUseCase
import dev.sergiosabater.rickmortypedia.domain.usecase.SearchCharactersUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharactersListViewModel @Inject constructor(
    private val getCharactersUseCase: GetCharactersUseCase,
    private val searchCharactersUseCase: SearchCharactersUseCase
) : ViewModel() {

    private val _characters = MutableStateFlow<List<Character>>(emptyList())
    val characters: StateFlow<List<Character>> = _characters.asStateFlow()


    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()


    private val _filteredCharacters = MutableStateFlow<List<Character>>(emptyList())
    val filteredCharacters: StateFlow<List<Character>> = _filteredCharacters.asStateFlow()


    private val _uiState = MutableStateFlow<CharactersListUiState>(CharactersListUiState.Loading)
    val uiState: StateFlow<CharactersListUiState> = _uiState.asStateFlow()

    // Debounce para búsquedas
    private var searchJob: Job? = null

    init {
        loadCharacters()
        setupSearchListener()
    }

    private fun loadCharacters() {
        viewModelScope.launch {
            _uiState.value = CharactersListUiState.Loading

            val result = getCharactersUseCase()
            if (result.isSuccess) {
                val loadedCharacters = result.getOrNull() ?: emptyList()
                _characters.value = loadedCharacters
                _filteredCharacters.value = loadedCharacters // Al inicio, mostrar todos
                _uiState.value = CharactersListUiState.Success
            } else {
                _uiState.value = CharactersListUiState.Error
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query

        // Cancelar búsqueda anterior
        searchJob?.cancel()

        // Nueva búsqueda con debounce de 300ms
        searchJob = viewModelScope.launch {
            delay(300)
            performSearch(query)
        }
    }

    private suspend fun performSearch(query: String) {
        if (query.isEmpty()) {
            // Si no hay query, mostrar todos los personajes
            _filteredCharacters.value = _characters.value
        } else {
            // Buscar en la base de datos local
            val result = searchCharactersUseCase(name = query)
            if (result.isSuccess) {
                _filteredCharacters.value = result.getOrNull() ?: emptyList()
            } else {
                // Fallback: filtrar localmente
                _filteredCharacters.value = _characters.value.filter { character ->
                    character.name.contains(query, ignoreCase = true)
                }
            }
        }
    }

    fun onCharacterClick(character: Character) {
        _uiState.value = CharactersListUiState.NavigateToDetail(character.id)
    }

    fun retry() {
        loadCharacters()
    }

    private fun setupSearchListener() {
        // Inicializar con búsqueda vacía
        viewModelScope.launch {
            performSearch("")
        }
    }
}

// Estado extendido para navegación
sealed class CharactersListUiState {
    object Loading : CharactersListUiState()
    object Success : CharactersListUiState()
    object Error : CharactersListUiState()
    data class NavigateToDetail(val characterId: Int) : CharactersListUiState()
}