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
import android.widget.Toast;

import org.techtown.reviewapp.R;


public class CommentOptionFragment extends Fragment {
    CommentOptionFragment commentOptionFragment;
    FrameLayout root;
    TextView cancle, report, delete, edit;
    String DB_num;

    public CommentOptionFragment(String DB_num) {
        // Required empty public constructor
        this.DB_num = DB_num;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_comment_option, container, false);
        //애니메이션 끝난뒤에 화면 불투명하게 만들기
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rootView.setBackgroundColor(0x378C92AC);
            }
        }, 380);

        commentOptionFragment = this;
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
                        .remove(commentOptionFragment)
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
                        .remove(commentOptionFragment)
                        .commit();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rootView.setBackgroundColor(Color.WHITE);
                rootView.setBackgroundColor(Color.TRANSPARENT);
                ((HomeActivity) HomeActivity.mContext)
                        .manager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.anim_slide_out_bottom, R.anim.anim_slide_out_bottom)
                        .remove(commentOptionFragment)
                        .commit();
                ((HomeActivity) HomeActivity.mContext)
                        .manager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.anim_appear_from_bottom, R.anim.anim_appear_from_bottom)
                        .add(R.id.frameLayout,new NotionFragment(DB_num, "", true)) //게시물의 DB번호를 아직 모름ㅜㅜ
                        .commit();
            }
        });

        return rootView;
    }

}
