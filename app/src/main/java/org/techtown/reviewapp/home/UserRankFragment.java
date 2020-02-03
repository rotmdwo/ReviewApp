package org.techtown.reviewapp.home;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.techtown.reviewapp.R;

public class UserRankFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final ViewGroup rootView =  (ViewGroup) inflater.inflate(R.layout.fragment_user_rank, container, false);


        return rootView;
    }

}
