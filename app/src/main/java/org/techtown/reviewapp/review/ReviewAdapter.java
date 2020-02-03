package org.techtown.reviewapp.review;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.techtown.reviewapp.R;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private ArrayList<Review> reviews = new ArrayList<>();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    Context context;

    public ReviewAdapter(Context context) { this.context = context; }

    @NonNull
    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.review_item,parent,false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.setItem(review);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView user_nickname, user_rank, restaurant, date, user_text, like;
        //ImageView user_photos, profile_photo;
        //RecyclerView comments;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            user_nickname = itemView.findViewById(R.id.user_nickname);
            user_rank = itemView.findViewById(R.id.user_rank);
            restaurant = itemView.findViewById(R.id.profile_photo);
            date = itemView.findViewById(R.id.date);
            user_text = itemView.findViewById(R.id.user_text);
            like = itemView.findViewById(R.id.like);

            //user_photos = itemView.findViewById(R.id.user_photos);
            //profile_photo = itemView.findViewById(R.id.profile_photo);
        }

        public void setItem(Review review){
            user_nickname.setText(review.getUser_nickname());
            user_rank.setText(review.getUser_rank());
            restaurant.setText(review.getRestaurant());
            date.setText(review.getDate());
            user_text.setText(review.getUser_text());
            like.setText(review.getLike());

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
