package com.programmersbox.livefrontpokedex.data.repo

import com.programmersbox.livefrontpokedex.data.Pokemon
import com.programmersbox.livefrontpokedex.data.PokemonInfo

/**
 * Repository for fetching Pokémon data.
 */
interface PokemonRepository {
    suspend fun fetchPokedexEntries(page: Int): Result<List<Pokemon>>
    suspend fun fetchPokemon(name: String): Result<PokemonInfo>
}