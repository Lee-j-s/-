package com.example.csi

import Fragment3
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private val Fragment_1 = 1
    private val Fragment_2 = 2
    private val Fragment_3 = 3
    private val Fragment_4 = 4
    private val Fragment_5 = 5
    private val Fragment_7 = 7

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragmentView(Fragment_4)

        findViewById<View>(R.id.btn1).setOnClickListener {
            fragmentView(Fragment_1)
        }
        findViewById<View>(R.id.btn2).setOnClickListener {
            fragmentView(Fragment_2)
        }
        findViewById<View>(R.id.btn3).setOnClickListener {
            fragmentView(Fragment_3)
        }
        findViewById<View>(R.id.btn5).setOnClickListener {
            fragmentView(Fragment_4)
        }
        findViewById<View>(R.id.btn6).setOnClickListener {
            fragmentView(Fragment_5)
        }
        findViewById<View>(R.id.btn4).setOnClickListener {
            fragmentView(Fragment_7)
        }
    }

    private fun fragmentView(fragment: Int) {
        val transaction = supportFragmentManager.beginTransaction()

        when (fragment) {
            1 -> {
                val fragment1 = Fragment1()
                transaction.replace(R.id.fragment_container, fragment1)
            }
            2 -> {
                val fragment2 = Fragment2()
                transaction.replace(R.id.fragment_container, fragment2)
            }
            3 -> {
                val fragment3 = Fragment3()
                transaction.replace(R.id.fragment_container, fragment3)
            }
            4 -> {
                val fragment4 = Fragment4()
                transaction.replace(R.id.fragment_container, fragment4)
            }
            5 -> {
                val fragment5 = Fragment5()
                transaction.replace(R.id.fragment_container, fragment5)
            }

            7 -> {
                val fragment7 = Fragment7()
                transaction.replace(R.id.fragment_container, fragment7)
            }
        }
        transaction.commit()
    }

}
