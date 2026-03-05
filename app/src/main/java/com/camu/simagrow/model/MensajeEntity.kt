package com.camu.simagrow.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MensajeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val asunto: String,
    val mensaje: String,
    val profesor: String,
    val fecha: Long,
    val alumnoNia: String
)
