package com.tvandinther.reps.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tvandinther.reps.data.model.ExerciseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Query(
        """
        SELECT * FROM exercises
        ORDER BY last_logged_at DESC NULLS LAST, created_at DESC
        """
    )
    fun getAll(): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE id = :id")
    fun getById(id: Long): Flow<ExerciseEntity?>

    @Query(
        """
        SELECT * FROM exercises
        WHERE name LIKE '%' || :query || '%'
        ORDER BY last_logged_at DESC NULLS LAST, created_at DESC
        """
    )
    fun search(query: String): Flow<List<ExerciseEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(exercise: ExerciseEntity): Long

    @Query("UPDATE exercises SET last_logged_at = :timestamp WHERE id = :id")
    suspend fun updateLastLoggedAt(id: Long, timestamp: Long)
}
