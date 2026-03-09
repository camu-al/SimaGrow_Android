package com.camu.simagrow.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.camu.simagrow.R
import com.camu.simagrow.model.MensajeEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MensajeAdapter(
    private var lista: List<MensajeEntity>,
    private val onEliminarClick: (MensajeEntity) -> Unit
) : RecyclerView.Adapter<MensajeAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val tvAsunto: TextView = view.findViewById(R.id.tvAsunto)
        val tvProfesor: TextView = view.findViewById(R.id.tvProfesor)
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
        val tvMensaje: TextView = view.findViewById(R.id.tvMensaje)
        val btnEliminar: ImageButton = view.findViewById(R.id.btnBorrar)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mensaje, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mensaje = lista[position]
        with(holder) {
            tvAsunto.text = mensaje.asunto
            tvProfesor.text = "Profesor: ${mensaje.profesor}"
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            tvFecha.text = sdf.format(Date(mensaje.fecha))
            tvMensaje.text = mensaje.mensaje

            btnEliminar.setOnClickListener {
                val builder = androidx.appcompat.app.AlertDialog.Builder(itemView.context)
                builder.setTitle("Eliminar mensaje")
                builder.setMessage("¿Seguro que deseas eliminar este mensaje?")

                builder.setPositiveButton("Eliminar") { _, _ ->
                    onEliminarClick(mensaje)
                }

                builder.setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }

                builder.show()
            }
        }
    }

    override fun getItemCount() = lista.size

    fun actualizarLista(nuevaLista: List<MensajeEntity>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}
