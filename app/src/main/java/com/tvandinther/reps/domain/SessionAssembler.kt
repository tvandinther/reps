package com.tvandinther.reps.domain

import com.tvandinther.reps.data.model.SetEntity

class SessionAssembler {

    // Input is ordered by logged_at DESC (most recent first), as returned by the DAO.
    // A new session boundary is declared when the gap between consecutive sets exceeds gapMs.
    fun assemble(rows: List<SetEntity>, gapMs: Long): List<Session> {
        if (rows.isEmpty()) return emptyList()

        val sessions = mutableListOf<MutableList<SetEntity>>()
        var current = mutableListOf<SetEntity>()

        for (i in rows.indices) {
            val row = rows[i]
            if (i > 0) {
                val prev = rows[i - 1]
                // rows are DESC, so prev.loggedAt > row.loggedAt
                if ((prev.loggedAt - row.loggedAt) > gapMs && current.isNotEmpty()) {
                    sessions.add(current)
                    current = mutableListOf()
                }
            }
            current.add(row)
        }
        if (current.isNotEmpty()) sessions.add(current)

        return sessions.map { sessionRows ->
            val groupedByExercise = sessionRows
                .groupBy { it.exerciseId }
                .map { (exerciseId, sets) ->
                    ExerciseGroup(
                        exerciseId = exerciseId,
                        sets = sets.sortedBy { it.loggedAt }.map { s ->
                            SessionSet(
                                id = s.id,
                                volumeValue = s.volumeValue,
                                resistanceValue = s.resistanceValue,
                                rpe = s.rpe,
                                note = s.note,
                                loggedAt = s.loggedAt,
                            )
                        },
                    )
                }
            Session(
                sets = groupedByExercise,
                startedAt = sessionRows.minOf { it.loggedAt },
                endedAt = sessionRows.maxOf { it.loggedAt },
            )
        }
    }
}
