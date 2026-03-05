package com.camu.simagrow.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.camu.simagrow.R
import com.camu.simagrow.activitis.MainActivity
import com.camu.simagrow.adapters.IncidenciaAdapter
import com.camu.simagrow.database.AppDatabase
import com.camu.simagrow.databinding.FragmentIncidenciasBinding
import com.camu.simagrow.model.IncidenciaEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.content.edit

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

        adapter = IncidenciaAdapter(emptyList()) { incidencia ->
            eliminarIncidencia(incidencia)
        }
        binding.recyclerIncidencias.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerIncidencias.adapter = adapter

        cargarIncidencias()
    }

    private fun cargarIncidencias() {
        val prefs = requireActivity().getSharedPreferences("usuario_prefs", AppCompatActivity.MODE_PRIVATE)
        val nia = prefs.getString("nia", null) ?: return

        lifecycleScope.launch(Dispatchers.IO) {
            val listaIncidencias = db.incidenciaDao().obtenerIncidenciasPorUsuario(nia)

            withContext(Dispatchers.Main) {
                adapter.actualizarLista(listaIncidencias)
                // Guardar contador
                val prefs = requireActivity().getSharedPreferences("contador_prefs", AppCompatActivity.MODE_PRIVATE)
                prefs.edit { putInt("total_incidencias", listaIncidencias.size) }
            }
        }
    }

    private fun eliminarIncidencia(incidencia: IncidenciaEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            db.incidenciaDao().eliminarIncidencia(incidencia)
            withContext(Dispatchers.Main) {
                cargarIncidencias()
            }
        }
    }

    private fun animarContador(tv: TextView) {
        tv.animate()
            .scaleX(1.2f)
            .scaleY(1.2f)
            .setDuration(120)
            .withEndAction {
                tv.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(120)
                    .start()
            }
            .start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
