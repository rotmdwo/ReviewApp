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


public class CommentOptionFragment extends Fragment {
    CommentOptionFragment commentOptionFragment;
    FrameLayout root;
    TextView cancle, report, delete, edit;

    public CommentOptionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_comment_option, container, false);
        commentOptionFragment = this;
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
                        .remove(commentOptionFragment)
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
                        .remove(commentOptionFragment)
                        .commit();
            }
        });

        return rootView;
    }

}
