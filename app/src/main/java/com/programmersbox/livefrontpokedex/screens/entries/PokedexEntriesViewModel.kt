package com.programmersbox.livefrontpokedex.screens.entries

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

    val pokedexEntries = mutableStateListOf<Pokemon>()
    var isLoading by mutableStateOf(false)
    var hasError by mutableStateOf(false)

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
                    pokedexEntries.addAll(it)
                }
                .onFailure {
                    isLoading = false
                    hasError = true
                }
        }
    }
}