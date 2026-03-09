package com.camu.simagrow.model.dto

data class IncidenciaDTO(
    val id: Int,
    val nombre: String,
    val tipo: String,
    val zona: String,
    val descripcion: String,
    val fecha: String,
    val alumnoNIA: Int,
    val estado: String
)
