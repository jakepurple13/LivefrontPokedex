package com.programmersbox.livefrontpokedex.screens.detail

import androidx.compose.runtime.Composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.programmersbox.livefrontpokedex.PokedexService
import com.programmersbox.livefrontpokedex.PokemonInfo
import com.programmersbox.livefrontpokedex.SpriteType
import com.programmersbox.livefrontpokedex.components.Pokeball
import com.programmersbox.livefrontpokedex.components.PokeballLoading
import com.programmersbox.livefrontpokedex.components.PokemonGraph
import com.programmersbox.livefrontpokedex.components.PokemonGraphBean
import com.programmersbox.livefrontpokedex.components.rememberPokemonGraphState
import com.programmersbox.livefrontpokedex.firstCharCapital
import com.programmersbox.livefrontpokedex.ui.theme.FemaleColor
import com.programmersbox.livefrontpokedex.ui.theme.MaleColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun PokedexDetail(
    onBackPress: () -> Unit,
    viewModel: PokedexDetailViewModel = viewModel { PokedexDetailViewModel(createSavedStateHandle(), PokedexService()) }
) {
   Crossfade(
       viewModel.pokemonInfo,
       label = "detail_state"
   ) { target ->
       when (target) {
           DetailState.Error -> {
               ErrorState(
                   onTryAgain = {},
                   onBackPress = onBackPress
               )
           }

           DetailState.Loading -> {
               Surface {
                   Box(modifier = Modifier.fillMaxSize()) {
                       PokeballLoading(
                           modifier = Modifier.align(Alignment.Center)
                       )
                   }
               }
           }

           is DetailState.Success -> {
               val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
               Scaffold(
                   topBar = {
                       ContentHeader(
                           pokemon = target.pokemonInfo,
                           onBackPress = onBackPress,
                           scrollBehavior = scrollBehavior
                       )
                   },
                   modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
               ) { padding ->
                   ContentBody(
                       pokemon = target.pokemonInfo,
                       paddingValues = padding
                   )
               }
           }
       }
   }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ErrorState(
    onTryAgain: () -> Unit,
    onBackPress: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pokedex") },
                navigationIcon = {
                    IconButton(onClick = onBackPress) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
            )
        }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Something went wrong")
                OutlinedButton(onClick = onTryAgain) {
                    Text("Try Again")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContentBody(
    pokemon: PokemonInfo,
    paddingValues: PaddingValues,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(paddingValues)
            .navigationBarsPadding()
            .fillMaxSize()
    ) {
        ImageWithBlurImage(
            url = pokemon.imageUrl,
            name = pokemon.name,
            modifier = Modifier
        )

        Text(
            pokemon.name.firstCharCapital(),
            style = MaterialTheme.typography.displayMedium
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
                Icon(Icons.Default.Scale, null)
                Text(
                    pokemon.getWeightString(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Height, null)
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
                "Base Stats",
                style = MaterialTheme.typography.displaySmall
            )

            val mValueProgressPercentage = remember {
                Animatable(0f)
            }
            LaunchedEffect(Unit) {
                mValueProgressPercentage.animateTo(1f)
            }

            PokemonGraph(
                state = rememberPokemonGraphState(
                    list = pokemon.stats.map {
                        PokemonGraphBean(
                            it.baseStat.toFloat(),
                            it.stat.shortenedName,
                            it.stat.statColor
                        )
                    },
                    maxValue = 300f
                ),
                mValueProgressPercentage = mValueProgressPercentage.value,
                modifier = Modifier
                    .size(425.dp)
            )

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
                "Show All Images",
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
                                        Icon(Icons.Default.Male, null, tint = MaleColor)
                                        Icon(Icons.Default.Female, null, tint = FemaleColor)
                                    }
                                }
                            } else {
                                {
                                    Icon(
                                        Icons.Default.Male,
                                        null,
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
                                    null,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContentHeader(
    pokemon: PokemonInfo,
    onBackPress: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    TopAppBar(
        title = { Text(pokemon.name.firstCharCapital()) },
        navigationIcon = {
            IconButton(onClick = onBackPress) {
                Icon(Icons.Default.ArrowBack, null)
            }
        },
        actions = {
            Text("#${pokemon.id}")
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
        ),
        scrollBehavior = scrollBehavior
    )
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
        modifier = modifier
    ) {
        val painter = rememberAsyncImagePainter(url)
        Image(
            painter = painter,
            contentDescription = name,
            contentScale = ContentScale.FillWidth,
            colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(3f) }),
            modifier = Modifier
                .size(blurSize)
                .fillMaxWidth(.9f)
                .wrapContentHeight(Alignment.Top, true)
                .scale(1f, 1.8f)
                .blur(70.dp, BlurredEdgeTreatment.Unbounded)
                .alpha(.5f)
        )
        Image(
            painter = painter,
            contentDescription = name,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(imageSize)
                .fillMaxWidth()
                .aspectRatio(1.2f)
                .fillMaxHeight()
        )
        additionalContent()
    }
}