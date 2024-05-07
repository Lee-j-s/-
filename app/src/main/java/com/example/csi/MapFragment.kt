package com.example.csi

import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.csi.databinding.ActivityMapBinding
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MapFragment : Fragment(), MapView.CurrentLocationEventListener {
    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK a70645bcfd024ce9073200946d6966ad" // REST API 키
    }

    private var _binding: ActivityMapBinding? = null
    private val binding get() = _binding!!

    private val listItems = arrayListOf<ListLayout>() // 리사이클러 뷰 아이템
    private val listAdapter = ListAdapter(listItems) // 리사이클러 뷰 어댑터
    private var pageNumber = 1 // 검색 페이지 번호
    private var keyword = "" // 검색 키워드
    private val customMarkerResourceId = R.drawable.img_cs_mini_cu // 사용자 정의 마커 이미지 리소스 ID

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 리사이클러 뷰
        binding.rvList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvList.adapter = listAdapter

        // 리스트 아이템 클릭 시 해당 위치로 이동
        listAdapter.setItemClickListener(object : ListAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                val mapPoint =
                    MapPoint.mapPointWithGeoCoord(listItems[position].y, listItems[position].x)
                binding.mapView.setMapCenterPointAndZoomLevel(mapPoint, 1, true)
            }
        })

        // 검색 버튼
        binding.imagebtnSearch.setOnClickListener {
            keyword = binding.etSearchField.text.toString()
            pageNumber = 1
            searchKeyword(keyword, pageNumber)
        }

        // 이전 페이지 버튼
        binding.btnPrevPage.setOnClickListener {
            pageNumber--
            binding.tvPageNumber.text = pageNumber.toString()
            searchKeyword(keyword, pageNumber)
        }

        // 다음 페이지 버튼
        binding.btnNextPage.setOnClickListener {
            pageNumber++
            binding.tvPageNumber.text = pageNumber.toString()
            searchKeyword(keyword, pageNumber)
        }

        // 지도 초기화
        binding.mapView.setMapViewEventListener(object : MapView.MapViewEventListener {
            override fun onMapViewInitialized(mapView: MapView) {
                // 지도 초기화 완료 시 처리
            }

            override fun onMapViewCenterPointMoved(mapView: MapView, mapPoint: MapPoint) {
                // 지도 중심 좌표 이동 시 처리
            }

            override fun onMapViewZoomLevelChanged(mapView: MapView, i: Int) {
                // 지도 줌 레벨 변경 시 처리
            }

            override fun onMapViewSingleTapped(mapView: MapView, mapPoint: MapPoint) {
                // 지도 위를 터치한 경우 처리
            }

            override fun onMapViewDoubleTapped(mapView: MapView, mapPoint: MapPoint) {
                // 지도를 더블 터치한 경우 처리
            }

            override fun onMapViewLongPressed(mapView: MapView, mapPoint: MapPoint) {
                // 지도를 길게 누른 경우 처리
            }

            override fun onMapViewDragStarted(mapView: MapView, mapPoint: MapPoint) {
                // 지도 드래그 시작 시 처리
            }

            override fun onMapViewDragEnded(mapView: MapView, mapPoint: MapPoint) {
                // 지도 드래그 종료 시 처리
            }

            override fun onMapViewMoveFinished(mapView: MapView, mapPoint: MapPoint) {
                // 지도 이동 완료 시 처리
            }
        })

        // 현재 위치 이벤트 리스너 등록
        binding.mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
        binding.mapView.setShowCurrentLocationMarker(true)
        binding.mapView.setCurrentLocationEventListener(this)

        // 현재 위치 로딩 시작
        binding.mapView.currentLocationTrackingMode =
            MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving
        binding.mapView.setShowCurrentLocationMarker(true)
        binding.mapView.setCurrentLocationEventListener(this)

        // 초기 지도 위치 설정
        val initialLatitude = 37.810694
        val initialLongitude = 127.070980
        val initialMapPoint = MapPoint.mapPointWithGeoCoord(initialLatitude, initialLongitude)
        binding.mapView.setMapCenterPoint(initialMapPoint, true)

        // 현재 위치를 기반으로 CU 편의점 검색
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ContextCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            lastKnownLocation?.let { location ->
                val latitude = location.latitude
                val longitude = location.longitude
                // CU 편의점 주변 검색
                searchKeyword("양주 경동대 CU", 1)
            }
        }
    }

    // 키워드 검색 함수
    private fun searchKeyword(keyword: String, page: Int) {
        // 기본적으로는 입력된 키워드를 사용하고, "cu" 키워드를 추가하여 검색 쿼리를 생성
        val searchQuery = "$keyword cu"

        val retrofit = Retrofit.Builder() // Retrofit 구성
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(/* service = */ KakaoAPI::class.java) // 통신 인터페이스를 객체로 생성
        val call = api.getSearchKeyword(API_KEY, searchQuery, page) // 검색 조건 입력

        // API 서버에 요청
        call.enqueue(object : Callback<ResultSearchKeyword> {
            override fun onResponse(
                call: Call<ResultSearchKeyword>,
                response: Response<ResultSearchKeyword>
            ) {
                // 통신 성공
                addItemsAndMarkers(response.body())
            }

            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                // 통신 실패
                Log.w("LocalSearch", "통신 실패: ${t.message}")
            }
        })
    }

    // 검색 결과 처리 함수
    private fun addItemsAndMarkers(searchResult: ResultSearchKeyword?) {
        if (!searchResult?.documents.isNullOrEmpty()) {
            // 검색 결과 있음
            listItems.clear() // 리스트 초기화
            binding.mapView.removeAllPOIItems() // 지도의 마커 모두 제거
            for (document in searchResult!!.documents) {
                // 결과를 리사이클러 뷰에 추가
                val item = ListLayout(
                    document.place_name,
                    document.road_address_name,
                    document.address_name,
                    document.x.toDouble(),
                    document.y.toDouble()
                )
                listItems.add(item)

                // 지도에 마커 추가
                val point = MapPOIItem()
                point.apply {
                    itemName = document.place_name
                    mapPoint = MapPoint.mapPointWithGeoCoord(
                        document.y.toDouble(),
                        document.x.toDouble()
                    )
                    markerType = MapPOIItem.MarkerType.CustomImage
                    customImageResourceId = customMarkerResourceId
                }
                binding.mapView.addPOIItem(point)
            }
            listAdapter.notifyDataSetChanged()

            binding.btnNextPage.isEnabled = !searchResult.meta.is_end // 페이지가 더 있을 경우 다음 버튼 활성화
            binding.btnPrevPage.isEnabled = pageNumber != 1 // 1페이지가 아닐 경우 이전 버튼 활성화

            // 첫 번째 검색 결과로 지도 이동
            if (listItems.isNotEmpty()) {
                val firstItem = listItems.first()
                val mapPoint = MapPoint.mapPointWithGeoCoord(firstItem.y, firstItem.x)
                binding.mapView.setMapCenterPointAndZoomLevel(mapPoint, 1, true)
            }

        } else {
            // 검색 결과 없음
            Toast.makeText(requireContext(), "검색 결과가 없습니다", Toast.LENGTH_SHORT).show()
        }
    }

    // 현재 위치 업데이트 처리
    override fun onCurrentLocationUpdate(mapView: MapView, mapPoint: MapPoint, v: Float) {
        // 구현 필요
    }

    // 현재 위치 디바이스 방향 업데이트 처리
    override fun onCurrentLocationDeviceHeadingUpdate(mapView: MapView, v: Float) {
        // 구현 필요
    }

    // 현재 위치 업데이트 실패 처리
    override fun onCurrentLocationUpdateFailed(mapView: MapView) {
        // 구현 필요
    }

    // 현재 위치 업데이트 취소 처리
    override fun onCurrentLocationUpdateCancelled(mapView: MapView) {
        // 구현 필요
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
