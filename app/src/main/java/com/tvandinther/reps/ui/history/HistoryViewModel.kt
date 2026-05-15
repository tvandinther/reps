package com.tvandinther.reps.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tvandinther.reps.data.db.ExerciseDao
import com.tvandinther.reps.data.db.SetDao
import com.tvandinther.reps.data.model.ExerciseEntity
import com.tvandinther.reps.domain.Session
import com.tvandinther.reps.domain.SessionAssembler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

private const val SESSION_GAP_MS = 90L * 60 * 1000

data class HistoryUiState(
    val sessions: List<Session> = emptyList(),
    val exercises: Map<Long, ExerciseEntity> = emptyMap(),
)

class HistoryViewModel(
    setDao: SetDao,
    exerciseDao: ExerciseDao,
) : ViewModel() {

    private val assembler = SessionAssembler()

    val uiState: StateFlow<HistoryUiState> = combine(
        setDao.getSetsWithSessionBreaks(SESSION_GAP_MS),
        exerciseDao.getAll(),
    ) { setsWithBreaks, exercises ->
        HistoryUiState(
            sessions = assembler.assemble(setsWithBreaks),
            exercises = exercises.associateBy { it.id },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HistoryUiState())
}
