package com.camu.simagrow.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.camu.simagrow.R
import com.camu.simagrow.activitis.MainActivity
import com.camu.simagrow.adapters.NoticiaAdapter
import com.camu.simagrow.databinding.FragmentInicioBinding
import com.camu.simagrow.model.Noticia
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.IOException

class InicioFragment : Fragment() {

    private var _binding: FragmentInicioBinding? = null
    private val binding get() = _binding!!

    private lateinit var noticiasAdapter: NoticiaAdapter
    private val noticiasList = mutableListOf<Noticia>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInicioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Mostrar saludo personalizado
        val prefs = requireActivity().getSharedPreferences("usuario_prefs", 0)
        val nombreCompleto = prefs.getString("nombre", "Usuario") ?: "Usuario"
        val nombre = nombreCompleto.split(" ")[0]
        binding.tvBienvenida.text = "Bienvenido a SimaGrow, $nombre"

        // Mostrar contador incidencias
        val prefsContador = requireActivity().getSharedPreferences("contador_prefs", AppCompatActivity.MODE_PRIVATE)
        val total = prefsContador.getInt("total_incidencias", 0)
        val tvContador = view.findViewById<TextView>(R.id.tvContador)
        tvContador.text = total.toString()

        // Boton ir formulario incidencias
        binding.cvCrearIncidencia.setOnClickListener {
            (requireActivity() as MainActivity).cargarFragments(FormularioIncidenciasFragment())
        }

        // Boton ir formulario mensje profesor
        binding.btnFormProfesor.setOnClickListener {
            (requireActivity() as MainActivity).cargarFragments(FormularioMensajeFragment())
        }

        // Configurar RecyclerView
        noticiasAdapter = NoticiaAdapter(noticiasList)
        binding.recyclerViewNoticias.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = noticiasAdapter
        }

        // Cargar noticias con Jsoup
        cargarNoticias()
    }

    private fun cargarNoticias() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val url = "https://portal.edu.gva.es/ieslluissimarro/entrades/"
                val doc = Jsoup.connect(url).get()

                val noticiasTemp = mutableListOf<Noticia>()

                doc.select("article").forEach { article ->
                    val titulo = article.selectFirst("h2.entry-title")?.text() ?: "Sin título"
                    val descripcion = article.selectFirst("div.entry-summary > p")?.text() ?: ""
                    val enlace = article.selectFirst("div.post-image > a")?.attr("href") ?: ""
                    val fecha = article.selectFirst("div.entry-meta time")?.attr("datetime") ?: ""
                    val imagenUrl = article.selectFirst("div.post-image > a > img")?.attr("src") ?: ""

                    noticiasTemp.add(Noticia(titulo, descripcion, enlace, fecha, imagenUrl))
                }

                withContext(Dispatchers.Main) {
                    noticiasList.clear()
                    noticiasList.addAll(noticiasTemp)
                    noticiasAdapter.notifyDataSetChanged()
                }

            } catch (e: IOException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No se pudieron cargar las noticias", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}