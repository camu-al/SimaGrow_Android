package com.camu.simagrow.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.camu.simagrow.adapters.MensajeAdapter
import com.camu.simagrow.database.AppDatabase
import com.camu.simagrow.databinding.FragmentMensajePorfeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MensajePorfeFragment : Fragment() {

    private var _binding: FragmentMensajePorfeBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: AppDatabase
    private lateinit var adapter: MensajeAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMensajePorfeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        db = AppDatabase.getDatabase(requireContext())

        adapter = MensajeAdapter(emptyList()) { mensaje ->
            // Datos usuario
            val prefs = requireActivity().getSharedPreferences("usuario_prefs", AppCompatActivity.MODE_PRIVATE)
            val nia = prefs.getString("nia", null) ?: return@MensajeAdapter

            lifecycleScope.launch(Dispatchers.IO) {
                db.mensajeDao().borrarMensajePorIdYNia(mensaje.id, nia)
                cargarMensajes()
            }
        }

        binding.rvMensajesBuzon.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMensajesBuzon.adapter = adapter

        cargarMensajes()
    }

    private fun cargarMensajes() {
        val prefs = requireActivity().getSharedPreferences("usuario_prefs", AppCompatActivity.MODE_PRIVATE)
        val nia = prefs.getString("nia", null) ?: return

        lifecycleScope.launch(Dispatchers.IO) {
            val mensajes = db.mensajeDao().obtenerMensajesPorAlumno(nia)
            withContext(Dispatchers.Main) {
                adapter.actualizarLista(mensajes)
            }
        }
    }
}
