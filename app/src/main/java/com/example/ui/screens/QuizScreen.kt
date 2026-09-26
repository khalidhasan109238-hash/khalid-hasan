package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.AppLanguage
import com.example.data.local.QuizScoreRecord
import com.example.data.repository.QuizCategoryInfo
import com.example.data.repository.QuizQuestionBank
import com.example.ui.theme.PriorityHighColor
import com.example.ui.theme.PriorityLowColor
import com.example.ui.viewmodel.QuizSessionState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun QuizScreen(
    sessionState: QuizSessionState,
    scoreHistory: List<QuizScoreRecord>,
    language: AppLanguage,
    onStartCategory: (QuizCategoryInfo) -> Unit,
    onStartMixedChallenge: () -> Unit,
    onSelectOption: (Int) -> Unit,
    onNextQuestion: () -> Unit,
    onExitQuiz: () -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isBn = language == AppLanguage.BENGALI

    if (sessionState.isInActiveGame) {
        BackHandler {
            onExitQuiz()
        }
        if (sessionState.isGameFinished) {
            QuizResultSummaryView(
                sessionState = sessionState,
                isBn = isBn,
                onReplay = {
                    val cat = sessionState.selectedCategory
                    if (cat != null && cat.id != "ALL") {
                        onStartCategory(cat)
                    } else {
                        onStartMixedChallenge()
                    }
                },
                onBackToLobby = onExitQuiz,
                modifier = modifier
            )
        } else {
            ActiveQuizPlayView(
                sessionState = sessionState,
                isBn = isBn,
                onSelectOption = onSelectOption,
                onNextQuestion = onNextQuestion,
                onExitQuiz = onExitQuiz,
                modifier = modifier
            )
        }
    } else {
        QuizLobbyView(
            scoreHistory = scoreHistory,
            isBn = isBn,
            onStartCategory = onStartCategory,
            onStartMixedChallenge = onStartMixedChallenge,
            onClearHistory = onClearHistory,
            modifier = modifier
        )
    }
}

@Composable
private fun QuizLobbyView(
    scoreHistory: List<QuizScoreRecord>,
    isBn: Boolean,
    onStartCategory: (QuizCategoryInfo) -> Unit,
    onStartMixedChallenge: () -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("quiz_lobby_container"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Quiz Arena Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quiz_hero_card"),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(205.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_quiz),
                        contentDescription = stringResource(id = R.string.hero_quiz_desc),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xE61E1B4B),
                                        Color(0xCC312E81),
                                        Color(0x991E1B4B)
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(
                                color = Color(0x33FBBF24),
                                shape = CircleShape
                            ) {
                                Text(
                                    text = if (isBn) "🏆 মেধা যাচাই ও কুইজ" else "🏆 Interactive Brain Arena",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color(0xFFFDE047),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                            Text(
                                text = if (isBn) "কুইজ চ্যালেঞ্জ এরিনা" else "Knowledge Quiz Arena",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White
                            )
                            Text(
                                text = if (isBn) {
                                    "২০ সেকেন্ডের টাইমার ও তাৎক্ষণিক ব্যাখ্যাসহ মেধা যাচাই করুন"
                                } else {
                                    "Timed questions with instant explanations & Room score tracking"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFE0E7FF)
                            )
                        }

                        Button(
                            onClick = onStartMixedChallenge,
                            modifier = Modifier.testTag("start_mixed_quiz_button")
                        ) {
                            Icon(imageVector = Icons.Default.Bolt, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isBn) "মেগা মিক্সড কুইজ শুরু করুন" else "Start Mega Mixed Quiz"
                            )
                        }
                    }
                }
            }
        }

        // Category Selection Header
        item {
            Text(
                text = if (isBn) "বিষয়ভিত্তিক কুইজ নির্বাচন করুন" else "Choose a Quiz Category",
                style = MaterialTheme.typography.titleLarge
            )
        }

        // Categories List
        items(QuizQuestionBank.categories, key = { it.id }) { category ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .clickable { onStartCategory(category) }
                    .testTag("quiz_category_${category.id}"),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.size(52.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = category.badgeEmoji,
                                    style = MaterialTheme.typography.headlineMedium
                                )
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = if (isBn) category.titleBn else category.titleEn,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = if (isBn) category.subtitleBn else category.subtitleEn,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = { onStartCategory(category) },
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = if (isBn) "কুইজ শুরু" else "Start Quiz",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Score History Persisted in Room
        if (scoreHistory.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBn) "সাম্প্রতিক স্কোর ও রেকর্ড" else "Recent Quiz Scores",
                        style = MaterialTheme.typography.titleLarge
                    )
                    TextButton(
                        onClick = onClearHistory,
                        modifier = Modifier.testTag("clear_quiz_history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isBn) "ইতিহাস মুছুন" else "Clear")
                    }
                }
            }

            items(scoreHistory.take(8), key = { it.id }) { record ->
                QuizScoreHistoryRow(record = record, isBn = isBn)
            }
        }
    }
}

