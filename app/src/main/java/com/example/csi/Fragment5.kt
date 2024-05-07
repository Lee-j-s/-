package com.example.csi

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment

class Fragment5 : Fragment(), View.OnClickListener {
    private lateinit var rootView: View
    private lateinit var imageButton1: ImageView
    private lateinit var imageButton2: ImageView
    private lateinit var imageButton3: ImageView
    private lateinit var imageButton4: ImageView
    private lateinit var imageButton5: ImageView
    private lateinit var imageButton6: ImageView
    private lateinit var imageButton7: ImageView
    private lateinit var imageButton8: ImageView
    private lateinit var imageButton9: ImageView
    private lateinit var imageButton10: ImageView
    private lateinit var imageButton11: ImageView
    private lateinit var imageButton12: ImageView
    private lateinit var imageButton13: ImageView
    private lateinit var imageButton14: ImageView
    private lateinit var imageButton15: ImageView
    private lateinit var imageButton16: ImageView
    private lateinit var imageButton17: ImageView
    private lateinit var imageButton18: ImageView
    private lateinit var imageButton19: ImageView
    private lateinit var imageButton20: ImageView
    private lateinit var imageButton21: ImageView
    private lateinit var imageButton22: ImageView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment5, container, false)

        // 이미지 뷰를 XML에서 찾아 변수에 할당
        imageButton1 = rootView.findViewById(R.id.imagebutton1)
        imageButton2 = rootView.findViewById(R.id.imagebutton2)
        imageButton3 = rootView.findViewById(R.id.imagebutton3)
        imageButton4 = rootView.findViewById(R.id.imagebutton4)
        imageButton5 = rootView.findViewById(R.id.imagebutton5)
        imageButton6 = rootView.findViewById(R.id.imagebutton6)
        imageButton7 = rootView.findViewById(R.id.imagebutton7)
        imageButton8 = rootView.findViewById(R.id.imagebutton8)
        imageButton9 = rootView.findViewById(R.id.imagebutton9)
        imageButton10 = rootView.findViewById(R.id.imagebutton10)
        imageButton11 = rootView.findViewById(R.id.imagebutton11)
        imageButton12 = rootView.findViewById(R.id.imagebutton12)
        imageButton13 = rootView.findViewById(R.id.imagebutton13)
        imageButton14 = rootView.findViewById(R.id.imagebutton14)
        imageButton15 = rootView.findViewById(R.id.imagebutton15)
        imageButton16 = rootView.findViewById(R.id.imagebutton16)
        imageButton17 = rootView.findViewById(R.id.imagebutton17)
        imageButton18 = rootView.findViewById(R.id.imagebutton18)
        imageButton19 = rootView.findViewById(R.id.imagebutton19)
        imageButton20 = rootView.findViewById(R.id.imagebutton20)
        imageButton21 = rootView.findViewById(R.id.imagebutton21)
        imageButton22 = rootView.findViewById(R.id.imagebutton22)


        // 각 이미지 뷰에 클릭 리스너 설정
        imageButton1.setOnClickListener(this)
        imageButton2.setOnClickListener(this)
        imageButton3.setOnClickListener(this)
        imageButton4.setOnClickListener(this)
        imageButton5.setOnClickListener(this)
        imageButton6.setOnClickListener(this)
        imageButton7.setOnClickListener(this)
        imageButton8.setOnClickListener(this)
        imageButton9.setOnClickListener(this)
        imageButton10.setOnClickListener(this)
        imageButton11.setOnClickListener(this)
        imageButton12.setOnClickListener(this)
        imageButton13.setOnClickListener(this)
        imageButton14.setOnClickListener(this)
        imageButton15.setOnClickListener(this)
        imageButton16.setOnClickListener(this)
        imageButton17.setOnClickListener(this)
        imageButton18.setOnClickListener(this)
        imageButton19.setOnClickListener(this)
        imageButton20.setOnClickListener(this)
        imageButton21.setOnClickListener(this)
        imageButton22.setOnClickListener(this)




        return rootView
    }

    override fun onClick(v: View?) {
        // 클릭된 이미지 뷰에 따라 해당하는 액티비티를 시작
        when (v?.id) {
            R.id.imagebutton1 -> {
                startActivity(Intent(activity, SearchActivity::class.java))
            }
            R.id.imagebutton2 -> {
                startActivity(Intent(activity, SearchActivity1::class.java))
            }
            R.id.imagebutton3 -> {
                startActivity(Intent(activity, SearchActivity2::class.java))
            }
            R.id.imagebutton4 -> {
                startActivity(Intent(activity, SearchActivity3::class.java))
            }
            R.id.imagebutton5 -> {
                startActivity(Intent(activity, SearchActivity4::class.java))
            }
            R.id.imagebutton6 -> {
                startActivity(Intent(activity, SearchActivity5::class.java))
            }
            R.id.imagebutton7 -> {
                startActivity(Intent(activity, SearchActivity6::class.java))
            }
            R.id.imagebutton8 -> {
                startActivity(Intent(activity, SearchActivity7::class.java))
            }
            R.id.imagebutton9 -> {
                startActivity(Intent(activity, SearchActivity8::class.java))
            }
            R.id.imagebutton10 -> {
                startActivity(Intent(activity, SearchActivity9::class.java))
            }
            R.id.imagebutton11 -> {
                startActivity(Intent(activity, SearchActivity10::class.java))
            }
            R.id.imagebutton12 -> {
                startActivity(Intent(activity, SearchActivity11::class.java))
            }
            R.id.imagebutton13 -> {
                startActivity(Intent(activity, SearchActivity12::class.java))
            }
            R.id.imagebutton14 -> {
                startActivity(Intent(activity, SearchActivity13::class.java))
            }
            R.id.imagebutton15 -> {
                startActivity(Intent(activity, SearchActivity14::class.java))
            }
            R.id.imagebutton16 -> {
                startActivity(Intent(activity, SearchActivity15::class.java))
            }
            R.id.imagebutton17 -> {
                startActivity(Intent(activity, SearchActivity16::class.java))
            }
            R.id.imagebutton18 -> {
                startActivity(Intent(activity, SearchActivity17::class.java))
            }
            R.id.imagebutton19 -> {
                startActivity(Intent(activity, SearchActivity18::class.java))
            }
            R.id.imagebutton20 -> {
                startActivity(Intent(activity, SearchActivity19::class.java))
            }
            R.id.imagebutton21 -> {
                startActivity(Intent(activity, SearchActivity20::class.java))
            }
            R.id.imagebutton22 -> {
                startActivity(Intent(activity, SearchActivity21::class.java))
            }

            // 나머지 이미지 뷰들에 대한 처리 추가
        }
    }
}
