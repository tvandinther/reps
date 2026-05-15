package com.tvandinther.reps.domain

data class Session(
    val sets: List<ExerciseGroup>,
    val startedAt: Long,
)

data class ExerciseGroup(
    val exerciseId: Long,
    val sets: List<SessionSet>,
)

data class SessionSet(
    val id: Long,
    val volumeValue: Double,
    val resistanceValue: Double?,
    val rpe: Int?,
    val note: String?,
    val loggedAt: Long,
)
