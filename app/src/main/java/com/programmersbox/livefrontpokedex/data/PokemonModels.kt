package com.programmersbox.livefrontpokedex.data

import androidx.compose.ui.graphics.Color
import com.programmersbox.livefrontpokedex.firstCharCapital
import com.programmersbox.livefrontpokedex.roundToDecimals
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PokemonResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<Pokemon>,
)

@Serializable
data class Pokemon(
    var page: Int = 0,
    val name: String,
    val url: String,
) {
    val pokedexEntry: String get() = url.split("/".toRegex()).dropLast(1).last()
    val imageUrl: String
        get() {
            val index = url.split("/".toRegex()).dropLast(1).last()
            return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$index.png"
        }

    val showdownImageUrl: String
        get() {
            val index = url.split("/".toRegex()).dropLast(1).last()
            return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/showdown/$index.png"
        }
}

@Serializable
data class PokemonInfo(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    @SerialName("base_experience") val experience: Int,
    val types: List<TypeResponse>,
    val stats: List<Stats>,
    var pokemonDescription: PokemonDescription? = null,
    val sprites: Sprites?
) {

    val cryUrl
        get() =
            "https://play.pokemonshowdown.com/audio/cries/${name.lowercase().replace("-", "")}.mp3"

    val imageUrl: String
        get() = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"

    val shinyImageUrl: String
        get() = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/shiny/$id.png"

    fun getWeightString(): String = "${(weight.toFloat() / 10).roundToDecimals(1)} KG"

    fun getHeightString(): String = "${(height.toFloat() / 10).roundToDecimals(1)} M"

    @Serializable
    data class TypeResponse(
        val slot: Int,
        val type: Type,
    ) {
        fun getTypeColor() = when (type.name) {
            "fighting" -> 0xff9F422A
            "flying" -> 0xff90B1C5
            "poison" -> 0xff642785
            "ground" -> 0xffAD7235
            "rock" -> 0xff4B190E
            "bug" -> 0xff179A55
            "ghost" -> 0xff363069
            "steel" -> 0xff5C756D
            "fire" -> 0xffB22328
            "water" -> 0xff2648DC
            "grass" -> 0xff007C42
            "electric" -> 0xffE0E64B
            "psychic" -> 0xffAC296B
            "ice" -> 0xff7ECFF2
            "dragon" -> 0xff378A94
            "fairy" -> 0xff9E1A44
            "dark" -> 0xff040706
            else -> 0xffB1A5A5
        }
    }

    @Serializable
    data class Type(
        val name: String,
    )

    @Serializable
    data class Stats(
        @SerialName("base_stat")
        val baseStat: Int,
        val stat: Stat,
    )

    @Serializable
    data class Stat(val name: String) {
        val shortenedName = when (name) {
            "hp" -> "HP"
            "attack" -> "ATK"
            "defense" -> "DEF"
            "speed" -> "SPD"
            "special-attack" -> "SP.ATK"
            "special-defense" -> "SP.DEF"
            else -> name
        }

        val statColor
            get() = when (name) {
                "hp" -> 0xffFF0000
                "attack" -> 0xffF08030
                "defense" -> 0xffF8D030
                "speed" -> 0xffF85888
                "special-attack" -> 0xff6890F0
                "special-defense" -> 0xff78C850
                else -> null
            }?.let { Color(it) }
    }
}

@Serializable
data class PokemonDescription(
    @SerialName("flavor_text_entries")
    val flavorTextEntries: List<FlavorText>,
) {
    private val tag = "en"
    val filtered by lazy {
        flavorTextEntries
            .filter { it.language.name == tag }
            .groupBy { it.flavorText }
            .map {
                FlavorText(
                    flavorText = it.key,
                    version = Version(
                        it.value.joinToString(", ") { v ->
                            v.version.name.firstCharCapital()
                        }
                    ),
                    language = FlavorLanguage(tag)
                )
            }
    }
}

@Serializable
data class FlavorText(
    @SerialName("flavor_text")
    val flavorText: String,
    val language: FlavorLanguage,
    val version: Version,
)

@Serializable
data class FlavorLanguage(val name: String)

@Serializable
data class Version(val name: String)

@Serializable
data class Sprites(
    @SerialName("back_default")
    var backDefault: String? = null,
    @SerialName("back_female")
    var backFemale: String? = null,
    @SerialName("back_shiny")
    var backShiny: String? = null,
    @SerialName("back_shiny_female")
    var backShinyFemale: String? = null,
    @SerialName("front_default")
    var frontDefault: String? = null,
    @SerialName("front_female")
    var frontFemale: String? = null,
    @SerialName("front_shiny")
    var frontShiny: String? = null,
    @SerialName("front_shiny_female")
    var frontShinyFemale: String? = null,
    @SerialName("other")
    var other: Other?
) {
    private val spriteList = listOfNotNull(
        backDefault,
        backShiny,
        frontDefault,
        frontShiny,
        other?.home?.frontDefault,
        other?.home?.frontShiny,
    )

    private val femaleSprites = listOfNotNull(
        backFemale,
        backShinyFemale,
        frontFemale,
        frontShinyFemale,
        other?.home?.frontFemale,
        other?.home?.frontShinyFemale,
    )

    val spriteMap = mapOf(
        SpriteType.Default to spriteList,
        SpriteType.Female to femaleSprites
    )
}

enum class SpriteType { Default, Female }

@Serializable
data class DreamWorld(
    @SerialName("front_default")
    var frontDefault: String? = null,
    @SerialName("front_female")
    var frontFemale: String? = null
)

@Serializable
data class OfficialArtwork(
    @SerialName("front_default")
    var frontDefault: String? = null
)

@Serializable
data class Home(
    @SerialName("front_default")
    var frontDefault: String? = null,
    @SerialName("front_female")
    var frontFemale: String? = null,
    @SerialName("front_shiny")
    var frontShiny: String? = null,
    @SerialName("front_shiny_female")
    var frontShinyFemale: String? = null
)

@Serializable
data class Other(
    @SerialName("dream_world")
    var dreamWorld: DreamWorld?,
    @SerialName("home")
    var home: Home?,
    @SerialName("official-artwork")
    var officialArtwork: OfficialArtwork?
)