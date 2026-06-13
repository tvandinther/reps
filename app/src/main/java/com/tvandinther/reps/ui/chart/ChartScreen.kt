package com.tvandinther.reps.ui.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tvandinther.reps.ui.theme.BarlowCondensedFamily
import com.tvandinther.reps.ui.theme.ColorInk
import com.tvandinther.reps.ui.theme.ColorInk3
import com.tvandinther.reps.ui.theme.ColorInk4
import com.tvandinther.reps.ui.theme.ColorSignal
import com.tvandinther.reps.ui.theme.ColorSurfaceDeep
import com.tvandinther.reps.ui.theme.StyleBody
import com.tvandinther.reps.ui.theme.StyleH1
import com.tvandinther.reps.ui.theme.StyleH3
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.math.floor

@Composable
fun ChartScreen(
    exerciseId: Long,
    onBack: () -> Unit,
    viewModel: ChartViewModel = koinViewModel(parameters = { parametersOf(exerciseId) }),
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorSurfaceDeep),
    ) {
        ChartHeader(
            exerciseName = uiState.exerciseName,
            volumeUnitLabel = uiState.volumeUnitLabel,
            resistanceUnitLabel = uiState.resistanceUnitLabel,
            setCount = uiState.points.size,
            isSingleAxis = uiState.isSingleAxis,
            onBack = onBack,
        )

        if (uiState.points.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "NO SETS LOGGED",
                    style = StyleH3,
                    color = ColorInk4,
                )
            }
        } else {
            DotPlot(
                points = uiState.points,
                isSingleAxis = uiState.isSingleAxis,
                maxSetCount = uiState.setCount,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 24.dp, bottom = 16.dp, start = 8.dp, end = 16.dp),
            )
        }
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun ChartHeader(
    exerciseName: String,
    volumeUnitLabel: String,
    resistanceUnitLabel: String,
    setCount: Int,
    isSingleAxis: Boolean,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorSurfaceDeep)
            .padding(top = 16.dp),
    ) {
        Text(
            text = "← LOGGING",
            style = StyleH3.copy(
                color = ColorSignal,
                fontSize = 11.sp,
                letterSpacing = 3.3.sp,
            ),
            modifier = Modifier
                .padding(horizontal = 18.dp)
                .combinedClickable(onClick = onBack),
        )

        Spacer(Modifier.height(8.dp))

        Column(modifier = Modifier.padding(horizontal = 18.dp)) {
            Text(
                text = exerciseName.uppercase(),
                style = StyleH1,
                color = ColorInk,
            )

            val axisDesc = buildString {
                if (!isSingleAxis) {
                    // 2-axis: resistance on X (→), volume on Y (↑)
                    if (resistanceUnitLabel.isNotEmpty()) append(resistanceUnitLabel.uppercase())
                    append(" → / ")
                    if (volumeUnitLabel.isNotEmpty()) append(volumeUnitLabel.uppercase())
                    append(" ↑")
                } else {
                    if (volumeUnitLabel.isNotEmpty()) append(volumeUnitLabel.uppercase())
                    append(" →")
                }
            }
            Text(
                text = axisDesc,
                style = StyleBody,
                color = ColorInk3,
                modifier = Modifier.padding(top = 4.dp),
            )

            if (setCount > 0) {
                Text(
                    text = "LAST $setCount SETS",
                    style = StyleBody.copy(fontStyle = FontStyle.Italic),
                    color = ColorInk4,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(ColorSignal),
        )
    }
}

@Composable
private fun DotPlot(
    points: List<ChartDataPoint>,
    isSingleAxis: Boolean,
    maxSetCount: Int,
    modifier: Modifier = Modifier,
) {
    val textMeasurer = rememberTextMeasurer()
    val labelStyle = TextStyle(
        fontFamily = BarlowCondensedFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 9.sp,
        color = ColorInk4,
        letterSpacing = 0.2.sp,
    )

    Canvas(modifier = modifier) {
        val n = points.size
        if (n == 0) return@Canvas

        // Layout margins
        val yAxisPad = if (isSingleAxis) 0f else 48.dp.toPx()
        val xAxisPad = 28.dp.toPx()
        val topPad = if (isSingleAxis) size.height * 0.35f else 8.dp.toPx()

        val chartLeft = yAxisPad
        val chartRight = size.width
        val chartTop = topPad
        val chartBottom = size.height - xAxisPad
        val chartWidth = chartRight - chartLeft
        val chartHeight = chartBottom - chartTop

        if (chartWidth <= 0f || chartHeight <= 0f) return@Canvas

        // 2-axis: resistance on X, volume on Y
        // 1-axis: volume on X, dots fixed at vertical centre
        val volumes = points.map { it.volumeValue }
        val resistances = if (!isSingleAxis) points.mapNotNull { it.resistanceValue } else emptyList()

        val xSeries = if (isSingleAxis) volumes else resistances
        val xMin = if (xSeries.isEmpty()) 0.0 else xSeries.min()
        val xMax = if (xSeries.isEmpty()) 1.0 else xSeries.max()
        val xSpan = if (xMax - xMin < 1e-9) 1.0 else xMax - xMin
        val xLo = xMin - xSpan * 0.10  // 10% padding keeps dots off the axis edges
        val xHi = xMax + xSpan * 0.10

        val yMin = volumes.min()
        val yMax = volumes.max()
        val ySpan = if (yMax - yMin < 1e-9) 1.0 else yMax - yMin
        val yLo = yMin - ySpan * 0.10
        val yHi = yMax + ySpan * 0.10

        // Coordinate helpers — data value → canvas pixel, aligned with the padded range
        val xPx: (Double) -> Float = { v -> chartLeft + ((v - xLo) / (xHi - xLo)).toFloat() * chartWidth }
        val yPx: (Double) -> Float = { v -> chartBottom - ((v - yLo) / (yHi - yLo)).toFloat() * chartHeight }

        val axisColor = Color(0xFF282828)
        val tickColor = Color(0xFF444444)
        val tickLen = 5.dp.toPx()
        val labelGap = 6.dp.toPx()

        // X axis line
        drawLine(color = axisColor, start = Offset(chartLeft, chartBottom), end = Offset(chartRight, chartBottom), strokeWidth = 1.dp.toPx())

        // Y axis line (2-axis only)
        if (!isSingleAxis) {
            drawLine(color = axisColor, start = Offset(chartLeft, chartTop), end = Offset(chartLeft, chartBottom), strokeWidth = 1.dp.toPx())
        }

        // X axis: tick + label centred on the data value's canvas position
        val xMinPx = xPx(xMin)
        val xMaxPx = xPx(xMax)
        val xLabelY = chartBottom + labelGap
        drawLine(color = tickColor, start = Offset(xMinPx, chartBottom), end = Offset(xMinPx, chartBottom + tickLen), strokeWidth = 1.dp.toPx())
        drawLine(color = tickColor, start = Offset(xMaxPx, chartBottom), end = Offset(xMaxPx, chartBottom + tickLen), strokeWidth = 1.dp.toPx())
        val minXLabel = textMeasurer.measure(formatChartValue(xMin), labelStyle)
        val maxXLabel = textMeasurer.measure(formatChartValue(xMax), labelStyle)
        drawText(minXLabel, topLeft = Offset(xMinPx - minXLabel.size.width / 2f, xLabelY))
        drawText(maxXLabel, topLeft = Offset(xMaxPx - maxXLabel.size.width / 2f, xLabelY))

        // Y axis: tick + label right-aligned to the data value's canvas position (2-axis only)
        if (!isSingleAxis) {
            val yMinPx = yPx(yMin)
            val yMaxPx = yPx(yMax)
            drawLine(color = tickColor, start = Offset(chartLeft, yMinPx), end = Offset(chartLeft - tickLen, yMinPx), strokeWidth = 1.dp.toPx())
            drawLine(color = tickColor, start = Offset(chartLeft, yMaxPx), end = Offset(chartLeft - tickLen, yMaxPx), strokeWidth = 1.dp.toPx())
            val minYLabel = textMeasurer.measure(formatChartValue(yMin), labelStyle)
            val maxYLabel = textMeasurer.measure(formatChartValue(yMax), labelStyle)
            drawText(minYLabel, topLeft = Offset(chartLeft - minYLabel.size.width - tickLen - labelGap, yMinPx - minYLabel.size.height / 2f))
            drawText(maxYLabel, topLeft = Offset(chartLeft - maxYLabel.size.width - tickLen - labelGap, yMaxPx - maxYLabel.size.height / 2f))
        }

        // Dot constants
        val basePx = 5.dp.toPx()
        val peakPx = 8.dp.toPx()

        // Draw dots oldest (index 0) to newest (index n-1) so newest renders on top.
        // t is anchored to the END of the gradient: newest is always t=1.0 (orange).
        // Older dots are placed proportionally back from there based on maxSetCount,
        // so a small number of recent sets all appear near the orange end.
        points.forEachIndexed { index, point ->
            val t = if (maxSetCount <= 1) 1f else {
                ((maxSetCount - n + index).toFloat() / (maxSetCount - 1).toFloat()).coerceIn(0f, 1f)
            }
            val color = dotColor(t)
            val radiusPx = if (t >= 0.88f) basePx + (peakPx - basePx) * ((t - 0.88f) / 0.12f) else basePx

            val xVal = if (isSingleAxis) point.volumeValue else (point.resistanceValue ?: xMin)
            val cx = xPx(xVal)
            val cy = if (isSingleAxis) chartTop + chartHeight * 0.5f else yPx(point.volumeValue)

            drawCircle(color = color, radius = radiusPx, center = Offset(cx, cy))
        }
    }
}

/**
 * Color gradient from oldest (t=0, dark grey) to newest (t=1, signal orange).
 * Saturation is reserved for only the most recent ~15% of sets.
 */
private fun dotColor(t: Float): Color = when {
    t < 0.5f -> lerp(Color(0xFF3A3A3A), Color(0xFF7A7A7A), t / 0.5f)
    t < 0.85f -> lerp(Color(0xFF7A7A7A), Color(0xFFBBBBBB), (t - 0.5f) / 0.35f)
    else -> lerp(Color(0xFFBBBBBB), ColorSignal, (t - 0.85f) / 0.15f)
}

private fun formatChartValue(v: Double): String =
    if (v == floor(v)) v.toLong().toString() else "%.1f".format(v)
