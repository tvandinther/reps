package com.tvandinther.reps.ui.logging

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tvandinther.reps.data.model.SetEntity
import com.tvandinther.reps.ui.theme.BarlowCondensedFamily
import com.tvandinther.reps.ui.theme.ColorBackground
import com.tvandinther.reps.ui.theme.ColorDividerSoft
import com.tvandinther.reps.ui.theme.ColorInk
import com.tvandinther.reps.ui.theme.ColorInk3
import com.tvandinther.reps.ui.theme.ColorInk5
import com.tvandinther.reps.ui.theme.ColorSignal
import com.tvandinther.reps.ui.theme.ColorSignalEdge
import com.tvandinther.reps.ui.theme.ColorSignalShadowBg
import com.tvandinther.reps.ui.theme.ColorSurfaceDeep
import com.tvandinther.reps.ui.theme.SpectralFamily
import com.tvandinther.reps.ui.theme.StyleBody
import com.tvandinther.reps.ui.theme.StyleDataL
import com.tvandinther.reps.ui.theme.StyleDataS
import com.tvandinther.reps.ui.theme.StyleEyebrow
import com.tvandinther.reps.ui.theme.StyleH1
import com.tvandinther.reps.ui.theme.StyleH3
import com.tvandinther.reps.ui.theme.StyleLabel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun LoggingScreen(
    exerciseId: Long,
    onBack: () -> Unit,
    onEditExercise: () -> Unit = {},
    viewModel: LoggingViewModel = koinViewModel(parameters = { parametersOf(exerciseId) }),
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddSheet by rememberSaveable { mutableStateOf(false) }
    var editingSet by remember { mutableStateOf<SetEntity?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val fabAlignment = if (uiState.isLeftHanded) Alignment.BottomStart else Alignment.BottomEnd

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding(),
        ) {
            // ── Custom flat header ──────────────────────────────────────────
            LoggingHeader(
                exerciseName = uiState.exercise?.name ?: "",
                volumeUnitLabel = uiState.volumeUnit?.label ?: "",
                resistanceUnitLabel = uiState.resistanceUnit?.label ?: "",
                onBack = onBack,
                onLongClickTitle = onEditExercise,
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 88.dp),
            ) {
                if (uiState.currentSessionSets.isNotEmpty()) {
                    item {
                        SectionHeader("This session")
                    }
                    items(uiState.currentSessionSets, key = { it.id }) { set ->
                        SwipeableSetRow(
                            onDelete = {
                                viewModel.deleteSet(set.id)
                                scope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "Set deleted",
                                        actionLabel = "Undo",
                                        duration = SnackbarDuration.Long,
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        viewModel.undoDelete()
                                    }
                                }
                            },
                            onEdit = { editingSet = set },
                        ) {
                            SetRow(
                                number = uiState.currentSessionSets.indexOf(set) + 1,
                                set = set,
                                hideResistance = uiState.hideResistanceField,
                                volumeUnitLabel = uiState.volumeUnit?.label ?: "",
                                resistanceUnitLabel = uiState.resistanceUnit?.label ?: "",
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(ColorDividerSoft),
                        )
                    }
                }

                if (uiState.previousSessionSets.isNotEmpty()) {
                    item { SectionHeader("Last session", muted = true) }
                    items(uiState.previousSessionSets, key = { "ghost-${it.id}" }) { set ->
                        Box(modifier = Modifier.alpha(0.35f)) {
                            SetRow(
                                number = uiState.previousSessionSets.indexOf(set) + 1,
                                set = set,
                                hideResistance = uiState.hideResistanceField,
                                volumeUnitLabel = uiState.volumeUnit?.label ?: "",
                                resistanceUnitLabel = uiState.resistanceUnit?.label ?: "",
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .alpha(0.35f)
                                .background(ColorDividerSoft),
                        )
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 88.dp),
            snackbar = { snackbarData ->
                val swipeState = rememberSwipeToDismissBoxState(
                    confirmValueChange = { value ->
                        if (value != SwipeToDismissBoxValue.Settled) {
                            snackbarData.dismiss()
                            true
                        } else false
                    },
                )
                SwipeToDismissBox(
                    state = swipeState,
                    backgroundContent = {},
                    content = { Snackbar(snackbarData = snackbarData) },
                )
            },
        )

        // ── FAB ─────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .align(fabAlignment)
                .padding(24.dp)
                .size(56.dp)
                .background(ColorSignal, shape = CircleShape)
                .combinedClickable(onClick = { showAddSheet = true }),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "+",
                fontFamily = BarlowCondensedFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color.White,
            )
        }
    }

    if (showAddSheet) {
        val lastSet = uiState.currentSessionSets.lastOrNull()
            ?: uiState.previousSessionSets.lastOrNull()

        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showAddSheet = false },
            sheetState = sheetState,
            containerColor = Color(0xFF0C0C0C),
            dragHandle = null,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(ColorSignal),
            )
            AddSetSheet(
                prefillVolume = lastSet?.volumeValue,
                prefillResistance = lastSet?.resistanceValue,
                hideResistance = uiState.hideResistanceField,
                volumeUnitLabel = uiState.volumeUnit?.label ?: "Volume",
                resistanceUnitLabel = uiState.resistanceUnit?.label ?: "Resistance",
                onSave = { vol, res, rpe, note ->
                    viewModel.addSet(vol, res, rpe, note)
                    showAddSheet = false
                },
                onDismiss = { showAddSheet = false },
            )
        }
    }

    editingSet?.let { setToEdit ->
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { editingSet = null },
            sheetState = sheetState,
            containerColor = Color(0xFF0C0C0C),
            dragHandle = null,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(ColorSignal),
            )
            AddSetSheet(
                prefillVolume = setToEdit.volumeValue,
                prefillResistance = setToEdit.resistanceValue,
                prefillRpe = setToEdit.rpe,
                prefillNote = setToEdit.note,
                title = "EDIT SET",
                hideResistance = uiState.hideResistanceField,
                volumeUnitLabel = uiState.volumeUnit?.label ?: "Volume",
                resistanceUnitLabel = uiState.resistanceUnit?.label ?: "Resistance",
                onSave = { vol, res, rpe, note ->
                    viewModel.updateSet(setToEdit.id, vol, res, rpe, note)
                    editingSet = null
                },
                onDismiss = { editingSet = null },
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LoggingHeader(
    exerciseName: String,
    volumeUnitLabel: String,
    resistanceUnitLabel: String,
    onBack: () -> Unit,
    onLongClickTitle: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorSurfaceDeep)
            .padding(top = 16.dp),
    ) {
        // Back link
        Text(
            text = "← EXERCISES",
            style = StyleH3.copy(
                color = ColorSignal,
                fontSize = 11.sp,
                letterSpacing = 3.3.sp,   // 0.3 em × 11 sp
            ),
            modifier = Modifier
                .padding(horizontal = 18.dp)
                .combinedClickable(onClick = onBack),
        )

        Spacer(Modifier.height(8.dp))

        // Exercise name — long-press to edit
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = {},
                    onLongClick = onLongClickTitle,
                )
                .padding(horizontal = 18.dp),
        ) {
            Text(
                text = exerciseName.uppercase(),
                style = StyleH1,
                color = ColorInk,
            )
            val unitSub = buildString {
                if (volumeUnitLabel.isNotEmpty()) append(volumeUnitLabel.uppercase())
                if (resistanceUnitLabel.isNotEmpty()) {
                    if (isNotEmpty()) append(" × ")
                    append(resistanceUnitLabel.uppercase())
                }
                append("  ·  RPE OPTIONAL")
            }
            Text(
                text = unitSub,
                style = StyleBody,
                color = ColorInk3,
                modifier = Modifier.padding(top = 4.dp),
            )
        }

        Spacer(Modifier.height(12.dp))

        // 3px signal-orange rule
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(ColorSignal),
        )
    }
}

