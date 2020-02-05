package org.techtown.reviewapp.UserRank;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



import org.techtown.reviewapp.R;


import java.util.ArrayList;

public class UserRankAdapter extends RecyclerView.Adapter<UserRankAdapter.ViewHolder> {
    private ArrayList<UserRank> items = new ArrayList<>();
    Context context;

    public UserRankAdapter(Context context) { // 생성자를 만들 때 Context를 안 넘겨주면 프래그먼트 나갔다가 들어올 때 마다 리사이클러뷰에 중복으로 쌓이는 버그 생김
        this.context = context;
    }

    @NonNull
    @Override
    public UserRankAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView;

        itemView = inflater.inflate(R.layout.user_rank_item,parent,false);
        return new UserRankAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserRankAdapter.ViewHolder holder, int position) {
        UserRank review = items.get(position);
        holder.setItem(review);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView, textView2,textView3;
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
            textView2 = itemView.findViewById(R.id.textView2);
            textView3 = itemView.findViewById(R.id.textView3);
            progressBar = itemView.findViewById(R.id.progressBar);
        }

        public void setItem(UserRank userRank) {
            textView.setText(userRank.getNickname());
            textView2.setText("초보자");
            textView3.setText(Integer.toString(userRank.getExp()));
            if(userRank.getExp() == 0){
                progressBar.setProgress(0);
            } else{
                progressBar.setProgress(100*userRank.getExp()/items.get(0).exp);
            }
        }

    }

    public void addUserRank(UserRank userRank) {
        items.add(userRank);
    }
}
