package com.programmersbox.livefrontpokedex.screens.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.programmersbox.livefrontpokedex.PokedexService
import com.programmersbox.livefrontpokedex.PokemonInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal class PokedexDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val service: PokedexService
): ViewModel() {
    private val name: String? = savedStateHandle["id"]
    var pokemonInfo: DetailState by mutableStateOf(DetailState.Loading)

    init {
        load()
    }

    private fun load() {
        pokemonInfo = DetailState.Loading
        viewModelScope.launch {
            name?.let { n ->
                /*val fromDb = pokedexDatabase
                    .getPokemonInfo(n)
                    ?.let { DetailState.Success(it) }*/
                pokemonInfo = /*fromDb ?:*/ service.fetchPokemon(n)
                    //.onSuccess { pokedexDatabase.insertPokemonInfo(it) }
                    .fold(
                        onSuccess = { DetailState.Success(it) },
                        onFailure = { DetailState.Error }
                    )
                /*pokedexDatabase.saved(n)
                    .onEach { savedPokemon = it }
                    .launchIn(viewModelScope)*/
            }
        }
    }
}
