package com.example.notesapp

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutinesScreen() {
    val context = LocalContext.current
    val db = remember { RoutineDatabase(context) }
    val scope = rememberCoroutineScope()

    // State for bottom sheet
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    // State for dialog
    var showDialog by remember { mutableStateOf(false) }
    var selectedTask by remember { mutableStateOf<RoutineTask?>(null) }

    // State for editing mode
    var isEditMode by remember { mutableStateOf(false) }

    // State for tasks
    var tasks by remember { mutableStateOf(emptyList<RoutineTask>()) }

    // Load tasks when the screen is first loaded
    LaunchedEffect(Unit) {
        tasks = db.getAllTasks()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Display routines as cards
        if (tasks.isEmpty()) {
            Text(
                text = "No routines yet. Add one!",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tasks) { task ->
                    TaskCard(
                        task = task,
                        onClick = {
                            selectedTask = task
                            showDialog = true
                        }
                    )
                }
            }
        }

        // FAB to show bottom sheet
        FloatingActionButton(
            onClick = {
                isEditMode = false
                selectedTask = null
                showBottomSheet = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = highlightYellow,
            contentColor = Color.Black
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Routine"
            )
        }
    }

    // Bottom sheet
    if (showBottomSheet) {
        TaskInputBottomSheet(
            task = if (isEditMode) selectedTask else null,
            onDismiss = { showBottomSheet = false },
            onTaskSaved = { task ->
                scope.launch {
                    if (isEditMode && selectedTask != null) {
                        db.updateTask(task)
                    } else {
                        db.addTask(task)
                    }
                    tasks = db.getAllTasks()
                    showBottomSheet = false
                }
            },
            sheetState = sheetState
        )
    }

    // Dialog for edit/delete options
    if (showDialog) {
        TaskOptionsDialog(
            onDismiss = { showDialog = false },
            onEdit = {
                isEditMode = true
                showDialog = false
                showBottomSheet = true
            },
            onDelete = {
                scope.launch {
                    selectedTask?.let { task ->
                        db.deleteTask(task.id)
                        tasks = db.getAllTasks()
                    }
                    showDialog = false
                }
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskCard(task: RoutineTask, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = task.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Time: ${task.time.format(DateTimeFormatter.ofPattern("hh:mm a"))}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Recurrence: ${task.recurrence}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskInputBottomSheet(
    task: RoutineTask?,
    onDismiss: () -> Unit,
    onTaskSaved: (RoutineTask) -> Unit,
    sheetState: SheetState
) {
    // State for task input
    var taskName by remember { mutableStateOf(task?.name ?: "") }
    var selectedTime by remember { mutableStateOf(task?.time ?: LocalTime.now()) }
    var selectedRecurrence by remember { mutableStateOf(task?.recurrence ?: "Daily") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (task != null) "Edit Task" else "Add New Task",
                style = MaterialTheme.typography.headlineSmall
            )

            // Task Name Field
            OutlinedTextField(
                value = taskName,
                onValueChange = { taskName = it },
                label = { Text("Task Name") },
                modifier = Modifier.fillMaxWidth()
            )

            // Time Picker
            TimePickerSection(
                selectedTime = selectedTime,
                onTimeSelected = { selectedTime = it }
            )

            // Recurrence Selection
            RecurrenceSelector(
                selectedRecurrence = selectedRecurrence,
                onRecurrenceSelected = { selectedRecurrence = it }
            )

            // Add Button
            Button(
                onClick = {
                    val newTask = RoutineTask(
                        id = task?.id ?: 0, // if editing, use existing ID
                        name = taskName,
                        time = selectedTime,
                        recurrence = selectedRecurrence
                    )
                    onTaskSaved(newTask)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                enabled = taskName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = highlightYellow, contentColor = Color.Black)
            ) {
                Text(text = if (task != null) "Update" else "Add")
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerSection(
    selectedTime: LocalTime,
    onTimeSelected: (LocalTime) -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }

    Column {
        Text(
            text = "Time: ${selectedTime.format(DateTimeFormatter.ofPattern("hh:mm a"))}",
            style = MaterialTheme.typography.bodyLarge
        )

        Button(
            onClick = { showTimePicker = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Text("Select Time")
        }
    }

    if (showTimePicker) {
        TimePickerDialog(
            onDismiss = { showTimePicker = false },
            onTimeSelected = {
                onTimeSelected(it)
                showTimePicker = false
            },
            initialTime = selectedTime
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecurrenceSelector(
    selectedRecurrence: String,
    onRecurrenceSelected: (String) -> Unit
) {
    Column {
        Text(
            text = "Recurrence:",
            style = MaterialTheme.typography.bodyLarge
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Daily", "Weekly", "Monthly", "Yearly", "Weekdays", "Weekend").forEach { option ->
                RecurrenceOption(
                    title = option,
                    isSelected = selectedRecurrence == option,
                    onClick = { onRecurrenceSelected(option) }
                )
            }
        }
    }
}

@Composable
fun RecurrenceOption(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) highlightYellow else MaterialTheme.colorScheme.surface,
            contentColor = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
        ),
        border = ButtonDefaults.outlinedButtonBorder
    ) {
        Text(title)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onTimeSelected: (LocalTime) -> Unit,
    initialTime: LocalTime
) {
    var hour by remember { mutableStateOf(initialTime.hour) }
    var minute by remember { mutableStateOf(initialTime.minute) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Select Time",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Simple time picker with hour and minute pickers
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hour picker
                    NumberPicker(
                        value = hour,
                        onValueChange = { hour = it },
                        range = 0..23
                    )

                    Text(
                        text = ":",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    // Minute picker
                    NumberPicker(
                        value = minute,
                        onValueChange = { minute = it },
                        range = 0..59
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }

                    TextButton(
                        onClick = {
                            onTimeSelected(LocalTime.of(hour, minute))
                        }
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }
}

@Composable
fun NumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = {
                val newValue = if (value == range.first) range.last else value - 1
                onValueChange(newValue)
            }
        ) {
            Text("▲")
        }

        Text(
            text = String.format("%02d", value),
            style = MaterialTheme.typography.headlineMedium
        )

        IconButton(
            onClick = {
                val newValue = if (value == range.last) range.first else value + 1
                onValueChange(newValue)
            }
        ) {
            Text("▼")
        }
    }
}

@Composable
fun TaskOptionsDialog(
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Task Options") },
        text = { Text("What would you like to do with this task?") },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = onEdit,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit")
                    }
                }

                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Delete")
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}