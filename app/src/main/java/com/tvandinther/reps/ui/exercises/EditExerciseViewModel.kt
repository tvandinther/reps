package com.tvandinther.reps.ui.exercises

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tvandinther.reps.data.db.ExerciseDao
import com.tvandinther.reps.data.db.SetDao
import com.tvandinther.reps.data.db.UnitDao
import com.tvandinther.reps.data.model.ExerciseEntity
import com.tvandinther.reps.data.model.UnitEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class EditExerciseUiState(
    val exercise: ExerciseEntity? = null,
    val units: List<UnitEntity> = emptyList(),
    val setCount: Int = 0,
)

class EditExerciseViewModel(
    private val exerciseId: Long,
    private val exerciseDao: ExerciseDao,
    private val setDao: SetDao,
    private val unitDao: UnitDao,
) : ViewModel() {

    val uiState: StateFlow<EditExerciseUiState> = combine(
        exerciseDao.getById(exerciseId),
        unitDao.getAll(),
        setDao.getForExercise(exerciseId).map { it.size },
    ) { exercise, units, setCount ->
        EditExerciseUiState(exercise = exercise, units = units, setCount = setCount)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), EditExerciseUiState())

    suspend fun saveChanges(
        name: String,
        volumeUnitId: Long,
        resistanceUnitId: Long,
        note: String?,
    ) {
        val current = uiState.value.exercise ?: return
        exerciseDao.update(
            current.copy(
                name = name.trim().uppercase(),
                volumeUnitId = volumeUnitId,
                resistanceUnitId = resistanceUnitId,
                note = note?.takeIf { it.isNotBlank() },
            )
        )
    }

    fun deleteExercise() {
        viewModelScope.launch { exerciseDao.deleteById(exerciseId) }
    }
}
