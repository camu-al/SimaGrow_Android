package com.camu.simagrow.fragments

import android.R
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
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
import com.camu.simagrow.api.RetroFitInstance
import com.camu.simagrow.model.dto.IncidenciaDTO

class IncidenciasFragment : Fragment() {
    private var _binding: FragmentIncidenciasBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: AppDatabase
    private lateinit var adapter: IncidenciaAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentIncidenciasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = AppDatabase.getDatabase(requireContext())

        // Spiner tipos
        val tipos = listOf("Todas", "Electricidad", "Fontaneria", "Limpieza", "Climatizacion", "Mobiliario", "Tecnologias", "Seguridad", "Otro")
        val adapterTipo = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, tipos)
        adapterTipo.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spinnerFiltro.adapter = adapterTipo

        // Spinner zonas
        val zonas = listOf("Edificio", "Planta", "Aula", "Laboratorio", "Baño", "Pasillo", "Patio", "Consejeria", "Otro")
        val adapterZona = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, zonas)
        adapterZona.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spinnerZona.adapter = adapterZona

        // Filtros
        binding.spinnerFiltro.onItemSelectedListener = filtroListener
        binding.spinnerZona.onItemSelectedListener = filtroListener

        // Recyclerview incidencias
        adapter = IncidenciaAdapter(emptyList()) { incidencia -> eliminarIncidencia(incidencia) }
        binding.recyclerIncidencias.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerIncidencias.adapter = adapter

        cargarIncidencias()
    }

    // Listener compartido
    private val filtroListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            aplicarFiltro()
        }
        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    // Filtrar por zona i tipo
    private fun aplicarFiltro() {
        val filtroTipo = binding.spinnerFiltro.selectedItem.toString()
        val filtroZona = binding.spinnerZona.selectedItem.toString()

        val listaFiltrada = adapter.listaOriginal.filter { inc ->
            val coincideTipo = filtroTipo == "Todas" || inc.tipo.equals(filtroTipo, ignoreCase = true)
            val coincideZona = filtroZona == "Todas" || inc.zona.equals(filtroZona, ignoreCase = true)
            coincideTipo && coincideZona
        }

        adapter.actualizarLista(listaFiltrada)
    }

    private fun limpiarComillas(texto: String): String {
        return if (texto.startsWith("\"") && texto.endsWith("\"")) texto.substring(1, texto.length - 1)
        else texto
    }

    // Cargar incidencias
    private fun cargarIncidencias() {
        val prefs = requireActivity().getSharedPreferences("usuario_prefs", AppCompatActivity.MODE_PRIVATE)
        val nia = prefs.getString("nia", null)
        val rol = prefs.getString("rol", "alumno")?.trim()?.lowercase()

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val api = RetroFitInstance.api

                // Cargar incidencias segun el rol
                val incidenciasDTO = if (rol == "alumno") {
                    api.getIncidencias(nia!!.toInt())
                } else {
                    api.getTodasIncidencias()
                }

                val incidenciasEntity = incidenciasDTO.map { dto ->
                    dto.toEntity().copy(titulo = limpiarComillas(dto.nombre))
                }

                // Añadir incidencias a room
                db.incidenciaDao().borrarTodas()
                db.incidenciaDao().insertarIncidencias(incidenciasEntity)

                withContext(Dispatchers.Main) {
                    adapter.listaOriginal = incidenciasEntity
                    adapter.actualizarLista(incidenciasEntity)
                }

            } catch (e: Exception) {
                val listaRoom = if (rol == "alumno") {
                    db.incidenciaDao().obtenerIncidenciasAlumno(nia!!)
                } else {
                    db.incidenciaDao().obtenerTodas()
                }

                withContext(Dispatchers.Main) {
                    adapter.listaOriginal = listaRoom
                    adapter.actualizarLista(listaRoom)
                }
            }
        }
    }

    // Modelmapper
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

    // Eliminar incidencias
    private fun eliminarIncidencia(incidencia: IncidenciaEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                RetroFitInstance.api.eliminarIncidenciaServidor(incidencia.id)
            } catch (_: Exception) {}

            db.incidenciaDao().eliminarIncidencia(incidencia)

            val prefs = requireActivity().getSharedPreferences("usuario_prefs", AppCompatActivity.MODE_PRIVATE)
            val nia = prefs.getString("nia", null)
            val rol = prefs.getString("rol", "alumno")?.trim()?.lowercase()

            // Room segun rol
            val listaRoom = if (rol == "profesor") {
                // Profesor
                db.incidenciaDao().obtenerTodas()
            } else {
                // Alumno
                db.incidenciaDao().obtenerIncidenciasAlumno(nia!!)
            }

            // Actualizar recycleview
            withContext(Dispatchers.Main) {
                adapter.listaOriginal = listaRoom
                adapter.actualizarLista(listaRoom)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
