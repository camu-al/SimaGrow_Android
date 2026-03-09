package com.camu.simagrow.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.camu.simagrow.adapters.SoporteAdapter
import com.camu.simagrow.database.AppDatabase
import com.camu.simagrow.databinding.FragmentGestionAlumnosBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GestionAlumnosFragment : Fragment() {

    private lateinit var binding: FragmentGestionAlumnosBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGestionAlumnosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Lista de mensaje soporte
        binding.recyclerSoporte.layoutManager = LinearLayoutManager(requireContext())
        lifecycleScope.launch(Dispatchers.IO) {
            val lista = AppDatabase.getDatabase(requireContext()).soporteDao().obtenerMensajesSoporte()
            withContext(Dispatchers.Main) {
                binding.recyclerSoporte.adapter = SoporteAdapter(lista)
            }
        }
    }
}
