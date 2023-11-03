package com.programmersbox.livefrontpokedex.screens.entries

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.programmersbox.livefrontpokedex.Pokemon
import com.programmersbox.livefrontpokedex.components.CustomAdaptiveGridCell
import com.programmersbox.livefrontpokedex.components.PokeballLoading
import com.programmersbox.livefrontpokedex.firstCharCapital
import com.programmersbox.livefrontpokedex.ui.theme.LivefrontPokedexTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PokedexEntries(
    onDetailNavigation: (String) -> Unit,
    viewModel: PokedexEntriesViewModel = viewModel { PokedexEntriesViewModel() },
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pokedex") },
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { padding ->
        LazyVerticalGrid(
            columns = CustomAdaptiveGridCell(500.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            contentPadding = padding,
            modifier = Modifier
                .padding(vertical = 2.dp)
                .fillMaxSize()
        ) {
            items(
                viewModel.pokedexEntries,
                key = { it.url },
                contentType = { it }
            ) { pokemon ->
                PokedexEntry(
                    pokemon = pokemon,
                    onClick = { onDetailNavigation(pokemon.pokedexEntry) },
                    modifier = Modifier.animateItemPlacement()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PokedexEntry(
    modifier: Modifier = Modifier,
    pokemon: Pokemon,
    onClick: () -> Unit,
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .sizeIn(minHeight = 250.dp)
                .animateContentSize()
                .padding(4.dp)
                .fillMaxSize()
        ) {
            val painter = rememberAsyncImagePainter(pokemon.imageUrl)

            Image(
                painter = painter,
                contentDescription = pokemon.name,
                contentScale = ContentScale.FillWidth,
                colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(3f) }),
                modifier = Modifier
                    .widthIn(max = 800.dp)
                    .fillMaxWidth(.9f)
                    .wrapContentHeight(Alignment.Top, true)
                    .scale(1f, 1.8f)
                    .blur(70.dp, BlurredEdgeTreatment.Unbounded)
                    .alpha(.5f)
            )
            Image(
                painter = painter,
                contentDescription = pokemon.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .widthIn(max = 500.dp)
                    .fillMaxWidth()
                    .aspectRatio(1.2f)
                    .fillMaxHeight()
            )

            //Needed because it thinks it's using the ColumnScope otherwise
            androidx.compose.animation.AnimatedVisibility(
                painter.state is AsyncImagePainter.State.Loading,
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                PokeballLoading(sizeDp = 150.dp)
            }

            Text(
                pokemon.pokedexEntry,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.TopStart)
            )

            Text(
                pokemon.name.firstCharCapital(),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Preview
@Composable
private fun PokedexEntryPreview() {
    LivefrontPokedexTheme {
        PokedexEntry(
            pokemon = Pokemon(name = "Pikachu", url = "https://pokeapi.co/api/v2/pokemon/25/"),
            onClick = {},
            modifier = Modifier.size(500.dp)
        )
    }
}