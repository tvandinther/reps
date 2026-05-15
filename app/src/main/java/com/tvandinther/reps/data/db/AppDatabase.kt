package com.tvandinther.reps.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tvandinther.reps.data.model.ExerciseEntity
import com.tvandinther.reps.data.model.SetEntity
import com.tvandinther.reps.data.model.UnitEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [UnitEntity::class, ExerciseEntity::class, SetEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun unitDao(): UnitDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun setDao(): SetDao

    companion object {
        fun create(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "reps.db")
                .addCallback(SeedCallback())
                .build()
    }
}

private class SeedCallback : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        CoroutineScope(Dispatchers.IO).launch {
            seedUnits(db)
        }
    }

    private fun seedUnits(db: SupportSQLiteDatabase) {
        val volumeUnits = listOf(
            Triple("reps", "volume", 1),
            Triple("seconds", "volume", 0),
            Triple("minutes", "volume", 0),
            Triple("meters", "volume", 0),
            Triple("km", "volume", 0),
            Triple("miles", "volume", 0),
            Triple("calories", "volume", 0),
        )
        val resistanceUnits = listOf(
            Triple("kg", "resistance", 1),
            Triple("lb", "resistance", 0),
            Triple("bodyweight", "resistance", 0),
            Triple("level", "resistance", 0),
            Triple("band", "resistance", 0),
            Triple("none", "resistance", 0),
        )
        (volumeUnits + resistanceUnits).forEach { (label, type, isDefault) ->
            db.execSQL(
                "INSERT INTO units (label, type, is_default) VALUES (?, ?, ?)",
                arrayOf(label, type, isDefault),
            )
        }
    }
}
