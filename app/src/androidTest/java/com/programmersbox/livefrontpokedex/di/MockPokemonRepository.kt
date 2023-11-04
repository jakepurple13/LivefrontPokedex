package com.programmersbox.livefrontpokedex.di

import com.programmersbox.livefrontpokedex.data.Pokemon
import com.programmersbox.livefrontpokedex.data.PokemonInfo
import com.programmersbox.livefrontpokedex.data.repo.PokemonRepository
import javax.inject.Singleton

@Singleton
class MockPokemonRepository : PokemonRepository {
    override suspend fun fetchPokedexEntries(page: Int): Result<List<Pokemon>> {
        return Result.success(pokemonList)
    }

    override suspend fun fetchPokemon(name: String): Result<PokemonInfo> {
        return if (name.equals("Pikachu", true))
            Result.success(Pikachu)
        else
            Result.failure(Exception("Cannot find $name"))
    }

    companion object {
        val pokemonList = listOf(
            Pokemon(name = "Pikachu", url = "https://pokeapi.co/api/v2/pokemon/pikachu/"),
            Pokemon(name = "Charmander", url = "https://pokeapi.co/api/v2/pokemon/charmander/"),
            Pokemon(name = "Pikachu-libre", url = "https://pokeapi.co/api/v2/pokemon/pikachu-libre/")
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
    }
}