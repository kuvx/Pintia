package com.example.pintia.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pintia.R

data class TutorialStep(val imageResId: Int, val title: String, val description: String)

class TutorialAdapter(private val steps: List<TutorialStep>) :
    RecyclerView.Adapter<TutorialAdapter.TutorialViewHolder>() {

    class TutorialViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.tutorialImage)
        val titleView: TextView = view.findViewById(R.id.tutorialTitle)
        val descriptionView: TextView = view.findViewById(R.id.tutorialDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TutorialViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tutorial_step, parent, false)
        return TutorialViewHolder(view)
    }

    override fun onBindViewHolder(holder: TutorialViewHolder, position: Int) {
        val step = steps[position]
        holder.imageView.setImageResource(step.imageResId)
        holder.titleView.text = step.title
        holder.descriptionView.text = step.description
    }

    override fun getItemCount() = steps.size
}
