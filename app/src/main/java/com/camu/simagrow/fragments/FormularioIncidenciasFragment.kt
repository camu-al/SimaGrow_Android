package com.camu.simagrow.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.camu.simagrow.api.RetroFitInstance
import com.camu.simagrow.database.AppDatabase
import com.camu.simagrow.databinding.FragmentFormularioIncidenciasBinding
import com.camu.simagrow.model.IncidenciaEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody


class FormularioIncidenciasFragment : Fragment() {

    private var _binding: FragmentFormularioIncidenciasBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFormularioIncidenciasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = AppDatabase.getDatabase(requireContext())

        val zonas = listOf("Edificio", "Planta", "Aula", "Laboratorio", "Baño", "Pasillo", "Patio", "Consejeria", "Otro")
        binding.spZona.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, zonas)

        val tipos = listOf("Electricidad", "Fontaneria", "Limpieza", "Climatizacion", "Mobiliario", "Tecnologias", "Seguridad", "Otro")
        binding.spTipo.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, tipos)

        binding.btnGuardar.setOnClickListener {
            val titulo = binding.etTitulo.text.toString().trim()
            val tipo = binding.spTipo.selectedItem.toString()
            val zona = binding.spZona.selectedItem.toString()
            val descripcion = binding.etDescripcion.text.toString().trim()

            if (titulo.isEmpty() || descripcion.isEmpty()) {
                Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

            val prefs = requireActivity().getSharedPreferences("usuario_prefs", AppCompatActivity.MODE_PRIVATE)
            val niaUsuario = prefs.getString("nia", null)

            if (niaUsuario == null) {
                Toast.makeText(requireContext(), "Error de sesión, nia no encontrado", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            reportarIncidenciaRoom(
                titulo = titulo,
                tipo = tipo,
                zona = zona,
                descripcion = descripcion,
                fecha = fecha,
                nia = niaUsuario
            )
        }
    }

    private fun reportarIncidenciaRoom(titulo: String, tipo: String, zona: String, descripcion: String, fecha: String, nia: String) {

        val incidencia = IncidenciaEntity(
            titulo = titulo,
            tipo = tipo,
            zona = zona,
            descripcion = descripcion,
            fecha = fecha,
            alumnoNia = nia
        )

        lifecycleScope.launch(Dispatchers.IO) {

            db.incidenciaDao().insertarIncidencia(incidencia)

            try {
                val nombreBody = titulo.toRequestBody("text/plain".toMediaType())

                RetroFitInstance.api.crearIncidencia(
                    nombre = nombreBody,
                    tipo = tipo,
                    zona = zona,
                    descripcion = descripcion,
                    fecha = fecha,
                    alumnoNIA = nia.toInt(),
                    estado = "ABIERTA"
                )
            } catch (e: Exception) {
                Log.e("API", "Error enviando incidencia: ${e.message}")
            }

            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Incidencia guardada", Toast.LENGTH_SHORT).show()
                binding.etTitulo.text?.clear()
                binding.etDescripcion.text?.clear()
                binding.spTipo.setSelection(0)
                binding.spZona.setSelection(0)
            }
        }
    }
}
