package com.example.androidtodo.ui.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RemainderText(remaining: Int, total: Int) {
    Text("$remaining of $total remaining", modifier = Modifier.padding(bottom = 32.dp))
}
