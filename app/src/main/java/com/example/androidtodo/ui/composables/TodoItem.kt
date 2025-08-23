package com.example.androidtodo.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.androidtodo.LocalConvexClient
import com.example.androidtodo.domain.Todo
import kotlinx.coroutines.launch

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
