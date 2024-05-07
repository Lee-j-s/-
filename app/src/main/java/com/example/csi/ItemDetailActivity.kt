package com.example.csi

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class ItemDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)

        val imageUrl = intent.getStringExtra("imageUrl")

        val imageView: ImageView = findViewById(R.id.imageView)
        Glide.with(this)
            .load(imageUrl)
            .into(imageView)
    }
}
