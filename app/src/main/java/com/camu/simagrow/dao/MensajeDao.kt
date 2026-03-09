package com.camu.simagrow.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.camu.simagrow.model.MensajeEntity

@Dao
interface MensajeDao {

    @Insert
    suspend fun insertarMensaje(m: MensajeEntity)

    @Query("SELECT * FROM mensajes WHERE alumnoNia = :nia ORDER BY fecha DESC")
    suspend fun obtenerMensajesPorAlumno(nia: String): List<MensajeEntity>

    @Query("SELECT * FROM mensajes ORDER BY fecha DESC")
    suspend fun obtenerTodosLosMensajes(): List<MensajeEntity>

    @Query("DELETE FROM mensajes WHERE id = :id AND alumnoNia = :nia")
    suspend fun borrarMensajePorIdYNia(id: Int, nia: String)

    @Query("DELETE FROM mensajes WHERE id = :id")
    suspend fun borrarMensajePorId(id: Int)
}
