package com.programmersbox.livefrontpokedex.screens.entries

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.programmersbox.livefrontpokedex.data.Pokemon
import com.programmersbox.livefrontpokedex.data.repo.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokedexEntriesViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    private val pokedexEntries = mutableStateListOf<Pokemon>()
    var isLoading by mutableStateOf(false)
        private set
    var hasError by mutableStateOf(false)
        private set

    var searchQuery by mutableStateOf("")
        private set

    var isSearchActive by mutableStateOf(false)
        private set

    val filteredEntries by derivedStateOf {
        pokedexEntries.filter { it.name.contains(searchQuery, true) }
    }

    init {
        loadEntries()
    }

    fun loadEntries() {
        viewModelScope.launch {
            isLoading = true
            hasError = false
            repository
                .fetchPokedexEntries(0)
                .onSuccess {
                    isLoading = false
                    pokedexEntries.clear()
                    pokedexEntries.addAll(it)
                }
                .onFailure {
                    isLoading = false
                    hasError = true
                }
        }
    }

    fun onSearchQueryChange(updatedQuery: String) {
        searchQuery = updatedQuery
    }

    fun changeSearchActiveState(state: Boolean) {
        isSearchActive = state
    }
}