package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.AppLanguage
import com.example.data.local.TaskCategory
import com.example.data.local.TaskPriority
import com.example.data.local.TodoTask
import com.example.ui.theme.PriorityHighColor
import com.example.ui.theme.PriorityLowColor
import com.example.ui.theme.PriorityMediumColor
import com.example.ui.viewmodel.TodoFilterStatus
import com.example.ui.viewmodel.TodoScreenState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TodoScreen(
    state: TodoScreenState,
    language: AppLanguage,
    showAddDialog: Boolean,
    onDismissAddDialog: () -> Unit,
    onOpenAddDialog: () -> Unit,
    onAddTask: (String, String, TaskCategory, TaskPriority, String) -> Unit,
    onToggleTask: (TodoTask) -> Unit,
    onDeleteTask: (TodoTask) -> Unit,
    onClearCompleted: () -> Unit,
    onFilterStatusChange: (TodoFilterStatus) -> Unit,
    onCategoryFilterChange: (TaskCategory?) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isBn = language == AppLanguage.BENGALI
    val progress = if (state.totalCount > 0) {
        state.completedCount.toFloat() / state.totalCount.toFloat()
    } else 0f

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("todo_list_container"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Productivity Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("todo_hero_card"),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(185.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_todo),
                        contentDescription = stringResource(id = R.string.hero_todo_desc),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xE6042F2E),
                                        Color(0xBF0F172A),
                                        Color(0x800F172A)
                                    )
                                )
                            )
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                color = Color(0x332DD4BF),
                                shape = CircleShape
                            ) {
                                Text(
                                    text = if (isBn) "✨ আজকের কর্মপরিকল্পনা" else "✨ Daily Task Planner",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color(0xFF5EEAD4),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                            Text(
                                text = if (isBn) "আপনার কাজের তালিকা" else "Smart Todo List",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White
                            )
                            Text(
                                text = if (isBn) {
                                    "মোট ${state.totalCount}টি কাজের মধ্যে ${state.completedCount}টি সম্পন্ন হয়েছে"
                                } else {
                                    "${state.completedCount} of ${state.totalCount} tasks completed today"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFE2E8F0)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Circular Progress Ring
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(78.dp)
                                .background(Color(0x33FFFFFF), CircleShape)
                                .padding(6.dp)
                        ) {
                            CircularProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.fillMaxSize(),
                                color = Color(0xFF2DD4BF),
                                trackColor = Color(0x33FFFFFF),
                                strokeWidth = 7.dp
                            )
                            Text(
                                text = "${(progress * 100).toInt()}%",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Search Field
        item {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("todo_search_input"),
                shape = MaterialTheme.shapes.medium,
                singleLine = true,
                placeholder = {
                    Text(
                        if (isBn) "কাজ বা নোট খুঁজুন..." else "Search tasks or notes..."
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    if (state.searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { onSearchQueryChange("") },
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = if (isBn) "মুছুন" else "Clear search"
                            )
                        }
                    }
                }
            )
        }

        // Status Filter Row + Clear Completed
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = state.filterStatus == TodoFilterStatus.ALL,
                        onClick = { onFilterStatusChange(TodoFilterStatus.ALL) },
                        label = { Text(if (isBn) "সব (${state.totalCount})" else "All (${state.totalCount})") },
                        modifier = Modifier.testTag("filter_all_chip")
                    )
                    FilterChip(
                        selected = state.filterStatus == TodoFilterStatus.ACTIVE,
                        onClick = { onFilterStatusChange(TodoFilterStatus.ACTIVE) },
                        label = {
                            val activeCount = state.totalCount - state.completedCount
                            Text(if (isBn) "বাকি আছে ($activeCount)" else "Active ($activeCount)")
                        },
                        modifier = Modifier.testTag("filter_active_chip")
                    )
                    FilterChip(
                        selected = state.filterStatus == TodoFilterStatus.COMPLETED,
                        onClick = { onFilterStatusChange(TodoFilterStatus.COMPLETED) },
                        label = {
                            Text(if (isBn) "সম্পন্ন (${state.completedCount})" else "Completed (${state.completedCount})")
                        },
                        modifier = Modifier.testTag("filter_completed_chip")
                    )
                    if (state.completedCount > 0) {
                        AssistChip(
                            onClick = onClearCompleted,
                            label = {
                                Text(if (isBn) "সম্পন্নগুলো মুছুন" else "Clear Done")
                            },
                            modifier = Modifier.testTag("clear_completed_chip")
                        )
                    }
                }

                // Category Filter Pills
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    FilterChip(
                        selected = state.selectedCategory == null,
                        onClick = { onCategoryFilterChange(null) },
                        label = { Text(if (isBn) "সকল বিভাগ" else "All Categories") }
                    )
                    TaskCategory.entries.forEach { category ->
                        FilterChip(
                            selected = state.selectedCategory == category,
                            onClick = {
                                onCategoryFilterChange(
                                    if (state.selectedCategory == category) null else category
                                )
                            },
                            label = { Text(if (isBn) category.labelBn else category.labelEn) }
                        )
                    }
                }
            }
        }

        // Empty State or Task Items
        if (state.tasks.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .testTag("todo_empty_state_card"),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                    ),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TaskAlt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = if (isBn) "কোনো কাজ পাওয়া যায়নি!" else "No tasks match your filter!",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = if (isBn) {
                                "নতুন একটি কাজ যুক্ত করতে নিচের '+' বাটনে ট্যাপ করুন।"
                            } else {
                                "Tap the button below to add a new task to your daily plan."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick = onOpenAddDialog,
                            modifier = Modifier.testTag("empty_state_add_task_button")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isBn) "নতুন কাজ যোগ করুন" else "Add New Task")
                        }
                    }
                }
            }
        } else {
            items(state.tasks, key = { it.id }) { task ->
                TodoTaskCard(
                    task = task,
                    isBn = isBn,
                    onToggle = { onToggleTask(task) },
                    onDelete = { onDeleteTask(task) }
                )
            }
        }
    }

    if (showAddDialog) {
        AddTodoTaskDialog(
            isBn = isBn,
            onDismiss = onDismissAddDialog,
            onConfirm = { title, notes, category, priority, due ->
                onAddTask(title, notes, category, priority, due)
                onDismissAddDialog()
            }
        )
    }
}

