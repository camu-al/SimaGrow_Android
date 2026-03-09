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
        val nia = prefs.getString("nia", null) ?: return

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val api = RetroFitInstance.api

                // Intentar cargar desde API
                val incidenciasDTO = api.getIncidencias(nia.toInt())

                // Convertir DTO a Entity
                val incidenciasEntity = incidenciasDTO.map { dto ->
                    dto.toEntity().copy(
                        titulo = limpiarComillas(dto.nombre)
                    )
                }

                // Remplazar Room
                db.incidenciaDao().borrarTodas()
                db.incidenciaDao().insertarIncidencias(incidenciasEntity)

                Log.i("INCIDENCIAS_API", "Cargado desde API: ${incidenciasEntity.size}")

                // Mostrar API
                withContext(Dispatchers.Main) {
                    adapter.actualizarLista(incidenciasEntity)
                }

                return@launch

            } catch (e: Exception) {
                Log.e("INCIDENCIAS_API", "Servidor NO disponible, usando Room. Error: ${e.message}")
            }

            // Si API falla → cargar Room
            val listaRoom = db.incidenciaDao().obtenerIncidenciasAlumno(nia)

            withContext(Dispatchers.Main) {
                adapter.actualizarLista(listaRoom)
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
            } catch (_: Exception) {

            }

            db.incidenciaDao().eliminarIncidencia(incidencia)

            val prefs = requireActivity().getSharedPreferences("usuario_prefs", AppCompatActivity.MODE_PRIVATE)
            val nia = prefs.getString("nia", null) ?: return@launch

            val listaRoom = db.incidenciaDao().obtenerIncidenciasAlumno(nia)

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
