package com.tvandinther.reps.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tvandinther.reps.data.model.SetEntity
import com.tvandinther.reps.data.model.SetWithBreak
import kotlinx.coroutines.flow.Flow

@Dao
interface SetDao {
    @Query("SELECT * FROM sets WHERE exercise_id = :exerciseId ORDER BY logged_at ASC")
    fun getForExercise(exerciseId: Long): Flow<List<SetEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(set: SetEntity): Long

    @Query("DELETE FROM sets WHERE id = :setId")
    suspend fun delete(setId: Long)

    @Query(
        """
        SELECT *,
          CASE
            WHEN (logged_at - LAG(logged_at) OVER (ORDER BY logged_at)) > :gapMs
            THEN 1 ELSE 0
          END AS session_break
        FROM sets
        ORDER BY logged_at DESC
        """
    )
    fun getSetsWithSessionBreaks(gapMs: Long): Flow<List<SetWithBreak>>
}
