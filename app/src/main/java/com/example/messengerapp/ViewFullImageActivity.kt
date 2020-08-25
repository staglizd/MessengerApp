package com.example.messengerapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.squareup.picasso.Picasso

class ViewFullImageActivity : AppCompatActivity() {

    private var image_viewer: ImageView? = null
    private var image_url: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_full_image)

        image_url = intent.getStringExtra("url")
        image_viewer = findViewById(R.id.image_viewer)

        Picasso.get().load(image_url).into(image_viewer)
    }
}