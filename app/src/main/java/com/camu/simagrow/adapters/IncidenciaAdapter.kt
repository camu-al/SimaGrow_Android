package com.camu.simagrow.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.camu.simagrow.R
import com.camu.simagrow.model.IncidenciaEntity

class IncidenciaAdapter(
    private var lista: List<IncidenciaEntity>,
    private val onEliminarClick: (IncidenciaEntity) -> Unit
) : RecyclerView.Adapter<IncidenciaAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val img: ImageView = view.findViewById(R.id.imgIncidencia)
        val titulo: TextView = view.findViewById(R.id.tvTituloIncidencia)
        val zona: TextView = view.findViewById(R.id.tvZona)
        val tipo: TextView = view.findViewById(R.id.tvTipo)
        val estado: TextView = view.findViewById(R.id.tvEstado)
        val fecha: TextView = view.findViewById(R.id.tvFecha)
        val descripcion: TextView = view.findViewById(R.id.tvDescripcionIncidencia)
        val btnEliminar: ImageButton = view.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_incidencias, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val incidencia = lista[position]

        Log.d("INCIDENCIA_ADAPTER", "Mostrando incidencia: ${incidencia.titulo}")

        with(holder) {

            titulo.text = incidencia.titulo
            zona.text = "Zona: ${incidencia.zona}"
            tipo.text = incidencia.tipo.uppercase()
            descripcion.text = incidencia.descripcion
            fecha.text = incidencia.fecha

            // Estado fijo por ahora
            estado.text = "PENDIENTE"

            // Imagen segun tipo
            val iconRes = when (incidencia.tipo.lowercase()) {
                "electricidad" -> R.drawable.outline_electric_bolt_24
                "fontaneria" -> R.drawable.baseline_water_drop_24
                "limpieza" -> R.drawable.outline_cleaning_services_24
                "climatizacion" -> R.drawable.baseline_ac_unit_24
                "mobiliario" -> R.drawable.outline_chair_alt_24
                "it", "tecnologias" -> R.drawable.outline_computer_24
                "seguridad" -> R.drawable.baseline_security_24
                "otro" -> R.drawable.baseline_report_problem_24
                else -> R.drawable.cade_foto_perfil
            }
            img.setImageResource(iconRes)

            val colorIcono = ContextCompat.getColor(
                itemView.context,
                R.color.tipo_incidencia
            )
            img.setColorFilter(colorIcono)

            btnEliminar.setOnClickListener {

                Log.w("INCIDENCIA_ADAPTER", "Intentando eliminar incidencia: ${incidencia.id}")

                val builder = androidx.appcompat.app.AlertDialog.Builder(itemView.context)
                builder.setTitle("Eliminar incidencia")
                builder.setMessage("¿Seguro que deseas eliminar esta incidencia?")

                builder.setPositiveButton("Eliminar") { _, _ ->
                    onEliminarClick(incidencia)
                }

                builder.setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }

                builder.show()
            }

        }
    }

    override fun getItemCount() = lista.size

    fun actualizarLista(nuevaLista: List<IncidenciaEntity>) {
        Log.i("INCIDENCIA_ADAPTER", "Actualizando lista con ${nuevaLista.size} incidencias")
        lista = nuevaLista
        notifyDataSetChanged()
    }
}
