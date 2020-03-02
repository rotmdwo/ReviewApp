package org.techtown.reviewapp.Restaurants.restaurant;


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
import org.techtown.reviewapp.Restaurants.RestaurantActivity;

public class InfoFragment extends Fragment{
    SupportMapFragment mapFragment;
    GoogleMap map;
    MarkerOptions markerOptions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_info, container, false);

        // 프래그먼트에서 구글맵 쓸 때 getChildFragmentManager() 써야함 --> https://stackoverflow.com/questions/54992812/map-fragmentkotlin-onmapready-not-working
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                try{
                    MapsInitializer.initialize(getContext());
                } catch (Exception e){
                    e.printStackTrace();
                }

                LatLng location = new LatLng(((RestaurantActivity)RestaurantActivity.mContext).latitude,((RestaurantActivity)RestaurantActivity.mContext).longitude);


                markerOptions = new MarkerOptions();
                markerOptions.position(location);
                markerOptions.title(((RestaurantActivity)RestaurantActivity.mContext).name);
                map.addMarker(markerOptions);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(location,18));
            }
        });



        return rootView;
    }
}
