package com.example.pintia.services

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import com.example.pintia.R
import com.example.pintia.services.DynamicViewBuilder.removeMarkerOfFile
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class ImageAdapter(private var imageUrls: List<String>) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

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
            if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
                // Es una URL, abrir en el navegador
                abrirNavegador(holder.itemView.context, imageUrl)
            } else {
                // Es un archivo local, mostrar opciones de "Descargar" o "Eliminar"
                mostrarOpciones(holder.itemView.context, imageUrl, position)
            }
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
    }

    // Método para actualizar las imágenes en el adaptador
    fun updateImages(newImages: List<String>) {
        imageUrls = newImages
        notifyDataSetChanged() // Notifica al RecyclerView que los datos han cambiado
    }

    // Función para mostrar opciones de descarga o eliminación
    private fun mostrarOpciones(context: Context, filePath: String, position: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Opciones")
        builder.setItems(arrayOf("Descargar", "Eliminar")) { _, which ->
            when (which) {
                0 -> descargarImagen(context, filePath) // Descargar imagen
                1 -> eliminarImagen(context, filePath, position) // Eliminar imagen
            }
        }
        builder.show()
    }

    // Función para descargar la imagen al almacenamiento público
    private fun descargarImagen(context: Context, filePath: String) {
        val file = File(filePath)

        if (!file.exists()) {
            Toast.makeText(context, "El archivo no existe", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val outputStream: OutputStream?
            val fileName = file.name

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Guardar en almacenamiento público en Android 10 o superior
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                val uri: Uri? = context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )

                outputStream = uri?.let { context.contentResolver.openOutputStream(it) }
            } else {
                // Guardar en almacenamiento público en Android 9 o inferior
                val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val newFile = File(directory, fileName)
                outputStream = FileOutputStream(newFile)
            }

            // Copiar los datos de la imagen al almacenamiento
            file.inputStream().use { input ->
                outputStream?.use { output ->
                    input.copyTo(output)
                }
            }

            Toast.makeText(context, "Imagen guardada en la galería", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Error al guardar la imagen: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Función para eliminar la imagen
    private fun eliminarImagen(context: Context, filePath: String, position: Int) {
        val file = File(filePath)

        if (file.exists()) {
            if (file.delete()) {
                Toast.makeText(context, "Imagen eliminada", Toast.LENGTH_SHORT).show()
                // Actualizar la lista de imágenes
                (imageUrls as MutableList).removeAt(position)
                removeMarkerOfFile(context, filePath)
                notifyItemRemoved(position)
            } else {
                Toast.makeText(context, "No se pudo eliminar la imagen", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "El archivo no existe", Toast.LENGTH_SHORT).show()
        }
    }
}