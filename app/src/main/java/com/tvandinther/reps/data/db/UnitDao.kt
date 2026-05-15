package com.tvandinther.reps.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tvandinther.reps.data.model.UnitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UnitDao {
    @Query("SELECT * FROM units ORDER BY type, label")
    fun getAll(): Flow<List<UnitEntity>>

    @Query("SELECT * FROM units WHERE type = :type ORDER BY label")
    fun getByType(type: String): Flow<List<UnitEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(unit: UnitEntity)
}