@Composable
private fun ActiveQuizPlayView(
    sessionState: QuizSessionState,
    isBn: Boolean,
    onSelectOption: (Int) -> Unit,
    onNextQuestion: () -> Unit,
    onExitQuiz: () -> Unit,
    modifier: Modifier = Modifier
) {
    val question = sessionState.questions.getOrNull(sessionState.currentIndex) ?: return
    val totalQuestions = sessionState.questions.size.coerceAtLeast(1)
    val progress = (sessionState.currentIndex + 1).toFloat() / totalQuestions.toFloat()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("active_quiz_container"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Header Bar
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        IconButton(
                            onClick = onExitQuiz,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("exit_quiz_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = if (isBn) "ফিরে যান" else "Back to Quiz Lobby"
                            )
                        }
                        Column {
                            Text(
                                text = if (isBn) {
                                    sessionState.selectedCategory?.titleBn ?: "কুইজ"
                                } else {
                                    sessionState.selectedCategory?.titleEn ?: "Quiz"
                                },
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = if (isBn) {
                                    "প্রশ্ন ${sessionState.currentIndex + 1} / $totalQuestions • স্কোর: ${sessionState.score}"
                                } else {
                                    "Question ${sessionState.currentIndex + 1} of $totalQuestions • Score: ${sessionState.score}"
                                },
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Countdown Timer Badge
                    Surface(
                        color = if (sessionState.remainingSeconds <= 5) {
                            PriorityHighColor.copy(alpha = 0.16f)
                        } else {
                            MaterialTheme.colorScheme.secondaryContainer
                        },
                        shape = CircleShape
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = if (sessionState.remainingSeconds <= 5) {
                                    PriorityHighColor
                                } else {
                                    MaterialTheme.colorScheme.onSecondaryContainer
                                },
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${sessionState.remainingSeconds}s",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (sessionState.remainingSeconds <= 5) {
                                    PriorityHighColor
                                } else {
                                    MaterialTheme.colorScheme.onSecondaryContainer
                                }
                            )
                        }
                    }
                }

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape)
                )
            }
        }

        // Question Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isBn) question.questionBn else question.questionEn,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Options List
        val options = if (isBn) question.optionsBn else question.optionsEn
        items(options.size) { index ->
            val optionText = options[index]
            val isSelected = sessionState.selectedOptionIndex == index
            val isCorrect = index == question.correctIndex
            val submitted = sessionState.isAnswerSubmitted

            val borderColor = when {
                !submitted -> MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                isCorrect -> PriorityLowColor
                isSelected && !isCorrect -> PriorityHighColor
                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
            }

            val containerColor = when {
                !submitted -> MaterialTheme.colorScheme.surface
                isCorrect -> PriorityLowColor.copy(alpha = 0.14f)
                isSelected && !isCorrect -> PriorityHighColor.copy(alpha = 0.14f)
                else -> MaterialTheme.colorScheme.surface
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .clickable(enabled = !submitted) { onSelectOption(index) }
                    .testTag("quiz_option_$index"),
                shape = MaterialTheme.shapes.medium,
                border = BorderStroke(width = 2.dp, color = borderColor),
                colors = CardDefaults.cardColors(containerColor = containerColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${'A' + index}",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(
                            text = optionText,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    if (submitted) {
                        if (isCorrect) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = if (isBn) "সঠিক উত্তর" else "Correct Answer",
                                tint = PriorityLowColor
                            )
                        } else if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = if (isBn) "ভুল উত্তর" else "Incorrect Answer",
                                tint = PriorityHighColor
                            )
                        }
                    }
                }
            }
        }

        // Explanation + Next Question Button
        item {
            AnimatedVisibility(
                visible = sessionState.isAnswerSubmitted,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = if (isBn) "ব্যাখ্যা (Explanation):" else "Explanation:",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isBn) question.explanationBn else question.explanationEn,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    Button(
                        onClick = onNextQuestion,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("next_question_button")
                    ) {
                        val isLast = sessionState.currentIndex + 1 >= sessionState.questions.size
                        Text(
                            text = if (isLast) {
                                if (isBn) "ফলাফল দেখুন" else "View Quiz Results"
                            } else {
                                if (isBn) "পরবর্তী প্রশ্ন" else "Next Question"
                            },
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuizResultSummaryView(
    sessionState: QuizSessionState,
    isBn: Boolean,
    onReplay: () -> Unit,
    onBackToLobby: () -> Unit,
    modifier: Modifier = Modifier
) {
    val total = sessionState.questions.size.coerceAtLeast(1)
    val accuracy = ((sessionState.score.toFloat() / total.toFloat()) * 100).toInt()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
            .testTag("quiz_result_container"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.size(84.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(46.dp)
                        )
                    }
                }

                Text(
                    text = if (isBn) "অভিনন্দন! কুইজ সম্পন্ন হয়েছে" else "Quiz Completed!",
                    style = MaterialTheme.typography.headlineMedium
                )

                Text(
                    text = "${sessionState.score} / $total ($accuracy%)",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = if (isBn) {
                        "সর্বোচ্চ ধারাবাহিক সঠিক উত্তর (Streak): ${sessionState.bestStreak}"
                    } else {
                        "Best Streak: ${sessionState.bestStreak} in a row"
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onBackToLobby,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("back_to_quiz_lobby_button")
                    ) {
                        Text(if (isBn) "সব বিভাগ" else "Categories")
                    }

                    Button(
                        onClick = onReplay,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("replay_quiz_button")
                    ) {
                        Icon(imageVector = Icons.Default.Replay, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isBn) "আবার খেলুন" else "Play Again")
                    }
                }
            }
        }
    }
}

@Composable
private fun QuizScoreHistoryRow(
    record: QuizScoreRecord,
    isBn: Boolean
) {
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = if (isBn) record.categoryNameBn else record.categoryNameEn,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = dateFormat.format(Date(record.playedAt)),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "${record.score}/${record.totalQuestions} (${record.accuracyPercent}%)",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}
