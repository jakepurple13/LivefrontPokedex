package com.programmersbox.livefrontpokedex.screens.entries

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.programmersbox.livefrontpokedex.LightAndDarkPreviews
import com.programmersbox.livefrontpokedex.R
import com.programmersbox.livefrontpokedex.Tags
import com.programmersbox.livefrontpokedex.components.CustomAdaptiveGridCell
import com.programmersbox.livefrontpokedex.components.DynamicSearchBar
import com.programmersbox.livefrontpokedex.components.ErrorState
import com.programmersbox.livefrontpokedex.components.PokeballLoading
import com.programmersbox.livefrontpokedex.components.PokeballLoadingAnimation
import com.programmersbox.livefrontpokedex.data.Pokemon
import com.programmersbox.livefrontpokedex.firstCharCapital
import com.programmersbox.livefrontpokedex.ui.theme.LivefrontPokedexTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PokedexEntries(
    onDetailNavigation: (String) -> Unit,
    isHorizontalOrientation: Boolean,
    viewModel: PokedexEntriesViewModel = hiltViewModel(),
) {
    val lazyGridState = rememberLazyGridState()
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            DynamicSearchBar(
                query = viewModel.searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                active = viewModel.isSearchActive,
                onActiveChange = viewModel::changeSearchActiveState,
                placeholder = { Text(stringResource(R.string.pokedex_title)) },
                onSearch = { viewModel.changeSearchActiveState(false) },
                leadingIcon = { Icon(Icons.Default.Search, stringResource(R.string.search_icon_entries)) },
                trailingIcon = {
                    AnimatedVisibility(
                        viewModel.searchQuery.isNotEmpty()
                    ) {
                        IconButton(
                            onClick = {
                                viewModel.onSearchQueryChange("")
                                scope.launch { lazyGridState.animateScrollToItem(0) }
                            }
                        ) { Icon(Icons.Default.Clear, stringResource(R.string.clear_search_text_entries)) }
                    }
                },
                isDocked = isHorizontalOrientation,
                modifier = Modifier
                    .testTag(Tags.SEARCH_BAR_ENTRIES)
                    .fillMaxWidth()
            ) {
                viewModel.filteredEntries
                    .take(10)
                    .forEach {
                        SearchItem(
                            pokemon = it,
                            onClick = { viewModel.onSearchQueryChange(it.name) },
                            modifier = Modifier.testTag(Tags.SEARCH_ITEM_ENTRIES)
                        )
                    }
            }
        },
    ) { padding ->
        Crossfade(
            viewModel.hasError,
            label = "entries_state"
        ) { target ->
            if (target) {
                ErrorState(
                    onTryAgain = viewModel::loadEntries,
                    modifier = Modifier.padding(padding)
                )
            } else {
                LazyVerticalGrid(
                    columns = CustomAdaptiveGridCell(500.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    contentPadding = padding,
                    state = lazyGridState,
                    modifier = Modifier
                        .padding(vertical = 2.dp)
                        .fillMaxSize()
                        .testTag(Tags.POKEDEX_LIST_ENTRIES)
                ) {
                    items(
                        viewModel.filteredEntries,
                        key = { it.url },
                        contentType = { it }
                    ) { pokemon ->
                        PokedexEntry(
                            pokemon = pokemon,
                            onClick = { onDetailNavigation(pokemon.pokedexEntry) },
                            modifier = Modifier
                                .animateItemPlacement()
                                .testTag(Tags.POKEDEX_ENTRY_ENTRIES)
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            viewModel.isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            PokeballLoading()
        }
    }
}

@Composable
private fun SearchItem(
    pokemon: Pokemon,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(pokemon.name) },
        modifier = modifier.clickable(
            role = Role.Button,
            onClickLabel = pokemon.name
        ) { onClick() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PokedexEntry(
    pokemon: Pokemon,
    modifier: Modifier = Modifier,
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
            SubcomposeAsyncImage(
                model = pokemon.imageUrl,
                contentDescription = pokemon.name,
                success = {
                    SubcomposeAsyncImageContent(
                        contentScale = ContentScale.FillWidth,
                        colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(3f) }),
                        modifier = Modifier
                            .widthIn(max = 800.dp)
                            .fillMaxWidth(.9f)
                            .wrapContentHeight(Alignment.Top, true)
                            .scale(1.8f, 1.8f)
                            .blur(70.dp, BlurredEdgeTreatment.Unbounded)
                            .alpha(.5f)
                    )
                    SubcomposeAsyncImageContent(
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .widthIn(max = 500.dp)
                            .fillMaxWidth()
                            .aspectRatio(1.2f)
                            .fillMaxHeight()
                    )
                },
                loading = {
                    PokeballLoadingAnimation(
                        sizeDp = 150.dp,
                    )
                }
            )

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

@LightAndDarkPreviews
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