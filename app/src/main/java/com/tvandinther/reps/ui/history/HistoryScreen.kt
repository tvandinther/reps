package com.tvandinther.reps.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tvandinther.reps.domain.Session
import com.tvandinther.reps.ui.theme.BarlowCondensedFamily
import com.tvandinther.reps.ui.theme.ColorBackground
import com.tvandinther.reps.ui.theme.ColorDividerSoft
import com.tvandinther.reps.ui.theme.ColorInk
import com.tvandinther.reps.ui.theme.ColorInk2
import com.tvandinther.reps.ui.theme.ColorInk4
import com.tvandinther.reps.ui.theme.ColorInk5
import com.tvandinther.reps.ui.theme.ColorSignal
import com.tvandinther.reps.ui.theme.ColorSignalEdge
import com.tvandinther.reps.ui.theme.ColorSignalShadowBg
import com.tvandinther.reps.ui.theme.SpectralFamily
import com.tvandinther.reps.ui.theme.StyleBody
import com.tvandinther.reps.ui.theme.StyleDataS
import com.tvandinther.reps.ui.theme.StyleEyebrow
import com.tvandinther.reps.ui.theme.StyleH3
import org.koin.androidx.compose.koinViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.sessions.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
        ) {
            Spacer(Modifier.height(64.dp))
            Text(
                "NO SESSIONS YET.",
                style = StyleEyebrow,
                color = Color(0xFF222222),
            )
        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(bottom = 80.dp),
    ) {
        items(uiState.sessions, key = { it.startedAt }) { session ->
            SessionCard(
                session = session,
                exerciseNames = uiState.exercises,
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(ColorDividerSoft),
            )
        }
    }
}

@Composable
private fun SessionCard(
    session: Session,
    exerciseNames: Map<Long, com.tvandinther.reps.data.model.ExerciseEntity>,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorBackground)
            .clickable { expanded = !expanded },
    ) {
        // ── Session header row ──────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = formatSessionDate(session.startedAt).uppercase(),
                    style = StyleH3.copy(fontSize = 15.sp),
                    color = ColorInk,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = formatSessionTimeRange(session.startedAt, session.endedAt).uppercase(),
                    style = StyleBody.copy(fontSize = 12.sp),
                    color = ColorInk4,
                )
            }
            Text(
                text = if (expanded) "−" else "+",
                fontFamily = BarlowCondensedFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = ColorSignal,
            )
        }

        // ── Expanded detail ─────────────────────────────────────────────────
        if (expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .padding(bottom = 11.dp),
            ) {
                session.sets.forEach { group ->
                    val exerciseName = (exerciseNames[group.exerciseId]?.name ?: "Unknown").uppercase()
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = exerciseName,
                        style = StyleH3.copy(fontSize = 15.sp, letterSpacing = 2.25.sp),
                        color = ColorInk2,
                    )
                    group.sets.forEachIndexed { index, set ->
                        Row(
                            modifier = Modifier.padding(start = 0.dp, top = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "${index + 1}",
                                style = StyleH3,
                                color = ColorInk4,
                                modifier = Modifier.width(20.dp),
                            )
                            Spacer(Modifier.width(6.dp))
                            // Volume
                            Text(
                                text = formatValue(set.volumeValue),
                                fontFamily = SpectralFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = ColorInk,
                            )
                            // Resistance
                            set.resistanceValue?.let { res ->
                                Text(
                                    text = " × ",
                                    fontFamily = BarlowCondensedFamily,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                    color = ColorInk5,
                                )
                                Text(
                                    text = formatValue(res),
                                    fontFamily = SpectralFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = ColorInk,
                                )
                            }
                            // RPE chip
                            set.rpe?.let { rpe ->
                                Spacer(Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .background(ColorSignalShadowBg)
                                        .border(1.dp, ColorSignalEdge)
                                        .padding(horizontal = 6.dp, vertical = 2.dp),
                                ) {
                                    Row(verticalAlignment = Alignment.Bottom) {
                                        Text(
                                            text = "RPE ",
                                            fontFamily = BarlowCondensedFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 7.sp,
                                            color = ColorSignal,
                                            letterSpacing = 0.5.sp,
                                        )
                                        Text(
                                            text = "$rpe",
                                            style = StyleDataS,
                                            color = ColorSignal,
                                        )
                                    }
                                }
                            }
                            // Note
                            set.note?.let { note ->
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = note,
                                    style = StyleBody.copy(
                                        fontWeight = FontWeight.Normal,
                                        color = ColorInk4,
                                    ),
                                    color = ColorInk4,
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Collapsed: show exercise names inline
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .padding(bottom = 8.dp),
            ) {
                session.sets.forEach { group ->
                    val exerciseName = (exerciseNames[group.exerciseId]?.name ?: "Unknown").uppercase()
                    Text(
                        text = exerciseName,
                        style = StyleBody.copy(fontSize = 12.sp),
                        color = ColorInk2,
                    )
                }
            }
        }
    }
}

private val dateFormatter = DateTimeFormatter.ofPattern("EEEE d MMM", Locale.getDefault())
private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())

private fun formatSessionDate(startMs: Long): String {
    val zone = ZoneId.systemDefault()
    return Instant.ofEpochMilli(startMs).atZone(zone).format(dateFormatter)
}

private fun formatSessionTimeRange(startMs: Long, endMs: Long): String {
    val zone = ZoneId.systemDefault()
    val startTime = Instant.ofEpochMilli(startMs).atZone(zone).format(timeFormatter)
    val endTime = Instant.ofEpochMilli(endMs).atZone(zone).format(timeFormatter)
    val totalMinutes = (endMs - startMs) / 60_000
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    val duration = when {
        hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
        hours > 0 -> "${hours}h"
        else -> "${minutes}m"
    }
    return "$startTime – $endTime ($duration)"
}

private fun formatValue(v: Double): String =
    if (v == v.toLong().toDouble()) v.toLong().toString() else v.toString()
