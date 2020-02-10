package org.techtown.reviewapp.home;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.techtown.reviewapp.R;

public class PostOptionFragment extends Fragment {
    PostOptionFragment postOptionFragment;
    FrameLayout root;
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
        root = rootView.findViewById(R.id.root);
        cancle = rootView.findViewById(R.id.cancle);
        report = rootView.findViewById(R.id.report);
        delete = rootView.findViewById(R.id.delete);
        edit = rootView.findViewById(R.id.edit);

        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((HomeActivity) HomeActivity.mContext)
                        .manager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.anim_slide_out_bottom, R.anim.anim_slide_out_bottom)
                        .remove(postOptionFragment)
                        .commit();
            }
        });

        cancle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ((HomeActivity) HomeActivity.mContext)
                        .manager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.anim_slide_out_bottom, R.anim.anim_slide_out_bottom)
                        .remove(postOptionFragment)
                        .commit();
            }
        });

        return rootView;
    }

}
