package com.example.pintia.services.model3d

import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout


class CustomWebView(context: Context, layout: RelativeLayout) : WebView(context) {
    private var initialX: Float = 0f
    private var initialY: Float = 0f
    private var isDragging: Boolean = false

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Guarda la posición inicial
                initialX = event.x
                initialY = event.y
                isDragging = false
                parent.requestDisallowInterceptTouchEvent(true) // Inicialmente bloquea el padre
            }

            MotionEvent.ACTION_MOVE -> {
                // Calcula el desplazamiento
                val deltaX = Math.abs(event.x - initialX)
                val deltaY = Math.abs(event.y - initialY)

                if (!isDragging) {
                    // Determina si es un desplazamiento vertical, horizontal o diagonal
                    if (deltaY > deltaX) {
                        // Desplazamiento vertical: Permite que el padre maneje el desplazamiento
                        parent.requestDisallowInterceptTouchEvent(false)
                    } else {
                        // Desplazamiento horizontal o diagonal: Bloquea el layout padre
                        parent.requestDisallowInterceptTouchEvent(true)
                        isDragging = true
                    }
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                // Restaura el estado predeterminado al finalizar el toque
                parent.requestDisallowInterceptTouchEvent(false)
            }
        }
        return super.onTouchEvent(event)
    }

    private var webChromeClient = object : WebChromeClient() {
        // Declarar variables globales para manejar la vista de pantalla completa
        private var fullScreenContainer: FrameLayout? = null
        private var customView: View? = null
        private var customViewCallback: WebChromeClient.CustomViewCallback? = null

        override fun onShowCustomView(view: View, callback: CustomViewCallback) {
            // Verifica si ya hay una vista en pantalla completa
            if (customView != null) {
                callback.onCustomViewHidden()
                return
            }

            // Guarda la vista y el callback para restaurar el estado
            customView = view
            customViewCallback = callback

            // Crea un contenedor para la vista de pantalla completa si no existe
            if (fullScreenContainer == null) {
                fullScreenContainer = FrameLayout(context).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                }
            }

            // Agrega la vista al contenedor y reemplaza el contenido principal
            fullScreenContainer?.addView(view)
            (layout.parent as ViewGroup).addView(fullScreenContainer)

            // Oculta la vista original del WebView
            layout.visibility = View.GONE
        }

        override fun onHideCustomView() {
            // Salir del modo de pantalla completa
            if (customView == null) return

            // Elimina la vista de pantalla completa
            fullScreenContainer?.removeView(customView)
            (layout.parent as ViewGroup).removeView(fullScreenContainer)

            customView = null
            customViewCallback?.onCustomViewHidden()

            // Restaura la vista original del WebView
            layout.visibility = View.VISIBLE
        }
    }
}

object Model3D {
    fun getWebChromeClient(layout: RelativeLayout, container: LinearLayout): WebView {

        return CustomWebView(layout.context, layout).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true // Habilita DOM Storage
                loadWithOverviewMode = true
            }
        }
    }
}