package com.tvandinther.reps.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tvandinther.reps.data.model.ExerciseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Query(
        """
        SELECT * FROM exercises
        ORDER BY CASE WHEN last_logged_at IS NULL THEN 1 ELSE 0 END, last_logged_at DESC, created_at DESC
        """
    )
    fun getAll(): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE id = :id")
    fun getById(id: Long): Flow<ExerciseEntity?>

    @Query(
        """
        SELECT * FROM exercises
        WHERE name LIKE '%' || :query || '%'
        ORDER BY CASE WHEN last_logged_at IS NULL THEN 1 ELSE 0 END, last_logged_at DESC, created_at DESC
        """
    )
    fun search(query: String): Flow<List<ExerciseEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(exercise: ExerciseEntity): Long

    @Query("UPDATE exercises SET last_logged_at = :timestamp WHERE id = :id")
    suspend fun updateLastLoggedAt(id: Long, timestamp: Long)

    @Update
    suspend fun update(exercise: ExerciseEntity)

    @Query("DELETE FROM exercises WHERE id = :id")
    suspend fun deleteById(id: Long)
}
