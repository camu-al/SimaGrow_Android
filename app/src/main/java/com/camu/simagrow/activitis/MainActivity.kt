package com.camu.simagrow.activitis

import MusicaPrincipal
import android.annotation.SuppressLint
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
import androidx.preference.PreferenceManager
import com.camu.simagrow.R
import com.camu.simagrow.database.AppDatabase
import com.camu.simagrow.databinding.ActivityMainBinding
import com.camu.simagrow.fragments.*
import com.google.android.material.navigation.NavigationView
import androidx.core.content.edit

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var musicaPrincipal: MusicaPrincipal
    private lateinit var prefs: SharedPreferences
    private lateinit var db: AppDatabase

    private var niaUsuario: String = ""
    private var nombreUsuario: String = "Usuario"
    private var cursoUsuario: String = ""
    private var materiaUsuario: String = ""
    private var rolUsuario: String = "alumno"
    private var isAlumno: Boolean = true

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        // -------------- CARGAR USUARIO --------------
        cargarDatosUsuario()

        // -------------- Drawer Cabezera --------------
        val header = binding.navView.getHeaderView(0)

        header.findViewById<TextView>(R.id.tvNombreUsuarioDrawer).text = nombreUsuario
        header.findViewById<TextView>(R.id.tvNiaUsuarioDrawer).text = "NIA: $niaUsuario"

        val infoDrawer = header.findViewById<TextView>(R.id.tvCursoUsuarioDrawer)

        if (isAlumno) {
            infoDrawer.text = "Curso: $cursoUsuario"
        } else {
            infoDrawer.text = "Materia: $materiaUsuario"
        }

        // -------------- Musica --------------
        musicaPrincipal = MusicaPrincipal(this)

        // -------------- Toolbar --------------
        setSupportActionBar(binding.toolbarInclude.miToolBar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // -------------- Bottom nav --------------
        binding.bottonNav.menu.clear()
        if (isAlumno) {
            binding.bottonNav.inflateMenu(R.menu.menu_alumno)
        } else {
            binding.bottonNav.inflateMenu(R.menu.menu_profesor)
        }

        // Botones del bottom menu segun el rol
        binding.bottonNav.setOnItemSelectedListener { item ->
            if (isAlumno) {
                when (item.itemId) {
                    R.id.bottom_Home -> { cargarFragments(InicioFragment()); true }
                    R.id.bottom_Incidencias -> { cargarFragments(IncidenciasFragment()); true }
                    R.id.bottom_MensajeProfe -> { cargarFragments(MensajePorfeFragment()); true }
                    else -> false
                }
            } else {
                when (item.itemId) {
                    R.id.bottom_HomeProfe -> { cargarFragments(InicioFragment()); true }
                    R.id.bottom_IncidenciasTotales -> { cargarFragments(IncidenciasFragment()); true }
                    R.id.bottom_Gestionar_Alumnos -> { cargarFragments(GestionAlumnosFragment()); true }
                    else -> false
                }
            }
        }

        // --------------- Drawer ---------------
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

        // Si es profesor que no tenga menu soporte
        if (!isAlumno) {
            val menu = binding.navView.menu
            menu.findItem(R.id.nav_soporte)?.isVisible = false
        }

        // -------------- Fragment default --------------
        if (savedInstanceState == null) {
            cargarFragments(InicioFragment())
            // Cargar segun el rol del usuario
            binding.bottonNav.selectedItemId = if (isAlumno) R.id.bottom_Home else R.id.bottom_HomeProfe
        }

        // -------------- Modo Oscuro --------------
        val modoOscuro = PreferenceManager.getDefaultSharedPreferences(this)
            .getBoolean("oscuro", false)

        AppCompatDelegate.setDefaultNightMode(
            if (modoOscuro) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    // -------------- Funciones --------------
    private fun cargarDatosUsuario() {
        prefs = getSharedPreferences("usuario_prefs", MODE_PRIVATE)

        // Datos del usuario
        niaUsuario = prefs.getString("nia", "") ?: ""
        nombreUsuario = prefs.getString("nombre", "Usuario") ?: "Usuario"
        cursoUsuario = prefs.getString("curso", "") ?: ""
        materiaUsuario = prefs.getString("materia", "") ?: ""

        // Limpiar rol
        rolUsuario = prefs.getString("rol", "alumno")?.trim()?.lowercase() ?: "alumno"

        // Si no es ningun rol alumno por defecto
        if (rolUsuario != "alumno" && rolUsuario != "profesor") {
            rolUsuario = "alumno"
        }

        isAlumno = rolUsuario == "alumno"
    }

    // Boton toolbar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // Boton toolbar formulario incidencias
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.actionRegistrarIncidencia -> {
                cargarFragments(FormularioIncidenciasFragment())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Menu segun el rol del usuario
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Aplicar menus segun el rol
        val permitido = when (rolUsuario) {
            "alumno" -> itemPermitidoParaAlumno(item.itemId)
            "profesor" -> itemPermitidoParaProfesor(item.itemId)
            else -> false
        }

        if (!permitido) {
            Toast.makeText(this, "Opción no disponible para tu rol", Toast.LENGTH_SHORT).show()
            return true
        }

        // Switch de los menus
        when (item.itemId) {
            R.id.nav_info -> cargarFragments(AcercaDeFragment())
            R.id.nav_soporte -> cargarFragments(SoporteFragment())
            R.id.nav_ajustes -> cargarFragments(AjustesFragment())
            R.id.nav_salir -> mostrarAlerta(
                titulo = "Cerrar sesión",
                mensaje = "¿Seguro que quieres cerrar sesión?"
            ) {
                prefs.edit { clear() }
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    // Funcion menu para alumno
    private fun itemPermitidoParaAlumno(itemId: Int): Boolean {
        return when (itemId) {
            R.id.nav_info,
            R.id.nav_soporte,
            R.id.nav_ajustes,
            R.id.nav_salir -> true
            else -> false
        }
    }

    // Funcion menu para profesor
    private fun itemPermitidoParaProfesor(itemId: Int): Boolean {
        return when (itemId) {
            R.id.nav_info,
            R.id.nav_ajustes,
            R.id.nav_salir -> true
            else -> false
        }
    }

    // Funcion cargar fragments
    fun cargarFragments(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.contenedorFragments, fragment)
            .commit()
    }

    // Funcion alerta
    private fun mostrarAlerta(titulo:String, mensaje:String, accionConfirmar:()->Unit){
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle(titulo)
        builder.setMessage(mensaje)
        builder.setPositiveButton("Sí"){ dialog,_ -> accionConfirmar(); dialog.dismiss() }
        builder.setNegativeButton("Cancelar"){ dialog,_ -> dialog.dismiss() }
        builder.show()
    }

    // Funciones musica
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
