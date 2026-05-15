package com.tvandinther.reps.ui.logging

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.tvandinther.reps.ui.theme.Background
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoggingScreen(
    exerciseId: Long,
    onBack: () -> Unit,
    viewModel: LoggingViewModel = koinViewModel(parameters = { parametersOf(exerciseId) }),
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddSheet by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
    ) {
        TopAppBar(
            title = {
                Column {
                    Text(
                        uiState.exercise?.name ?: "",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    uiState.exercise?.let {
                        val vol = uiState.volumeUnit?.label ?: ""
                        val res = uiState.resistanceUnit?.label ?: ""
                        Text(
                            text = "$vol × $res · RPE optional",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Background,
            ),
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 80.dp),
        ) {
            if (uiState.currentSessionSets.isNotEmpty()) {
                item {
                    SectionHeader("This session")
                }
                items(uiState.currentSessionSets, key = { it.id }) { set ->
                    SwipeToDeleteRow(
                        onDelete = { viewModel.deleteSet(set.id) },
                    ) {
                        SetRow(
                            number = uiState.currentSessionSets.indexOf(set) + 1,
                            set = set,
                            hideResistance = uiState.hideResistanceField,
                        )
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                }
            }

            item {
                TextButton(
                    onClick = { showAddSheet = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Text("+ Add set", fontSize = 16.sp)
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
                        )
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                }
            }
        }
    }

    if (showAddSheet) {
        val lastSet = uiState.currentSessionSets.lastOrNull()
            ?: uiState.previousSessionSets.lastOrNull()

        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showAddSheet = false },
            sheetState = sheetState,
        ) {
            AddSetSheet(
                prefillVolume = lastSet?.volumeValue,
                prefillResistance = lastSet?.resistanceValue,
                hideResistance = uiState.hideResistanceField,
                onSave = { vol, res, rpe, note ->
                    viewModel.addSet(vol, res, rpe, note)
                    showAddSheet = false
                },
                onDismiss = { showAddSheet = false },
            )
        }
    }
}

@Composable
private fun SectionHeader(label: String, muted: Boolean = false) {
    Text(
        text = label,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = if (muted) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
        else MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    )
}

@Composable
private fun SetRow(
    number: Int,
    set: SetEntity,
    hideResistance: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "$number",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(24.dp),
        )
        Text(
            text = formatValue(set.volumeValue),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
        )
        if (!hideResistance && set.resistanceValue != null) {
            Text(
                text = "×",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp),
            )
            Text(
                text = formatValue(set.resistanceValue),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.weight(1f))
        set.rpe?.let {
            Text(
                text = "RPE $it",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDeleteRow(
    onDelete: () -> Unit,
    content: @Composable () -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else false
        }
    )
    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF8B0000))
                    .padding(end = 16.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
            }
        },
        enableDismissFromStartToEnd = false,
        content = { content() },
    )
}

@Composable
private fun AddSetSheet(
    prefillVolume: Double?,
    prefillResistance: Double?,
    hideResistance: Boolean,
    onSave: (volume: Double, resistance: Double?, rpe: Int?, note: String?) -> Unit,
    onDismiss: () -> Unit,
) {
    var volumeText by rememberSaveable { mutableStateOf(prefillVolume?.let { formatValue(it) } ?: "") }
    var resistanceText by rememberSaveable { mutableStateOf(prefillResistance?.let { formatValue(it) } ?: "") }
    var rpeText by rememberSaveable { mutableStateOf("") }
    var noteText by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .imePadding(),
    ) {
        Text("Add set", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = volumeText,
                onValueChange = { volumeText = it },
                label = { Text("Volume") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
            )
            if (!hideResistance) {
                OutlinedTextField(
                    value = resistanceText,
                    onValueChange = { resistanceText = it },
                    label = { Text("Resistance") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                )
            }
        }

        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = rpeText,
                onValueChange = { if (it.length <= 2) rpeText = it },
                label = { Text("RPE (optional)") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                placeholder = { Text("1–10") },
            )
            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                label = { Text("Note (optional)") },
                modifier = Modifier.weight(2f),
                singleLine = true,
            )
        }

        Spacer(Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                Text("Cancel")
            }
            Button(
                onClick = {
                    val vol = volumeText.toDoubleOrNull() ?: return@Button
                    val res = if (hideResistance) null else resistanceText.toDoubleOrNull()
                    val rpe = rpeText.toIntOrNull()?.takeIf { it in 1..10 }
                    onSave(vol, res, rpe, noteText)
                },
                modifier = Modifier.weight(2f),
                enabled = volumeText.toDoubleOrNull() != null,
            ) {
                Text("Save", fontSize = 16.sp)
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

private fun formatValue(v: Double): String =
    if (v == v.toLong().toDouble()) v.toLong().toString() else v.toString()
