package com.example.csi

import ContentModel
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.csi.databinding.ActivityContentDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class ContentDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityContentDetailBinding
    private lateinit var contentModel: ContentModel
    private lateinit var database: DatabaseReference
    private lateinit var currentUserEmail: String
    private var contentLikesRef: DatabaseReference? = null
    private var userLikedRef: DatabaseReference? = null
    private var userLiked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        contentModel = intent.getParcelableExtra("contentModel") ?: return
        val imageUrl = contentModel.imageUrl
        if (imageUrl.isNotEmpty()) {
            Picasso.get().load(imageUrl).into(binding.imageView)
        } else {
            binding.imageView.setImageDrawable(null)
        }

        binding.textViewTitle.text = contentModel.title
        binding.textViewContent.text = contentModel.content
        database = FirebaseDatabase.getInstance().reference.child("content")
        currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
        val username = currentUserEmail.substringBefore('@')  // '@' 이전의 부분만 추출

        contentLikesRef = database.child(contentModel.id).child("likes")
        userLikedRef = database.child(contentModel.id).child("likedBy").child(username)  // username을 사용

        userLikedRef?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userLiked = snapshot.exists()
                updateLikeButtonState()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ContentDetailActivity, "좋아요 상태를 불러오는 중 에러가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })

        binding.imagelikeButton.setOnClickListener {
            toggleLike()
        }

        binding.imagedeleteButton.setOnClickListener {
            deleteContent(contentModel)
        }
    }


    private fun toggleLike() {
        if (userLiked) {
            // 사용자가 이미 좋아요를 눌렀을 때: 좋아요를 취소합니다.
            contentLikesRef?.runTransaction(object : Transaction.Handler {
                override fun doTransaction(mutableData: MutableData): Transaction.Result {
                    val likes = (mutableData.getValue(Int::class.java) ?: 0) - 1
                    mutableData.value = likes
                    return Transaction.success(mutableData)
                }

                override fun onComplete(databaseError: DatabaseError?, committed: Boolean, currentData: DataSnapshot?) {
                    if (databaseError != null) {
                        // 에러 처리
                        Toast.makeText(this@ContentDetailActivity, "좋아요 업데이트 중 에러가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        userLikedRef?.removeValue()
                        userLiked = false
                        updateLikeButtonState()
                        updateLikesCount(-1) // 좋아요 개수 감소
                    }
                }
            })
        } else {
            // 사용자가 좋아요를 누르지 않았을 때: 좋아요를 추가합니다.
            contentLikesRef?.runTransaction(object : Transaction.Handler {
                override fun doTransaction(mutableData: MutableData): Transaction.Result {
                    val likes = (mutableData.getValue(Int::class.java) ?: 0) + 1
                    mutableData.value = likes
                    return Transaction.success(mutableData)
                }

                override fun onComplete(databaseError: DatabaseError?, committed: Boolean, currentData: DataSnapshot?) {
                    if (databaseError != null) {
                        // 에러 처리
                        Toast.makeText(this@ContentDetailActivity, "좋아요 업데이트 중 에러가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        userLikedRef?.setValue(true)
                        userLiked = true
                        updateLikeButtonState()
                        updateLikesCount(1) // 좋아요 개수 증가
                    }
                }
            })
        }
    }

    private fun updateLikesCount(countChange: Int) {
        contentModel.likes += countChange
        database.child(contentModel.id).child("likes").setValue(contentModel.likes)
    }
    private fun updateLikeButtonState() {
        if (userLiked) {
            binding.imagelikeButton.setBackgroundResource(R.drawable.baseline_favorite_24) // 좋아요를 이미 눌렀을 때
        } else {
            binding.imagelikeButton.setBackgroundResource(R.drawable.baseline_favorite_border_24) // 좋아요를 아직 누르지 않았을 때
        }
    }

    private fun deleteContent(contentModel: ContentModel) {
        val contentId = contentModel.id

        // 해당 게시물의 작성자 아이디를 가져옵니다.
        val authorEmail = contentModel.userEmail

        // 현재 사용자의 아이디를 가져옵니다.
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email

        // 현재 사용자의 아이디가 null이 아니고, 작성자의 아이디와 일치하는 경우에만 삭제합니다.
        if (currentUserEmail != null && currentUserEmail == authorEmail) {
            database.child(contentId).removeValue()
                .addOnSuccessListener {
                    // MainActivity에서 ContentListFragment로 전환하기 위해 MainActivity로 되돌아가야 함
                    onBackPressed()
                    Toast.makeText(this, "게시물이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "삭제 실패", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "게시물을 삭제할 수 있는 권한이 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }


}

