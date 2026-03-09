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
import androidx.core.content.edit
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

            if (nia.isEmpty() || password.isEmpty()) {
                toast("Los campos son obligatorios")
                return@setOnClickListener
            }

            if (!nia.matches(Regex("\\d{8}"))) {
                toast("El NIA debe tener 8 números")
                return@setOnClickListener
            }

            // -------------------- Login ------------------
            login(nia, password)
        }

        binding.btnRegistro.setOnClickListener {
            startActivity(Intent(this, RegistreActivity::class.java))
        }
    }

    // LOGIN
    private fun login(nia: String, password: String) {
        lifecycleScope.launch {
            val usuarioLocal = db.usuarioDao().login(nia, password)

            if (usuarioLocal != null) {
                toast("Login correcto")
                abrirMain(usuarioLocal)
            } else {
                toast("Credenciales incorrectas")
            }
        }
    }

    private fun abrirMain(usuario: UsuarioEntity) {
        guardarUsuario(
            nia = usuario.nia,
            nombre = usuario.nombre,
            rol = usuario.rol,
            curso = usuario.curso
        )

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
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
            putString("rol", rol)
            putString("curso", curso)
        }
    }
}
