package com.programmersbox.livefrontpokedex

import com.programmersbox.livefrontpokedex.data.PokedexService
import com.programmersbox.livefrontpokedex.data.repo.PokemonRepositoryImpl
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PokemonRepositoryTest {
    private val mockService: PokedexService = mockk()
    private lateinit var repository: PokemonRepositoryImpl

    @Before
    fun setup() {
        repository = PokemonRepositoryImpl(mockService)
    }

    @Test
    fun `PokedexService loads and saves`() = runTest {
        mockService.returnSuccess()
        val result = repository.fetchPokedexEntries(0).getOrNull()

        assertEquals(pokemonList, result)
    }

    @Test
    fun `PokedexService fails`() = runTest {
        mockService.returnFailure()
        val result = repository.fetchPokedexEntries(0).isFailure

        assertTrue(result)
    }

    @Test
    fun `PokedexService loads Pikachu`() = runTest {
        mockService.returnSuccess()

        //Load entries
        val result = repository.fetchPokedexEntries(0)
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull().orEmpty().any { it.name == "Pikachu" })

        //Fetching pokemon
        val pokemonResult = mockService.fetchPokemon("Pikachu")
        assertTrue(pokemonResult.isSuccess)
        assertEquals(Pikachu, pokemonResult.getOrNull())

        //Fetching from cache, should be faster than above
        val pokemonResultFromCache = mockService.fetchPokemon("Pikachu")
        assertTrue(pokemonResultFromCache.isSuccess)
        assertEquals(Pikachu, pokemonResultFromCache.getOrNull())
    }

    @Test
    fun `PokedexService fails loading Pikachu`() = runTest {
        mockService.returnFailure()

        //Fetching but there is an error. Maybe bad name or network error.
        val pokemonResult = mockService.fetchPokemon("Pikachu")
        assertTrue(pokemonResult.isFailure)
        assertTrue(pokemonResult.getOrNull() == null)
    }
}