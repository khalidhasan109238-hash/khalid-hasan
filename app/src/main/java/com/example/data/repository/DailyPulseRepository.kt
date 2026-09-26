package com.example.data.repository

import com.example.data.local.QuizScoreDao
import com.example.data.local.QuizScoreRecord
import com.example.data.local.SavedCityDao
import com.example.data.local.SavedWeatherCity
import com.example.data.local.TaskCategory
import com.example.data.local.TaskPriority
import com.example.data.local.TodoDao
import com.example.data.local.TodoTask
import com.example.data.remote.GeocodingApiService
import com.example.data.remote.GeocodingCityDto
import com.example.data.remote.WeatherApiService
import kotlinx.coroutines.flow.Flow
import kotlin.math.roundToInt

data class HourlyForecastItem(
    val hourLabel: String,
    val temperatureC: Double,
    val weatherCode: Int,
    val rainProbability: Int
)

data class DailyForecastItem(
    val dateString: String,
    val maxTempC: Double,
    val minTempC: Double,
    val weatherCode: Int,
    val rainProbability: Int,
    val uvIndex: Double
)

data class WeatherSnapshot(
    val cityNameEn: String,
    val cityNameBn: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val currentTempC: Double,
    val feelsLikeTempC: Double,
    val humidityPercent: Int,
    val windSpeedKmh: Double,
    val pressureHpa: Int,
    val weatherCode: Int,
    val isDay: Boolean,
    val uvIndexMax: Double,
    val hourlyItems: List<HourlyForecastItem>,
    val dailyItems: List<DailyForecastItem>,
    val updatedAtMillis: Long = System.currentTimeMillis()
)

