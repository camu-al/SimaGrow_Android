package com.camu.simagrow.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "incidencias",
    indices = [Index(value = ["alumnoNia"])]
)
data class IncidenciaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val tipo: String,
    val zona: String,
    val descripcion: String,
    val fecha: String,
    val estado: String = "Pendiente",
    val alumnoNia: String
)




