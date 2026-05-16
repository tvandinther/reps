package com.tvandinther.reps.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tvandinther.reps.data.model.SetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SetDao {
    @Query("SELECT * FROM sets WHERE exercise_id = :exerciseId ORDER BY logged_at ASC")
    fun getForExercise(exerciseId: Long): Flow<List<SetEntity>>

    @Query("SELECT * FROM sets ORDER BY logged_at DESC")
    fun getAll(): Flow<List<SetEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(set: SetEntity): Long

    @Query("SELECT * FROM sets WHERE id = :setId")
    suspend fun getById(setId: Long): SetEntity?

    @Query("DELETE FROM sets WHERE id = :setId")
    suspend fun delete(setId: Long)
}
