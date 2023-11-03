package com.programmersbox.livefrontpokedex.components

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.*
import com.programmersbox.livefrontpokedex.roundToDecimals
import kotlin.math.*

internal data class PokemonGraphBean(
    val value: Float,
    val label: String,
    val color: Color? = null
)

internal class PokemonGraphState(
    val mList: List<PokemonGraphBean>,
    var mMaxValue: Float = 0f
) {
    internal val mPointPairList = mutableStateListOf<Pair<Double, Double>>()
    internal val mPointInnerPairList = mutableStateListOf<Pair<Double, Double>>()
    internal var mSideLength = -1
    internal var mAverageAngle = 0.0
    internal var mRadius = -1f
    internal var mFramePadding = -1
    internal var mInnerFramePercentage = DEF_INNER_FRAME_PERCENTAGE
    internal var mValuePath = Path()
    internal var mInnerFramePath = Path()
    internal var mFramePath = Path()
    internal var mTextPercentage = DEF_TEXT_PERCENTAGE
}

@Composable
internal fun rememberPokemonGraphState(
    list: List<PokemonGraphBean>,
    maxValue: Float = 0f
) = remember(list, maxValue) { PokemonGraphState(list, maxValue) }

/**
 * Compose version of https://github.com/LZ9/PokemonGraph/blob/master/PokemonGraph/src/main/java/com/lodz/android/PokemonGraph/PokemonGraphView.kt
 */
@OptIn(ExperimentalTextApi::class)
@Composable
internal fun PokemonGraph(
    state: PokemonGraphState,
    modifier: Modifier = Modifier,
    isShowLine: Boolean = true,
    valueColor: Color = MaterialTheme.colorScheme.primary,
    frameColor: Color = MaterialTheme.colorScheme.onSurface,
    innerFrameColor: Color = MaterialTheme.colorScheme.onSurface,
    mValueProgressPercentage: Float = 1f,
) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier) {
        anchorParam(state)
        drawFrameLine(state.mFramePath, state.mPointPairList, frameColor)
        drawFrameLine(state.mInnerFramePath, state.mPointInnerPairList, innerFrameColor)

        drawPolygon(state, valueColor, mValueProgressPercentage)

        state.mList.forEachIndexed { i, bean ->
            if (isShowLine) {
                drawLinePokemonGraph(
                    state.mPointPairList[i].first,
                    state.mPointPairList[i].second,
                    bean.color ?: innerFrameColor
                )
            }
            drawLabel(bean, i, state, textMeasurer, innerFrameColor, mValueProgressPercentage)
        }
    }
}

private fun DrawScope.drawFrameLine(path: Path, list: List<Pair<Double, Double>>, color: Color) {
    for (i in list.indices) {
        val x = list[i].first.toFloat()
        val y = list[i].second.toFloat()
        if (i == 0) {
            path.moveTo(x, y)
            continue
        }
        path.lineTo(x, y)
        if (i == list.size - 1) {
            path.close()
        }
    }
    drawPath(path, color, style = Stroke(1f))
}

private fun DrawScope.drawLinePokemonGraph(x: Double, y: Double, color: Color) {
    drawLine(color, center, Offset(x.toFloat(), y.toFloat()))
}

private fun DrawScope.drawLabel(
    bean: PokemonGraphBean,
    i: Int,
    state: PokemonGraphState,
    textMeasurer: TextMeasurer,
    color: Color,
    mValueProgressPercentage: Float
) {
    val value = (bean.value * mValueProgressPercentage).roundToDecimals(1)

    val label = textMeasurer.measure(bean.label).size
    val valueText = textMeasurer.measure(bean.value.toString()).size

    val pair = getXY(i, state.mRadius, state.mTextPercentage, state.mAverageAngle, center.x, center.y)
    val x = pair.first
    val y = pair.second

    drawText(
        textMeasurer,
        bean.label,
        Offset(
            x.toFloat() - label.width / 2f,
            (y.toFloat() - label.height / 2f) - 20f,
        ),
        style = TextStyle.Default.copy(color = color)
    )

    drawText(
        textMeasurer,
        value.toString(),
        Offset(
            x.toFloat() - valueText.width / 2f,
            y.toFloat() + valueText.height - 20f,
        ),
        style = TextStyle.Default.copy(color = color)
    )
}

