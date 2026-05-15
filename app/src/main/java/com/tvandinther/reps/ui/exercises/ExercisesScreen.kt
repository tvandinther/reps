package com.tvandinther.reps.ui.exercises

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tvandinther.reps.data.model.UnitEntity
import com.tvandinther.reps.ui.theme.Ghost
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisesScreen(
    onExerciseSelected: (Long) -> Unit,
    viewModel: ExercisesViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusRequester = remember { FocusRequester() }
    val scope = rememberCoroutineScope()
    var showAddSheet by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
    ) {
        OutlinedTextField(
            value = uiState.query,
            onValueChange = viewModel::setQuery,
            placeholder = { Text("Search exercises") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .focusRequester(focusRequester),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        )

        if (uiState.showAddButton) {
            TextButton(
                onClick = { showAddSheet = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                Text(
                    text = "Add \"${uiState.query}\"",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(bottom = 80.dp),
        ) {
            items(uiState.rows, key = { it.exercise.id }) { row ->
                ExerciseRow(
                    row = row,
                    onClick = { onExerciseSelected(row.exercise.id) },
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            }
        }
    }

    if (showAddSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showAddSheet = false },
            sheetState = sheetState,
        ) {
            AddExerciseSheet(
                initialName = uiState.query,
                units = uiState.units,
                defaultVolumeUnitId = uiState.defaultVolumeUnit?.id ?: 0L,
                defaultResistanceUnitId = uiState.defaultResistanceUnit?.id ?: 0L,
                onCreate = { name, volId, resId ->
                    scope.launch {
                        val id = viewModel.createExercise(name, volId, resId)
                        showAddSheet = false
                        viewModel.setQuery("")
                        onExerciseSelected(id)
                    }
                },
            )
        }
    }
}

@Composable
private fun ExerciseRow(
    row: com.tvandinther.reps.ui.exercises.ExerciseRow,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Text(
            text = row.exercise.name,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(2.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = row.exercise.lastLoggedAt?.let { relativeTime(it) } ?: "Never",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            row.lastSet?.let { set ->
                Text(
                    text = formatLastSet(set, row.volumeUnit, row.resistanceUnit),
                    fontSize = 13.sp,
                    color = Ghost,
                )
            }
        }
    }
}

@Composable
private fun AddExerciseSheet(
    initialName: String,
    units: List<UnitEntity>,
    defaultVolumeUnitId: Long,
    defaultResistanceUnitId: Long,
    onCreate: (name: String, volumeUnitId: Long, resistanceUnitId: Long) -> Unit,
) {
    var name by rememberSaveable { mutableStateOf(initialName) }
    var selectedVolumeUnitId by rememberSaveable { mutableLongStateOf(defaultVolumeUnitId) }
    var selectedResistanceUnitId by rememberSaveable { mutableLongStateOf(defaultResistanceUnitId) }

    val volumeUnits = units.filter { it.type == "volume" }
    val resistanceUnits = units.filter { it.type == "resistance" }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .imePadding(),
    ) {
        Text("New exercise", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        Spacer(Modifier.height(16.dp))
        Text("Volume unit", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        UnitPicker(
            units = volumeUnits,
            selectedId = selectedVolumeUnitId,
            onSelect = { selectedVolumeUnitId = it },
        )

        Spacer(Modifier.height(16.dp))
        Text("Resistance unit", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        UnitPicker(
            units = resistanceUnits,
            selectedId = selectedResistanceUnitId,
            onSelect = { selectedResistanceUnitId = it },
        )

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { onCreate(name, selectedVolumeUnitId, selectedResistanceUnitId) },
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotBlank(),
        ) {
            Text("Create", fontSize = 16.sp)
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun UnitPicker(
    units: List<UnitEntity>,
    selectedId: Long,
    onSelect: (Long) -> Unit,
) {
    androidx.compose.foundation.layout.FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        units.forEach { unit ->
            val isSelected = unit.id == selectedId
            if (isSelected) {
                Button(
                    onClick = { onSelect(unit.id) },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                ) {
                    Text(unit.label, fontSize = 14.sp)
                }
            } else {
                TextButton(
                    onClick = { onSelect(unit.id) },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                ) {
                    Text(unit.label, fontSize = 14.sp)
                }
            }
        }
    }
}

private fun relativeTime(timestampMs: Long): String {
    val now = Instant.now()
    val then = Instant.ofEpochMilli(timestampMs)
    val days = ChronoUnit.DAYS.between(
        then.atZone(ZoneId.systemDefault()).toLocalDate(),
        now.atZone(ZoneId.systemDefault()).toLocalDate(),
    )
    return when {
        days == 0L -> "today"
        days == 1L -> "yesterday"
        days < 7L -> "$days days ago"
        days < 14L -> "1 week ago"
        days < 30L -> "${days / 7} weeks ago"
        days < 60L -> "1 month ago"
        else -> "${days / 30} months ago"
    }
}

private fun formatLastSet(
    set: com.tvandinther.reps.data.model.SetEntity,
    volumeUnit: UnitEntity?,
    resistanceUnit: UnitEntity?,
): String {
    val vol = formatValue(set.volumeValue)
    val volLabel = volumeUnit?.label ?: ""
    val resLabel = resistanceUnit?.label ?: ""
    return when {
        resLabel == "bodyweight" -> "$vol $volLabel (bodyweight)"
        resLabel == "none" -> "$vol $volLabel"
        set.resistanceValue != null -> "$vol $volLabel × ${formatValue(set.resistanceValue)} $resLabel"
        else -> "$vol $volLabel"
    }
}

private fun formatValue(v: Double): String =
    if (v == v.toLong().toDouble()) v.toLong().toString() else v.toString()
