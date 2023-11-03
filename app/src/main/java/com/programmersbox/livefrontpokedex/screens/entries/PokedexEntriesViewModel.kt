package com.programmersbox.livefrontpokedex.screens.entries

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.programmersbox.livefrontpokedex.PokedexService
import com.programmersbox.livefrontpokedex.Pokemon
import kotlinx.coroutines.launch

class PokedexEntriesViewModel(
    service: PokedexService = PokedexService()
) : ViewModel() {

    val pokedexEntries = mutableStateListOf<Pokemon>()

    init {
        /*pokedexDatabase.getPokemonList()
            .onEach {
                pokedexEntries.clear()
                pokedexEntries.addAll(it)
            }
            .launchIn(viewModelScope)*/
        viewModelScope.launch {
            service.fetchPokemonList(0).onSuccess { pokedexEntries.addAll(it.results) }
        }
    }
}