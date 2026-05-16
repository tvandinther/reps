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
    val setCount: Int,
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

    // For each exercise, all sets (used for lastSet and setCount)
    private val exerciseSets = allExercises.flatMapLatest { exercises ->
        combine(
            exercises.map { exercise ->
                setDao.getForExercise(exercise.id)
            }.ifEmpty { listOf(kotlinx.coroutines.flow.flowOf(emptyList())) }
        ) { setsArrays ->
            exercises.mapIndexed { index, exercise ->
                exercise.id to setsArrays[index].toList()
            }.toMap()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val uiState: StateFlow<ExercisesUiState> = combine(
        query,
        allExercises,
        allUnits,
        exerciseSets,
    ) { q, exercises, units, setsMap ->
        val unitMap = units.associateBy { it.id }

        val filtered = if (q.isBlank()) {
            exercises
        } else {
            val lower = q.lowercase()
            exercises
                .mapNotNull { exercise ->
                    val nameLower = exercise.name.lowercase()
                    // Substring match always wins; fuzzy is a typo fallback
                    val score = if (nameLower.contains(lower)) 100
                                else FuzzySearch.partialRatio(lower, nameLower)
                    if (score >= 60) exercise to score else null
                }
                .sortedByDescending { (_, score) -> score }
                .map { (exercise, _) -> exercise }
        }

        val rows = filtered.map { exercise ->
            val sets = setsMap[exercise.id] ?: emptyList()
            ExerciseRow(
                exercise = exercise,
                volumeUnit = unitMap[exercise.volumeUnitId],
                resistanceUnit = unitMap[exercise.resistanceUnitId],
                lastSet = sets.maxByOrNull { it.loggedAt },
                setCount = sets.size,
            )
        }

        val showAdd = q.isNotBlank() && exercises.none { it.name.equals(q.trim(), ignoreCase = true) }

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
        note: String? = null,
    ): Long = exerciseDao.insert(
        ExerciseEntity(
            name = name.trim().uppercase(),
            volumeUnitId = volumeUnitId,
            resistanceUnitId = resistanceUnitId,
            note = note?.takeIf { it.isNotBlank() },
            createdAt = System.currentTimeMillis(),
        )
    )

    fun deleteExercise(id: Long) {
        viewModelScope.launch { exerciseDao.deleteById(id) }
    }
}