@Composable
private fun SectionHeader(label: String, muted: Boolean = false) {
    Text(
        text = label.uppercase(),
        style = StyleEyebrow,
        color = if (muted) Color(0xFF222222).copy(alpha = 0.35f) else Color(0xFF222222),
        modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 6.dp, bottom = 2.dp),
    )
}

@Composable
private fun SetRow(
    number: Int,
    set: SetEntity,
    hideResistance: Boolean,
    volumeUnitLabel: String,
    resistanceUnitLabel: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorBackground)
            .padding(horizontal = 18.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Set number
        Text(
            text = "$number",
            style = StyleH3,
            color = ColorInk5,
            modifier = Modifier.width(28.dp),
        )

        // Volume value + unit
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = formatValue(set.volumeValue),
                style = StyleDataL,
                color = ColorInk,
            )
            if (volumeUnitLabel.isNotEmpty()) {
                Spacer(Modifier.width(3.dp))
                Text(
                    text = volumeUnitLabel.uppercase(),
                    style = StyleLabel,
                    color = ColorInk5,
                    modifier = Modifier.padding(bottom = 3.dp),
                )
            }
        }

        // Separator + resistance
        if (!hideResistance && set.resistanceValue != null) {
            Text(
                text = "×",
                fontFamily = BarlowCondensedFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                color = ColorInk5,
                modifier = Modifier.padding(horizontal = 8.dp),
            )
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = formatValue(set.resistanceValue),
                    style = StyleDataL,
                    color = ColorInk,
                )
                if (resistanceUnitLabel.isNotEmpty()) {
                    Spacer(Modifier.width(3.dp))
                    Text(
                        text = resistanceUnitLabel.uppercase(),
                        style = StyleLabel,
                        color = ColorInk5,
                        modifier = Modifier.padding(bottom = 3.dp),
                    )
                }
            }
        }

        Spacer(Modifier.weight(1f))

        // RPE chip
        set.rpe?.let { rpe ->
            Box(
                modifier = Modifier
                    .background(ColorSignalShadowBg)
                    .border(1.dp, ColorSignalEdge)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text(
                    text = "RPE $rpe",
                    fontFamily = BarlowCondensedFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = ColorSignal,
                    letterSpacing = 0.5.sp,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableSetRow(
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    content: @Composable () -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.EndToStart -> { onDelete(); true }
                SwipeToDismissBoxValue.StartToEnd -> { onEdit(); false }
                else -> false
            }
        }
    )
    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val isDelete = dismissState.targetValue == SwipeToDismissBoxValue.EndToStart
            val isEdit = dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd
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
                contentAlignment = if (isDelete) Alignment.CenterEnd else Alignment.CenterStart,
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
        enableDismissFromStartToEnd = true,
        content = {
            Box(modifier = Modifier.background(ColorBackground)) {
                content()
            }
        },
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AddSetSheet(
    prefillVolume: Double?,
    prefillResistance: Double?,
    prefillRpe: Int? = null,
    prefillNote: String? = null,
    title: String = "ADD SET",
    hideResistance: Boolean,
    volumeUnitLabel: String,
    resistanceUnitLabel: String,
    onSave: (volume: Double, resistance: Double?, rpe: Int?, note: String?) -> Unit,
    onDismiss: () -> Unit,
) {
    var volumeText by rememberSaveable { mutableStateOf(prefillVolume?.let { formatValue(it) } ?: "") }
    var resistanceText by rememberSaveable { mutableStateOf(prefillResistance?.let { formatValue(it) } ?: "") }
    // 0 = not set (null when saving); 1–10 = RPE value
    var rpeSlider by rememberSaveable { mutableFloatStateOf(prefillRpe?.toFloat() ?: 0f) }
    var noteText by rememberSaveable { mutableStateOf(prefillNote ?: "") }

    val rpeValue: Int? = if (rpeSlider < 0.5f) null else rpeSlider.roundToInt().coerceIn(1, 10)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .imePadding(),
    ) {
        Text(
            text = title,
            style = StyleH3,
            color = ColorInk,
        )
        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = volumeText,
                onValueChange = { volumeText = it },
                label = { Text(volumeUnitLabel.uppercase()) },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
            )
            if (!hideResistance) {
                OutlinedTextField(
                    value = resistanceText,
                    onValueChange = { resistanceText = it },
                    label = { Text(resistanceUnitLabel.uppercase()) },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "RPE",
                style = StyleH3,
                color = ColorInk3,
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = rpeValue?.toString() ?: "—",
                style = if (rpeValue != null) StyleDataL else StyleDataL.copy(color = ColorInk3),
                color = if (rpeValue != null) ColorInk else ColorInk3,
                fontSize = 18.sp,
            )
        }
        Slider(
            value = rpeSlider,
            onValueChange = { rpeSlider = it },
            valueRange = 0f..10f,
            steps = 9,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = noteText,
            onValueChange = { noteText = it },
            label = { Text("Note (optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        Spacer(Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                Text("Cancel", style = StyleH3, color = ColorInk3)
            }
            Box(
                modifier = Modifier
                    .weight(2f)
                    .height(44.dp)
                    .background(
                        if (volumeText.toDoubleOrNull() != null) ColorSignal
                        else Color(0xFF3D1408)
                    )
                    .combinedClickable(
                        enabled = volumeText.toDoubleOrNull() != null,
                        onClick = {
                            val vol = volumeText.toDoubleOrNull() ?: return@combinedClickable
                            val res = if (hideResistance) null else resistanceText.toDoubleOrNull()
                            onSave(vol, res, rpeValue, noteText)
                        },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "SAVE",
                    style = StyleH3.copy(fontSize = 15.sp),
                    color = Color.White,
                )
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

private fun formatValue(v: Double): String =
    if (v == v.toLong().toDouble()) v.toLong().toString() else v.toString()
