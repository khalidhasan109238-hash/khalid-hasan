package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppLanguage
import com.example.data.local.QuizScoreRecord
import com.example.data.local.SavedWeatherCity
import com.example.data.local.TaskCategory
import com.example.data.local.TaskPriority
import com.example.data.local.TodoTask
import com.example.data.remote.GeocodingCityDto
import com.example.data.repository.DailyPulseRepository
import com.example.data.repository.QuizCategoryInfo
import com.example.data.repository.QuizQuestion
import com.example.data.repository.QuizQuestionBank
import com.example.data.repository.WeatherSnapshot
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

enum class MainDestination(val route: String) {
    TODO("todo_dashboard"),
    WEATHER("weather_forecast"),
    QUIZ("quiz_arena")
}

enum class TodoFilterStatus {
    ALL, ACTIVE, COMPLETED
}

sealed class WeatherLoadState {
    data object Loading : WeatherLoadState()
    data class Success(val snapshot: WeatherSnapshot) : WeatherLoadState()
    data class Error(val messageEn: String, val messageBn: String) : WeatherLoadState()
}

data class TodoScreenState(
    val tasks: List<TodoTask> = emptyList(),
    val totalCount: Int = 0,
    val completedCount: Int = 0,
    val filterStatus: TodoFilterStatus = TodoFilterStatus.ALL,
    val selectedCategory: TaskCategory? = null,
    val searchQuery: String = ""
)

data class QuizSessionState(
    val isInActiveGame: Boolean = false,
    val isGameFinished: Boolean = false,
    val selectedCategory: QuizCategoryInfo? = null,
    val questions: List<QuizQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val isAnswerSubmitted: Boolean = false,
    val score: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val remainingSeconds: Int = 20
)

