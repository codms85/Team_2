package com.greener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;

public class LikedMap extends Fragment implements OnMapReadyCallback {

    private GPSTracker gpsTracker;
    private MapView mapView;
    private NaverMap naverMap;
    private FusedLocationSource mLocationSource;
    private InfoWindow mInfoWindow;

    private DatabaseReference databaseReference;
    private FirebaseDatabase database;
    private View view;
    private Button btn;

    private OverlayImage image = OverlayImage.fromResource(R.drawable.ic_place_marker);

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mLocationSource = new FusedLocationSource(this, 100);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        System.out.println("Liked Map changed");
        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.liked_map, container, false);

        btn = (Button)view.findViewById(R.id.btn);

        mapView = (MapView)view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        mLocationSource = new FusedLocationSource(this, 100);

        database = FirebaseDatabase.getInstance();

        String path = "user/" + MainActivity.uid + "/저장";

        databaseReference = database.getReference(path); // DB 테이블 연결
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 파이어베이스 데이터베이스의 데이터를 받아오는 곳
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) { // 반복문으로 데이터 List를 추출해냄
                    StoreList LikedList = snapshot.getValue(StoreList.class); // 만들어뒀던 User 객체에 데이터를 담는다.

                    // System.out.println("x : "+LikedList.getX()+", y : "+LikedList.getY());

                    Double x = Double.parseDouble(LikedList.getX());
                    Double y = Double.parseDouble(LikedList.getY());
                    LatLng ll = new LatLng(x, y);
                    setmark(LikedList.getNameStr(), ll);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 디비를 가져오던중 에러 발생 시
                Log.e("TestActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gpsTracker = new GPSTracker(getContext());

                double latitude = gpsTracker.getLatitude();
                double longitude = gpsTracker.getLongitude();

                CameraPosition cameraPosition = new CameraPosition(new LatLng(latitude, longitude), 13);
                naverMap.setCameraPosition(cameraPosition);
            }
        });

        return view;
    }

    private void setmark(String name, LatLng latlng) {
        Marker marker = new Marker();
        marker.setPosition(latlng); // 마커 위치 찍기
        marker.setWidth(100);
        marker.setHeight(100);
        marker.setIcon(image); // 마커 이미지 넣기
        marker.setCaptionText(name);

        marker.setOnClickListener(new Overlay.OnClickListener(){
            @Override
            public boolean onClick(@NonNull Overlay overlay) {
                mInfoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(getContext())
                {
                    @NonNull
                    @Override
                    public CharSequence getText(@NonNull InfoWindow infoWindow)
                    {
                        return name;
                    }
                });
                mInfoWindow.open(marker);
                return false;
            }
        });


        marker.setMap(naverMap);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        naverMap.setLocationSource(mLocationSource);

        mInfoWindow = new InfoWindow();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.actionbar_map_action, menu);
    }
}