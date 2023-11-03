package com.programmersbox.livefrontpokedex

sealed class PokedexScreen(val route: String) {
    data object PokedexEntries: PokedexScreen("pokedex")
    data object PokedexDetail: PokedexScreen("pokedex/detail/{id}")
}