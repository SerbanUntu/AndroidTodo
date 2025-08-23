package com.example.androidtodo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.example.androidtodo.ui.theme.AndroidTodoTheme
import androidx.compose.runtime.staticCompositionLocalOf
import com.example.androidtodo.ui.views.TodoView
import com.example.androidtodo.ui.layouts.MainLayout
import dev.convex.android.ConvexClient

val LocalConvexClient = staticCompositionLocalOf<ConvexClient> {
    error("No ConvexClient provided. Did you forget to wrap your composables in CompositionLocalProvider?")
}

class MainActivity : ComponentActivity() {
    lateinit var convex: ConvexClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        convex = ConvexClient(BuildConfig.CONVEX_URL)

        enableEdgeToEdge()
        setContent {
            AndroidTodoTheme(dynamicColor = false) {
                CompositionLocalProvider(LocalConvexClient provides convex) {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        MainLayout(innerPadding) {
                            TodoView()
                        }
                    }
                }
            }
        }
    }
}