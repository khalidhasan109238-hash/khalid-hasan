package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM todo_tasks ORDER BY isCompleted ASC, createdAt DESC")
    fun getAllTasks(): Flow<List<TodoTask>>

    @Query("SELECT COUNT(*) FROM todo_tasks")
    suspend fun getTaskCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TodoTask)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTasks(tasks: List<TodoTask>)

    @Update
    suspend fun updateTask(task: TodoTask)

    @Delete
    suspend fun deleteTask(task: TodoTask)

    @Query("DELETE FROM todo_tasks WHERE isCompleted = 1")
    suspend fun clearCompletedTasks()
}

@Dao
interface QuizScoreDao {
    @Query("SELECT * FROM quiz_scores ORDER BY playedAt DESC")
    fun getAllScores(): Flow<List<QuizScoreRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScore(scoreRecord: QuizScoreRecord)

    @Query("DELETE FROM quiz_scores")
    suspend fun clearAllScores()
}

@Dao
interface SavedCityDao {
    @Query("SELECT * FROM saved_cities ORDER BY isSelected DESC, id ASC")
    fun getAllCities(): Flow<List<SavedWeatherCity>>

    @Query("SELECT COUNT(*) FROM saved_cities")
    suspend fun getCityCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(city: SavedWeatherCity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCities(cities: List<SavedWeatherCity>)

    @Query("UPDATE saved_cities SET isSelected = CASE WHEN id = :cityId THEN 1 ELSE 0 END")
    suspend fun selectCity(cityId: Int)

    @Query("DELETE FROM saved_cities WHERE id = :cityId")
    suspend fun deleteCityById(cityId: Int)
}
