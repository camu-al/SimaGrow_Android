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
import com.camu.simagrow.databinding.FragmentFormularioMensajeBinding
import com.camu.simagrow.model.MensajeEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FormularioMensajeFragment : Fragment() {

    private var _binding: FragmentFormularioMensajeBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: AppDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFormularioMensajeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        db = AppDatabase.getDatabase(requireContext())

        binding.btnEnviarMensaje.setOnClickListener {
            val profesor = binding.etProfesor.text.toString().trim()
            val asunto = binding.etAsunto.text.toString().trim()
            val mensaje = binding.etMensaje.text.toString().trim()

            if (profesor.isEmpty() || asunto.isEmpty() || mensaje.isEmpty()) {
                Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Datos usuario
            val prefs = requireActivity().getSharedPreferences("usuario_prefs", AppCompatActivity.MODE_PRIVATE)
            val nia = prefs.getString("nia", null)

            if (nia == null) {
                Toast.makeText(requireContext(), "Error de sesión", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Enviar mensaje
            enviarMensajeProfe(asunto,mensaje,profesor,nia)
        }
    }
    private fun enviarMensajeProfe(asunto: String, mensaje: String, profesor: String, nia: String) {
        val mensajeEntity = MensajeEntity(
            asunto = asunto,
            mensaje = mensaje,
            profesor = profesor,
            fecha = System.currentTimeMillis(),
            alumnoNia = nia
        )

        lifecycleScope.launch(Dispatchers.IO) {
            db.mensajeDao().insertarMensaje(mensajeEntity)

            // Limpiar formulario
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Mensaje enviado", Toast.LENGTH_SHORT).show()
                binding.etProfesor.setText("")
                binding.etAsunto.setText("")
                binding.etMensaje.setText("")
            }
        }
    }
}
