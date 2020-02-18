package org.techtown.reviewapp.home;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.techtown.reviewapp.R;
import org.techtown.reviewapp.post.Post;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotionFragment extends Fragment {
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("SKKU").child("Status");
    NotionFragment notionFragment;
    String DB_num, post_DB_num;
    boolean isComment;
    TextView yes, no;

    //Comment를 삭제하려는 경우, isComment는 true임
    public NotionFragment(String DB_num, String post_DB_num, boolean isComment) {
        // Required empty public constructor
        this.DB_num = DB_num;
        this.post_DB_num = post_DB_num;
        this.isComment = isComment;
    }

    //Status나 Review를 삭제하려는 경우, isComment는 false임
    public NotionFragment(String DB_num, boolean isComment) {
        this.DB_num = DB_num;
        this.isComment = isComment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_notion, container, false);
        notionFragment = this;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rootView.setBackgroundColor(0x378C92AC);
            }
        }, 110);

        //((HomeFragment)HomeFragment.mContext).postAdapter;

        yes = rootView.findViewById(R.id.yes);
        no = rootView.findViewById(R.id.no);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 비활성화 되었던 네비게이션 버튼 재활성화
                ((HomeActivity) HomeActivity.mContext).home.setEnabled(true);
                ((HomeActivity) HomeActivity.mContext).restaurant.setEnabled(true);
                ((HomeActivity) HomeActivity.mContext).settings.setEnabled(true);
                ((HomeActivity) HomeActivity.mContext).rank.setEnabled(true);
                ((HomeActivity) HomeActivity.mContext)
                        .manager
                        .beginTransaction()
                        .remove(notionFragment)
                        .commit();

                //디비 삭제하기
                if(isComment) { //댓글임 -> DB_num, post_DB_num 필요
                    Map<String, Object> childUpdates1 = new HashMap<>();
                    childUpdates1.put(post_DB_num +"/comments/"+ DB_num , null);
                    Log.d("debug", "부모: " + post_DB_num + " 댓글: " + DB_num);
                    reference.updateChildren(childUpdates1);

                } else { //post임 -> DB_num만 필요
                    Map<String, Object> childUpdates1 = new HashMap<>();
                    childUpdates1.put(DB_num ,null);
                    //reference.updateChildren(childUpdates1);
                }
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 비활성화 되었던 네비게이션 버튼 재활성화
                ((HomeActivity) HomeActivity.mContext).home.setEnabled(true);
                ((HomeActivity) HomeActivity.mContext).restaurant.setEnabled(true);
                ((HomeActivity) HomeActivity.mContext).settings.setEnabled(true);
                ((HomeActivity) HomeActivity.mContext).rank.setEnabled(true);
                ((HomeActivity) HomeActivity.mContext)
                        .manager
                        .beginTransaction()
                        .remove(notionFragment)
                        .commit();
            }
        });

        return rootView;
    }

}
