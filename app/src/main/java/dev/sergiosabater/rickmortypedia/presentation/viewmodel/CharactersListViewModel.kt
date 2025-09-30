package dev.sergiosabater.rickmortypedia.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sergiosabater.rickmortypedia.domain.model.Character
import dev.sergiosabater.rickmortypedia.domain.usecase.GetCharactersUseCase
import dev.sergiosabater.rickmortypedia.domain.usecase.SearchCharactersUseCase
import dev.sergiosabater.rickmortypedia.presentation.ui.component.SpeciesFilter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    private val _navigationEvent = MutableSharedFlow<Int>()
    val navigationEvent: SharedFlow<Int> = _navigationEvent.asSharedFlow()

    private val _selectedSpecies = MutableStateFlow(SpeciesFilter.ALL)
    val selectedSpecies: StateFlow<SpeciesFilter> = _selectedSpecies.asStateFlow()

    //For debounce
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

        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(300)
            performSearch(query)
        }
    }

    private suspend fun performSearch(query: String) {
        val currentSpeciesFilter = _selectedSpecies.value

        if (query.isEmpty() && currentSpeciesFilter == SpeciesFilter.ALL) {
            // Show all
            _filteredCharacters.value = _characters.value
        } else {
            // Filter by name
            val searchResults = if (query.isNotEmpty()) {
                val result = searchCharactersUseCase(name = query)
                if (result.isSuccess) {
                    result.getOrNull() ?: emptyList()
                } else {
                    _characters.value.filter { character ->
                        character.name.contains(query, ignoreCase = true)
                    }
                }
            } else {
                _characters.value
            }

            // Apply filter by species
            _filteredCharacters.value = if (currentSpeciesFilter != SpeciesFilter.ALL) {
                searchResults.filter { character ->
                    character.species.equals(currentSpeciesFilter.filterValue, ignoreCase = true)
                }
            } else {
                searchResults
            }
        }
    }

    fun onCharacterClick(character: Character) {
        viewModelScope.launch {
            _navigationEvent.emit(character.id)
        }
    }

    private fun setupSearchListener() {
        viewModelScope.launch {
            performSearch("")
        }
    }

    fun onSpeciesFilterChange(species: SpeciesFilter) {
        _selectedSpecies.value = species
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            performSearch(_searchQuery.value)
        }
    }
}

sealed class CharactersListUiState {
    object Loading : CharactersListUiState()
    object Success : CharactersListUiState()
    object Error : CharactersListUiState()
}