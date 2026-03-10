package com.camu.simagrow.adapters

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.camu.simagrow.R
import com.camu.simagrow.model.Noticia
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.core.net.toUri

class NoticiaAdapter(private val noticias: List<Noticia>) :
    RecyclerView.Adapter<NoticiaAdapter.NoticiaViewHolder>() {

    class NoticiaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgNoticia: ImageView = itemView.findViewById(R.id.imgNoticia)
        val tvTitulo: TextView = itemView.findViewById(R.id.tvTitulo)
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticiaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_noticia, parent, false)
        return NoticiaViewHolder(view)
    }
    override fun onBindViewHolder(holder: NoticiaViewHolder, position: Int) {
        val noticia = noticias[position]

        // Mostrar Noticias del centro
        with(holder){
            tvTitulo.text = noticia.titulo
            tvDescripcion.text = resumirTextoNoticia(noticia.descripcion)
            holder.tvFecha.text = formatearFecha(noticia.fecha)

            Glide.with(itemView.context)
                .load(noticia.imagenUrl)
                .centerCrop()
                .into(imgNoticia)

            itemView.setOnClickListener {
                val context = holder.itemView.context
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = noticia.enlace.toUri()
                context.startActivity(intent)
            }
        }

    }
    override fun getItemCount(): Int = noticias.size
    // Formatear el tipo de la fecha
    fun formatearFecha(fechaOriginal: String): String {
        return try {
            val inputFormat = if (fechaOriginal.contains("T")) {
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
            } else {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            }

            val date = inputFormat.parse(fechaOriginal)

            val localeCa = Locale.forLanguageTag("ca-ES")
            val outputFormat = SimpleDateFormat("d 'de' MMMM 'de' yyyy", localeCa)
            outputFormat.format(date!!).lowercase()

        } catch (e: Exception) {
            fechaOriginal
        }
    }

    // Limitar el texto de la noticia
    fun resumirTextoNoticia(texto: String, maxChars: Int = 120): String {
        return if (texto.length > maxChars) {
            texto.take(maxChars).trimEnd() + "…"
        } else {
            texto
        }
    }
}