package com.programmersbox.livefrontpokedex.screens.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.programmersbox.livefrontpokedex.LightAndDarkPreviews
import com.programmersbox.livefrontpokedex.R
import com.programmersbox.livefrontpokedex.Tags
import com.programmersbox.livefrontpokedex.components.ErrorState
import com.programmersbox.livefrontpokedex.components.PokeballLoading
import com.programmersbox.livefrontpokedex.data.PokemonInfo
import com.programmersbox.livefrontpokedex.data.SpriteType
import com.programmersbox.livefrontpokedex.firstCharCapital
import com.programmersbox.livefrontpokedex.ui.theme.FemaleColor
import com.programmersbox.livefrontpokedex.ui.theme.LivefrontPokedexTheme
import com.programmersbox.livefrontpokedex.ui.theme.MaleColor
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
internal fun PokedexDetail(
    onBackPress: () -> Unit,
    viewModel: PokedexDetailViewModel = hiltViewModel()
) {
    Crossfade(
        viewModel.pokemonInfo,
        label = "detail_state"
    ) { target ->
        when (target) {
            DetailState.Error -> {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(stringResource(R.string.pokedex_title)) },
                            navigationIcon = {
                                IconButton(
                                    onClick = onBackPress,
                                    modifier = Modifier.testTag(Tags.BACK_BUTTON)
                                ) {
                                    Icon(Icons.Default.ArrowBack, stringResource(id = R.string.back_button))
                                }
                            },
                        )
                    }
                ) { padding ->
                    ErrorState(
                        onTryAgain = viewModel::loadPokemon,
                        modifier = Modifier.padding(padding)
                    )
                }
            }

            DetailState.Loading -> {
                PokeballLoading()
            }

            is DetailState.Success -> {
                val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(target.pokemonInfo.name.firstCharCapital()) },
                            navigationIcon = {
                                IconButton(
                                    onClick = onBackPress,
                                    modifier = Modifier.testTag(Tags.BACK_BUTTON)
                                ) {
                                    Icon(Icons.Default.ArrowBack, stringResource(R.string.back_button))
                                }
                            },
                            actions = { Text("#${target.pokemonInfo.id}") },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.Transparent,
                                scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                            ),
                            scrollBehavior = scrollBehavior
                        )
                    },
                    modifier = Modifier
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                        .testTag(Tags.SUCCESS_DETAILS)
                ) { padding ->
                    ContentBody(
                        pokemon = target.pokemonInfo,
                        padding = padding,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun ContentBody(
    pokemon: PokemonInfo,
    padding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(padding)
            .then(modifier)
    ) {
        ImageWithBlurImage(
            url = pokemon.imageUrl,
            name = pokemon.name,
            modifier = Modifier
        )

        Text(
            pokemon.name.firstCharCapital(),
            style = MaterialTheme.typography.displayMedium,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            pokemon.types.forEach {
                val typeColor = Color(it.getTypeColor())
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = typeColor,
                ) {
                    Text(
                        it.type.name.firstCharCapital(),
                        color = if (typeColor.luminance() > .5)
                            MaterialTheme.colorScheme.surface
                        else
                            MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Scale, stringResource(R.string.pokemon_weight_details))
                Text(
                    pokemon.getWeightString(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Height, stringResource(R.string.pokemon_height_details))
                Text(
                    pokemon.getHeightString(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                stringResource(R.string.base_stats_details),
                style = MaterialTheme.typography.displaySmall
            )

            val mValueProgressPercentage = remember {
                Animatable(0f)
            }
            LaunchedEffect(Unit) {
                mValueProgressPercentage.animateTo(1f)
            }

            pokemon.stats.forEach {
                StatInfoBar(
                    color = it.stat.statColor ?: MaterialTheme.colorScheme.primary,
                    statType = it.stat.shortenedName,
                    statAmount = "${it.baseStat}/300",
                    statCount = it.baseStat / 300f
                )
            }
        }

        ShowImages(pokemon)

        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            pokemon.pokemonDescription
                ?.filtered
                ?.forEach {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ListItem(
                            headlineContent = { Text(it.version.name) },
                            supportingContent = { Text(it.flavorText) }
                        )
                    }
                }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun ShowImages(pokemon: PokemonInfo) {
    pokemon.sprites?.spriteMap?.let { spritesList ->
        var showMoreImages by remember { mutableStateOf(false) }

        ElevatedCard(
            onClick = { showMoreImages = !showMoreImages }
        ) {
            Text(
                stringResource(R.string.show_all_images_details),
                modifier = Modifier.padding(8.dp)
            )
        }

        AnimatedVisibility(showMoreImages) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalArrangement = Arrangement.Center
            ) {
                spritesList.forEach { sprite ->
                    val iconChoice: @Composable (Modifier) -> Unit = when (sprite.key) {
                        SpriteType.Default -> {
                            if (spritesList[SpriteType.Female]?.isEmpty() == true) {
                                {
                                    Row(it) {
                                        Icon(
                                            Icons.Default.Male,
                                            stringResource(R.string.pokemon_has_unique_male_form_details),
                                            tint = MaleColor
                                        )
                                        Icon(
                                            Icons.Default.Female,
                                            stringResource(R.string.pokemon_has_unique_female_form_details),
                                            tint = FemaleColor
                                        )
                                    }
                                }
                            } else {
                                {
                                    Icon(
                                        Icons.Default.Male,
                                        stringResource(R.string.pokemon_has_unique_male_form_details),
                                        modifier = it,
                                        tint = MaleColor
                                    )
                                }
                            }
                        }

                        SpriteType.Female -> {
                            {
                                Icon(
                                    Icons.Default.Female,
                                    stringResource(R.string.pokemon_has_unique_female_form_details),
                                    modifier = it,
                                    tint = FemaleColor
                                )
                            }
                        }
                    }
                    sprite.value.forEach {
                        ElevatedCard(
                            modifier = Modifier
                                .size(160.dp)
                                .padding(vertical = 2.dp)
                        ) {
                            ImageWithBlurImage(
                                url = it,
                                name = pokemon.name,
                                blurSize = 160.dp,
                                imageSize = 100.dp,
                            ) { iconChoice(Modifier.align(Alignment.TopEnd)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatInfoBar(
    color: Color,
    statType: String,
    statAmount: String,
    statCount: Float,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(Modifier.width(24.dp))
        Text(
            statType,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(3f)
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.weight(3f)
        ) {
            val animationProgress = animateDelay(statCount)

            LinearProgressIndicator(
                progress = animationProgress.value,
                color = color,
                trackColor = MaterialTheme.colorScheme.onSurface,
                strokeCap = StrokeCap.Round,
                modifier = Modifier.height(16.dp),
            )
            Text(
                statAmount,
                color = MaterialTheme.colorScheme.surface
            )
        }
        Spacer(Modifier.width(24.dp))
    }
}

@Composable
private fun animateDelay(
    toValue: Float
): Animatable<Float, AnimationVector1D> {
    val animationProgress = remember { Animatable(initialValue = 0f) }

    LaunchedEffect(toValue) {
        animationProgress.animateTo(
            targetValue = toValue,
            animationSpec = tween(
                durationMillis = (8 * toValue * 100).roundToInt(),
                easing = LinearEasing
            )
        )
    }
    return animationProgress
}

@Composable
private fun ImageWithBlurImage(
    url: String,
    name: String,
    modifier: Modifier = Modifier,
    blurSize: Dp = 300.dp,
    imageSize: Dp = 240.dp,
    additionalContent: @Composable BoxScope.() -> Unit = {}
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.semantics { contentDescription = name }
    ) {
        GlideImage(
            imageModel = { url },
            imageOptions = ImageOptions(
                contentScale = ContentScale.FillWidth,
                colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(3f) }),
            ),
            modifier = Modifier
                .size(blurSize)
                .fillMaxWidth(.9f)
                .wrapContentHeight(Alignment.Top, true)
                .scale(1.8f, 1.8f)
                .blur(70.dp, BlurredEdgeTreatment.Unbounded)
                .alpha(.5f)
        )

        GlideImage(
            imageModel = { url },
            imageOptions = ImageOptions(
                contentScale = ContentScale.Fit,
            ),
            modifier = Modifier
                .size(imageSize)
                .fillMaxWidth()
                .aspectRatio(1.2f)
                .fillMaxHeight()
        )
        additionalContent()
    }
}

@LightAndDarkPreviews
@Composable
private fun StateInfoPreview() {
    LivefrontPokedexTheme {
        Surface {
            StatInfoBar(
                color = Color.Blue,
                statType = "ATK",
                statAmount = "100",
                statCount = 100f,
            )
        }
    }
}

@LightAndDarkPreviews
@Composable
private fun ContentBodyPreview() {
    LivefrontPokedexTheme {
        Surface {
            ContentBody(
                pokemon = PokemonInfo(
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
                ),
                padding = PaddingValues(0.dp)
            )
        }
    }
}

