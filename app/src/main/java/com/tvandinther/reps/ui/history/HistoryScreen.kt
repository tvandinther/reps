package com.tvandinther.reps.ui.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tvandinther.reps.domain.ExerciseGroup
import com.tvandinther.reps.domain.Session
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
                "No sessions yet.",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
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
            .clickable { expanded = !expanded }
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Text(
            text = formatSessionHeader(session.startedAt, session.endedAt),
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium,
        )
        Spacer(Modifier.height(6.dp))

        session.sets.forEach { group ->
            val exerciseName = (exerciseNames[group.exerciseId]?.name ?: "Unknown").uppercase()
            if (expanded) {
                Text(
                    text = exerciseName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 8.dp),
                )
                group.sets.forEachIndexed { index, set ->
                    Row(modifier = Modifier.padding(start = 8.dp, top = 2.dp)) {
                        Text(
                            text = "${index + 1}.  ",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = buildSetSummary(set),
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        set.note?.let { note ->
                            Text(
                                text = "  $note",
                                fontSize = 13.sp,
                                fontStyle = FontStyle.Italic,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            } else {
                Text(
                    text = exerciseName,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }
    }
}

private fun buildSetSummary(set: com.tvandinther.reps.domain.SessionSet): String {
    val vol = formatValue(set.volumeValue)
    val res = set.resistanceValue?.let { " × ${formatValue(it)}" } ?: ""
    val rpe = set.rpe?.let { "  RPE $it" } ?: ""
    return "$vol$res$rpe"
}

private val dateFormatter = DateTimeFormatter.ofPattern("EEEE d MMM", Locale.getDefault())
private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())

private fun formatSessionHeader(startMs: Long, endMs: Long): String {
    val zone = ZoneId.systemDefault()
    val date = Instant.ofEpochMilli(startMs).atZone(zone).format(dateFormatter)
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
    return "$date · $startTime – $endTime ($duration)"
}

private fun formatValue(v: Double): String =
    if (v == v.toLong().toDouble()) v.toLong().toString() else v.toString()
