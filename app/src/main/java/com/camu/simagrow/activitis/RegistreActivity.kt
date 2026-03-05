package com.camu.simagrow.activitis

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.camu.simagrow.database.AppDatabase
import com.camu.simagrow.databinding.ActivityRegistreBinding
import com.camu.simagrow.model.UsuarioEntity
import kotlinx.coroutines.launch
import android.util.Log

class RegistreActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistreBinding
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegistreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        val cursos = listOf("1SMX", "2SMX","1DAM", "2DAM","1DAW", "2DAW", "1ASIR", "2ASIR")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cursos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spCurso.adapter = adapter

        binding.btnCrearCuenta.setOnClickListener {
            val nombre = binding.etNombreRegistro.text.toString().trim()
            val nia = binding.etNiaRegistro.text.toString().trim()
            val password = binding.etPasswordRegistro.text.toString()
            val confirmar = binding.etConfirmarPasswordRegistro.text.toString()
            val curso = binding.spCurso.selectedItem.toString()

            // ---------------- VALIDACIONES ----------------
            // Campos vacíos
            if (nombre.isEmpty() || nia.isEmpty() || password.isEmpty() || confirmar.isEmpty()) {
                toast("Todos los campos son obligatorios")
                return@setOnClickListener
            }

            // Nombre mínimo
            if (nombre.length < 3) {
                toast("El nombre es demasiado corto")
                return@setOnClickListener
            }

            // NIA: 8 números
            if (!nia.matches(Regex("\\d{8}"))) {
                toast("El NIA debe tener 8 números")
                return@setOnClickListener
            }

            // Contraseña mínima
            if (password.length < 6) {
                toast("La contraseña debe tener al menos 6 caracteres")
                return@setOnClickListener
            }

            // Contraseñas iguales
            if (password != confirmar) {
                toast("Las contraseñas no coinciden")
                return@setOnClickListener
            }

            // ---------------- REGISTRAR USUARIO ----------------
            lifecycleScope.launch {
                val usuarioExistente = db.usuarioDao().obtenerUsuarioPorNia(nia)
                if (usuarioExistente != null) {
                    toast("Este NIA ya está registrado")
                    return@launch
                }

                val usuario = UsuarioEntity(
                    nia = nia,
                    nombre = nombre,
                    password = password,
                    rol = "alumno",
                    curso = curso,
                    materia = null
                )

                try {
                    db.usuarioDao().insertarUsuario(usuario)
                    toast("Usuario registrado correctamente")
                    finish()
                } catch (e: Exception) {
                    Log.e("REGISTRO", "Error al insertar usuario: ${e.message}")
                    toast("Error al registrar usuario")
                }
            }
        }

        binding.btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
