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
                    if (volumeUnitLabel.isNotEmpty()) append(volumeUnitLabel.uppercase())
                    append(" → / ")
                    if (resistanceUnitLabel.isNotEmpty()) append(resistanceUnitLabel.uppercase())
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

        // Axis layout margins
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

        // Volume range with edge padding so dots don't sit on the axis lines
        val volumes = points.map { it.volumeValue }
        val minVol = volumes.min()
        val maxVol = volumes.max()
        val volSpan = if (maxVol - minVol < 1e-9) 1.0 else maxVol - minVol
        val volPad = volSpan * 0.08
        val volLo = minVol - volPad
        val volHi = maxVol + volPad

        // Resistance range
        val resistances = if (!isSingleAxis) points.mapNotNull { it.resistanceValue } else emptyList()
        val minRes = resistances.minOrNull() ?: 0.0
        val maxRes = resistances.maxOrNull() ?: 0.0
        val resSpan = if (maxRes - minRes < 1e-9) 1.0 else maxRes - minRes
        val resPad = resSpan * 0.08
        val resLo = minRes - resPad
        val resHi = maxRes + resPad

        val axisColor = Color(0xFF282828)

        // X axis line
        drawLine(
            color = axisColor,
            start = Offset(chartLeft, chartBottom),
            end = Offset(chartRight, chartBottom),
            strokeWidth = 1.dp.toPx(),
        )

        // Y axis line (2-axis only)
        if (!isSingleAxis) {
            drawLine(
                color = axisColor,
                start = Offset(chartLeft, chartTop),
                end = Offset(chartLeft, chartBottom),
                strokeWidth = 1.dp.toPx(),
            )
        }

        // X axis value labels
        val minVolLabel = textMeasurer.measure(formatChartValue(minVol), labelStyle)
        val maxVolLabel = textMeasurer.measure(formatChartValue(maxVol), labelStyle)
        val labelY = chartBottom + 6.dp.toPx()
        drawText(minVolLabel, topLeft = Offset(chartLeft, labelY))
        drawText(maxVolLabel, topLeft = Offset(chartRight - maxVolLabel.size.width, labelY))

        // Y axis value labels (2-axis only)
        if (!isSingleAxis) {
            val minResLabel = textMeasurer.measure(formatChartValue(minRes), labelStyle)
            val maxResLabel = textMeasurer.measure(formatChartValue(maxRes), labelStyle)
            drawText(
                minResLabel,
                topLeft = Offset(
                    chartLeft - minResLabel.size.width - 6.dp.toPx(),
                    chartBottom - minResLabel.size.height / 2,
                ),
            )
            drawText(
                maxResLabel,
                topLeft = Offset(
                    chartLeft - maxResLabel.size.width - 6.dp.toPx(),
                    chartTop - maxResLabel.size.height / 2,
                ),
            )
        }

        // Dot size constants (in px)
        val basePx = 5.dp.toPx()
        val peakPx = 8.dp.toPx()

        // Draw dots oldest (index 0) to newest (index n-1) so newest renders on top
        points.forEachIndexed { index, point ->
            val t = if (n <= 1) 1f else index.toFloat() / (n - 1).toFloat()
            val color = dotColor(t)

            // Size gradient: only the most recent ~12% grow larger; cut off sharply
            val radiusPx = if (t >= 0.88f) {
                basePx + (peakPx - basePx) * ((t - 0.88f) / 0.12f)
            } else basePx

            val cx = chartLeft + ((point.volumeValue - volLo) / (volHi - volLo)).toFloat() * chartWidth
            val cy = if (isSingleAxis) {
                chartTop + chartHeight * 0.5f
            } else {
                val res = point.resistanceValue ?: minRes
                // Higher resistance = higher on canvas = lower Y
                chartBottom - ((res - resLo) / (resHi - resLo)).toFloat() * chartHeight
            }

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
