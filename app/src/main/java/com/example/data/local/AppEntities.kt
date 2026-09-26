package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class AppLanguage {
    BENGALI,
    ENGLISH
}

enum class TaskCategory(val labelEn: String, val labelBn: String) {
    WORK("Work", "কাজ ও অফিস"),
    STUDY("Study", "পড়াশোনা"),
    PERSONAL("Personal", "ব্যক্তিগত"),
    HEALTH("Health", "স্বাস্থ্য ও ফিটনেস")
}

enum class TaskPriority(val labelEn: String, val labelBn: String, val weight: Int) {
    HIGH("High", "জরুরি", 3),
    MEDIUM("Medium", "মাঝারি", 2),
    LOW("Low", "সাধারণ", 1)
}

@Entity(tableName = "todo_tasks")
data class TodoTask(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val notes: String = "",
    val category: String = TaskCategory.PERSONAL.name,
    val priority: String = TaskPriority.MEDIUM.name,
    val dueDateLabel: String = "Today",
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "quiz_scores")
data class QuizScoreRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val categoryId: String,
    val categoryNameEn: String,
    val categoryNameBn: String,
    val score: Int,
    val totalQuestions: Int,
    val accuracyPercent: Int,
    val bestStreak: Int,
    val playedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_cities")
data class SavedWeatherCity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nameEn: String,
    val nameBn: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val isSelected: Boolean = false
)
