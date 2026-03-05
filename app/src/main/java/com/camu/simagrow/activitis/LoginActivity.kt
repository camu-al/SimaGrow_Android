package com.camu.simagrow.activitis

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.camu.simagrow.database.AppDatabase
import com.camu.simagrow.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch
import android.util.Log
import androidx.core.content.edit

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

            // ---------------- VALIDACIONES ----------------
            // Campos vacíos
            if (nia.isEmpty() || password.isEmpty()) {
                toast("Los campos son obligatorios")
                return@setOnClickListener
            }

            // NIA debe tener 8 números
            if (!nia.matches(Regex("\\d{8}"))) {
                toast("El NIA debe tener 8 números")
                return@setOnClickListener
            }

            // Contraseña mínima
            if (password.length < 6) {
                toast("La contraseña debe tener al menos 6 caracteres")
                return@setOnClickListener
            }

            // ---------------- LOGIN ----------------
            lifecycleScope.launch {
                val usuario = db.usuarioDao().login(nia, password)

                if (usuario != null) {
                    Log.d("DEBUG_LOGIN", "Login correcto: $usuario")

                    guardarUsuario(
                        nia = usuario.nia,
                        nombre = usuario.nombre,
                        rol = usuario.rol,
                        curso = usuario.curso
                    )

                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    Log.d("LOGIN", "Login fallido para NIA: $nia")
                    toast("Credenciales incorrectas")
                }
            }
        }

        binding.btnRegistro.setOnClickListener {
            startActivity(Intent(this, RegistreActivity::class.java))
        }
    }

    // Toast
    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    // Guardar datos del usuario
    fun guardarUsuario(nia: String, nombre: String, rol: String, curso: String?) {
        val prefs = getSharedPreferences("usuario_prefs", MODE_PRIVATE)
        prefs.edit {
            putString("nia", nia)
            putString("nombre", nombre)
            putString("rol", rol)
            putString("curso", curso)
        }
    }
}
