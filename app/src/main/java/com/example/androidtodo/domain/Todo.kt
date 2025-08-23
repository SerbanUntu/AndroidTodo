package com.example.androidtodo.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Todo(
    @SerialName("_id") val id: String,
    @SerialName("_creationTime") val creationTime: Double,
    val text: String,
    var done: Boolean
)
