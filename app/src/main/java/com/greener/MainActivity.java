package com.greener;

import androidx.annotation.NonNull;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, Overlay.OnClickListener {

    private MapView mapView;
    private NaverMap mNaverMap;
    private FusedLocationSource mLocationSource;
    private InfoWindow mInfoWindow;

    private BottomNavigationView bottomNavigationView;
    private FragmentManager manager;
    private FragmentTransaction transaction;

    private ShopActivity shop_main;
    private HotelActivity hotel_main;
    private LikedActivity liked_main;
    private MediaActivity media_main;
    private SettingActivity setting_main;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //네이버 지도
        mapView = (MapView) findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // 위치를 반환하는 구현체인 FusedLocationSource 생성
        mLocationSource = new FusedLocationSource(this, 100);

        //타이틀 바 없애기
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //화면 전환
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.shop_menu:
                        setFragment(0);
                        break;
                    case R.id.hotel_menu:
                        setFragment(1);
                        break;
                    case R.id.liked_menu:
                        setFragment(2);
                        break;
                    case R.id.media_menu:
                        setFragment(3);
                        break;
                    case R.id.setting_menu:
                        setFragment(4);
                        break;
                }
                return false;
            }
        });
        shop_main = new ShopActivity();
        hotel_main = new HotelActivity();
        liked_main = new LikedActivity();
        media_main = new MediaActivity();
        setting_main = new SettingActivity();
        setFragment(0);
    }


    @Override
    public void onMapReady(@NonNull NaverMap naverMap)
    {
        // 지도상에 마커 표시
        Marker marker = new Marker(); // 마커 생성
        marker.setPosition(new LatLng(37.5670135, 126.9783740)); // 마커 위치 찍기
        marker.setMap(naverMap); // 마커 지도에 넣기

        // 커스텀 마커
        marker.setWidth(100);
        marker.setHeight(100);
        marker.setIcon(OverlayImage.fromResource(R.drawable.ic_place_marker)); // 마커 이미지 넣기
        marker.setOnClickListener(this); // 마커 click listener 등록

        // NaverMap 객체 받아서 NaverMap 객체에 위치 소스 지정
        mNaverMap = naverMap;
        mNaverMap.setLocationSource(mLocationSource);

        UiSettings uiSettings = mNaverMap.getUiSettings();
        uiSettings.setCompassEnabled(false); // 기본값 : true
        uiSettings.setLocationButtonEnabled(true); // 기본값 : false

        mInfoWindow = new InfoWindow();

        mInfoWindow.setAdapter(new InfoWindow.DefaultViewAdapter(this){
            @NonNull
            @Override
            protected View getContentView(@NonNull InfoWindow infoWindow) {
                Marker marker = infoWindow.getMarker();
                PlaceInfo info = (PlaceInfo) marker.getTag();
                View view = View.inflate(MainActivity.this, R.layout.view_info_window, null);
                ((TextView) view.findViewById(R.id.title)).setText("Wooa Studio");
                ((TextView) view.findViewById(R.id.details)).setText("Info Window 테스트");
                return view;
            }
        });
    }

    // Life Cycle handling

    @Override
    public void onStart()
    {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public boolean onClick(@NonNull Overlay overlay) {

        if (overlay instanceof Marker) {
            Marker marker = (Marker) overlay;
            if (marker.getInfoWindow() != null) {
                mInfoWindow.close();
                Toast.makeText(this.getApplicationContext(), "InfoWindow Close.", Toast.LENGTH_LONG).show();
            }
            else {
                mInfoWindow.open(marker);
                Toast.makeText(this.getApplicationContext(), "InfoWindow Open.", Toast.LENGTH_LONG).show();
            }
            return true;
        }


        return false;
    }

    private void setFragment(int n){
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        switch(n){
            case 0:
                transaction.replace(R.id.main_frame, shop_main);
                transaction.commit();   //save
                break;
            case 1:
                transaction.replace(R.id.main_frame, hotel_main);
                transaction.commit();
                break;
            case 2:
                transaction.replace(R.id.main_frame, liked_main);
                transaction.commit();
                break;
            case 3:
                transaction.replace(R.id.main_frame, media_main);
                transaction.commit();
                break;
            case 4:
                transaction.replace(R.id.main_frame, setting_main);
                transaction.commit();
                break;
        }
    }
}