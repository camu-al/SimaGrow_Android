package com.camu.simagrow.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.camu.simagrow.database.AppDatabase
import com.camu.simagrow.databinding.FragmentSoporteBinding
import com.camu.simagrow.model.SoporteEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SoporteFragment : Fragment() {
    private var _binding: FragmentSoporteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSoporteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnEnviarMensajeSoporte.setOnClickListener {
            val asunto = binding.etAsuntoSoporte.text.toString()
            val mensaje = binding.etMensajeSoporte.text.toString()

            if (asunto.isEmpty() || mensaje.isEmpty()) {
                Toast.makeText(requireContext(), "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fechaActual = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            // Datos usuario
            val prefs = requireActivity().getSharedPreferences("usuario_prefs", AppCompatActivity.MODE_PRIVATE)
            val nombreUsuario = prefs.getString("nombre", "Desconocido") ?: "Desconocido"
            val niaUsuario = prefs.getString("nia", "----") ?: "----"

            enviarMensajeSoporteRoom(nombreUsuario, niaUsuario, asunto, mensaje, fechaActual)
        }
    }

    private fun enviarMensajeSoporteRoom(nombreUsuario: String, niaUsuario: String, asunto: String, mensaje: String, fechaActual: String) {
        val soporte = SoporteEntity(
            nombre = nombreUsuario,
            nia = niaUsuario,
            asunto = asunto,
            mensaje = mensaje,
            fecha = fechaActual
        )

        lifecycleScope.launch(Dispatchers.IO) {
            AppDatabase.getDatabase(requireContext()).soporteDao().insertarMensajeSoporte(soporte)
        }

        Toast.makeText(requireContext(), "Mensaje enviado a soporte", Toast.LENGTH_SHORT).show()

    }

}