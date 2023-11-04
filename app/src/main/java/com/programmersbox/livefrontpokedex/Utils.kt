package com.programmersbox.livefrontpokedex

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview
import kotlin.math.roundToInt

fun String.firstCharCapital(): String =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

fun Float.roundToDecimals(decimals: Int): Float {
    var dotAt = 1
    repeat(decimals) { dotAt *= 10 }
    val roundedValue = (this * dotAt).roundToInt()
    return (roundedValue / dotAt) + (roundedValue % dotAt).toFloat() / dotAt
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, group = "themes")
@Preview(showBackground = true, group = "themes")
annotation class LightAndDarkPreviews