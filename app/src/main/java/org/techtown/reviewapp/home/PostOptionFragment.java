package org.techtown.reviewapp.home;


import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
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
    String post_num_in_DB;

    public PostOptionFragment(String post_num_in_DB) {
        // Required empty public constructor
        this.post_num_in_DB=post_num_in_DB;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_post_option, container, false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rootView.setBackgroundColor(0x378C92AC);
            }
        }, 380);

        postOptionFragment = this;
        root = rootView.findViewById(R.id.root);
        cancle = rootView.findViewById(R.id.cancle);
        report = rootView.findViewById(R.id.report);
        delete = rootView.findViewById(R.id.delete);
        edit = rootView.findViewById(R.id.edit);

        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 비활성화 되었던 네비게이션 버튼 재활성화
                ((HomeActivity) HomeActivity.mContext).home.setEnabled(true);
                ((HomeActivity) HomeActivity.mContext).restaurant.setEnabled(true);
                ((HomeActivity) HomeActivity.mContext).settings.setEnabled(true);
                ((HomeActivity) HomeActivity.mContext).rank.setEnabled(true);
                rootView.setBackgroundColor(Color.WHITE);
                rootView.setBackgroundColor(Color.TRANSPARENT);
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
                // 비활성화 되었던 네비게이션 버튼 재활성화
                ((HomeActivity) HomeActivity.mContext).home.setEnabled(true);
                ((HomeActivity) HomeActivity.mContext).restaurant.setEnabled(true);
                ((HomeActivity) HomeActivity.mContext).settings.setEnabled(true);
                ((HomeActivity) HomeActivity.mContext).rank.setEnabled(true);
                rootView.setBackgroundColor(Color.WHITE);
                rootView.setBackgroundColor(Color.TRANSPARENT);
                ((HomeActivity) HomeActivity.mContext)
                        .manager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.anim_slide_out_bottom, R.anim.anim_slide_out_bottom)
                        .remove(postOptionFragment)
                        .commit();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) HomeActivity.mContext).
                        manager.beginTransaction().
                        setCustomAnimations(R.anim.anim_slide_in_bottom, R.anim.anim_slide_out_bottom).
                        remove(postOptionFragment).
                        add(R.id.frameLayout,new NotionFragment(post_num_in_DB,false)).
                        commit();
            }
        });

        return rootView;
    }

}
