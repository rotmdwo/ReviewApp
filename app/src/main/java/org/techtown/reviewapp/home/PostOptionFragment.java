package org.techtown.reviewapp.home;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.techtown.reviewapp.R;

public class PostOptionFragment extends Fragment {
    PostOptionFragment postOptionFragment;
    TextView cancle, report, delete, edit;

    public PostOptionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_post_option, container, false);
        postOptionFragment = this;
        Log.d("postoption", "3");
        cancle = rootView.findViewById(R.id.cancle);
        report = rootView.findViewById(R.id.report);
        delete = rootView.findViewById(R.id.delete);
        edit = rootView.findViewById(R.id.edit);

        cancle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ((HomeActivity) HomeActivity.mContext).manager.beginTransaction().remove(postOptionFragment).commit();
            }
        });

        return rootView;
    }

}
