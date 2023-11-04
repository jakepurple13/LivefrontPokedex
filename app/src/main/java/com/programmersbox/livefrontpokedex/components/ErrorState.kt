package com.programmersbox.livefrontpokedex.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.programmersbox.livefrontpokedex.LightAndDarkPreviews
import com.programmersbox.livefrontpokedex.ui.theme.LivefrontPokedexTheme

@Composable
fun ErrorState(
    onTryAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
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

@LightAndDarkPreviews
@Composable
private fun ErrorStatePreview() {
    LivefrontPokedexTheme {
        Surface {
            ErrorState(
                onTryAgain = {},
            )
        }
    }
}