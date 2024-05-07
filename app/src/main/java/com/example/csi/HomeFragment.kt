package com.example.csi

import ImagePagerAdapter
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.csi.databinding.Fragment4Binding
import com.example.csi.R
import java.util.Timer
import java.util.TimerTask

class HomeFragment : Fragment() {
    private var _binding: Fragment4Binding? = null
    private val binding get() = _binding!!

    private lateinit var viewPager: ViewPager
    private lateinit var timer: Timer


    private val delay: Long = 3000 // 슬라이드 간격 (3초)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = Fragment4Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set 버튼 클릭 시 SetFragment로 이동
        binding.ImagebuttonSet.setOnClickListener {
            val setFragment = SetFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, setFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        // ViewPager를 초기화하고 이미지 슬라이드 배너 설정
        viewPager = binding.viewPager
        val images = arrayOf(R.drawable.image1, R.drawable.image2, R.drawable.image3)
        val adapter = ImagePagerAdapter(requireContext(), images, onImageClickListener)
        viewPager.adapter = adapter
        startAutoSlide()
    }

    private fun startAutoSlide() {
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                Handler(Looper.getMainLooper()).post {
                    if (viewPager.currentItem == viewPager.adapter?.count?.minus(1)) {
                        viewPager.currentItem = 0
                    } else {
                        viewPager.currentItem = viewPager.currentItem + 1
                    }
                }
            }
        }, delay, delay)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        timer.cancel() // 프래그먼트가 제거될 때 타이머도 함께 종료합니다.
    }
    private val onImageClickListener = object : ImagePagerAdapter.OnImageClickListener {
        override fun onImageClick(position: Int) {
            // 각 이미지를 클릭했을 때 실행되는 로직
            val url = when (position) {
                0 -> "https://cu.bgfretail.com/brand_info/news_view.do?category=brand_info&depth2=5&idx=1025"
                1 -> "https://cu.bgfretail.com/brand_info/news_view.do?category=brand_info&depth2=5&idx=1022"
                2 -> "https://cu.bgfretail.com/brand_info/news_view.do?category=brand_info&depth2=5&idx=1021"
                else -> "https://example.com" // 기본 URL
            }
            openWebPage(url)
        }
    }


    // 웹페이지 열기
    private fun openWebPage(url: String) {
        val webpage = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, webpage)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        }
    }
}
