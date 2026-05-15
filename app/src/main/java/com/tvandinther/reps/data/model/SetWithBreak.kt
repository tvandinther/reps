package com.tvandinther.reps.data.model

import androidx.room.ColumnInfo

data class SetWithBreak(
    val id: Long,
    @ColumnInfo(name = "exercise_id") val exerciseId: Long,
    @ColumnInfo(name = "volume_value") val volumeValue: Double,
    @ColumnInfo(name = "resistance_value") val resistanceValue: Double?,
    val rpe: Int?,
    val note: String?,
    @ColumnInfo(name = "logged_at") val loggedAt: Long,
    @ColumnInfo(name = "session_break") val sessionBreak: Int, // 1 = new session starts here
)
