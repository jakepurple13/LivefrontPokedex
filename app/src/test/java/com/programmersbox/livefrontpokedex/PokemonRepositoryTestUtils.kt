package com.programmersbox.livefrontpokedex

import com.programmersbox.livefrontpokedex.data.PokedexService
import com.programmersbox.livefrontpokedex.data.PokemonResponse
import io.mockk.coEvery

fun PokedexService.returnSuccess() {
    coEvery { fetchPokemonList(0) } returns Result.success(
        PokemonResponse(count = 2, next = null, previous = null, results = pokemonList)
    )

    coEvery { fetchPokemon("Pikachu") } returns Result.success(Pikachu)
}

fun PokedexService.returnFailure() {
    coEvery { fetchPokemonList(0) } returns Result.failure(Exception("Something went wrong"))
    coEvery { fetchPokemon("Pikachu") } returns Result.failure(Exception("Cannot find entry"))
}