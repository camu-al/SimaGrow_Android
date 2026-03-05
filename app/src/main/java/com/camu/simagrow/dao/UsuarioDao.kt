package com.camu.simagrow.dao

import androidx.room.*
import com.camu.simagrow.model.UsuarioEntity

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertarUsuario(usuario: UsuarioEntity)

    @Query("SELECT * FROM usuarios WHERE nia = :nia AND password = :password")
    suspend fun login(nia: String, password: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE nia = :nia")
    suspend fun obtenerUsuarioPorNia(nia: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios")
    suspend fun getAllUsuarios(): UsuarioEntity?

}