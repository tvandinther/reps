package com.tvandinther.reps.ui.exercises

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tvandinther.reps.data.db.ExerciseDao
import com.tvandinther.reps.data.db.SetDao
import com.tvandinther.reps.data.db.UnitDao
import com.tvandinther.reps.data.model.ExerciseEntity
import com.tvandinther.reps.data.model.SetEntity
import com.tvandinther.reps.data.model.UnitEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.xdrop.fuzzywuzzy.FuzzySearch

data class ExerciseRow(
    val exercise: ExerciseEntity,
    val volumeUnit: UnitEntity?,
    val resistanceUnit: UnitEntity?,
    val lastSet: SetEntity?,
)

data class ExercisesUiState(
    val query: String = "",
    val rows: List<ExerciseRow> = emptyList(),
    val showAddButton: Boolean = false,
    val units: List<UnitEntity> = emptyList(),
    val defaultVolumeUnit: UnitEntity? = null,
    val defaultResistanceUnit: UnitEntity? = null,
)

@OptIn(ExperimentalCoroutinesApi::class)
class ExercisesViewModel(
    private val exerciseDao: ExerciseDao,
    private val setDao: SetDao,
    private val unitDao: UnitDao,
) : ViewModel() {

    val query = MutableStateFlow("")

    private val allExercises = exerciseDao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val allUnits = unitDao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // For each exercise, the most recent set
    private val lastSets = allExercises.flatMapLatest { exercises ->
        combine(
            exercises.map { exercise ->
                setDao.getForExercise(exercise.id)
            }.ifEmpty { listOf(kotlinx.coroutines.flow.flowOf(emptyList())) }
        ) { setsArrays ->
            exercises.mapIndexed { index, exercise ->
                exercise.id to setsArrays[index].maxByOrNull { it.loggedAt }
            }.toMap()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val uiState: StateFlow<ExercisesUiState> = combine(
        query,
        allExercises,
        allUnits,
        lastSets,
    ) { q, exercises, units, lastSetMap ->
        val unitMap = units.associateBy { it.id }

        val filtered = if (q.isBlank()) {
            exercises
        } else {
            exercises
                .map { exercise ->
                    exercise to FuzzySearch.ratio(q.lowercase(), exercise.name.lowercase())
                }
                .filter { (_, score) -> score >= 60 }
                .sortedByDescending { (_, score) -> score }
                .map { (exercise, _) -> exercise }
        }

        val rows = filtered.map { exercise ->
            ExerciseRow(
                exercise = exercise,
                volumeUnit = unitMap[exercise.volumeUnitId],
                resistanceUnit = unitMap[exercise.resistanceUnitId],
                lastSet = lastSetMap[exercise.id],
            )
        }

        val showAdd = q.isNotBlank() && rows.isEmpty()

        ExercisesUiState(
            query = q,
            rows = rows,
            showAddButton = showAdd,
            units = units,
            defaultVolumeUnit = units.firstOrNull { it.type == "volume" && it.isDefault },
            defaultResistanceUnit = units.firstOrNull { it.type == "resistance" && it.isDefault },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ExercisesUiState())

    fun setQuery(q: String) { query.value = q }

    suspend fun createExercise(
        name: String,
        volumeUnitId: Long,
        resistanceUnitId: Long,
    ): Long = exerciseDao.insert(
        ExerciseEntity(
            name = name.trim(),
            volumeUnitId = volumeUnitId,
            resistanceUnitId = resistanceUnitId,
            createdAt = System.currentTimeMillis(),
        )
    )
}
