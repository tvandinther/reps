package com.tvandinther.reps.ui.logging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tvandinther.reps.data.db.ExerciseDao
import com.tvandinther.reps.data.db.SetDao
import com.tvandinther.reps.data.db.UnitDao
import com.tvandinther.reps.data.model.ExerciseEntity
import com.tvandinther.reps.data.model.SetEntity
import com.tvandinther.reps.data.model.UnitEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val SESSION_GAP_MS = 90L * 60 * 1000

data class LoggingUiState(
    val exercise: ExerciseEntity? = null,
    val volumeUnit: UnitEntity? = null,
    val resistanceUnit: UnitEntity? = null,
    val currentSessionSets: List<SetEntity> = emptyList(),
    val previousSessionSets: List<SetEntity> = emptyList(),
)

val LoggingUiState.hideResistanceField: Boolean
    get() = resistanceUnit?.label == "bodyweight" || resistanceUnit?.label == "none"

class LoggingViewModel(
    private val exerciseId: Long,
    private val exerciseDao: ExerciseDao,
    private val setDao: SetDao,
    private val unitDao: UnitDao,
) : ViewModel() {

    private val exercise = exerciseDao.getById(exerciseId).filterNotNull()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val allUnits = unitDao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val allSetsForExercise = setDao.getForExercise(exerciseId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val uiState: StateFlow<LoggingUiState> = combine(
        exercise,
        allUnits,
        allSetsForExercise,
    ) { ex, units, sets ->
        if (ex == null) return@combine LoggingUiState()
        val unitMap = units.associateBy { it.id }
        val now = System.currentTimeMillis()

        // Cluster sets into sessions by gap. Work backwards from the most recent set.
        val sortedDesc = sets.sortedByDescending { it.loggedAt }
        val currentSets = mutableListOf<SetEntity>()
        val previousSets = mutableListOf<SetEntity>()

        var sessionBoundary: Long? = null
        for (set in sortedDesc) {
            if (sessionBoundary == null) {
                // First set defines the anchor for the current session
                if (now - set.loggedAt < SESSION_GAP_MS) {
                    currentSets.add(set)
                    sessionBoundary = set.loggedAt
                } else {
                    // Most recent set is already older than the gap — no current session
                    previousSets.add(set)
                }
            } else {
                if (sessionBoundary - set.loggedAt < SESSION_GAP_MS) {
                    currentSets.add(set)
                    sessionBoundary = set.loggedAt
                } else {
                    previousSets.add(set)
                }
            }
        }

        // Find previous session: contiguous sets just before the gap
        val prevSession = mutableListOf<SetEntity>()
        if (previousSets.isNotEmpty()) {
            val prevAnchor = previousSets[0].loggedAt
            for (set in previousSets) {
                if (prevAnchor - set.loggedAt < SESSION_GAP_MS) {
                    prevSession.add(set)
                } else break
            }
        }

        LoggingUiState(
            exercise = ex,
            volumeUnit = unitMap[ex.volumeUnitId],
            resistanceUnit = unitMap[ex.resistanceUnitId],
            currentSessionSets = currentSets.sortedBy { it.loggedAt },
            previousSessionSets = prevSession.sortedBy { it.loggedAt },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LoggingUiState())

    fun addSet(volumeValue: Double, resistanceValue: Double?, rpe: Int?, note: String?) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            setDao.insert(
                SetEntity(
                    exerciseId = exerciseId,
                    volumeValue = volumeValue,
                    resistanceValue = resistanceValue,
                    rpe = rpe,
                    note = note?.takeIf { it.isNotBlank() },
                    loggedAt = now,
                )
            )
            exerciseDao.updateLastLoggedAt(exerciseId, now)
        }
    }

    fun deleteSet(setId: Long) {
        viewModelScope.launch { setDao.delete(setId) }
    }
}
