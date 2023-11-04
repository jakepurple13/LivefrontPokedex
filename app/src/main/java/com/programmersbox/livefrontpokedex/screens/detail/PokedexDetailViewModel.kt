package com.programmersbox.livefrontpokedex.screens.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.programmersbox.livefrontpokedex.data.repo.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokedexDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: PokemonRepository
) : ViewModel() {
    private val name: String? = savedStateHandle["id"]
    var pokemonInfo: DetailState by mutableStateOf(DetailState.Loading)

    init {
        loadPokemon()
    }

    fun loadPokemon() {
        pokemonInfo = DetailState.Loading
        viewModelScope.launch {
            val state = name?.let { n ->
                repository.fetchPokemon(n)
                    .fold(
                        onSuccess = { println(it);DetailState.Success(it) },
                        onFailure = { DetailState.Error }
                    )
            } ?: DetailState.Error
            pokemonInfo = state
        }
    }
}
