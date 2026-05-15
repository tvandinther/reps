package com.tvandinther.reps.domain

import com.tvandinther.reps.data.model.SetWithBreak

class SessionAssembler {

    // Input is ordered by logged_at DESC (most recent first), as returned by the DAO.
    // session_break = 1 means the gap before this set (looking backwards in time) exceeds
    // the threshold — i.e., this set is the most recent set of an *older* session.
    fun assemble(rows: List<SetWithBreak>): List<Session> {
        if (rows.isEmpty()) return emptyList()

        val sessions = mutableListOf<MutableList<SetWithBreak>>()
        var current = mutableListOf<SetWithBreak>()

        for (row in rows) {
            if (row.sessionBreak == 1 && current.isNotEmpty()) {
                sessions.add(current)
                current = mutableListOf()
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
            )
        }
    }
}
