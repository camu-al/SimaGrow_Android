package com.camu.simagrow.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.camu.simagrow.model.MensajeEntity

@Dao
interface MensajeDao {

    @Insert
    suspend fun insertarMensaje(m: MensajeEntity)

    @Query("SELECT * FROM MensajeEntity WHERE alumnoNia = :nia ORDER BY fecha DESC")
    suspend fun obtenerMensajesPorAlumno(nia: String): List<MensajeEntity>

    @Query("DELETE FROM MensajeEntity WHERE id = :id AND alumnoNia = :nia")
    suspend fun borrarMensajePorIdYNia(id: Int, nia: String)



}
