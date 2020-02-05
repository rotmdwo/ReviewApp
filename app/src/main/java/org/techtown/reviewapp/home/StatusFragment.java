package org.techtown.reviewapp.home;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.techtown.reviewapp.R;


public class StatusFragment extends Fragment {
    StatusFragment statusFragment;
    EditText editText;
    TextView textView;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference ref = storage.getReference().child("status_picture");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_status, container, false);
        statusFragment = this;

        editText = rootView.findViewById(R.id.editText);

        Button button = rootView.findViewById(R.id.button); // 취소버튼
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) HomeActivity.mContext).manager.beginTransaction().remove(statusFragment).commit();  // 프래그먼트 자기자신 보이지 않는 법
            }
        });

        Button button2 = rootView.findViewById(R.id.button); // 등록버튼
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Button button3 = rootView.findViewById(R.id.button); // 사진첨부 버튼
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return rootView;
    }

}
