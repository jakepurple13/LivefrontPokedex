package com.programmersbox.livefrontpokedex.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

private const val PAGE_SIZE: Int = 2000

class PokedexService(
    private val client: HttpClient
) {
    suspend fun fetchPokemonList(page: Int): Result<PokemonResponse> = fetchPokemonList(
        offset = page * PAGE_SIZE,
        limit = PAGE_SIZE,
    )

    private suspend fun fetchPokemonList(
        offset: Int = 0,
        limit: Int = PAGE_SIZE,
    ) = runCatching {
        client.get("pokemon/?offset=$offset&limit=$limit").body<PokemonResponse>()
    }

    suspend fun fetchPokemon(name: String): Result<PokemonInfo> = runCatching {
        client.get("pokemon/$name").body<PokemonInfo>()
            .copy(
                pokemonDescription = fetchPokemonDescription(name)
                    .onFailure { it.printStackTrace() }
                    .getOrNull()
            )
    }

    private suspend fun fetchPokemonDescription(name: String) = runCatching {
        client.get("pokemon-species/$name").body<PokemonDescription>()
    }
}
