package com.camu.simagrow.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UsuarioEntity(

    @PrimaryKey
    val nia: String,
    val nombre: String,
    val password: String,
    val rol: String, // alumno o profesor
    val curso: String?,
    val materia: String?
)