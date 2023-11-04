package com.programmersbox.livefrontpokedex.di

import com.programmersbox.livefrontpokedex.data.repo.PokemonRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class]
)
object TestModule {

    @Provides
    @Singleton
    fun providePokemonRepository(): PokemonRepository {
        return MockPokemonRepository()
    }
}