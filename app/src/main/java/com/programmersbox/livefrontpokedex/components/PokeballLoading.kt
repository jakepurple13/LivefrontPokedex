package com.programmersbox.livefrontpokedex.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun PokeballLoading() {
    Box(modifier = Modifier.fillMaxSize()) {
        PokeballLoadingAnimation(
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun PokeballLoadingAnimation(
    modifier: Modifier = Modifier,
    sizeDp: Dp = 200.dp,
    topColor: Color = Color.Red,
    bottomColor: Color = Color.White
) {
    val animation = rememberInfiniteTransition(label = "pokeball_animation")
    val rotation by animation.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Restart
        ),
        label = "pokeball_rotation"
    )

    Pokeball(
        sizeDp = sizeDp,
        topColor = topColor,
        bottomColor = bottomColor,
        modifier = modifier.rotate(rotation)
    )
}

@Composable
fun Pokeball(
    modifier: Modifier = Modifier,
    sizeDp: Dp = 200.dp,
    topColor: Color = Color.Red,
    bottomColor: Color = Color.White
) {
    val sizePx = with(LocalDensity.current) { sizeDp.toPx() }

    val blackLineColor = Color.Black
    val strokeWidth = sizePx * .04f
    val outerBallPercentage = .25f
    val innerBallPercentage = .17f
    val centerBallPercentage = .10f

    Canvas(
        modifier = modifier.size(sizeDp)
    ) {
        drawArc(
            brush = Brush.linearGradient(listOf(bottomColor, bottomColor)),
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false
        )
        drawArc(
            brush = Brush.linearGradient(listOf(topColor, topColor)),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false
        )
        drawArc(
            color = blackLineColor,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(2f)
        )

        drawLine(
            color = blackLineColor,
            start = Offset(
                x = 0f,
                y = (size.height / 2)
            ),
            end = Offset(
                x = size.width,
                y = (size.height / 2)
            ),
            strokeWidth = strokeWidth

        )

        drawCircle(
            color = Color.Black,
            radius = sizePx * outerBallPercentage / 2,
        )

        drawCircle(
            color = Color.White,
            radius = sizePx * innerBallPercentage / 2,
        )

        val centerBallSizePx = sizePx * centerBallPercentage
        val centerBallMarginPx = ((sizePx - centerBallSizePx) / 2)
        drawArc(
            color = Color.LightGray,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = Offset(
                x = centerBallMarginPx,
                y = centerBallMarginPx,
            ),
            size = Size(
                width = centerBallSizePx,
                height = centerBallSizePx,
            ),
            style = Stroke(4f)
        )
    }
}

@Preview
@Composable
private fun PokeballPreview() {
    Pokeball()
}