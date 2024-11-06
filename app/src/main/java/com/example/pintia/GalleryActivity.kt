package com.example.pintia

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pintia.components.Header
import com.example.pintia.services.ImageAdapter

class GalleryActivity : AppCompatActivity() {

    private lateinit var recyclerView:RecyclerView
    private lateinit var imageAdapter: ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        val header = findViewById<Header>(R.id.header)
        header.title = getString(R.string.gallery)

        recyclerView = findViewById(R.id.imageRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2) // 2 columnas

        val imageUrls = listOf(
            "https://http.cat/404.jpg",
            "https://http.cat/103.jpg",
            "https://http.cat/402.jpg",
            "https://http.cat/400.jpg",
            "https://http.cat/202.jpg",
            "https://http.cat/404.jpg",
            "https://http.cat/103.jpg",
            "https://http.cat/402.jpg",
            "https://http.cat/400.jpg",
            "https://http.cat/202.jpg",
            "https://http.cat/404.jpg",
            "https://http.cat/103.jpg",
            "https://http.cat/402.jpg",
            "https://http.cat/400.jpg",
            "https://http.cat/202.jpg",
            "https://http.cat/404.jpg",
            "https://http.cat/103.jpg",
            "https://http.cat/402.jpg",
            "https://http.cat/400.jpg",
            "https://http.cat/202.jpg",
            "https://http.cat/404.jpg",
            "https://http.cat/103.jpg",
            "https://http.cat/402.jpg",
            "https://http.cat/400.jpg",
            "https://http.cat/202.jpg",
            "https://http.cat/404.jpg",
            "https://http.cat/103.jpg",
            "https://http.cat/402.jpg",
            "https://http.cat/400.jpg",
            "https://http.cat/202.jpg",
            "https://http.cat/404.jpg",
            "https://http.cat/103.jpg",
            "https://http.cat/402.jpg",
            "https://http.cat/400.jpg",
            "https://http.cat/202.jpg",
        )

        imageAdapter = ImageAdapter(imageUrls)
        recyclerView.adapter = imageAdapter
    }
}