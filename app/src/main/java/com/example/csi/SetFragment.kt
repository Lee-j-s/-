package com.example.csi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.example.csi.LoginActivity // LoginActivity의 패키지명으로 수정 필요

class SetFragment : Fragment(R.layout.fragment6) {

    // FirebaseAuth 객체 가져오기
    private val firebaseAuth = FirebaseAuth.getInstance()

    // 사용자 로그아웃 메서드
    private fun signOut() {
        // 사용자 로그아웃
        firebaseAuth.signOut()

        // 로그아웃 성공 시 LoginActivity로 이동
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish() // 현재 액티비티 종료
    }

    // 사용자 회원 탈퇴 메서드
    private fun deleteAccount() {
        // 사용자 삭제
        firebaseAuth.currentUser?.delete()
            ?.addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // 회원 탈퇴 성공 시 로그아웃
                    signOut()
                } else {
                    // 회원 탈퇴 실패 시 메시지 표시
                    // TODO: 실패 시 처리할 내용 추가
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 로그아웃 버튼 클릭 시 로그아웃 메서드 호출
        view.findViewById<Button>(R.id.logout_button).setOnClickListener {
            signOut()
        }

        // 회원 탈퇴 버튼 클릭 시 회원 탈퇴 메서드 호출
        view.findViewById<Button>(R.id.delete_account_button).setOnClickListener {
            deleteAccount()
        }
    }
}
