package com.programmersbox.livefrontpokedex.data.repo

import com.programmersbox.livefrontpokedex.data.PokedexService
import com.programmersbox.livefrontpokedex.data.Pokemon
import com.programmersbox.livefrontpokedex.data.PokemonInfo
import javax.inject.Inject

class PokemonRepositoryImpl @Inject constructor(
    private val service: PokedexService
) : PokemonRepository {
    private val pokedexEntries = mutableListOf<Pokemon>()
    private val pokemonEntries = mutableMapOf<String, PokemonInfo>()
    override suspend fun fetchPokedexEntries(page: Int): Result<List<Pokemon>> {
        return runCatching {
            if (pokedexEntries.isEmpty()) {
                pokedexEntries.addAll(service.fetchPokemonList(page).getOrNull()?.results.orEmpty())
            }
            pokedexEntries
        }
    }

    override suspend fun fetchPokemon(name: String): Result<PokemonInfo> {
        return runCatching {
            pokemonEntries[name] ?: service.fetchPokemon(name)
                .onSuccess { pokemonEntries[name] = it }
                .getOrThrow()
        }
    }
}