class DailyPulseRepository(
    private val todoDao: TodoDao,
    private val quizScoreDao: QuizScoreDao,
    private val savedCityDao: SavedCityDao,
    private val weatherApi: WeatherApiService,
    private val geocodingApi: GeocodingApiService
) {
    val allTasks: Flow<List<TodoTask>> = todoDao.getAllTasks()
    val allQuizScores: Flow<List<QuizScoreRecord>> = quizScoreDao.getAllScores()
    val allSavedCities: Flow<List<SavedWeatherCity>> = savedCityDao.getAllCities()

    suspend fun ensureInitialSeedData() {
        if (todoDao.getTaskCount() == 0) {
            val now = System.currentTimeMillis()
            val starterTasks = listOf(
                TodoTask(
                    title = "সকালের মর্নিং ওয়াক ও ব্যায়াম (Morning Walk & Exercise)",
                    notes = "৩০ মিনিট হাঁটা এবং পর্যাপ্ত পানি পান করা",
                    category = TaskCategory.HEALTH.name,
                    priority = TaskPriority.HIGH.name,
                    dueDateLabel = "Today • 7:00 AM",
                    isCompleted = true,
                    createdAt = now - 3600_000L
                ),
                TodoTask(
                    title = "প্রজেক্টের UI ডিজাইন ও কোড রিভিউ (Review Android App UI)",
                    notes = "Jetpack Compose এবং Room Database মডিউলগুলো যাচাই করা",
                    category = TaskCategory.WORK.name,
                    priority = TaskPriority.HIGH.name,
                    dueDateLabel = "Today • 11:30 AM",
                    isCompleted = false,
                    createdAt = now - 2400_000L
                ),
                TodoTask(
                    title = "নতুন একটি অধ্যায় পড়া ও নোট তৈরি (Read & Summarize 1 Chapter)",
                    notes = "কটলিন এবং অ্যালগরিদম অনুশীলন করা",
                    category = TaskCategory.STUDY.name,
                    priority = TaskPriority.MEDIUM.name,
                    dueDateLabel = "Today • 5:00 PM",
                    isCompleted = false,
                    createdAt = now - 1200_000L
                ),
                TodoTask(
                    title = "পরিবারের সাথে কথা বলা ও বাজার তালিকা (Family Call & Groceries)",
                    notes = "ফলমূল, শাকসবজি ও প্রয়োজনীয় জিনিসপত্র কেনা",
                    category = TaskCategory.PERSONAL.name,
                    priority = TaskPriority.LOW.name,
                    dueDateLabel = "Evening • 8:00 PM",
                    isCompleted = false,
                    createdAt = now - 600_000L
                )
            )
            todoDao.insertAllTasks(starterTasks)
        }

        if (savedCityDao.getCityCount() == 0) {
            val starterCities = listOf(
                SavedWeatherCity(
                    nameEn = "Dhaka",
                    nameBn = "ঢাকা",
                    country = "Bangladesh",
                    latitude = 23.8103,
                    longitude = 90.4125,
                    isSelected = true
                ),
                SavedWeatherCity(
                    nameEn = "Chattogram",
                    nameBn = "চট্টগ্রাম",
                    country = "Bangladesh",
                    latitude = 22.3569,
                    longitude = 91.7832,
                    isSelected = false
                ),
                SavedWeatherCity(
                    nameEn = "Sylhet",
                    nameBn = "সিলেট",
                    country = "Bangladesh",
                    latitude = 24.8949,
                    longitude = 91.8687,
                    isSelected = false
                ),
                SavedWeatherCity(
                    nameEn = "Rajshahi",
                    nameBn = "রাজশাহী",
                    country = "Bangladesh",
                    latitude = 24.3745,
                    longitude = 88.6042,
                    isSelected = false
                ),
                SavedWeatherCity(
                    nameEn = "Kolkata",
                    nameBn = "কলকাতা",
                    country = "India",
                    latitude = 22.5726,
                    longitude = 88.3639,
                    isSelected = false
                ),
                SavedWeatherCity(
                    nameEn = "London",
                    nameBn = "লন্ডন",
                    country = "United Kingdom",
                    latitude = 51.5074,
                    longitude = -0.1278,
                    isSelected = false
                )
            )
            savedCityDao.insertAllCities(starterCities)
        }
    }

    // Todo Operations
    suspend fun addTask(task: TodoTask) = todoDao.insertTask(task)
    suspend fun updateTask(task: TodoTask) = todoDao.updateTask(task)
    suspend fun deleteTask(task: TodoTask) = todoDao.deleteTask(task)
    suspend fun clearCompletedTasks() = todoDao.clearCompletedTasks()

    // Quiz Operations
    suspend fun saveQuizScore(record: QuizScoreRecord) = quizScoreDao.insertScore(record)
    suspend fun clearQuizHistory() = quizScoreDao.clearAllScores()

    // Weather & City Operations
    suspend fun selectSavedCity(cityId: Int) = savedCityDao.selectCity(cityId)

    suspend fun addAndSelectCity(
        nameEn: String,
        nameBn: String,
        country: String,
        latitude: Double,
        longitude: Double
    ): SavedWeatherCity {
        val city = SavedWeatherCity(
            nameEn = nameEn,
            nameBn = nameBn,
            country = country,
            latitude = latitude,
            longitude = longitude,
            isSelected = true
        )
        val newId = savedCityDao.insertCity(city).toInt()
        savedCityDao.selectCity(newId)
        return city.copy(id = newId)
    }

    suspend fun deleteSavedCity(cityId: Int) = savedCityDao.deleteCityById(cityId)

    suspend fun searchCitiesOnline(query: String): List<GeocodingCityDto> {
        if (query.trim().length < 2) return emptyList()
        val response = geocodingApi.searchCity(query = query.trim())
        return response.results.orEmpty()
    }

    suspend fun fetchLiveWeather(city: SavedWeatherCity): WeatherSnapshot {
        val response = weatherApi.getForecast(
            latitude = city.latitude,
            longitude = city.longitude
        )

        val current = response.current
        val hourly = response.hourly
        val daily = response.daily

        val hourlyList = mutableListOf<HourlyForecastItem>()
        if (hourly != null) {
            val count = minOf(12, hourly.time.size, hourly.temperature2m.size, hourly.weatherCode.size)
            for (i in 0 until count) {
                val rawTime = hourly.time[i]
                val hourPart = rawTime.substringAfter("T", rawTime)
                val rainProb = hourly.precipitationProbability?.getOrNull(i) ?: 0
                hourlyList.add(
                    HourlyForecastItem(
                        hourLabel = hourPart,
                        temperatureC = hourly.temperature2m[i],
                        weatherCode = hourly.weatherCode[i],
                        rainProbability = rainProb
                    )
                )
            }
        }

        val dailyList = mutableListOf<DailyForecastItem>()
        if (daily != null) {
            val count = minOf(
                7,
                daily.time.size,
                daily.temperature2mMax.size,
                daily.temperature2mMin.size,
                daily.weatherCode.size
            )
            for (i in 0 until count) {
                dailyList.add(
                    DailyForecastItem(
                        dateString = daily.time[i],
                        maxTempC = daily.temperature2mMax[i],
                        minTempC = daily.temperature2mMin[i],
                        weatherCode = daily.weatherCode[i],
                        rainProbability = daily.precipitationProbabilityMax?.getOrNull(i) ?: 0,
                        uvIndex = daily.uvIndexMax?.getOrNull(i) ?: 0.0
                    )
                )
            }
        }

        val maxUvToday = daily?.uvIndexMax?.firstOrNull() ?: 5.0

        return WeatherSnapshot(
            cityNameEn = city.nameEn,
            cityNameBn = city.nameBn,
            country = city.country,
            latitude = city.latitude,
            longitude = city.longitude,
            currentTempC = current?.temperature2m ?: 28.0,
            feelsLikeTempC = current?.apparentTemperature ?: 30.0,
            humidityPercent = current?.relativeHumidity2m ?: 65,
            windSpeedKmh = current?.windSpeed10m ?: 12.0,
            pressureHpa = (current?.surfacePressure ?: 1012.0).roundToInt(),
            weatherCode = current?.weatherCode ?: 0,
            isDay = (current?.isDay ?: 1) == 1,
            uvIndexMax = maxUvToday,
            hourlyItems = hourlyList,
            dailyItems = dailyList
        )
    }
}
