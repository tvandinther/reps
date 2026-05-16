package com.tvandinther.reps.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import com.tvandinther.reps.ui.theme.ColorDivider
import com.tvandinther.reps.ui.theme.ColorInk
import com.tvandinther.reps.ui.theme.ColorInk3
import com.tvandinther.reps.ui.theme.ColorInk4
import com.tvandinther.reps.ui.theme.ColorInk6
import com.tvandinther.reps.ui.theme.ColorSignal
import com.tvandinther.reps.ui.theme.StyleBody
import com.tvandinther.reps.ui.theme.StyleEyebrow
import com.tvandinther.reps.ui.theme.StyleH3
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp),
    ) {
        // ── Session gap ─────────────────────────────────────────────────────
        SectionHeader("Session Gap")

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "SESSION GAP",
                style = StyleH3,
                color = ColorInk3,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "${uiState.sessionGapMinutes} MIN",
                style = StyleH3,
                color = ColorInk,
            )
        }

        Slider(
            value = ((uiState.sessionGapMinutes / 15) - 1).toFloat(),
            onValueChange = { sliderValue ->
                val minutes = ((sliderValue.roundToInt() + 1) * 15).coerceIn(15, 180)
                viewModel.setSessionGapMinutes(minutes)
            },
            valueRange = 0f..11f,
            steps = 10,
            colors = SliderDefaults.colors(
                thumbColor = ColorSignal,
                activeTrackColor = ColorSignal,
                inactiveTrackColor = ColorDivider,
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
        ) {
            Text(
                text = "15 MIN",
                style = StyleBody,
                color = ColorInk4,
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "180 MIN",
                style = StyleBody,
                color = ColorInk4,
            )
        }

        Spacer(Modifier.height(4.dp))
        Text(
            text = "Two sets separated by more than this interval are counted as separate sessions in the history view.",
            style = StyleBody,
            color = ColorInk4,
            modifier = Modifier.padding(horizontal = 18.dp),
        )

        Spacer(Modifier.height(24.dp))

        // ── Left-handed mode ────────────────────────────────────────────────
        SectionHeader("Ergonomics")

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "LEFT-HANDED MODE",
                    style = StyleH3,
                    color = ColorInk3,
                )
                Text(
                    text = "MOVE ACTION BUTTON TO LEFT SIDE",
                    style = StyleBody,
                    color = ColorInk4,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
            Spacer(Modifier.width(16.dp))
            BrutalistToggle(
                checked = uiState.isLeftHanded,
                onCheckedChange = { viewModel.setLeftHanded(it) },
            )
        }
    }
}

@Composable
private fun SectionHeader(label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 18.dp, end = 18.dp, top = 6.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label.uppercase(),
            style = StyleEyebrow,
            color = ColorInk6,
        )
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(Color(0xFF1A1A1A)),
        )
    }
}

/** Custom pill toggle: 56×30dp, round ends, dark track, orange when on. */
@Composable
private fun BrutalistToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val trackColor = if (checked) ColorSignal else ColorDivider
    val trackWidth = 56.dp
    val trackHeight = 30.dp
    val knobSize = 24.dp
    val knobTravel = trackWidth - knobSize - 4.dp   // 2dp margin each side

    Box(
        modifier = Modifier
            .size(width = trackWidth, height = trackHeight)
            .clip(RoundedCornerShape(percent = 50))
            .background(trackColor)
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.CenterStart,
    ) {
        val offsetX: Dp = if (checked) knobTravel + 2.dp else 2.dp
        Box(
            modifier = Modifier
                .offset(x = offsetX)
                .size(knobSize)
                .clip(CircleShape)
                .background(Color.White),
        )
    }
}

