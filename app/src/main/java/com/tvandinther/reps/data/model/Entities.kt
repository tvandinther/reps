package com.tvandinther.reps.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "units")
data class UnitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val label: String,
    val type: String, // "volume" | "resistance"
    @ColumnInfo(name = "is_default") val isDefault: Boolean = false,
)

@Entity(
    tableName = "exercises",
    foreignKeys = [
        ForeignKey(
            entity = UnitEntity::class,
            parentColumns = ["id"],
            childColumns = ["volume_unit_id"],
            onDelete = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = UnitEntity::class,
            parentColumns = ["id"],
            childColumns = ["resistance_unit_id"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [
        Index("volume_unit_id"),
        Index("resistance_unit_id"),
    ],
)
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "volume_unit_id") val volumeUnitId: Long,
    @ColumnInfo(name = "resistance_unit_id") val resistanceUnitId: Long,
    val note: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "last_logged_at") val lastLoggedAt: Long? = null,
)

@Entity(
    tableName = "sets",
    foreignKeys = [
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exercise_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("exercise_id")],
)
data class SetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "exercise_id") val exerciseId: Long,
    @ColumnInfo(name = "volume_value") val volumeValue: Double,
    @ColumnInfo(name = "resistance_value") val resistanceValue: Double? = null,
    val rpe: Int? = null,
    val note: String? = null,
    @ColumnInfo(name = "logged_at") val loggedAt: Long,
)
