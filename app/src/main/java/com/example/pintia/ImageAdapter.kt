package com.example.pintia

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import android.content.Intent
import android.net.Uri
import android.widget.Toast

class ImageAdapter(private val imageUrls: List<String>) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        // Asignamos el click listener a cada imagen
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.component_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageUrl = imageUrls[position]
        Glide.with(holder.imageView.context)
            .load(imageUrl)
            .centerCrop()
            .into(holder.imageView)
        // Asignamos el click listener
        holder.imageView.setOnClickListener {
            abrirNavegador(holder.itemView.context, imageUrl) // Llamamos a la función para abrir la URL
        }
    }

    override fun getItemCount() = imageUrls.size

    // Función para abrir el navegador
    private fun abrirNavegador(context: Context, url: String) {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)

        // Intentar abrir la URL con el navegador predeterminado del dispositivo
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "No se pudo abrir la URL", Toast.LENGTH_SHORT).show()
        }
    }}