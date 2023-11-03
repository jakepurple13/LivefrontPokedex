package com.programmersbox.livefrontpokedex.screens.detail

import com.programmersbox.livefrontpokedex.data.PokemonInfo

sealed class DetailState {
    class Success(val pokemonInfo: PokemonInfo) : DetailState()

    data object Loading : DetailState()

    data object Error : DetailState()
}