package com.programmersbox.livefrontpokedex.data.repo

import com.programmersbox.livefrontpokedex.data.PokedexService
import com.programmersbox.livefrontpokedex.data.Pokemon
import com.programmersbox.livefrontpokedex.data.PokemonInfo
import javax.inject.Inject

/**
 * An implementation of the [PokemonRepository] interface.
 *
 * This class is responsible for managing the interaction with the Pokemon data.
 * It retrieves the Pokemon data from the [PokedexService] and caches the data locally.
 *
 * @property service The [PokedexService] instance to retrieve the Pokemon data.
 * @property pokedexEntries The list of Pokemon retrieved from the Pokedex service.
 * @property pokemonEntries The map of PokemonInfo objects retrieved from the Pokedex service, with the Pokemon name as the key.
 */
class PokemonRepositoryImpl @Inject constructor(
    private val service: PokedexService
) : PokemonRepository {
    private val pokedexEntries = mutableListOf<Pokemon>()
    private val pokemonEntries = mutableMapOf<String, PokemonInfo>()
    override suspend fun fetchPokedexEntries(page: Int): Result<List<Pokemon>> {
        return runCatching {
            if (pokedexEntries.isEmpty()) {
                pokedexEntries.addAll(service.fetchPokemonList(page).getOrThrow().results)
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