package com.camu.simagrow.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.camu.simagrow.model.IncidenciaEntity

@Dao
interface IncidenciaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarIncidencia(incidencia: IncidenciaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarIncidencias(incidencias: List<IncidenciaEntity>)

    @Query("SELECT * FROM incidencias WHERE alumnoNia = :nia")
    suspend fun obtenerIncidenciasAlumno(nia: String): List<IncidenciaEntity>

    @Query("SELECT * FROM incidencias")
    suspend fun obtenerTodas(): List<IncidenciaEntity>

    @Query("SELECT * FROM incidencias WHERE alumnoNia = :nia ORDER BY id DESC")
    fun observarIncidencias(nia: String): LiveData<List<IncidenciaEntity>>

    @Delete
    suspend fun eliminarIncidencia(incidencia: IncidenciaEntity)

    @Query("DELETE FROM incidencias")
    suspend fun borrarTodas()



}