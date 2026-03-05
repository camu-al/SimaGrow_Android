package com.camu.simagrow.dao

import androidx.room.*
import com.camu.simagrow.model.IncidenciaEntity

@Dao
interface IncidenciaDao {

    @Insert
    suspend fun insertarIncidencia(incidencia: IncidenciaEntity)

    @Query("SELECT * FROM incidencias WHERE alumnoNia = :nia")
    suspend fun obtenerIncidenciasAlumno(nia: String): List<IncidenciaEntity>

    @Query("SELECT * FROM incidencias")
    suspend fun obtenerTodas(): List<IncidenciaEntity>
    @Query("SELECT * FROM incidencias WHERE alumnoNia = :nia ORDER BY id DESC")
    suspend fun obtenerIncidenciasPorUsuario(nia: String): List<IncidenciaEntity>

    @Delete
    suspend fun eliminarIncidencia(incidencia: IncidenciaEntity)


}