package com.example.pintia.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.example.pintia.R
import com.example.pintia.models.TutorialStep

class TutorialManager(
    private val activity: Activity,
    private val tutorialOverlay: FrameLayout,
    private val tutorialSteps: List<TutorialStep>
) {

    private var currentStepIndex = 0

    fun showTutorial(fragmentName:String) {
        tutorialOverlay.visibility = View.VISIBLE
        showStep(currentStepIndex, fragmentName)
    }

    private fun showStep(index: Int, fragmentName:String) {
        // Limpiar cualquier contenido previo
        tutorialOverlay.removeAllViews()

        // Inflar el layout del tutorial
        val stepView = LayoutInflater.from(activity).inflate(R.layout.tutorial_stepper, tutorialOverlay, false)

        // Configurar los datos del paso actual
        val step = tutorialSteps[index]
        val imageView: ImageView = stepView.findViewById(R.id.tutorialImage)
        val titleView: TextView = stepView.findViewById(R.id.tutorialTitle)
        val descriptionView: TextView = stepView.findViewById(R.id.tutorialDescription)
        val nextButton: Button = stepView.findViewById(R.id.tutorialNextButton)
        val skipButton: TextView = stepView.findViewById(R.id.tutorialSkipButton)

        imageView.setImageResource(step.imageResId)
        titleView.text = step.title
        descriptionView.text = step.description

        // Configurar botón de siguiente
        nextButton.setOnClickListener {
            if (currentStepIndex < tutorialSteps.size - 1) {
                currentStepIndex++
                showStep(currentStepIndex, fragmentName)
            } else {
                endTutorial(fragmentName)
            }
        }

        // Configurar botón de saltar
        skipButton.setOnClickListener {
            endTutorial(fragmentName)
        }

        // Añadir la vista del paso actual al overlay
        tutorialOverlay.addView(stepView)
    }

    private fun endTutorial(fragmentName:String) {
        // Ocultar el overlay
        tutorialOverlay.visibility = View.GONE

        // Guardar en preferencias que el tutorial ya se mostró
        val preferences = activity.getSharedPreferences("TutorialPreferences", Context.MODE_PRIVATE)
        preferences.edit().putBoolean("TutorialShown_${fragmentName}", false).apply()
    }

    companion object {
        fun isFirstTimeTutorial(activity: Activity, fragmentName:String): Boolean {
            val preferences = activity.getSharedPreferences("TutorialPreferences", Context.MODE_PRIVATE)
            return preferences.getBoolean("TutorialShown_${fragmentName}", true)
        }
    }


}
