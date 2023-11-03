package com.programmersbox.livefrontpokedex.di

import com.programmersbox.livefrontpokedex.data.PokedexService
import com.programmersbox.livefrontpokedex.data.repo.PokemonRepository
import com.programmersbox.livefrontpokedex.data.repo.PokemonRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.http.ContentType
import io.ktor.http.URLBuilder
import io.ktor.http.appendEncodedPathSegments
import io.ktor.http.contentType
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

private const val baseUrl = "https://pokeapi.co/api/v2/"

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePokedexService(): PokedexService {
        return PokedexService(
            client = HttpClient {
                install(ContentNegotiation) {
                    json(Json { ignoreUnknownKeys = true })
                }

                install(Logging) {
                    logger = Logger.SIMPLE
                    level = LogLevel.BODY
                }

                defaultRequest {
                    contentType(ContentType.Application.Json)
                    url.takeFrom(URLBuilder().takeFrom(baseUrl).appendEncodedPathSegments(url.encodedPathSegments))
                }
            }
        )
    }

    @Provides
    @Singleton
    fun providePokemonRepository(service: PokedexService): PokemonRepository {
        return PokemonRepositoryImpl(service)
    }
}