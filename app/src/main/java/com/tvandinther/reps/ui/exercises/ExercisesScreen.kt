package com.tvandinther.reps.ui.exercises

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
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
    onEditExercise: (Long) -> Unit,
    viewModel: ExercisesViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusRequester = remember { FocusRequester() }
    val scope = rememberCoroutineScope()
    var showAddSheet by rememberSaveable { mutableStateOf(false) }
    var pendingDeleteRow by remember { mutableStateOf<ExerciseRow?>(null) }
    var pendingDeleteFinalRow by remember { mutableStateOf<ExerciseRow?>(null) }

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
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search,
                capitalization = KeyboardCapitalization.Characters,
            ),
            trailingIcon = {
                if (uiState.query.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setQuery("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear search")
                    }
                }
            },
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

        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(contentPadding = PaddingValues(bottom = 80.dp)) {
                items(uiState.rows, key = { it.exercise.id }) { row ->
                    SwipeableExerciseRow(
                        row = row,
                        onClick = {
                            viewModel.setQuery("")
                            onExerciseSelected(row.exercise.id)
                        },
                        onLongClick = { onEditExercise(row.exercise.id) },
                        onEdit = { onEditExercise(row.exercise.id) },
                        onDeleteRequest = { pendingDeleteRow = row },
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                }
            }

            if (uiState.rows.isEmpty() && uiState.query.isBlank()) {
                Text(
                    text = "To add your first exercise, type in the search bar and tap \"Add\".",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(32.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }

    pendingDeleteRow?.let { row ->
        val setCount = row.setCount
        AlertDialog(
            onDismissRequest = { pendingDeleteRow = null },
            title = { Text("Delete ${row.exercise.name}?") },
            text = {
                if (setCount > 0) {
                    Text("This will also delete $setCount logged set${if (setCount == 1) "" else "s"}.")
                } else {
                    Text("This exercise has no logged sets.")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    pendingDeleteRow = null
                    if (setCount > 0) {
                        pendingDeleteFinalRow = row
                    } else {
                        viewModel.deleteExercise(row.exercise.id)
                    }
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteRow = null }) {
                    Text("Cancel")
                }
            },
        )
    }

    pendingDeleteFinalRow?.let { row ->
        val setCount = row.setCount
        AlertDialog(
            onDismissRequest = { pendingDeleteFinalRow = null },
            title = { Text("Are you sure?") },
            text = {
                Text("Deleting this exercise will permanently remove $setCount set${if (setCount == 1) "" else "s"}. This cannot be undone.")
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteExercise(row.exercise.id)
                    pendingDeleteFinalRow = null
                }) {
                    Text("Delete permanently", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteFinalRow = null }) {
                    Text("Cancel")
                }
            },
        )
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
                onCreate = { name, volId, resId, note ->
                    scope.launch {
                        val id = viewModel.createExercise(name, volId, resId, note)
                        showAddSheet = false
                        viewModel.setQuery("")
                        onExerciseSelected(id)
                    }
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun SwipeableExerciseRow(
    row: ExerciseRow,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onEdit: () -> Unit,
    onDeleteRequest: () -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.StartToEnd -> { onEdit(); false }
                SwipeToDismissBoxValue.EndToStart -> { onDeleteRequest(); false }
                else -> false
            }
        },
        positionalThreshold = { it * 0.35f },
    )
    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val isEdit = dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd
            val bgColor = if (isEdit) Color(0xFF1565C0) else Color(0xFF8B0000)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(bgColor)
                    .padding(horizontal = 20.dp),
                contentAlignment = if (isEdit) Alignment.CenterStart else Alignment.CenterEnd,
            ) {
                Icon(
                    imageVector = if (isEdit) Icons.Default.Edit else Icons.Default.Delete,
                    contentDescription = if (isEdit) "Edit" else "Delete",
                    tint = Color.White,
                )
            }
        },
        content = {
            Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                ExerciseRow(row = row, onClick = onClick, onLongClick = onLongClick)
            }
        },
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ExerciseRow(
    row: ExerciseRow,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Text(
            text = row.exercise.name.uppercase(),
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
    onCreate: (name: String, volumeUnitId: Long, resistanceUnitId: Long, note: String?) -> Unit,
) {
    var name by rememberSaveable { mutableStateOf(initialName) }
    var note by rememberSaveable { mutableStateOf("") }
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
            onValueChange = { name = it.uppercase() },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
        )

        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Note (optional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4,
        )

        Spacer(Modifier.height(16.dp))
        Text("Volume unit", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        UnitPicker(units = volumeUnits, selectedId = selectedVolumeUnitId, onSelect = { selectedVolumeUnitId = it })

        Spacer(Modifier.height(16.dp))
        Text("Resistance unit", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        UnitPicker(units = resistanceUnits, selectedId = selectedResistanceUnitId, onSelect = { selectedResistanceUnitId = it })

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { onCreate(name, selectedVolumeUnitId, selectedResistanceUnitId, note) },
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotBlank(),
        ) {
            Text("Create", fontSize = 16.sp)
        }
        Spacer(Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun UnitPicker(
    units: List<UnitEntity>,
    selectedId: Long,
    onSelect: (Long) -> Unit,
) {
    FlowRow(
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
