package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [TodoTask::class, QuizScoreRecord::class, SavedWeatherCity::class],
    version = 1,
    exportSchema = false
)
abstract class DailyPulseDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
    abstract fun quizScoreDao(): QuizScoreDao
    abstract fun savedCityDao(): SavedCityDao

    companion object {
        @Volatile
        private var INSTANCE: DailyPulseDatabase? = null

        fun getDatabase(context: Context): DailyPulseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DailyPulseDatabase::class.java,
                    "daily_pulse_db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
