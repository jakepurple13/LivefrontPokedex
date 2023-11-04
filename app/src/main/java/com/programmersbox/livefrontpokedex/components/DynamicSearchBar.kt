package com.programmersbox.livefrontpokedex.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarColors
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    active: Boolean,
    onActiveChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isDocked: Boolean = false,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    shape: Shape = if (isDocked) SearchBarDefaults.dockedShape else SearchBarDefaults.inputFieldShape,
    colors: SearchBarColors = SearchBarDefaults.colors(),
    tonalElevation: Dp = SearchBarDefaults.Elevation,
    windowInsets: WindowInsets = SearchBarDefaults.windowInsets,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable ColumnScope.() -> Unit,
) {
    if (isDocked) {
        DockedSearchBar(
            query,
            onQueryChange,
            onSearch,
            active,
            onActiveChange,
            modifier.windowInsetsPadding(windowInsets),
            enabled,
            placeholder,
            leadingIcon,
            trailingIcon,
            shape,
            colors,
            tonalElevation,
            interactionSource,
            content
        )
    } else {
        SearchBar(
            query,
            onQueryChange,
            onSearch,
            active,
            onActiveChange,
            modifier,
            enabled,
            placeholder,
            leadingIcon,
            trailingIcon,
            shape,
            colors,
            tonalElevation,
            windowInsets,
            interactionSource,
            content
        )
    }
}