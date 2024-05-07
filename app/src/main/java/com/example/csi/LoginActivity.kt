package com.example.csi

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.csi.MainActivity
import com.example.csi.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        // FirebaseAuth 인스턴스에 AuthStateListener를 추가
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                // 사용자가 로그인한 경우 MainActivity로 이동
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish() // 로그인 화면을 종료하여 뒤로 가기 버튼을 눌렀을 때 다시 로그인 화면이 나타나지 않도록 함
            } else {
                // 사용자가 로그아웃한 경우
                // 추가적인 처리가 필요하다면 여기에 작성
            }
        }

        val loginBtn = findViewById<Button>(R.id.loginBtn)
        loginBtn.setOnClickListener {
            val email = findViewById<TextInputEditText>(R.id.email).text.toString()
            val password = findViewById<TextInputEditText>(R.id.pwd).text.toString()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (!task.isSuccessful) {
                        // 로그인에 실패한 경우
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                    }
                }
        }

        val signupBtn = findViewById<Button>(R.id.signupBtn)
        signupBtn.setOnClickListener {
            val intent = Intent(this, JoinActivity::class.java)
            startActivity(intent)
        }
    }
    override fun onStart() {
        super.onStart()
        // AuthStateListener를 등록하여 사용자의 인증 상태를 감시
        auth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        // 액티비티가 중지될 때 AuthStateListener를 제거하여 메모리 누수를 방지
        auth.removeAuthStateListener(authStateListener)
    }
    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            // 로그인이 성공했을 때 MainActivity로 이동
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // 로그인 화면을 종료하여 뒤로 가기 버튼을 눌렀을 때 다시 로그인 화면이 나타나지 않도록 함
        } else {
            // 로그인에 실패한 경우 처리할 내용 추가
        }
    }
}
