package com.camu.simagrow.model.dto

data class UsuarioDTO(
    val nia: Int,
    val nombre: String,
    val password: String,
    val rol: String,
    val curso: String?,
    val materia: String?
)
