package org.techtown.reviewapp.review;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;

import org.techtown.reviewapp.R;
import org.techtown.reviewapp.comment.Comment;
import org.techtown.reviewapp.comment.CommentAdapter;
import org.techtown.reviewapp.home.HomeActivity;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private ArrayList<Review> reviews = new ArrayList<Review>();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    Context context;
    static int view_num = 0;
    RecyclerView recyclerView;
    CommentAdapter adapter;

    public ReviewAdapter(Context context) { this.context = context; }

    @NonNull
    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView;

        switch(viewType){  //xml 파일 두 개 쓰는 방법
            case 0:  // 사진 없을 때
                itemView = inflater.inflate(R.layout.no_picture_status_item,parent,false);
                return new ReviewAdapter.ViewHolder(itemView);
            case 1:  // 사진 있을 때
                itemView = inflater.inflate(R.layout.review_item,parent,false);
                return new ReviewAdapter.ViewHolder(itemView);
        }

        itemView = inflater.inflate(R.layout.no_picture_status_item,parent,false);
        return new ReviewAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.setItem(review);
    }

    @Override
    public int getItemViewType(int position) {  //xml 파일 두 개 쓰는 방법
        Review review = reviews.get(position);
        if(review.photo_num>=1){
            return view_num = 1;
        } else{
            return view_num = 0;
        }
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView user_nickname, user_rank, restaurant, date, user_text, like;
        ImageView user_photos, profile_photo;
        //RecyclerView comments;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            user_nickname = itemView.findViewById(R.id.user_nickname);
            user_rank = itemView.findViewById(R.id.user_rank);
            restaurant = itemView.findViewById(R.id.restaurant);
            date = itemView.findViewById(R.id.date);
            user_text = itemView.findViewById(R.id.user_text);
            like = itemView.findViewById(R.id.like);
            recyclerView = itemView.findViewById(R.id.comments);
            recyclerView.setLayoutManager(new LinearLayoutManager((HomeActivity) HomeActivity.mContext,LinearLayoutManager.VERTICAL,false)) ;
            adapter = new CommentAdapter((HomeActivity)HomeActivity.mContext);

            profile_photo = itemView.findViewById(R.id.profile_photo);

            if(view_num == 1) {
                user_photos = itemView.findViewById(R.id.user_photos);
            }
        }

        public void setItem(Review review){

            user_nickname.setText(review.getUser_nickname());
            user_rank.setText(review.getUser_rank());
            restaurant.setText(review.getRestaurant());
            date.setText(review.getDate());
            user_text.setText(review.getUser_text());
            like.setText(Integer.toString(review.getLike()));

            for(int i=1 ; i<=review.comments.size();i++){
               adapter.addComment(review.comments.get(i-1));
            }

            recyclerView.setAdapter(adapter);
            if(view_num == 1){

            }
            //프로필사진
            /*
            String file_path = "profile_picture/profile_picture_"+review.getUser_id();
            StorageReference ref = storage.getReference().child(file_path);

            ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){

                        Glide.with(context).load(task.getResult()).into(profile_photo);
                    }
                }
            });*/

            //본문 사진
            //일단 한개만 받아옴
            /*
            String file_path2 = review.getUser_id() + "/"+ review.getDate();
            StorageReference ref2 = storage.getReference().child(file_path2);

            ref2.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){

                        Glide.with(context).load(task.getResult()).into(user_photos);
                    }
                }
            });*/
        }
    }

    public void addReview(Review review) {
        reviews.add(review);
    }

}
