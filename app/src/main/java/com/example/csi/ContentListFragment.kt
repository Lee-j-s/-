package com.example.csi

import ContentAdapter
import com.example.csi.ContentWriteActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.csi.databinding.FragmentContentListBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ContentListFragment : Fragment() {
    private lateinit var binding: FragmentContentListBinding
    private lateinit var contentAdapter: ContentAdapter
    private val contentList = mutableListOf<ContentModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_content_list, container, false)

        contentAdapter = ContentAdapter(contentList) { contentModel ->
            onContentItemClick(contentModel)
        }

        binding.recyclerview.adapter = contentAdapter
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())

        binding.contentWriteBtn.setOnClickListener {
            startActivity(Intent(requireContext(), ContentWriteActivity::class.java))
        }

        getFBContentData()

        return binding.root
    }

    private fun getFBContentData() {
        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                contentList.clear()

                for (data in snapshot.children) {
                    val item = data.getValue(ContentModel::class.java)
                    Log.d("ContentListFragment", "item: $item")
                    item?.let { contentList.add(it) }
                }
                contentList.reverse()
                contentAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }
        FBRef.contentRef.addValueEventListener(postListener)
    }

    private fun onContentItemClick(contentModel: ContentModel) {
        val intent = Intent(requireContext(), ContentDetailActivity::class.java)
        intent.putExtra("contentModel", contentModel)
        startActivity(intent)
    }

}