@Composable
private fun TodoTaskCard(
    task: TodoTask,
    isBn: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val categoryEnum = runCatching { TaskCategory.valueOf(task.category) }
        .getOrDefault(TaskCategory.PERSONAL)
    val priorityEnum = runCatching { TaskPriority.valueOf(task.priority) }
        .getOrDefault(TaskPriority.MEDIUM)

    val priorityColor = when (priorityEnum) {
        TaskPriority.HIGH -> PriorityHighColor
        TaskPriority.MEDIUM -> PriorityMediumColor
        TaskPriority.LOW -> PriorityLowColor
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onToggle)
            .testTag("task_item_card_${task.id}"),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (task.isCompleted) 0.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.Top
        ) {
            IconButton(
                onClick = onToggle,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("task_toggle_${task.id}")
            ) {
                Icon(
                    imageVector = if (task.isCompleted) {
                        Icons.Default.CheckCircle
                    } else {
                        Icons.Default.RadioButtonUnchecked
                    },
                    contentDescription = if (isBn) "কাজের অবস্থা পরিবর্তন" else "Toggle task completion",
                    tint = if (task.isCompleted) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outline
                    },
                    modifier = Modifier.size(26.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    color = if (task.isCompleted) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )

                AnimatedVisibility(
                    visible = task.notes.isNotBlank(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        text = task.notes,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Priority Badge
                    Surface(
                        color = priorityColor.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (isBn) priorityEnum.labelBn else priorityEnum.labelEn,
                            style = MaterialTheme.typography.labelMedium,
                            color = priorityColor,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    // Category Badge
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (isBn) categoryEnum.labelBn else categoryEnum.labelEn,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    // Due Label
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = task.dueDateLabel,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("task_delete_${task.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = if (isBn) "কাজ মুছুন" else "Delete task",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AddTodoTaskDialog(
    isBn: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String, String, TaskCategory, TaskPriority, String) -> Unit
) {
    var title by rememberSaveable { mutableStateOf("") }
    var notes by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf(TaskCategory.PERSONAL) }
    var selectedPriority by rememberSaveable { mutableStateOf(TaskPriority.MEDIUM) }
    var dueLabel by rememberSaveable { mutableStateOf("Today • 6:00 PM") }

    val duePresets = listOf(
        "Today • 9:00 AM",
        "Today • 6:00 PM",
        "Tomorrow",
        "This Weekend"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("add_task_dialog"),
        title = {
            Text(
                text = if (isBn) "নতুন কাজ যোগ করুন" else "Create New Task",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(if (isBn) "কাজের শিরোনাম *" else "Task Title *") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_task_title_input")
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(if (isBn) "সংক্ষিপ্ত নোট বা বিবরণ" else "Notes / Details (optional)") },
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_task_notes_input")
                )

                Text(
                    text = if (isBn) "বিভাগ (Category)" else "Category",
                    style = MaterialTheme.typography.labelLarge
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TaskCategory.entries.forEach { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(if (isBn) category.labelBn else category.labelEn) }
                        )
                    }
                }

                Text(
                    text = if (isBn) "গুরুত্ব (Priority)" else "Priority",
                    style = MaterialTheme.typography.labelLarge
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TaskPriority.entries.forEach { priority ->
                        FilterChip(
                            selected = selectedPriority == priority,
                            onClick = { selectedPriority = priority },
                            label = { Text(if (isBn) priority.labelBn else priority.labelEn) }
                        )
                    }
                }

                Text(
                    text = if (isBn) "সময়সীমা (Due Time)" else "Schedule / Due Time",
                    style = MaterialTheme.typography.labelLarge
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    duePresets.forEach { preset ->
                        FilterChip(
                            selected = dueLabel == preset,
                            onClick = { dueLabel = preset },
                            label = { Text(preset) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title, notes, selectedCategory, selectedPriority, dueLabel)
                    }
                },
                enabled = title.isNotBlank(),
                modifier = Modifier.testTag("confirm_add_task_button")
            ) {
                Text(if (isBn) "সংরক্ষণ করুন" else "Save Task")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_add_task_button")
            ) {
                Text(if (isBn) "বাতিল" else "Cancel")
            }
        }
    )
}
