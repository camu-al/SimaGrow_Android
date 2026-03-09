package com.camu.simagrow.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.camu.simagrow.adapters.IncidenciaAdapter
import com.camu.simagrow.database.AppDatabase
import com.camu.simagrow.databinding.FragmentIncidenciasBinding
import com.camu.simagrow.model.IncidenciaEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.content.edit
import com.camu.simagrow.api.RetroFitInstance
import com.camu.simagrow.model.dto.IncidenciaDTO

class IncidenciasFragment : Fragment() {
    private var _binding: FragmentIncidenciasBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: AppDatabase
    private lateinit var adapter: IncidenciaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIncidenciasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = AppDatabase.getDatabase(requireContext())

        adapter = IncidenciaAdapter(emptyList()) { incidencia -> eliminarIncidencia(incidencia) }
        binding.recyclerIncidencias.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerIncidencias.adapter = adapter

        cargarIncidencias()
    }

    // ---------------------------
    // QUITAR COMILLAS DEL SERVIDOR
    // ---------------------------
    private fun limpiarComillas(texto: String): String {
        return if (texto.length >= 2 && texto.startsWith("\"") && texto.endsWith("\"")) {
            texto.substring(1, texto.length - 1)
        } else texto
    }
    private fun cargarIncidencias() {
        val prefs = requireActivity().getSharedPreferences("usuario_prefs", AppCompatActivity.MODE_PRIVATE)
        val nia = prefs.getString("nia", null)
        val rol = prefs.getString("rol", "alumno")?.trim()?.lowercase()
        Log.d("INCIDENCIAS", "ROL LEÍDO DE PREFS: '$rol'")
        Log.d("INCIDENCIAS", "NIA LEÍDO DE PREFS: '$nia'")
        Log.d("INCIDENCIAS", "¿ES PROFESOR? → ${rol == "profesor"}")

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val api = RetroFitInstance.api

                val incidenciasDTO = if (rol == "alumno") {
                    // Alumno
                    api.getIncidencias(nia!!.toInt())
                } else {
                    // Profesor
                    api.getTodasIncidencias()
                }

                // Modelmapper
                val incidenciasEntity = incidenciasDTO.map { dto ->
                    dto.toEntity().copy(
                        titulo = limpiarComillas(dto.nombre)
                    )
                }

                // Guardar en Room
                db.incidenciaDao().borrarTodas()
                db.incidenciaDao().insertarIncidencias(incidenciasEntity)

                withContext(Dispatchers.Main) {
                    adapter.actualizarLista(incidenciasEntity)
                }

            } catch (e: Exception) {
                Log.e("INCIDENCIAS_API", "Error API: ${e.message}")

                // Cargar room por defecto
                val listaRoom = if (rol == "alumno") {
                    db.incidenciaDao().obtenerIncidenciasAlumno(nia!!)
                } else {
                    db.incidenciaDao().obtenerTodas()
                }

                withContext(Dispatchers.Main) {
                    adapter.actualizarLista(listaRoom)
                }
            }
        }
    }


    // ModelMapper DTO a Entity
    fun IncidenciaDTO.toEntity(): IncidenciaEntity {
        return IncidenciaEntity(
            id = this.id,
            titulo = this.nombre,
            tipo = this.tipo,
            zona = this.zona,
            descripcion = this.descripcion,
            fecha = this.fecha,
            estado = this.estado,
            alumnoNia = this.alumnoNIA.toString()
        )
    }

    private fun eliminarIncidencia(incidencia: IncidenciaEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                RetroFitInstance.api.eliminarIncidenciaServidor(incidencia.id)
            } catch (_: Exception) {}

            db.incidenciaDao().eliminarIncidencia(incidencia)

            val prefs = requireActivity().getSharedPreferences("usuario_prefs", AppCompatActivity.MODE_PRIVATE)
            val nia = prefs.getString("nia", null)
            val rol = prefs.getString("rol", "alumno")?.trim()?.lowercase()

            val listaRoom = if (rol == "profesor") {
                db.incidenciaDao().obtenerTodas()
            } else {
                db.incidenciaDao().obtenerIncidenciasAlumno(nia!!)
            }

            withContext(Dispatchers.Main) {
                adapter.actualizarLista(listaRoom)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
