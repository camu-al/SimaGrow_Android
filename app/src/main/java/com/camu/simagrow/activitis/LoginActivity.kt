package com.camu.simagrow.activitis

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.camu.simagrow.database.AppDatabase
import com.camu.simagrow.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch
import androidx.core.content.edit
import com.camu.simagrow.api.RetroFitInstance
import com.camu.simagrow.model.UsuarioEntity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        binding.btnIniciarSesion.setOnClickListener {
            val nia = binding.etNia.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            // Validaciones
            if (nia.isEmpty() || password.isEmpty()) {
                toast("Los campos son obligatorios")
                return@setOnClickListener
            }

            if (!nia.matches(Regex("\\d{8}"))) {
                toast("El NIA debe tener 8 números")
                return@setOnClickListener
            }

            // Login
            login(nia, password)
        }

        // Ir al registro
        binding.btnRegistro.setOnClickListener {
            startActivity(Intent(this, RegistreActivity::class.java))
        }
    }

    private fun login(nia: String, password: String) {
        lifecycleScope.launch {
            try {
                val usuarios = RetroFitInstance.api.getUsuarios()

                // Validacion
                val usuarioDTO = usuarios.find {
                    it.nia == nia.toInt() && it.password.equals(password, ignoreCase = true)
                }

                if (usuarioDTO != null) {
                    toast("Login correcto")
                    // Datos de usuario
                    val usuarioLocal = UsuarioEntity(
                        nia = usuarioDTO.nia.toString(),
                        nombre = usuarioDTO.nombre,
                        rol = usuarioDTO.rol ?: "alumno",
                        curso = usuarioDTO.curso,
                        password = usuarioDTO.password,
                        materia = usuarioDTO.materia
                    )

                    // Añadir en room
                    db.usuarioDao().borrarUsuarios()
                    db.usuarioDao().insertarUsuario(usuarioLocal)

                    abrirMain(usuarioLocal)

                } else {
                    toast("Credenciales incorrectas")
                }

            } catch (e: Exception) {
                toast("Error de conexión")
            }
        }
    }

    private fun abrirMain(usuario: UsuarioEntity) {

        val prefs = getSharedPreferences("usuario_prefs", MODE_PRIVATE)
        prefs.edit { clear() }

        // Limpiar rol
        val rolNormalizado = usuario.rol?.replace("\"", "")?.trim()?.lowercase() ?: "alumno"

        // Guardar usuario
        guardarUsuario(
            nia = usuario.nia,
            nombre = usuario.nombre,
            rol = rolNormalizado,
            curso = usuario.curso
        )

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    fun guardarUsuario(nia: String, nombre: String, rol: String, curso: String?) {
        val prefs = getSharedPreferences("usuario_prefs", MODE_PRIVATE)
        prefs.edit {
            putString("nia", nia)
            putString("nombre", nombre)
            putString("rol", rol.replace("\"", "").trim().lowercase())
            putString("curso", curso)
        }
    }
}
