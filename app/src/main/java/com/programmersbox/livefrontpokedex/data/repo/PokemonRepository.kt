package com.programmersbox.livefrontpokedex.data.repo

import com.programmersbox.livefrontpokedex.data.Pokemon
import com.programmersbox.livefrontpokedex.data.PokemonInfo

/**
 * Repository for fetching Pokémon data.
 */
interface PokemonRepository {

    /**
     * Fetches Pokedex entries from a page
     */
    suspend fun fetchPokedexEntries(page: Int): Result<List<Pokemon>>

    /**
     * Fetches a single Pokemon information
     */
    suspend fun fetchPokemon(name: String): Result<PokemonInfo>
}