private fun DrawScope.drawPolygon(
    state: PokemonGraphState,
    color: Color,
    mValueProgressPercentage: Float
) {
    val path = state.mValuePath
    val (x1, y1) = center
    for (i in 0 until state.mList.size) {
        val value = (state.mList[i].value * mValueProgressPercentage).roundToDecimals(1)
        val offset = state.mRadius * state.mInnerFramePercentage//偏移量
        val r = if (state.mMaxValue == 0f) offset else (state.mRadius - offset) * value / state.mMaxValue + offset
        val pair = getXY(i, r, 1f, state.mAverageAngle, x1, y1)
        val x = pair.first.toFloat()
        val y = pair.second.toFloat()
        if (i == 0) {
            path.moveTo(x, y)
            continue
        }
        path.lineTo(x, y)
        if (i == state.mList.size - 1) {
            path.close()
        }
    }
    drawPath(state.mValuePath, color)
}

private fun DrawScope.anchorParam(
    state: PokemonGraphState
) {
    val (width, height) = size
    val mCenterX: Float
    val mCenterY: Float

    if (state.mSideLength == -1) {
        state.mPointPairList.clear()
        state.mPointInnerPairList.clear()
        state.mSideLength = min(width, height).roundToInt()
        state.mFramePadding = state.mSideLength / 6
        if (width == height) {
            mCenterX = state.mSideLength / 2.0f
            mCenterY = state.mSideLength / 2.0f
            state.mRadius = mCenterX - state.mFramePadding
        } else if (width > height) {
            mCenterX = state.mSideLength / 2.0f + (width - height) / 2f
            mCenterY = state.mSideLength / 2.0f
            state.mRadius = mCenterY - state.mFramePadding
        } else {
            mCenterX = state.mSideLength / 2.0f
            mCenterY = state.mSideLength / 2.0f + (height - width) / 2f
            state.mRadius = mCenterX - state.mFramePadding
        }
        state.mAverageAngle = 360.0 / state.mList.size
        var max = 0f
        state.mList.forEachIndexed { i, bean ->
            state.mPointPairList.add(
                getXY(
                    i,
                    state.mRadius,
                    1.0f,
                    state.mAverageAngle,
                    mCenterX,
                    mCenterY
                )
            )
            state.mPointInnerPairList.add(
                getXY(
                    i,
                    state.mRadius,
                    state.mInnerFramePercentage,
                    state.mAverageAngle,
                    mCenterX,
                    mCenterY
                )
            )
            if (max < bean.value) {
                max = bean.value
            }
        }
        if (state.mMaxValue <= max) {
            state.mMaxValue = max
        }
    }
}

private fun getXY(
    index: Int,
    radius: Float,
    percentage: Float,
    mAverageAngle: Double,
    mCenterX: Float,
    mCenterY: Float
): Pair<Double, Double> {
    val angle = mAverageAngle * index + DEF_START_ANGLE
    val x = cos(Math.toRadians(angle)) * radius * percentage + mCenterX
    val y = sin(Math.toRadians(angle)) * radius * percentage + mCenterY
    return Pair(x, y)
}

internal object Math {
    fun toRadians(degrees: Double) = (degrees * PI) / 180
}

private const val DEF_FRAME_STROKE_WIDTH = 5
private const val DEF_INNER_FRAME_PERCENTAGE = 0.3f
private const val DEF_TEXT_PERCENTAGE = 1.3f
private const val DEF_SRC_BG_PERCENTAGE = 0.6f
private const val DEF_START_ANGLE = -90.0
private const val DEF_TEXT_SIZE = 35