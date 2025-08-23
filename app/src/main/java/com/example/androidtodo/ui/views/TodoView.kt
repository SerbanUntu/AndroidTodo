package com.example.androidtodo.ui.views

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidtodo.LocalConvexClient
import com.example.androidtodo.domain.Todo
import com.example.androidtodo.ui.composables.RemainderText
import com.example.androidtodo.ui.composables.TodoList
import com.example.androidtodo.ui.theme.Typography
import kotlinx.coroutines.launch

@Composable
fun TodoView() {
    val convex = LocalConvexClient.current
    val scope = rememberCoroutineScope()

    var todos: List<Todo> by remember { mutableStateOf(listOf()) }
    var todoText by remember { mutableStateOf("") }
    val total = todos.size
    val remaining = todos.count { !it.done }

    suspend fun handleCreate(text: String) {
        val newTodoId = convex.mutation<String>(
            "functions:createTodo",
            args = mapOf("text" to text)
        )
        Log.d("Todo App", "Todo created with id: $newTodoId")
    }

    LaunchedEffect("onLaunch") {
        convex.subscribe<List<Todo>>("functions:getAllTodos").collect { result ->
            result.onSuccess {
                Log.d("Todo App", "Successfully fetched the todos")
                todos = it
            }
            result.onFailure { error -> throw error }
        }
    }

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
                val currentText = todoText
                scope.launch {
                    handleCreate(currentText)
                }
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
