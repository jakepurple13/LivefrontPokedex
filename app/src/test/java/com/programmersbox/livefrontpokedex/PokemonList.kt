package com.programmersbox.livefrontpokedex

import com.programmersbox.livefrontpokedex.data.Pokemon
import com.programmersbox.livefrontpokedex.data.PokemonInfo

val pokemonList = listOf(
    Pokemon(name = "Pikachu", url = "https://pokeapi.co/api/v2/pokemon/25/"),
    Pokemon(name = "Charmander", url = "https://pokeapi.co/api/v2/pokemon/4/"),
    Pokemon(name = "Pikachu-libre", url = "https://pokeapi.co/api/v2/pokemon/10084/")
)

val Pikachu = PokemonInfo(
    id = 25,
    name = "Pikachu",
    height = 4,
    weight = 60,
    experience = 1,
    types = listOf(
        PokemonInfo.TypeResponse(
            slot = 1,
            type = PokemonInfo.Type("electric")
        )
    ),
    stats = listOf(
        PokemonInfo.Stats(
            baseStat = 55,
            stat = PokemonInfo.Stat("attack")
        ),
        PokemonInfo.Stats(
            baseStat = 90,
            stat = PokemonInfo.Stat("speed")
        )
    ),
    pokemonDescription = null,
    sprites = null
)