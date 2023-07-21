package com.example.ingreportesestudiantes.adaptadores

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ingreportesestudiantes.R
import com.example.ingreportesestudiantes.modelos.Reporte
import com.squareup.picasso.Picasso
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class adapterReportes(c: Context): RecyclerView.Adapter<adapterReportes.ViewHolder>()  {
    var lista = ArrayList<Reporte>()
    private var context = c

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val fecha = view.findViewById<TextView>(R.id.textViewNombre)
        val direccion = view.findViewById<TextView>(R.id.textViewDireccion)
        val oficio = view.findViewById<TextView>(R.id.textViewOficio)
        val palabrasClaves = view.findViewById<TextView>(R.id.textViewPalabrasClaves)
        // System.out.println("adapter " + use.getNombre())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_reportes, parent, false)
        return ViewHolder(view)
    }
    override fun getItemCount(): Int {
        return lista.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.fecha.text = lista[position].fechaHora.trim()
        holder.direccion.text = lista[position].direccion.trim()
        holder.oficio.text = lista[position].oficio.trim()
        holder.palabrasClaves.text = lista[position].palabrasClaves.trim()


    }

    fun agregarUsuario(reportes: Reporte){
        lista.add(reportes)
        println("tamaño de la lista: ${lista.size}")
        notifyItemInserted(lista.size)
    }

}