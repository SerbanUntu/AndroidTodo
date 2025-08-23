package com.example.androidtodo.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.androidtodo.domain.Todo

@Composable
fun TodoList(todos: List<Todo>) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        todos.map { todo -> TodoItem(todo) }
    }
}
