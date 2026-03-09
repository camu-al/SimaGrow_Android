package com.camu.simagrow.api

import com.camu.simagrow.model.IncidenciaEntity
import com.camu.simagrow.model.dto.IncidenciaDTO
import com.camu.simagrow.model.dto.UsuarioDTO
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface SimaGrowApi {

    // Registro
    @FormUrlEncoded
    @POST("usuaris")
    suspend fun registrarUsuario(
        @Field("nia") nia: Int,
        @Field("nombre") nombre: String,
        @Field("password") password: String,
        @Field("rol") rol: String,
        @Field("curso") curso: String,
        @Field("materia") materia: String
    ): Response<UsuarioDTO>

    // Lista usuaris
    @GET("usuaris")
    suspend fun getUsuarios(): List<UsuarioDTO>

    // Lista incidencias por nia
    @GET("incidencias/{nia}")
    suspend fun getIncidencias(@Path("nia") nia: Int): List<IncidenciaDTO>

    // Lista incidencias
    @GET("incidencias")
    suspend fun getTodasIncidencias(): List<IncidenciaDTO>

    // Guardar incidencias
    @POST("incidencias")
    suspend fun crearIncidencia(
        @Body nombre: RequestBody,
        @Query("tipo") tipo: String,
        @Query("zona") zona: String,
        @Query("descripcion") descripcion: String,
        @Query("fecha") fecha: String,
        @Query("alumnoNIA") alumnoNIA: Int,
        @Query("estado") estado: String
    ): Response<IncidenciaDTO>

    // Eliminar incidencia
    @DELETE("incidencias/eliminar/{id}")
    suspend fun eliminarIncidenciaServidor(@Path("id") id: Int)


}