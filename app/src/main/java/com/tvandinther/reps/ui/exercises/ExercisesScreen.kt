package com.tvandinther.reps.ui.exercises

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tvandinther.reps.data.model.UnitEntity
import com.tvandinther.reps.ui.theme.BarlowCondensedFamily
import com.tvandinther.reps.ui.theme.ColorBackground
import com.tvandinther.reps.ui.theme.ColorBorder
import com.tvandinther.reps.ui.theme.ColorDividerSoft
import com.tvandinther.reps.ui.theme.ColorInk
import com.tvandinther.reps.ui.theme.ColorInk2
import com.tvandinther.reps.ui.theme.ColorInk4
import com.tvandinther.reps.ui.theme.ColorInk5
import com.tvandinther.reps.ui.theme.ColorInk6
import com.tvandinther.reps.ui.theme.ColorSignal
import com.tvandinther.reps.ui.theme.ColorSignalDim
import com.tvandinther.reps.ui.theme.ColorSurfaceRaised
import com.tvandinther.reps.ui.theme.SpectralFamily
import com.tvandinther.reps.ui.theme.StyleBody
import com.tvandinther.reps.ui.theme.StyleButton
import com.tvandinther.reps.ui.theme.StyleDataL
import com.tvandinther.reps.ui.theme.StyleEyebrow
import com.tvandinther.reps.ui.theme.StyleH2
import com.tvandinther.reps.ui.theme.StyleH3
import com.tvandinther.reps.ui.theme.StyleLabel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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
        // ── Search bar ──────────────────────────────────────────────────────
        FlatSearchBar(
            query = uiState.query,
            onQueryChange = viewModel::setQuery,
            focusRequester = focusRequester,
            onClear = { viewModel.setQuery("") },
        )

        // ── Add button ──────────────────────────────────────────────────────
        if (uiState.showAddButton) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .background(ColorSignal)
                    .combinedClickable(onClick = { showAddSheet = true }),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "+ ADD \"${uiState.query.uppercase()}\"",
                    style = StyleButton,
                    color = Color.White,
                )
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(contentPadding = PaddingValues(bottom = 80.dp)) {
                // Section header — only show when there are rows
                if (uiState.rows.isNotEmpty()) {
                    val todayMs = System.currentTimeMillis()
                    val hasToday = uiState.rows.any { row ->
                        row.exercise.lastLoggedAt?.let {
                            val days = ChronoUnit.DAYS.between(
                                Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate(),
                                Instant.ofEpochMilli(todayMs).atZone(ZoneId.systemDefault()).toLocalDate(),
                            )
                            days == 0L
                        } == true
                    }
                    item {
                        EyebrowDivider(if (hasToday) "TODAY" else "RECENT")
                    }
                }

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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(ColorDividerSoft),
                    )
                }
            }

            if (uiState.rows.isEmpty() && uiState.query.isBlank()) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    EyebrowDivider("START HERE")
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "TYPE IN THE SEARCH BAR AND TAP ADD TO LOG YOUR FIRST EXERCISE.",
                        style = StyleBody,
                        color = ColorInk4,
                    )
                }
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
                    Text("Delete", color = ColorSignal)
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
                    Text("Delete permanently", color = ColorSignal)
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
            containerColor = Color(0xFF0C0C0C),
            dragHandle = null,
        ) {
            // 3px signal-orange top line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(ColorSignal),
            )
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FlatSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    focusRequester: FocusRequester,
    onClear: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val signal = ColorSignal

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 12.dp)
            .background(ColorSurfaceRaised)
            .border(1.dp, ColorBorder)
            .then(
                if (isFocused) Modifier.drawBehind {
                    drawLine(
                        color = signal,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = 1.dp.toPx(),
                    )
                } else Modifier
            )
            .padding(horizontal = 14.dp, vertical = 10.dp),
    ) {
        BasicTextField(
            value = query,
            onValueChange = { onQueryChange(it.uppercase()) },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            singleLine = true,
            textStyle = TextStyle(
                fontFamily = BarlowCondensedFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                color = ColorInk,
                letterSpacing = 1.5.sp,
            ),
            cursorBrush = SolidColor(ColorSignal),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search,
                capitalization = KeyboardCapitalization.Characters,
            ),
            interactionSource = interactionSource,
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        if (query.isEmpty()) {
                            Text(
                                text = "Search exercises",
                                style = TextStyle(
                                    fontFamily = BarlowCondensedFamily,
                                    fontWeight = FontWeight.Normal,
                                    fontStyle = FontStyle.Italic,
                                    fontSize = 15.sp,
                                    color = ColorInk4,
                                    letterSpacing = 1.5.sp,
                                ),
                            )
                        }
                        innerTextField()
                    }
                    if (query.isNotEmpty()) {
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "×",
                            fontFamily = BarlowCondensedFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = ColorInk4,
                            modifier = Modifier.combinedClickable(onClick = onClear),
                        )
                    }
                }
            },
        )
    }
}

