package com.programmersbox.livefrontpokedex.viewmodels

import com.programmersbox.livefrontpokedex.data.repo.PokemonRepository
import com.programmersbox.livefrontpokedex.pokemonList
import com.programmersbox.livefrontpokedex.screens.entries.PokedexEntriesViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PokedexEntriesViewModelTest {
    private lateinit var viewModel: PokedexEntriesViewModel
    private val repository: PokemonRepository = mockk()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @Test
    fun `PokedexEntriesViewModel loads data successfully`() = runTest {
        coEvery { repository.fetchPokedexEntries(0) } returns Result.success(pokemonList)

        viewModel = PokedexEntriesViewModel(repository)

        assertEquals(pokemonList, viewModel.filteredEntries.toList())
        assertFalse(viewModel.isLoading)
        assertFalse(viewModel.hasError)
    }

    @Test
    fun `PokedexEntriesViewModel fails loading data`() = runTest {
        coEvery { repository.fetchPokedexEntries(0) } returns Result.failure(Exception("Something went wrong"))

        viewModel = PokedexEntriesViewModel(repository)

        assertTrue(viewModel.filteredEntries.isEmpty())
        assertFalse(viewModel.isLoading)
        assertTrue(viewModel.hasError)
    }

    @Test
    fun `PokedexEntriesViewModel searches for Pikachu`() = runTest {
        coEvery { repository.fetchPokedexEntries(0) } returns Result.success(pokemonList)

        viewModel = PokedexEntriesViewModel(repository)

        viewModel.onSearchQueryChange("Pikachu")

        assertTrue(viewModel.filteredEntries.all { it.name.contains("Pikachu", true) })
    }
}