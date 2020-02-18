package org.techtown.reviewapp.Restaurants.restaurant;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.techtown.reviewapp.R;
import org.techtown.reviewapp.Restaurants.RestaurantActivity;
import org.techtown.reviewapp.home.HomeActivity;
import org.techtown.reviewapp.post.Post;
import org.techtown.reviewapp.post.PostAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    FirebaseStorage storage = FirebaseStorage.getInstance();
    String restaurantName = ((RestaurantActivity)RestaurantActivity.mContext).name;
    //data
    public ArrayList<Review> items = new ArrayList<>();

    //viewType
    public int REVIEW = 0; //사진 있는 리뷰
    public int NOPHOTO = 1; //사진 없는 리뷰

    //context
    private final Context context;

    public ReviewAdapter(Context context) {
        this.context = context;
    }

    private class PhotoViewHolder extends RecyclerView.ViewHolder {
        TextView user_nickname, date, user_text;
        ImageView profile_photo;

        //Review, Status의 공통 요소
        TextView user_rank, like;
        ImageView like_button;
        Boolean liked = false;

        //Review의 요소
        ImageView user_photos;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            user_nickname = itemView.findViewById(R.id.user_nickname);
            date = itemView.findViewById(R.id.date);
            user_text = itemView.findViewById(R.id.user_text);
            user_rank = itemView.findViewById(R.id.user_rank);
            profile_photo = itemView.findViewById(R.id.profile_photo);
            like = itemView.findViewById(R.id.like);
            like_button = itemView.findViewById(R.id.imageView);
            user_photos = itemView.findViewById(R.id.user_photos);
        }

        private void bind(final int position) {
            int photo_size = items.get(position).photo.size();
            for(int i = 1 ; i <= photo_size ; i++){  // 사진 여러개 쓸 때 수정
                String file_path = items.get(position).photo.get(i-1);
                StorageReference ref = storage.getReference().child(file_path);
                ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            Glide.with(context).load(task.getResult()).into(user_photos);
                        }
                    }
                });
            }

            // 프로필 사진 적용
            Review review = items.get(position);
            String file_path1 = "SKKU/profile_picture/profile_picture_" + review.user_id;
            StorageReference ref1 = storage.getReference().child(file_path1);
            ref1.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Glide.with(context).load(task.getResult()).into(profile_photo);
                    }
                }
            });

            // Like 버튼 이미지 바인딩
            final String DB_num = review.getDB_num();
            DatabaseReference reference_like = FirebaseDatabase.getInstance().getReference().child("SKKU");
            reference_like.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Map<String, Object> message_i = (Map<String, Object>) dataSnapshot.child("Review").child(restaurantName).child(DB_num).getValue();
                    int like_num = Integer.parseInt(message_i.get("like").toString());

                    if(like_num >= 1){
                        Map<String, Object> message_who_liked = (Map<String, Object>) message_i.get("who_liked");
                        Set<String> keySet = message_who_liked.keySet();
                        Iterator<String> iterator = keySet.iterator();
                        while(iterator.hasNext()){
                            String id = iterator.next();
                            if(id.equals(restoreState())){
                                liked = true;
                                break;
                            }
                        }
                    }

                    if(liked == false){
                        like_button.setImageResource(R.drawable.no_like);
                    } else{
                        like_button.setImageResource(R.drawable.like);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            // Like 버튼 누를시 데이터베이스 업데이트 및 버튼 이미지 변경
            like_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(liked == false){
                        like_button.setImageResource(R.drawable.like);
                        Toast.makeText(context, "좋아요!", Toast.LENGTH_SHORT).show();
                        liked = true;

                        DatabaseReference reference_like = FirebaseDatabase.getInstance().getReference().child("SKKU");
                        reference_like.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                Map<String, Object> message_i = (Map<String, Object>) dataSnapshot.child("Review").child(restaurantName).child(DB_num).getValue();
                                int like_num = Integer.parseInt(message_i.get("like").toString());

                                // 좋아요 +1 업데이트
                                Review temp = items.get(position);
                                temp.setLike(like_num+1);
                                setItem(temp,position);
                                like.setText(temp.getLike()+"명이 좋아합니다.");

                                Map<String, Object> childUpdates1 = new HashMap<>();
                                Map<String, Object> childUpdates2 = new HashMap<>();


                                childUpdates1.put(temp.getDB_num()+"/like",temp.getLike());
                                childUpdates2.put(temp.getDB_num()+"/who_liked/"+restoreState(), restoreState());

                                DatabaseReference reference_upload = FirebaseDatabase.getInstance().getReference().child("SKKU").child("Review").child(restaurantName);
                                reference_upload.updateChildren(childUpdates1);
                                reference_upload.updateChildren(childUpdates2);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    } else{
                        like_button.setImageResource(R.drawable.no_like);
                        Toast.makeText(context, "좋아요 취소해요..", Toast.LENGTH_SHORT).show();
                        liked = false;

                        DatabaseReference reference_like = FirebaseDatabase.getInstance().getReference().child("SKKU");
                        reference_like.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                Map<String, Object> message_i = (Map<String, Object>) dataSnapshot.child("Review").child(restaurantName).child(DB_num).getValue();
                                int like_num = Integer.parseInt(message_i.get("like").toString());

                                // 좋아요 -1 업데이트
                                Review temp = items.get(position);
                                temp.setLike(like_num-1);
                                setItem(temp, position);
                                like.setText(temp.getLike()+"명이 좋아합니다.");

                                Map<String, Object> childUpdates1 = new HashMap<>();
                                Map<String, Object> childUpdates2 = new HashMap<>();
                                childUpdates1.put(temp.getDB_num()+"/like",temp.getLike());

                                DatabaseReference reference_upload = FirebaseDatabase.getInstance().getReference().child("SKKU").child("Review").child(restaurantName);
                                reference_upload.updateChildren(childUpdates1);
                                reference_upload.child(temp.DB_num+"/who_liked/"+restoreState()).setValue(null);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                }
            });

            user_nickname.setText(review.getUser_nickname());
            user_rank.setText(review.getUser_rank());
            date.setText(review.getDate());
            user_text.setText(review.getUser_text());
            like.setText(review.getLike() + "명이 좋아합니다.");
        }
    }

    private class NophotoViewHolder extends RecyclerView.ViewHolder {
        TextView user_nickname, date, user_text;
        ImageView profile_photo;

        //Review, Status의 공통 요소
        TextView user_rank, like;
        ImageView like_button;
        Boolean liked = false;

        public NophotoViewHolder(@NonNull View itemView) {
            super(itemView);
            user_nickname = itemView.findViewById(R.id.user_nickname);
            date = itemView.findViewById(R.id.date);
            user_text = itemView.findViewById(R.id.user_text);
            user_rank = itemView.findViewById(R.id.user_rank);
            profile_photo = itemView.findViewById(R.id.profile_photo);
            like = itemView.findViewById(R.id.like);
            like_button = itemView.findViewById(R.id.imageView);
        }

        private void bind(final int position) {
            // 프로필 사진 적용
            Review review = items.get(position);
            String file_path1 = "SKKU/profile_picture/profile_picture_" + review.user_id;
            StorageReference ref1 = storage.getReference().child(file_path1);
            ref1.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Glide.with(context).load(task.getResult()).into(profile_photo);
                    }
                }
            });

            // Like 버튼 이미지 바인딩
            final String DB_num = review.getDB_num();
            DatabaseReference reference_like = FirebaseDatabase.getInstance().getReference().child("SKKU");
            reference_like.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Map<String, Object> message_i = (Map<String, Object>) dataSnapshot.child("Review").child(restaurantName).child(DB_num).getValue();
                    int like_num = Integer.parseInt(message_i.get("like").toString());

                    if(like_num >= 1){
                        Map<String, Object> message_who_liked = (Map<String, Object>) message_i.get("who_liked");
                        Set<String> keySet = message_who_liked.keySet();
                        Iterator<String> iterator = keySet.iterator();
                        while(iterator.hasNext()){
                            String id = iterator.next();
                            if(id.equals(restoreState())){
                                liked = true;
                                break;
                            }
                        }
                    }

                    if(liked == false){
                        like_button.setImageResource(R.drawable.no_like);
                    } else{
                        like_button.setImageResource(R.drawable.like);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            // Like 버튼 누를시 데이터베이스 업데이트 및 버튼 이미지 변경
            like_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(liked == false){
                        like_button.setImageResource(R.drawable.like);
                        Toast.makeText(context, "좋아요!", Toast.LENGTH_SHORT).show();
                        liked = true;

                        DatabaseReference reference_like = FirebaseDatabase.getInstance().getReference().child("SKKU");
                        reference_like.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                Map<String, Object> message_i = (Map<String, Object>) dataSnapshot.child("Review").child(restaurantName).child(DB_num).getValue();
                                int like_num = Integer.parseInt(message_i.get("like").toString());

                                // 좋아요 +1 업데이트
                                Review temp = items.get(position);
                                temp.setLike(like_num+1);
                                setItem(temp,position);
                                like.setText(temp.getLike()+"명이 좋아합니다.");

                                Map<String, Object> childUpdates1 = new HashMap<>();
                                Map<String, Object> childUpdates2 = new HashMap<>();


                                childUpdates1.put(temp.getDB_num()+"/like",temp.getLike());
                                childUpdates2.put(temp.getDB_num()+"/who_liked/"+restoreState(), restoreState());

                                DatabaseReference reference_upload = FirebaseDatabase.getInstance().getReference().child("SKKU").child("Review").child(restaurantName);
                                reference_upload.updateChildren(childUpdates1);
                                reference_upload.updateChildren(childUpdates2);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    } else{
                        like_button.setImageResource(R.drawable.no_like);
                        Toast.makeText(context, "좋아요 취소해요..", Toast.LENGTH_SHORT).show();
                        liked = false;

                        DatabaseReference reference_like = FirebaseDatabase.getInstance().getReference().child("SKKU");
                        reference_like.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                Map<String, Object> message_i = (Map<String, Object>) dataSnapshot.child("Review").child(restaurantName).child(DB_num).getValue();
                                int like_num = Integer.parseInt(message_i.get("like").toString());

                                // 좋아요 -1 업데이트
                                Review temp = items.get(position);
                                temp.setLike(like_num-1);
                                setItem(temp, position);
                                like.setText(temp.getLike()+"명이 좋아합니다.");

                                Map<String, Object> childUpdates1 = new HashMap<>();
                                Map<String, Object> childUpdates2 = new HashMap<>();
                                childUpdates1.put(temp.getDB_num()+"/like",temp.getLike());

                                DatabaseReference reference_upload = FirebaseDatabase.getInstance().getReference().child("SKKU").child("Review").child(restaurantName);
                                reference_upload.updateChildren(childUpdates1);
                                reference_upload.child(temp.DB_num+"/who_liked/"+restoreState()).setValue(null);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                }
            });

            user_nickname.setText(review.getUser_nickname());
            user_rank.setText(review.getUser_rank());
            date.setText(review.getDate());
            user_text.setText(review.getUser_text());
            like.setText(review.getLike() + "명이 좋아합니다.");
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == REVIEW) {
            return new ReviewAdapter.PhotoViewHolder(LayoutInflater.from(context).inflate(R.layout.photo_item, parent,false));
        } else {
            return new ReviewAdapter.NophotoViewHolder(LayoutInflater.from(context).inflate(R.layout.no_photo_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = items.get(position).type;
        if(viewType == REVIEW) {
            ((PhotoViewHolder) holder).bind(position);
        } else {
            ((NophotoViewHolder) holder).bind(position);
        }
    }

    @Override
    public int getItemViewType(int position) {  //xml 파일 두 개 쓰는 방법
        return items.get(position).type;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public String getLast_DB_num() {
        return items.get(items.size() - 1).getDB_num();
    }

    public void addItem(Review review) {
        items.add(review);
    }

    public void setItem(Review review ,int index){
        items.set(index, review);
    }

    public String restoreState() {
        SharedPreferences pref = ((HomeActivity)HomeActivity.mContext).getSharedPreferences("pref", Activity.MODE_PRIVATE);
        return pref.getString("id","");
    }
}