@Composable
private fun EyebrowDivider(label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 18.dp, end = 18.dp, top = 6.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = StyleEyebrow,
            color = ColorInk6,
        )
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(ColorDividerSoft),
        )
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
            val isEdit = dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd
            val isDelete = dismissState.targetValue == SwipeToDismissBoxValue.EndToStart
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        when {
                            isDelete -> Color(0xFF8B0000)
                            isEdit -> Color(0xFF0A2A18)
                            else -> Color.Transparent
                        }
                    )
                    .padding(horizontal = 18.dp),
                contentAlignment = if (isEdit) Alignment.CenterStart else Alignment.CenterEnd,
            ) {
                if (isDelete) {
                    Text(
                        text = "×",
                        fontFamily = BarlowCondensedFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color.White,
                    )
                } else if (isEdit) {
                    Text(
                        text = "EDIT",
                        fontFamily = BarlowCondensedFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White,
                        letterSpacing = 1.sp,
                    )
                }
            }
        },
        content = {
            Box(modifier = Modifier.background(ColorBackground)) {
                ExerciseRowItem(row = row, onClick = onClick, onLongClick = onLongClick)
            }
        },
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ExerciseRowItem(
    row: ExerciseRow,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    val isHot = row.exercise.lastLoggedAt?.let {
        val days = ChronoUnit.DAYS.between(
            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate(),
            Instant.now().atZone(ZoneId.systemDefault()).toLocalDate(),
        )
        days == 0L
    } == true

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(horizontal = 18.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Left: name + subline
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = row.exercise.name.uppercase(),
                style = StyleH2,
                color = ColorInk2,
            )
            val subline = buildString {
                row.exercise.lastLoggedAt?.let { append(relativeTime(it)) } ?: append("NEVER")
                if (row.setCount > 0) append("  ·  ${row.setCount} SETS")
            }
            Text(
                text = subline.uppercase(),
                style = StyleBody,
                color = ColorInk4,
                modifier = Modifier.padding(top = 2.dp),
            )
        }

        // Right: last set value + unit
        row.lastSet?.let { set ->
            Spacer(Modifier.width(12.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatValue(set.volumeValue),
                    style = StyleDataL,
                    color = if (isHot) ColorSignal else ColorInk,
                )
                Text(
                    text = (row.volumeUnit?.label ?: "").uppercase(),
                    style = StyleLabel,
                    color = if (isHot) ColorSignalDim else ColorInk5,
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
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
        Text(
            text = "NEW EXERCISE",
            style = StyleH3,
            color = ColorInk,
        )
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
        Text("VOLUME UNIT", style = StyleEyebrow, color = ColorInk6)
        Spacer(Modifier.height(4.dp))
        UnitPicker(units = volumeUnits, selectedId = selectedVolumeUnitId, onSelect = { selectedVolumeUnitId = it })

        Spacer(Modifier.height(16.dp))
        Text("RESISTANCE UNIT", style = StyleEyebrow, color = ColorInk6)
        Spacer(Modifier.height(4.dp))
        UnitPicker(units = resistanceUnits, selectedId = selectedResistanceUnitId, onSelect = { selectedResistanceUnitId = it })

        Spacer(Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(if (name.isNotBlank()) ColorSignal else Color(0xFF3D1408))
                .combinedClickable(
                    enabled = name.isNotBlank(),
                    onClick = { onCreate(name, selectedVolumeUnitId, selectedResistanceUnitId, note) },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "CREATE",
                style = StyleButton,
                color = Color.White,
            )
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
                    Text(unit.label.uppercase(), style = StyleLabel)
                }
            } else {
                TextButton(
                    onClick = { onSelect(unit.id) },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                ) {
                    Text(unit.label.uppercase(), style = StyleLabel, color = ColorInk4)
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
