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
import com.camu.simagrow.api.RetroFitInstance
import com.camu.simagrow.model.dto.UsuarioDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RegistreActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistreBinding
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegistreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        // Spinner de los cursos
        val cursos = listOf("1 SMX", "2 SMX","1 DAM", "2 DAM","1 DAW", "2 DAW", "1 ASIR", "2 ASIR")
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
            // Campos vacios
            if (nombre.isEmpty() || nia.isEmpty() || password.isEmpty() || confirmar.isEmpty()) {
                toast("Todos los campos son obligatorios")
                return@setOnClickListener
            }

            // Nombre minim de 3 lletres
            if (nombre.length < 3) {
                toast("El nombre es demasiado corto")
                return@setOnClickListener
            }

            // Nia con 8 numeros
            if (!nia.matches(Regex("\\d{8}"))) {
                toast("El NIA debe tener 8 números")
                return@setOnClickListener
            }

            // Contraseña minima
            if (password.length < 6) {
                toast("La contraseña debe tener al menos 6 caracteres")
                return@setOnClickListener
            }

            // Contraseñas igual a confirmarContraseña
            if (password != confirmar) {
                toast("Las contraseñas no coinciden")
                return@setOnClickListener
            }

            // ---------------- Registrar usuario ----------------
            registrarUsuario(nombre,nia,password, curso)
        }

        binding.btnVolver.setOnClickListener {
            finish()
        }
    }

    // Registrar
    private fun registrarUsuario(nombre: String, nia: String, password: String, curso: String) {
        lifecycleScope.launch(Dispatchers.IO) {

            val usuario = UsuarioEntity(
                nia = nia,
                nombre = nombre,
                password = password,
                rol = "alumno",
                curso = curso,
                materia = null
            )

            var registradoEnServidor = false

            try {
                // Intentar registrar en servidor un alumno
                val response = RetroFitInstance.api.registrarUsuario(
                    nia = nia.toInt(),
                    nombre = nombre,
                    password = password,
                    rol = "alumno",
                    curso = curso,
                    materia = ""
                )

                if (response.isSuccessful) {
                    Log.i("REGISTRO", "Usuario registrado en servidor")
                    registradoEnServidor = true
                } else {
                    Log.e("REGISTRO", "Error del servidor: ${response.code()}")
                }

            } catch (e: Exception) {
                Log.e("REGISTRO", "Servidor NO disponible: ${e.message}")
            }

            // Guardar en Room
            db.usuarioDao().insertarUsuario(usuario)

            withContext(Dispatchers.Main) {
                if (registradoEnServidor) {
                    toast("Usuario registrado en servidor y guardado localmente")
                } else {
                    toast("Servidor no disponible. Usuario guardado localmente")
                }
                finish()
            }
        }
    }

    // Modelmapper
    fun UsuarioEntity.toDTO(): UsuarioDTO {
        return UsuarioDTO(
            nia = this.nia.toInt(),
            nombre = this.nombre,
            password = this.password,
            rol = this.rol,
            curso = this.curso,
            materia = this.materia
        )
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
