package com.tvandinther.reps.ui.exercises

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditExerciseScreen(
    exerciseId: Long,
    onBack: () -> Unit,
    onDeleted: () -> Unit,
    viewModel: EditExerciseViewModel = koinViewModel(parameters = { parametersOf(exerciseId) }),
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    var name by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }
    var selectedVolumeUnitId by rememberSaveable { mutableLongStateOf(0L) }
    var selectedResistanceUnitId by rememberSaveable { mutableLongStateOf(0L) }
    var initialized by rememberSaveable { mutableStateOf(false) }

    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showDeleteFinal by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.exercise) {
        if (!initialized && uiState.exercise != null) {
            val ex = uiState.exercise!!
            name = ex.name
            note = ex.note ?: ""
            selectedVolumeUnitId = ex.volumeUnitId
            selectedResistanceUnitId = ex.resistanceUnitId
            initialized = true
        }
    }

    val volumeUnits = uiState.units.filter { it.type == "volume" }
    val resistanceUnits = uiState.units.filter { it.type == "resistance" }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Edit exercise") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .imePadding(),
        ) {
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

            Spacer(Modifier.height(32.dp))
            Button(
                onClick = {
                    scope.launch {
                        viewModel.saveChanges(
                            name = name,
                            volumeUnitId = selectedVolumeUnitId,
                            resistanceUnitId = selectedResistanceUnitId,
                            note = note,
                        )
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && selectedVolumeUnitId != 0L && selectedResistanceUnitId != 0L,
            ) {
                Text("Save", fontSize = 16.sp)
            }

            Spacer(Modifier.height(8.dp))
            TextButton(
                onClick = { showDeleteConfirm = true },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "Delete exercise",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete exercise?") },
            text = {
                val setCount = uiState.setCount
                if (setCount > 0) {
                    Text("This will also delete $setCount logged set${if (setCount == 1) "" else "s"}.")
                } else {
                    Text("This exercise has no logged sets.")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    if (uiState.setCount > 0) {
                        showDeleteFinal = true
                    } else {
                        viewModel.deleteExercise()
                        onDeleted()
                    }
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            },
        )
    }

    if (showDeleteFinal) {
        val setCount = uiState.setCount
        AlertDialog(
            onDismissRequest = { showDeleteFinal = false },
            title = { Text("Are you sure?") },
            text = {
                Text("Deleting this exercise will permanently remove $setCount set${if (setCount == 1) "" else "s"}. This cannot be undone.")
            },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteFinal = false
                    viewModel.deleteExercise()
                    onDeleted()
                }) {
                    Text("Delete permanently", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteFinal = false }) {
                    Text("Cancel")
                }
            },
        )
    }
}
