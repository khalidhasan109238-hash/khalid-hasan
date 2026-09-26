package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.AppLanguage
import com.example.data.local.DailyPulseDatabase
import com.example.data.remote.NetworkModule
import com.example.data.repository.DailyPulseRepository
import com.example.ui.screens.QuizScreen
import com.example.ui.screens.TodoScreen
import com.example.ui.screens.WeatherScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.DailyPulseViewModel
import com.example.ui.viewmodel.MainDestination

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val context = LocalContext.current.applicationContext
                val database = DailyPulseDatabase.getDatabase(context)
                val repository = DailyPulseRepository(
                    todoDao = database.todoDao(),
                    quizScoreDao = database.quizScoreDao(),
                    savedCityDao = database.savedCityDao(),
                    weatherApi = NetworkModule.weatherApi,
                    geocodingApi = NetworkModule.geocodingApi
                )
                val viewModel: DailyPulseViewModel = viewModel(
                    factory = DailyPulseViewModel.provideFactory(repository)
                )
                DailyPulseApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyPulseApp(viewModel: DailyPulseViewModel) {
    val language by viewModel.language.collectAsStateWithLifecycle()
    val currentDestination by viewModel.currentDestination.collectAsStateWithLifecycle()
    val todoState by viewModel.todoUiState.collectAsStateWithLifecycle()
    val weatherState by viewModel.weatherState.collectAsStateWithLifecycle()
    val savedCities by viewModel.savedCities.collectAsStateWithLifecycle()
    val useCelsius by viewModel.useCelsius.collectAsStateWithLifecycle()
    val citySearchQuery by viewModel.citySearchQuery.collectAsStateWithLifecycle()
    val citySearchResults by viewModel.citySearchResults.collectAsStateWithLifecycle()
    val isSearchingCity by viewModel.isSearchingCity.collectAsStateWithLifecycle()
    val quizSession by viewModel.quizSession.collectAsStateWithLifecycle()
    val quizHistory by viewModel.quizHistory.collectAsStateWithLifecycle()

    var showAddTodoDialog by rememberSaveable { mutableStateOf(false) }
    val isBn = language == AppLanguage.BENGALI

    if (currentDestination != MainDestination.TODO && !quizSession.isInActiveGame) {
        BackHandler {
            viewModel.selectDestination(MainDestination.TODO)
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 600.dp

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = stringResource(id = R.string.app_name),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = if (isBn) {
                                    stringResource(id = R.string.app_subtitle_bn)
                                } else {
                                    stringResource(id = R.string.app_subtitle_en)
                                },
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    actions = {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .minimumInteractiveComponentSize()
                                .clip(CircleShape)
                                .clickable { viewModel.toggleLanguage() }
                                .testTag("toggle_language_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = stringResource(id = R.string.toggle_language_desc),
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Text(
                                    text = if (isBn) "বাংলা • EN" else "EN • বাংলা",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            },
            floatingActionButton = {
                if (currentDestination == MainDestination.TODO) {
                    ExtendedFloatingActionButton(
                        onClick = { showAddTodoDialog = true },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(id = R.string.add_task_desc)
                            )
                        },
                        text = {
                            Text(
                                text = if (isBn) "নতুন কাজ" else "New Task",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.testTag("fab_add_task")
                    )
                }
            },
            bottomBar = {
                if (!isWideScreen) {
                    NavigationBar {
                        NavigationBarItem(
                            selected = currentDestination == MainDestination.TODO,
                            onClick = { viewModel.selectDestination(MainDestination.TODO) },
                            icon = {
                                Icon(
                                    imageVector = if (currentDestination == MainDestination.TODO) {
                                        Icons.Filled.CheckCircle
                                    } else {
                                        Icons.Outlined.CheckCircle
                                    },
                                    contentDescription = stringResource(id = R.string.nav_todo_en)
                                )
                            },
                            label = {
                                Text(
                                    if (isBn) stringResource(id = R.string.nav_todo_bn)
                                    else stringResource(id = R.string.nav_todo_en)
                                )
                            },
                            modifier = Modifier.testTag("nav_tab_todo")
                        )

                        NavigationBarItem(
                            selected = currentDestination == MainDestination.WEATHER,
                            onClick = { viewModel.selectDestination(MainDestination.WEATHER) },
                            icon = {
                                Icon(
                                    imageVector = if (currentDestination == MainDestination.WEATHER) {
                                        Icons.Filled.Cloud
                                    } else {
                                        Icons.Outlined.Cloud
                                    },
                                    contentDescription = stringResource(id = R.string.nav_weather_en)
                                )
                            },
                            label = {
                                Text(
                                    if (isBn) stringResource(id = R.string.nav_weather_bn)
                                    else stringResource(id = R.string.nav_weather_en)
                                )
                            },
                            modifier = Modifier.testTag("nav_tab_weather")
                        )

                        NavigationBarItem(
                            selected = currentDestination == MainDestination.QUIZ,
                            onClick = { viewModel.selectDestination(MainDestination.QUIZ) },
                            icon = {
                                Icon(
                                    imageVector = if (currentDestination == MainDestination.QUIZ) {
                                        Icons.Filled.Quiz
                                    } else {
                                        Icons.Outlined.Quiz
                                    },
                                    contentDescription = stringResource(id = R.string.nav_quiz_en)
                                )
                            },
                            label = {
                                Text(
                                    if (isBn) stringResource(id = R.string.nav_quiz_bn)
                                    else stringResource(id = R.string.nav_quiz_en)
                                )
                            },
                            modifier = Modifier.testTag("nav_tab_quiz")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (isWideScreen) {
                    NavigationRail(
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        NavigationRailItem(
                            selected = currentDestination == MainDestination.TODO,
                            onClick = { viewModel.selectDestination(MainDestination.TODO) },
                            icon = {
                                Icon(
                                    imageVector = if (currentDestination == MainDestination.TODO) {
                                        Icons.Filled.CheckCircle
                                    } else {
                                        Icons.Outlined.CheckCircle
                                    },
                                    contentDescription = stringResource(id = R.string.nav_todo_en)
                                )
                            },
                            label = {
                                Text(
                                    if (isBn) stringResource(id = R.string.nav_todo_bn)
                                    else stringResource(id = R.string.nav_todo_en)
                                )
                            },
                            modifier = Modifier.testTag("nav_rail_todo")
                        )

                        NavigationRailItem(
                            selected = currentDestination == MainDestination.WEATHER,
                            onClick = { viewModel.selectDestination(MainDestination.WEATHER) },
                            icon = {
                                Icon(
                                    imageVector = if (currentDestination == MainDestination.WEATHER) {
                                        Icons.Filled.Cloud
                                    } else {
                                        Icons.Outlined.Cloud
                                    },
                                    contentDescription = stringResource(id = R.string.nav_weather_en)
                                )
                            },
                            label = {
                                Text(
                                    if (isBn) stringResource(id = R.string.nav_weather_bn)
                                    else stringResource(id = R.string.nav_weather_en)
                                )
                            },
                            modifier = Modifier.testTag("nav_rail_weather")
                        )

                        NavigationRailItem(
                            selected = currentDestination == MainDestination.QUIZ,
                            onClick = { viewModel.selectDestination(MainDestination.QUIZ) },
                            icon = {
                                Icon(
                                    imageVector = if (currentDestination == MainDestination.QUIZ) {
                                        Icons.Filled.Quiz
                                    } else {
                                        Icons.Outlined.Quiz
                                    },
                                    contentDescription = stringResource(id = R.string.nav_quiz_en)
                                )
                            },
                            label = {
                                Text(
                                    if (isBn) stringResource(id = R.string.nav_quiz_bn)
                                    else stringResource(id = R.string.nav_quiz_en)
                                )
                            },
                            modifier = Modifier.testTag("nav_rail_quiz")
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    val contentModifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 760.dp)

                    when (currentDestination) {
                        MainDestination.TODO -> {
                            TodoScreen(
                                state = todoState,
                                language = language,
                                showAddDialog = showAddTodoDialog,
                                onDismissAddDialog = { showAddTodoDialog = false },
                                onOpenAddDialog = { showAddTodoDialog = true },
                                onAddTask = viewModel::addTask,
                                onToggleTask = viewModel::toggleTaskCompletion,
                                onDeleteTask = viewModel::deleteTask,
                                onClearCompleted = viewModel::clearCompletedTasks,
                                onFilterStatusChange = viewModel::setTodoFilterStatus,
                                onCategoryFilterChange = viewModel::setTodoCategoryFilter,
                                onSearchQueryChange = viewModel::setTodoSearchQuery,
                                modifier = contentModifier
                            )
                        }

                        MainDestination.WEATHER -> {
                            WeatherScreen(
                                weatherState = weatherState,
                                savedCities = savedCities,
                                useCelsius = useCelsius,
                                citySearchQuery = citySearchQuery,
                                citySearchResults = citySearchResults,
                                isSearchingCity = isSearchingCity,
                                language = language,
                                onToggleUnit = viewModel::toggleTemperatureUnit,
                                onSelectSavedCity = viewModel::selectCityAndFetchWeather,
                                onSearchQueryChange = viewModel::updateCitySearchQuery,
                                onSelectSearchResult = viewModel::selectSearchedCity,
                                onUseDeviceLocation = viewModel::useDeviceCoordinates,
                                onRefresh = viewModel::refreshWeather,
                                modifier = contentModifier
                            )
                        }

                        MainDestination.QUIZ -> {
                            QuizScreen(
                                sessionState = quizSession,
                                scoreHistory = quizHistory,
                                language = language,
                                onStartCategory = viewModel::startQuizCategory,
                                onStartMixedChallenge = viewModel::startMixedChallenge,
                                onSelectOption = viewModel::submitQuizOption,
                                onNextQuestion = viewModel::proceedToNextQuestion,
                                onExitQuiz = viewModel::exitQuizSession,
                                onClearHistory = viewModel::clearQuizScores,
                                modifier = contentModifier
                            )
                        }
                    }
                }
            }
        }
    }
}
