package dev.sergiosabater.rickmortypedia.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sergiosabater.rickmortypedia.presentation.ui.theme.ThemePreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val themePreferences: ThemePreferences
) : ViewModel() {

    private val _isDarkTheme = MutableStateFlow<Boolean?>(null)
    val isDarkTheme: StateFlow<Boolean?> = _isDarkTheme.asStateFlow()

    init {
        observeThemePreference()
    }

    private fun observeThemePreference() {
        viewModelScope.launch {
            themePreferences.isDarkTheme.collect { isDark ->
                _isDarkTheme.value = isDark
            }
        }
    }

    fun toggleTheme() {
        viewModelScope.launch {
            themePreferences.toggleTheme()
        }
    }

    fun setDarkTheme(isDark: Boolean) {
        viewModelScope.launch {
            themePreferences.setDarkTheme(isDark)
        }
    }
}