package com.example.csi

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.csi.R

class TradeFragment : Fragment(R.layout.fragment7) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageView = view.findViewById<ImageView>(R.id.imagebutton1)
        // ImageView 사용 예: 클릭 리스너 설정
        imageView.setOnClickListener {
            // 이미지 클릭 시 수행할 작업
        }

        val imageView1 = view.findViewById<ImageView>(R.id.imagebutton2)
        // ImageView 사용 예: 클릭 리스너 설정
        imageView1.setOnClickListener {
            // 이미지 클릭 시 수행할 작업
        }

        val imageView2 = view.findViewById<ImageView>(R.id.imagebutton3)
        // ImageView 사용 예: 클릭 리스너 설정
        imageView2.setOnClickListener {
            // 이미지 클릭 시 수행할 작업
        }
    }
}
