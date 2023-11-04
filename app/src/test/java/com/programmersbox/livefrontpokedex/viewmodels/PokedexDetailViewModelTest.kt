package com.programmersbox.livefrontpokedex.viewmodels

import androidx.lifecycle.SavedStateHandle
import com.programmersbox.livefrontpokedex.Pikachu
import com.programmersbox.livefrontpokedex.data.repo.PokemonRepository
import com.programmersbox.livefrontpokedex.screens.detail.DetailState
import com.programmersbox.livefrontpokedex.screens.detail.PokedexDetailViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PokedexDetailViewModelTest {
    private lateinit var viewModel: PokedexDetailViewModel
    private val mockkSaveStateHandle: SavedStateHandle = mockk()
    private val repository: PokemonRepository = mockk()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @Test
    fun `PokedexDetailViewModel successfully fetches Pokemon`() = runTest {
        coEvery { repository.fetchPokemon("Pikachu") } returns Result.success(Pikachu)
        every { mockkSaveStateHandle.get<String>("id") } returns "Pikachu"

        viewModel = PokedexDetailViewModel(mockkSaveStateHandle, repository)

        assertTrue(viewModel.pokemonInfo is DetailState.Success)
        assertEquals(Pikachu, (viewModel.pokemonInfo as DetailState.Success).pokemonInfo)
    }

    @Test
    fun `PokedexDetailViewModel successfully fetches Pokemon from cache`() = runTest {
        `PokedexDetailViewModel successfully fetches Pokemon`()
        `PokedexDetailViewModel successfully fetches Pokemon`()
    }

    @Test
    fun `PokedexDetailViewModel fails to fetches Pokemon`() = runTest {
        coEvery { repository.fetchPokemon("Pikachu") } returns Result.failure(Exception("Cannot fetch pokemon"))
        every { mockkSaveStateHandle.get<String>("id") } returns "Pikachu"

        viewModel = PokedexDetailViewModel(mockkSaveStateHandle, repository)

        assertTrue(viewModel.pokemonInfo is DetailState.Error)
    }
}