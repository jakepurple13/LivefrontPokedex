package com.programmersbox.livefrontpokedex

import androidx.compose.ui.graphics.Color
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private const val PAGE_SIZE: Int = 2000

class PokedexService(
    includeLogging: Boolean = true
) {
    private val baseUrl = "https://pokeapi.co/api/v2/"
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }

        if (includeLogging) {
            install(Logging) {
                logger = Logger.SIMPLE
                level = LogLevel.BODY
            }
        }

        defaultRequest {
            contentType(ContentType.Application.Json)
            url.takeFrom(URLBuilder().takeFrom(baseUrl).appendEncodedPathSegments(url.encodedPathSegments))
        }
    }

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
