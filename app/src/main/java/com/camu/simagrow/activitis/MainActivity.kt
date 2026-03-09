package com.camu.simagrow.activitis

import MusicaPrincipal
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.camu.simagrow.R
import com.camu.simagrow.database.AppDatabase
import com.camu.simagrow.databinding.ActivityMainBinding
import com.camu.simagrow.fragments.*
import com.camu.simagrow.model.UsuarioEntity
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch
import androidx.core.content.edit
import android.util.Log
import com.camu.simagrow.fragments.SoporteFragment

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var musicaPrincipal: MusicaPrincipal
    private lateinit var prefs: SharedPreferences
    private lateinit var db: AppDatabase
    private var isAlumno: Boolean = true

    private var niaUsuario: String? = null
    private var nombreUsuario: String? = null
    private var cursoUsuario: String? = null
    private var rolUsuario: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        // -------------- MOSTRAR NOMBRE USUARIO DRAWER --------------
        isAlumno = intent.getBooleanExtra("isAlumno", true)
        cargarDatosUsuario()
        val header = binding.navView.getHeaderView(0)
        val tvNombreDrawer = header.findViewById<TextView>(R.id.tvNombreUsuarioDrawer)
        val tvNia = header.findViewById<TextView>(R.id.tvNiaUsuarioDrawer)
        val tvCurso = header.findViewById<TextView>(R.id.tvCursoUsuarioDrawer)
        tvNombreDrawer.text = nombreUsuario
        tvNia.text = "Nia: $niaUsuario"
        tvCurso.text = "Curso: $cursoUsuario"

        // -------------- MUSICA PRINCIPAL --------------
        musicaPrincipal = MusicaPrincipal(this)

        // -------------- TOOLBAR --------------
        setSupportActionBar(binding.toolbarInclude.miToolBar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // -------------- BOTTOM NAV --------------
        binding.bottonNav.menu.clear()
        if (isAlumno) {
            binding.bottonNav.inflateMenu(R.menu.menu_alumno)
        }else{
            binding.bottonNav.inflateMenu(R.menu.menu_profesor)
        }

        binding.bottonNav.setOnItemSelectedListener { item ->
            when(item.itemId){
                // Alumno
                R.id.bottom_Home -> { cargarFragments(InicioFragment()); true }
                R.id.bottom_Incidencias -> { cargarFragments(IncidenciasFragment()); true }
                R.id.bottom_MensajeProfe -> { cargarFragments(MensajePorfeFragment()); true }

                // Profesor
                /*R.id.bottom_HomeProfe -> { cargarFragments(InicioFragment()); true }
                R.id.bottom_IncidenciasTotales -> { cargarFragments(IncidenciasFragment()); true }
                R.id.bottom_Gestionar_Alumnos -> { cargarFragments(GestionAlumnosFragment()); true }
                R.id.bottom_Mensaje_Alumnos -> { cargarFragments(MensajeProfeFragment()); true }
                */

                else -> false
            }
        }

        // --------------- Conf Menu Drawer ---------------
        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbarInclude.miToolBar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navView.setNavigationItemSelectedListener(this)

        // Cargar fragmento por defecto
        if (savedInstanceState == null) {
            cargarFragments(InicioFragment())
            if (isAlumno){
                binding.bottonNav.selectedItemId = R.id.bottom_Home
            } else
                binding.bottonNav.selectedItemId = R.id.bottom_HomeProfe
        }

        // Modo oscuro
        val modoOscuro: Boolean = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("oscuro", false)
        if (modoOscuro){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    // --------------- FUNCIONES ---------------
    private fun cargarDatosUsuario() {
        prefs = getSharedPreferences("usuario_prefs", MODE_PRIVATE)

        niaUsuario = prefs.getString("nia", "")
        nombreUsuario = prefs.getString("nombre", "Usuario")
        cursoUsuario = prefs.getString("curso", "")
        rolUsuario = prefs.getString("rol", "alumno")

        isAlumno = rolUsuario == "alumno"

        Log.d("MAIN", "Nombre usuario: $nombreUsuario")
        Log.d("MAIN", "NIA usuario: $niaUsuario")
        Log.d("MAIN", "Rol usuario: $rolUsuario")
        Log.d("MAIN", "Curso usuario: $cursoUsuario")
        Log.d("MAIN", "isAlumno: $isAlumno")
    }

    // Boton menu superiror
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.actionRegistrarIncidencia -> {
                cargarFragments(FormularioIncidenciasFragment())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Botones menu drawer
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_info -> cargarFragments(AcercaDeFragment())
            R.id.nav_soporte -> cargarFragments(SoporteFragment())
            R.id.nav_mensajeSoporte -> cargarFragments(GestionAlumnosFragment())
            R.id.nav_ajustes -> cargarFragments(AjustesFragment())
            R.id.nav_salir -> mostrarAlerta(
                titulo = "Cerrar sesión",
                mensaje = "¿Seguro que quieres cerrar sesión?"
            ){
                prefs.edit { clear() }
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun cargarFragments(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.contenedorFragments, fragment)
            .commit()
    }

    private fun showToast(msg: String){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
    }

    private fun mostrarAlerta(titulo:String, mensaje:String, accionConfirmar:()->Unit){
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle(titulo)
        builder.setMessage(mensaje)
        builder.setPositiveButton("Sí"){ dialog,_ -> accionConfirmar(); dialog.dismiss() }
        builder.setNegativeButton("Cancelar"){ dialog,_ -> dialog.dismiss() }
        builder.show()
    }
    fun iniciarMusica() { musicaPrincipal.reproducirSnd(R.raw.lofi_music2) }
    fun pararMusica() { musicaPrincipal.detenerMusica() }
    override fun onStart() {
        super.onStart()
        val musicaActivada = PreferenceManager
            .getDefaultSharedPreferences(this)
            .getBoolean("musica", true)
        if (musicaActivada) iniciarMusica()
    }
    override fun onStop() {
        pararMusica()
        super.onStop()
    }

}