package com.example.androidtodo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidtodo.ui.theme.AndroidTodoTheme
import com.example.androidtodo.ui.theme.Typography
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList

data class Todo(val text: String, var done: Boolean)

@Composable
fun RemainderText(remaining: Int, total: Int) {
    Text("$remaining of $total remaining", modifier = Modifier.padding(bottom = 32.dp))
}

@Composable
fun TodoItem(todos: SnapshotStateList<Todo>, index: Int) {
    val todo = todos[index]
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(checked = todo.done, onCheckedChange = { isChecked ->
                todos[index] = todo.copy(done = isChecked)
            })
            Text(
                todo.text,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth(0.65f)
            )
        }
        Button(
            { todos.removeAt(index) }, colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red,
                contentColor = Color.White
            )
        ) {
            Text("Delete")
        }
    }
}

@Composable
fun TodoList(todos: SnapshotStateList<Todo>) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        todos.mapIndexed { index, _ -> TodoItem(todos, index) }
    }
}

@Composable
fun App() {
    val todos = remember { mutableStateListOf<Todo>() }
    var todoText by remember { mutableStateOf("") }
    val total = todos.size
    val remaining = todos.count { !it.done }

    Text(text = "Tasks", style = Typography.titleLarge)
    RemainderText(remaining, total)
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 32.dp)
    ) {
        OutlinedTextField(
            value = todoText,
            { newValue -> todoText = newValue },
            placeholder = { Text("Add a new task...") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary
            )
        )
        Button(
            onClick = {
                todos.add(Todo(todoText, false))
                todoText = ""
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("+", style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold))
        }
    }
    TodoList(todos)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidTodoTheme(dynamicColor = false) {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .padding(top = 64.dp, start = 24.dp, end = 24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        App()
                    }
                }
            }
        }
    }
}