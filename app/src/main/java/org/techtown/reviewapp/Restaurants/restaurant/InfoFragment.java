package org.techtown.reviewapp.Restaurants.restaurant;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.techtown.reviewapp.R;
import org.techtown.reviewapp.Restaurants.Restaurant;
import org.techtown.reviewapp.Restaurants.RestaurantActivity;

public class InfoFragment extends Fragment{
    RestaurantMap mapFragment;
    GoogleMap map;
    MarkerOptions markerOptions;
    Context mContext;

    // static을 사용하지 않고 다른 액티비티의 context 사용하는 법
    public InfoFragment(Context mContext){
        this.mContext = mContext;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_info, container, false);

        // 프래그먼트에서 구글맵 쓸 때 getChildFragmentManager() 써야함 --> https://stackoverflow.com/questions/54992812/map-fragmentkotlin-onmapready-not-working
        mapFragment = (RestaurantMap) getChildFragmentManager().findFragmentById(R.id.map);

        // 스크롤뷰 안에서 구글맵 터치할 시 스크롤뷰의 영향 받지 않고 온전히 구글맵에 터치되게 함. (RestaurantMap class까지 만들어야 함.)
        mapFragment.setListener(new RestaurantMap.OnTouchListener(){
           @Override
           public void onTouch(){
               ((RestaurantActivity) mContext).nestedScrollView.requestDisallowInterceptTouchEvent(true);
           }
        });
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                try{
                    MapsInitializer.initialize(getContext());
                } catch (Exception e){
                    e.printStackTrace();
                }

                LatLng location = new LatLng(((RestaurantActivity) mContext).latitude,((RestaurantActivity) mContext).longitude);


                markerOptions = new MarkerOptions();
                markerOptions.position(location);
                markerOptions.title(((RestaurantActivity) mContext).name);
                map.addMarker(markerOptions);
                map.setMyLocationEnabled(true);
                map.setBuildingsEnabled(true);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(location,18));
            }
        });



        return rootView;
    }
}
