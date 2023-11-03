package com.programmersbox.livefrontpokedex.screens.detail

import com.programmersbox.livefrontpokedex.PokemonInfo

internal sealed class DetailState {
    class Success(val pokemonInfo: PokemonInfo) : DetailState()

    data object Loading : DetailState()

    data object Error : DetailState()
}