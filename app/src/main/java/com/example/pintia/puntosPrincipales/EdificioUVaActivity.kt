package com.example.pintia.puntosPrincipales

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.pintia.R
import com.example.pintia.components.Header


class EdificioUVaActivity : AppCompatActivity() {

    //private lateinit var modelViewer: ModelViewer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edificio_uva)
        val header = findViewById<Header>(R.id.header)
        header.title = getString(R.string.app_name)

        // Obtén la referencia del WebView
        val webView: WebView = findViewById(R.id.webView)

        // Configura el WebView para habilitar JavaScript y cargar contenido HTML
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()

        // Carga el HTML directo
        val htmlContent = """
                <div class="sketchfab-embed-wrapper"> <iframe title="Catapult" frameborder="0" allowfullscreen mozallowfullscreen="true" webkitallowfullscreen="true" allow="autoplay; fullscreen; xr-spatial-tracking" xr-spatial-tracking execution-while-out-of-viewport execution-while-not-rendered web-share src="https://sketchfab.com/models/7a9e2ada9fa449efa62899d8e14bdb85/embed?autostart=1"> </iframe> </div>
        """.trimIndent()

        webView.loadData(htmlContent, "text/html", "UTF-8")

    }

}