class DailyPulseViewModel(
    private val repository: DailyPulseRepository
) : ViewModel() {

    private val _language = MutableStateFlow(AppLanguage.BENGALI)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _currentDestination = MutableStateFlow(MainDestination.TODO)
    val currentDestination: StateFlow<MainDestination> = _currentDestination.asStateFlow()

    // Todo Filter States
    private val _todoFilterStatus = MutableStateFlow(TodoFilterStatus.ALL)
    private val _selectedTaskCategory = MutableStateFlow<TaskCategory?>(null)
    private val _todoSearchQuery = MutableStateFlow("")

    val todoUiState: StateFlow<TodoScreenState> = combine(
        repository.allTasks,
        _todoFilterStatus,
        _selectedTaskCategory,
        _todoSearchQuery
    ) { allTasks, status, category, query ->
        val filtered = allTasks.filter { task ->
            val matchesStatus = when (status) {
                TodoFilterStatus.ALL -> true
                TodoFilterStatus.ACTIVE -> !task.isCompleted
                TodoFilterStatus.COMPLETED -> task.isCompleted
            }
            val matchesCategory = category == null || task.category == category.name
            val matchesQuery = query.isBlank() ||
                task.title.contains(query, ignoreCase = true) ||
                task.notes.contains(query, ignoreCase = true)
            matchesStatus && matchesCategory && matchesQuery
        }
        TodoScreenState(
            tasks = filtered,
            totalCount = allTasks.size,
            completedCount = allTasks.count { it.isCompleted },
            filterStatus = status,
            selectedCategory = category,
            searchQuery = query
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TodoScreenState()
    )

    // Weather States
    val savedCities: StateFlow<List<SavedWeatherCity>> = repository.allSavedCities
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _weatherState = MutableStateFlow<WeatherLoadState>(WeatherLoadState.Loading)
    val weatherState: StateFlow<WeatherLoadState> = _weatherState.asStateFlow()

    private val _useCelsius = MutableStateFlow(true)
    val useCelsius: StateFlow<Boolean> = _useCelsius.asStateFlow()

    private val _citySearchQuery = MutableStateFlow("")
    val citySearchQuery: StateFlow<String> = _citySearchQuery.asStateFlow()

    private val _citySearchResults = MutableStateFlow<List<GeocodingCityDto>>(emptyList())
    val citySearchResults: StateFlow<List<GeocodingCityDto>> = _citySearchResults.asStateFlow()

    private val _isSearchingCity = MutableStateFlow(false)
    val isSearchingCity: StateFlow<Boolean> = _isSearchingCity.asStateFlow()

    private var activeWeatherCity: SavedWeatherCity = SavedWeatherCity(
        id = 1,
        nameEn = "Dhaka",
        nameBn = "ঢাকা",
        country = "Bangladesh",
        latitude = 23.8103,
        longitude = 90.4125,
        isSelected = true
    )

    // Quiz States
    val quizHistory: StateFlow<List<QuizScoreRecord>> = repository.allQuizScores
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _quizSession = MutableStateFlow(QuizSessionState())
    val quizSession: StateFlow<QuizSessionState> = _quizSession.asStateFlow()

    private var quizTimerJob: Job? = null
    private var citySearchJob: Job? = null

    init {
        viewModelScope.launch {
            repository.ensureInitialSeedData()
            loadWeatherForCity(activeWeatherCity)
        }
    }

    fun toggleLanguage() {
        _language.update {
            if (it == AppLanguage.BENGALI) AppLanguage.ENGLISH else AppLanguage.BENGALI
        }
    }

    fun selectDestination(destination: MainDestination) {
        _currentDestination.value = destination
    }

    // --- TODO ACTIONS ---
    fun setTodoFilterStatus(status: TodoFilterStatus) {
        _todoFilterStatus.value = status
    }

    fun setTodoCategoryFilter(category: TaskCategory?) {
        _selectedTaskCategory.value = category
    }

    fun setTodoSearchQuery(query: String) {
        _todoSearchQuery.value = query
    }

    fun addTask(
        title: String,
        notes: String,
        category: TaskCategory,
        priority: TaskPriority,
        dueDateLabel: String
    ) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.addTask(
                TodoTask(
                    title = title.trim(),
                    notes = notes.trim(),
                    category = category.name,
                    priority = priority.name,
                    dueDateLabel = dueDateLabel.ifBlank { "Today" }
                )
            )
        }
    }

    fun toggleTaskCompletion(task: TodoTask) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(task: TodoTask) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun clearCompletedTasks() {
        viewModelScope.launch {
            repository.clearCompletedTasks()
        }
    }

    // --- WEATHER ACTIONS ---
    fun toggleTemperatureUnit() {
        _useCelsius.update { !it }
    }

    fun selectCityAndFetchWeather(city: SavedWeatherCity) {
        activeWeatherCity = city
        viewModelScope.launch {
            if (city.id != 0) {
                repository.selectSavedCity(city.id)
            }
            loadWeatherForCity(city)
        }
    }

    fun refreshWeather() {
        viewModelScope.launch {
            loadWeatherForCity(activeWeatherCity)
        }
    }

    fun updateCitySearchQuery(query: String) {
        _citySearchQuery.value = query
        citySearchJob?.cancel()
        if (query.trim().length < 2) {
            _citySearchResults.value = emptyList()
            _isSearchingCity.value = false
            return
        }
        citySearchJob = viewModelScope.launch {
            _isSearchingCity.value = true
            delay(350)
            try {
                val results = repository.searchCitiesOnline(query)
                _citySearchResults.value = results
            } catch (_: Exception) {
                _citySearchResults.value = emptyList()
            } finally {
                _isSearchingCity.value = false
            }
        }
    }

    fun selectSearchedCity(dto: GeocodingCityDto) {
        _citySearchQuery.value = ""
        _citySearchResults.value = emptyList()
        viewModelScope.launch {
            val saved = repository.addAndSelectCity(
                nameEn = dto.name,
                nameBn = dto.name,
                country = dto.country ?: dto.admin1 ?: "",
                latitude = dto.latitude,
                longitude = dto.longitude
            )
            activeWeatherCity = saved
            loadWeatherForCity(saved)
        }
    }

    fun useDeviceCoordinates(latitude: Double, longitude: Double, labelEn: String, labelBn: String) {
        val gpsCity = SavedWeatherCity(
            id = 0,
            nameEn = labelEn,
            nameBn = labelBn,
            country = "GPS",
            latitude = latitude,
            longitude = longitude,
            isSelected = true
        )
        activeWeatherCity = gpsCity
        viewModelScope.launch {
            loadWeatherForCity(gpsCity)
        }
    }

    private suspend fun loadWeatherForCity(city: SavedWeatherCity) {
        _weatherState.value = WeatherLoadState.Loading
        try {
            val snapshot = repository.fetchLiveWeather(city)
            _weatherState.value = WeatherLoadState.Success(snapshot)
        } catch (e: Exception) {
            _weatherState.value = WeatherLoadState.Error(
                messageEn = "Unable to connect to Open-Meteo weather service. Check your internet connection and tap Retry.",
                messageBn = "আবহাওয়ার লাইভ সার্ভারের সাথে সংযোগ করা যায়নি। ইন্টারনেট সংযোগ যাচাই করে আবার চেষ্টা করুন।"
            )
        }
    }

    // --- QUIZ ACTIONS ---
    fun startQuizCategory(category: QuizCategoryInfo) {
        val questions = QuizQuestionBank.getQuestionsForCategory(category.id)
        if (questions.isEmpty()) return
        _quizSession.value = QuizSessionState(
            isInActiveGame = true,
            isGameFinished = false,
            selectedCategory = category,
            questions = questions,
            currentIndex = 0,
            selectedOptionIndex = null,
            isAnswerSubmitted = false,
            score = 0,
            currentStreak = 0,
            bestStreak = 0,
            remainingSeconds = 20
        )
        startQuestionTimer()
    }

    fun startMixedChallenge() {
        val mixedCategory = QuizCategoryInfo(
            id = "ALL",
            titleEn = "Mega Mixed Challenge",
            titleBn = "মেগা মিক্সড চ্যালেঞ্জ",
            subtitleEn = "8 randomized questions across all topics",
            subtitleBn = "সব বিষয়ের ওপর বাছাইকৃত ৮টি প্রশ্ন",
            badgeEmoji = "⚡"
        )
        startQuizCategory(mixedCategory)
    }

    private fun startQuestionTimer() {
        quizTimerJob?.cancel()
        quizTimerJob = viewModelScope.launch {
            while (_quizSession.value.remainingSeconds > 0 &&
                !_quizSession.value.isAnswerSubmitted &&
                _quizSession.value.isInActiveGame &&
                !_quizSession.value.isGameFinished
            ) {
                delay(1000L)
                _quizSession.update { state ->
                    if (state.isAnswerSubmitted || !state.isInActiveGame) {
                        state
                    } else {
                        val nextSec = (state.remainingSeconds - 1).coerceAtLeast(0)
                        if (nextSec == 0) {
                            state.copy(
                                remainingSeconds = 0,
                                isAnswerSubmitted = true,
                                currentStreak = 0
                            )
                        } else {
                            state.copy(remainingSeconds = nextSec)
                        }
                    }
                }
            }
        }
    }

    fun submitQuizOption(optionIndex: Int) {
        val current = _quizSession.value
        if (!current.isInActiveGame || current.isAnswerSubmitted || current.isGameFinished) return
        val question = current.questions.getOrNull(current.currentIndex) ?: return

        quizTimerJob?.cancel()
        val isCorrect = optionIndex == question.correctIndex
        val newScore = if (isCorrect) current.score + 1 else current.score
        val newStreak = if (isCorrect) current.currentStreak + 1 else 0
        val newBestStreak = maxOf(current.bestStreak, newStreak)

        _quizSession.value = current.copy(
            selectedOptionIndex = optionIndex,
            isAnswerSubmitted = true,
            score = newScore,
            currentStreak = newStreak,
            bestStreak = newBestStreak
        )
    }

    fun proceedToNextQuestion() {
        val current = _quizSession.value
        if (!current.isInActiveGame) return

        if (current.currentIndex + 1 < current.questions.size) {
            _quizSession.value = current.copy(
                currentIndex = current.currentIndex + 1,
                selectedOptionIndex = null,
                isAnswerSubmitted = false,
                remainingSeconds = 20
            )
            startQuestionTimer()
        } else {
            quizTimerJob?.cancel()
            val total = current.questions.size.coerceAtLeast(1)
            val accuracy = ((current.score.toDouble() / total.toDouble()) * 100.0).roundToInt()
            val category = current.selectedCategory
            viewModelScope.launch {
                repository.saveQuizScore(
                    QuizScoreRecord(
                        categoryId = category?.id ?: "ALL",
                        categoryNameEn = category?.titleEn ?: "Quiz Challenge",
                        categoryNameBn = category?.titleBn ?: "কুইজ চ্যালেঞ্জ",
                        score = current.score,
                        totalQuestions = total,
                        accuracyPercent = accuracy,
                        bestStreak = current.bestStreak
                    )
                )
            }
            _quizSession.value = current.copy(
                isGameFinished = true,
                isAnswerSubmitted = false
            )
        }
    }

    fun exitQuizSession() {
        quizTimerJob?.cancel()
        _quizSession.value = QuizSessionState()
    }

    fun clearQuizScores() {
        viewModelScope.launch {
            repository.clearQuizHistory()
        }
    }

    companion object {
        fun provideFactory(repository: DailyPulseRepository): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DailyPulseViewModel(repository) as T
                }
            }
        }
    }
}
