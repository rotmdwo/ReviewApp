package org.techtown.reviewapp.Restaurants;


import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.techtown.reviewapp.R;


public class RestaurantFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_restaurant, container, false);

        ConstraintLayout honbap = rootView.findViewById(R.id.honbap);
        honbap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),RestaurantListActivity.class);
                intent.putExtra("category_kor","혼밥");
                intent.putExtra("category_eng","honbap");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_not_move);
            }
        });

        ConstraintLayout buffet = rootView.findViewById(R.id.buffet);
        buffet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),RestaurantListActivity.class);
                intent.putExtra("category_kor","무한리필");
                intent.putExtra("category_eng","buffet");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_not_move);
            }
        });

        ConstraintLayout chinese = rootView.findViewById(R.id.chinese);
        chinese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),RestaurantListActivity.class);
                intent.putExtra("category_kor","중식");
                intent.putExtra("category_eng","chinese");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_not_move);
            }
        });

        return rootView;
    }

}
