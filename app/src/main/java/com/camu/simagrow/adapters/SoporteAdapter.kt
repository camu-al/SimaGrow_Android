package com.camu.simagrow.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.camu.simagrow.R
import com.camu.simagrow.model.SoporteEntity

class SoporteAdapter(private val lista: List<SoporteEntity>) :
    RecyclerView.Adapter<SoporteAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.tvNombre)
        val nia: TextView = view.findViewById(R.id.tvNia)
        val asunto: TextView = view.findViewById(R.id.tvAsunto)
        val mensaje: TextView = view.findViewById(R.id.tvMensaje)
        val fecha: TextView = view.findViewById(R.id.tvFecha)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_soporte, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]

        with(holder){
            nombre.text = item.nombre
            nia.text = "NIA: ${item.nia}"
            asunto.text = item.asunto
            mensaje.text = item.mensaje
            fecha.text = item.fecha
        }

    }
}
