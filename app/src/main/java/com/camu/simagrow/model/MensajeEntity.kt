package com.camu.simagrow.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mensajes")
data class MensajeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val asunto: String,
    val mensaje: String,
    val profesor: String,
    val fecha: Long,
    val alumnoNia: String,
    val alumnoNombre: String
)

