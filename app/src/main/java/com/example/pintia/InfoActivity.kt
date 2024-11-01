package com.example.pintia

import android.media.Image
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pintia.components.Header

class InfoActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_info)
        val header = findViewById<Header>(R.id.header)
        header.title = getString(R.string.info)
        val descriptionContent = findViewById<TextView>(R.id.description_content)
        descriptionContent.text = getString(R.string.lore)
        val moreInfoContent = findViewById<TextView>(R.id.more_info_content)
        moreInfoContent.text = getString(R.string.lore)

        mediaPlayer = MediaPlayer.create(this,R.raw.audio)

        val audioButton = findViewById<ImageButton>(R.id.audio_player)
        audioButton.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            } else {
                mediaPlayer.start()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}