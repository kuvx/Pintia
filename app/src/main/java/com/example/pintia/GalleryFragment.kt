package com.example.pintia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pintia.services.ImageAdapter
import kotlin.math.min

class GalleryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var prevButton: Button
    private lateinit var nextButton: Button
    private lateinit var imageUrls: List<String>

    private var currentPage = 0
    private val pageSize = 20  // Número de imágenes que se cargarán por página

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_gallery, container, false)

        (requireActivity() as MainActivity).updateHeader(getString(R.string.gallery))

        imageUrls = getImgsOfFile()

        recyclerView = rootView.findViewById(R.id.imageRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2) // 2 columnas

        imageAdapter = ImageAdapter(mutableListOf())
        recyclerView.adapter = imageAdapter

        prevButton = rootView.findViewById(R.id.prevButton)
        nextButton = rootView.findViewById(R.id.nextButton)

        loadPage(currentPage)

        // Navegar a la página anterior
        prevButton.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                loadPage(currentPage)
            }
        }

        // Navegar a la página siguiente
        nextButton.setOnClickListener {
            if ((currentPage + 1) * pageSize < imageUrls.size) {
                currentPage++
                loadPage(currentPage)
            }
        }

        return rootView
    }

    // Función para cargar una página específica de imágenes
    private fun loadPage(page: Int) {
        val startIndex = page * pageSize
        val endIndex = min((page + 1) * pageSize, imageUrls.size)
        val pageImages = imageUrls.subList(startIndex, endIndex)

        imageAdapter.updateImages(pageImages)

        // Habilitar o deshabilitar los botones según sea necesario
        prevButton.isEnabled = page > 0
        nextButton.isEnabled = (page + 1) * pageSize < imageUrls.size
    }

    private fun getImgsOfFile(): List<String> {
        // Listar todos los archivos en el directorio de caché y filtrar los que comienzan con "photo_"
        val photoFiles = requireContext().cacheDir.listFiles { file ->
            // Filtrar archivos que empiezan con "photo_"
            file.name.startsWith("photo_") &&
                    file.isFile
        }
        return (photoFiles?.map { it.absolutePath })?.toList() ?: emptyList()
    }
}
