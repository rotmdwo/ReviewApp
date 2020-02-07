package org.techtown.reviewapp.home;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.techtown.reviewapp.R;
import org.techtown.reviewapp.UserRank.UserRank;
import org.techtown.reviewapp.UserRank.UserRankAdapter;

import java.util.Arrays;
import java.util.Map;

public class UserRankFragment extends Fragment {
    RecyclerView recyclerView;
    UserRankAdapter userRankAdapter;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("SKKU");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final ViewGroup rootView =  (ViewGroup) inflater.inflate(R.layout.fragment_user_rank, container, false);

        userRankAdapter  = new UserRankAdapter(getContext());  // 생성자를 만들 때 Context를 안 넘겨주면 프래그먼트 나갔다가 들어올 때 마다 리사이클러뷰에 중복으로 쌓이는 버그 생김
        recyclerView = rootView.findViewById(R.id.recyclerView) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false)) ;
        reference.addListenerForSingleValueEvent(dataListener);

        return rootView;
    }

    ValueEventListener dataListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Map<String, Object> message_SKKU = (Map<String, Object>) dataSnapshot.getValue();
            Map<String, Object> message_user = (Map<String, Object>) message_SKKU.get("user");
            int user_num = Integer.parseInt(message_user.get("num").toString());

            UserRank[] array = new UserRank[user_num];

            for(int i=1 ; i <=user_num ; i++){
                Map<String, Object> message_i = (Map<String, Object>) message_user.get(Integer.toString(i));
                array[i-1] = new UserRank((String)message_i.get("id"),(String)message_i.get("nickname"),Integer.parseInt(message_i.get("exp").toString()),Integer.parseInt(message_i.get("num_of_reviews").toString()));
                //arrayList.add(new UserRank((String)message_i.get("id"),(String)message_i.get("nickname"),Integer.parseInt(message_i.get("exp").toString()),Integer.parseInt(message_i.get("num_of_reviews").toString())));
            }

            Arrays.sort(array);

            if(user_num >= 10){
                for(int i = 0; i < 10 ; i++){
                    userRankAdapter.addUserRank(array[i]);
                }
            } else{
                for(int i=0;i<user_num;i++){
                    userRankAdapter.addUserRank(array[i]);
                }
            }

            recyclerView.setAdapter(userRankAdapter);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

}
