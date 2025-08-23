package com.example.androidtodo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.LayoutDirection
import dev.convex.android.ConvexClient
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.collections.listOf

@Serializable
data class Todo(
    @SerialName("_id") val id: String,
    @SerialName("_creationTime") val creationTime: Double,
    val text: String,
    var done: Boolean
)

val LocalConvexClient = staticCompositionLocalOf<ConvexClient> {
    error("No ConvexClient provided. Did you forget to wrap your composables in CompositionLocalProvider?")
}

@Composable
fun RemainderText(remaining: Int, total: Int) {
    Text("$remaining of $total remaining", modifier = Modifier.padding(bottom = 32.dp))
}

@Composable
fun TodoItem(todo: Todo) {
    val convex = LocalConvexClient.current
    val scope = rememberCoroutineScope()

    suspend fun handleDelete() {
        convex.mutation("functions:deleteTodo", args = mapOf("id" to todo.id))
    }

    suspend fun handleCheck(done: Boolean) {
        convex.mutation("functions:completeTodo", args = mapOf("id" to todo.id, "done" to done))
    }

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
                scope.launch {
                    handleCheck(isChecked)
                }
            })
            Text(
                todo.text,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth(0.65f)
            )
        }
        Button(
            {
                scope.launch {
                    handleDelete()
                }
            }, colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red,
                contentColor = Color.White
            )
        ) {
            Text("Delete")
        }
    }
}

@Composable
fun TodoList(todos: List<Todo>) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        todos.map { todo -> TodoItem(todo) }
    }
}

@Composable
fun App() {
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

class MainActivity : ComponentActivity() {
    lateinit var convex: ConvexClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        convex = ConvexClient(BuildConfig.CONVEX_URL)

        enableEdgeToEdge()
        setContent {
            AndroidTodoTheme(dynamicColor = false) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(
                                top = 64.dp + innerPadding.calculateTopPadding(),
                                start = 24.dp + innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                                end = 24.dp + innerPadding.calculateEndPadding(LayoutDirection.Ltr),
                                bottom = innerPadding.calculateBottomPadding()
                            )
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CompositionLocalProvider(LocalConvexClient provides convex) {
                            App()
                        }
                    }
                }
            }
        }
    }
}