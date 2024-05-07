package com.example.csi

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.csi.databinding.ActivityMapBinding
import com.google.android.gms.location.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MapActivity : AppCompatActivity(), MapView.CurrentLocationEventListener {
    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK a70645bcfd024ce9073200946d6966ad" // REST API 키
        private const val PERMISSION_REQUEST_CODE = 1001
    }

    private lateinit var binding: ActivityMapBinding
    private val listItems = arrayListOf<ListLayout>() // 리사이클러 뷰 아이템
    private val listAdapter = ListAdapter(listItems) // 리사이클러 뷰 어댑터
    private var pageNumber = 1 // 검색 페이지 번호
    private var keyword = "" // 검색 키워드

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // 위치 설정 확인
        checkLocationSettings()

        // 퓨즈드 위치 클라이언트 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // 위치 권한 확인 및 요청
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            // 권한이 부여되지 않았다면 요청
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE
            )
        } else {
            // 권한이 부여되었으면 위치 업데이트 시작
            startLocationUpdates()
        }

        // 리사이클러 뷰
        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvList.adapter = listAdapter

        // 리스트 아이템 클릭 시 해당 위치로 이동
        listAdapter.setItemClickListener(object : ListAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                val selectedItem = listItems[position]
                moveMapToLocation(selectedItem.y, selectedItem.x)
            }
        })

        // 검색 버튼 클릭 시 이벤트 처리
        binding.imagebtnSearch.setOnClickListener {
            keyword = binding.etSearchField.text.toString()
            pageNumber = 1
            searchAndMoveToFirstResult(keyword, pageNumber) // 검색 및 결과 이동 함수 호출
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

        // 초기 지도 위치 설정
        val initialLatitude = 37.810694
        val initialLongitude = 127.070980
        val initialMapPoint = MapPoint.mapPointWithGeoCoord(initialLatitude, initialLongitude)
        binding.mapView.setMapCenterPoint(initialMapPoint, true)

        // 자동으로 현재 위치 주변의 CU 편의점을 표시
        displayCUNearby()
    }

    private fun searchAndMoveToFirstResult(keyword: String, pageNumber: Int) {
        // 키워드 검색 함수 호출
        searchKeyword(keyword, pageNumber)
    }

    private fun checkLocationSettings() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // GPS가 꺼져 있음
            AlertDialog.Builder(this)
                .setTitle("위치 설정")
                .setMessage("위치 서비스가 꺼져 있습니다. 위치 설정을 켜시겠습니까?")
                .setPositiveButton("예") { _, _ ->
                    // 사용자가 예를 선택한 경우 위치 설정 화면으로 이동
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                .setNegativeButton("아니오") { dialog, _ ->
                    // 사용자가 아니오를 선택한 경우 다이얼로그 닫기
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000 // 업데이트 간격 (밀리초 단위)
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                p0.lastLocation?.let { location ->
                    // 위치 업데이트 처리
                    val mapPoint = MapPoint.mapPointWithGeoCoord(location.latitude, location.longitude)
                    binding.mapView.setMapCenterPoint(mapPoint, true) // 현재 위치를 지도의 중심으로 설정
                }
            }
        }

        // 위치 업데이트 요청 전에 위치 권한이 있는지 확인
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED) {
            // 위치 권한이 있는 경우에만 위치 업데이트 요청
            fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper()) // Looper 지정
        }
    }

    private fun searchKeyword(keyword: String, page: Int) {
        // 기본적으로는 입력된 키워드를 사용하고, "cu" 키워드를 추가하여 검색 쿼리를 생성
        val searchQuery = "$keyword cu"

        val retrofit = Retrofit.Builder() // Retrofit 구성
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KakaoAPI::class.java) // 통신 인터페이스를 객체로 생성
        val call = api.getSearchKeyword(API_KEY, searchQuery, page) // 검색 조건 입력

        // API 서버에 요청
        call.enqueue(object : Callback<ResultSearchKeyword> {
            override fun onResponse(call: Call<ResultSearchKeyword>, response: Response<ResultSearchKeyword>) {
                // 통신 성공
                addItemsAndMarkers(response.body())
                // 검색 결과가 있으면 첫 번째 결과 항목의 위치로 지도 이동
                if (!response.body()?.documents.isNullOrEmpty()) {
                    val firstItem = response.body()?.documents?.first()
                    firstItem?.let {
                        moveMapToLocation(it.y.toDouble(), it.x.toDouble())
                    }
                }
            }

            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                // 통신 실패
                Log.w("LocalSearch", "통신 실패: ${t.message}")
            }
        })
    }

    private fun addItemsAndMarkers(searchResult: ResultSearchKeyword?) {
        if (!searchResult?.documents.isNullOrEmpty()) {
            // 검색 결과 있음
            listItems.clear() // 리스트 초기화
            binding.mapView.removeAllPOIItems() // 지도의 마커 모두 제거
            for (document in searchResult!!.documents) {
                // 결과를 리사이클러 뷰에 추가
                val item = ListLayout(document.place_name,
                    document.road_address_name,
                    document.address_name,
                    document.x.toDouble(),
                    document.y.toDouble())
                listItems.add(item)
                // 지도에 마커 추가
                val point = MapPOIItem()
                point.apply {
                    itemName = document.place_name
                    mapPoint = MapPoint.mapPointWithGeoCoord(document.y.toDouble(),
                        document.x.toDouble())
                    markerType = MapPOIItem.MarkerType.BluePin
                    selectedMarkerType = MapPOIItem.MarkerType.RedPin
                }
                binding.mapView.addPOIItem(point)
            }
            listAdapter.notifyDataSetChanged()

            binding.btnNextPage.isEnabled = !searchResult.meta.is_end // 페이지가 더 있을 경우 다음 버튼 활성화
            binding.btnPrevPage.isEnabled = pageNumber != 1 // 1페이지가 아닐 경우 이전 버튼 활성화

            // 첫 번째 결과 항목으로 지도 이동
            if (listItems.isNotEmpty()) {
                val firstItem = listItems.first()
                moveMapToLocation(firstItem.y, firstItem.x)
            }

        } else {
            // 검색 결과 없음
            Toast.makeText(this, "검색 결과가 없습니다", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCurrentLocationUpdate(mapView: MapView, mapPoint: MapPoint, v: Float) {
        // 현재 위치 업데이트 처리
    }

    override fun onCurrentLocationDeviceHeadingUpdate(mapView: MapView, v: Float) {
        // 현재 위치 디바이스 방향 업데이트 처리
    }

    override fun onCurrentLocationUpdateFailed(mapView: MapView) {
        // 현재 위치 업데이트 실패 처리
    }

    override fun onCurrentLocationUpdateCancelled(mapView: MapView) {
        // 현재 위치 업데이트 취소 처리
    }

    private fun moveMapToLocation(latitude: Double, longitude: Double) {
        // 클릭한 항목의 위치로 지도 이동
        val mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude)
        binding.mapView.setMapCenterPointAndZoomLevel(mapPoint, 1, true)
    }

    private fun displayCUNearby() {
        // 현재 위치를 기반으로 CU 편의점 검색
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ContextCompat.checkSelfPermission(this,
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
}
