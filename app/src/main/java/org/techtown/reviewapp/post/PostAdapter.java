package org.techtown.reviewapp.post;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.techtown.reviewapp.R;
import org.techtown.reviewapp.home.HomeActivity;
import org.techtown.reviewapp.home.HomeFragment;
import org.techtown.reviewapp.home.PostOptionFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //DB
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("SKKU").child("Status");
    int upload_pos, commentNum;
    String upload_text, upload_num;
    long lastComment_in_DB;

    //viewType
    public int REVIEW = 0; //사진 있는 리뷰
    public int STATUS = 1; //사진 없는 리뷰
    public int COMMENT = 2; //댓글

    //context
    private final Context context;

    //data
    public ArrayList<Post> posts = new ArrayList<>();

    InputMethodManager imm;

    public ItemAddListener itemAddListener;
    public PostOptionListener postOptionListener;

    public interface ItemAddListener {
        void itemAdded(long prev_num, int position, String DB_num);
    }

    public interface PostOptionListener {
        void optionTouched(String post_num_in_DB, Boolean isWriter);
        void commentTouched(int pos, String comment_num_in_DB, String parent_num_in_DB, Boolean isWriter);
    }

    //생성자
    public PostAdapter(Context context) {
        this.context = context;
        imm = (InputMethodManager)context.getSystemService(INPUT_METHOD_SERVICE);
    }

    private class CommentViewHolder extends RecyclerView.ViewHolder {
        //Review, Status, 댓글의 공통적인 요소
        TextView user_nickname, date, user_text;
        CircleImageView profile_photo;

        CommentViewHolder(@NonNull final View itemView) {
            super(itemView);

            user_nickname = itemView.findViewById(R.id.user_nickname);
            date = itemView.findViewById(R.id.date);
            user_text = itemView.findViewById(R.id.user_comment);

            profile_photo = itemView.findViewById(R.id.profile_photo);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int pos = getAdapterPosition();
                    if(postOptionListener != null) {
                        boolean isWriter = false;
                        if(restoreState().equals(posts.get(pos).user_id)) {
                            isWriter = true;
                        }
                        postOptionListener.commentTouched(pos, posts.get(pos).getDB_num(), posts.get(pos).getParent_DB_num(), isWriter);
                    }
                    return false;
                }
            });
        }

        private void bind(int position) {
            // This method will be called anytime a list post is created or update its data
            //Do your stuff here
            Post post = posts.get(position);
            user_nickname.setText(post.getUser_nickname());
            user_text.setText(post.getUser_text());

            // 프로필 사진 적용
            String file_path = "SKKU/profile_picture/profile_picture_" + post.getUser_id();
            StorageReference ref = storage.getReference().child(file_path);
            ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Glide.with(HomeActivity.mContext).load(task.getResult()).into(profile_photo);
                    }
                }
            });

            Date date1 = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
            String current_date = format.format(date1);
            String then_date = post.getDate();
            int current_year = Integer.parseInt(current_date.substring(0,4));
            int then_year = Integer.parseInt(then_date.substring(0,4));
            int current_month = Integer.parseInt(current_date.substring(5,7));
            int then_month = Integer.parseInt(then_date.substring(5,7));
            int current_day = Integer.parseInt(current_date.substring(8,10));
            int then_day = Integer.parseInt(then_date.substring(8,10));
            int current_hour = Integer.parseInt(current_date.substring(11,13));
            int then_hour = Integer.parseInt(then_date.substring(11,13));
            int current_minute = Integer.parseInt(current_date.substring(14,16));
            int then_minute = Integer.parseInt(then_date.substring(14,16));
            int current_second = Integer.parseInt(current_date.substring(17,19));
            int then_second = Integer.parseInt(then_date.substring(17,19));

            if((current_year-then_year>=2) || (current_year - then_year == 1 && current_month - then_month >= 0)){
                date.setText(current_year-then_year+"년 전");
            } else if(current_year - then_year == 1 && current_month - then_month < 0){
                date.setText(current_month + 12 - then_month+"달 전");
            } else if((current_month-then_month>=2) || (current_month - then_month == 1 && current_day - then_day >= 0)){
                date.setText(current_month-then_month+"달 전");
            } else if(current_month - then_month == 1 && current_day - then_day < 0){
                date.setText(current_day + 30 - then_day+"일 전");
            } else if((current_day-then_day>=2) || (current_day - then_day == 1 && current_hour - then_hour >= 0)){
                date.setText(current_day-then_day+"일 전");
            } else if(current_day - then_day == 1 && current_hour - then_hour < 0){
                date.setText(current_hour + 24 - then_hour+"시간 전");
            } else if((current_hour-then_hour>=2) || (current_hour - then_hour == 1 && current_minute - then_minute >= 0)){
                date.setText(current_hour-then_hour+"시간 전");
            } else if(current_hour - then_hour == 1 && current_minute - then_minute < 0){
                date.setText(current_minute + 60 - then_minute+"분 전");
            } else if((current_minute-then_minute>=2) || (current_minute - then_minute == 1 && current_second - then_second >= 0)){
                date.setText(current_minute-then_minute+"분 전");
            } else{
                date.setText("방금 전");
            }
        }
    }

    private class StatusViewHolder extends RecyclerView.ViewHolder {
        //Review, Status, 댓글의 공통적인 요소
        TextView user_nickname, date, user_text;
        ImageView profile_photo;

        //Review, Status의 공통 요소
        TextView user_rank, restaurant, like, comment_num;
        EditText input_comment;
        Button comment_upload;
        ImageView like_button, post_option;
        Boolean liked = false;

        StatusViewHolder(@NonNull final View itemView) {
            super(itemView);

            user_nickname = itemView.findViewById(R.id.user_nickname);
            user_rank = itemView.findViewById(R.id.user_rank);
            restaurant = itemView.findViewById(R.id.restaurant);
            date = itemView.findViewById(R.id.date);
            user_text = itemView.findViewById(R.id.user_text);
            like = itemView.findViewById(R.id.like);
            comment_num = itemView.findViewById(R.id.comment_num);
            input_comment = itemView.findViewById(R.id.input_comment);
            comment_upload = itemView.findViewById(R.id.comment_upload);
            post_option = itemView.findViewById(R.id.post_option);
            like_button = itemView.findViewById(R.id.imageView);

            profile_photo = itemView.findViewById(R.id.profile_photo);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    Log.d("debug", getAdapterPosition()+ "");
                }
            });

            post_option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    int pos = getAdapterPosition();
                    post_option.setImageResource(R.drawable.option_selected);
                    if(postOptionListener != null) {
                        boolean isWriter = false;
                        if(restoreState().equals(posts.get(pos).user_id)) {
                            isWriter = true;
                        }
                        postOptionListener.optionTouched(posts.get(pos).DB_num, isWriter);
                    }
                }
            });

            comment_upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION) {
                        imm.hideSoftInputFromWindow(input_comment.getWindowToken(), 0);
                        upload_num = posts.get(pos).DB_num;
                        upload_text = input_comment.getText().toString();
                        upload_pos = pos;
                        commentNum = posts.get(pos).getComment_num();
                        //자기 자신에 댓글이 1이상인지 본다.
                        //  1이상이면 lastComment_in_DB에 가장 최근에 쓴 댓글의 DB_num을 넘겨줘야해
                        //  아니면 그냥 -1을 넘긴다.
                        if(posts.get(pos).comment_num >= 1) {
                            lastComment_in_DB = Long.parseLong(posts.get(pos - 1).getDB_num());
                        } else {
                            lastComment_in_DB = -1;
                        }
                        if(upload_text.equals("")) {
                            Toast.makeText(context, "최소한 한 글자 이상 입력해주세요", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        reference.addListenerForSingleValueEvent(dataListener1);
                        input_comment.setText("");
                    }
                }
            });

        }

        private void bind(final int position){
            Post post = posts.get(position);
            user_nickname.setText(post.getUser_nickname());
            user_rank.setText(post.getUser_rank());
            if(post.getRestaurant().equals("NO")){
                restaurant.setText("");
            } else{
                restaurant.setText(post.getRestaurant());
            }

            // 프로필 사진 적용
            String file_path = "SKKU/profile_picture/profile_picture_" + post.getUser_id();
            StorageReference ref = storage.getReference().child(file_path);
            ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Glide.with(HomeActivity.mContext).load(task.getResult()).into(profile_photo);
                    }
                }
            });

            // Like 버튼 이미지 바인딩
            final String DB_num = post.getDB_num();

            DatabaseReference reference_like = FirebaseDatabase.getInstance().getReference().child("SKKU");
            reference_like.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Map<String, Object> message_i = (Map<String, Object>) dataSnapshot.child("Status").child(DB_num).getValue();
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

                                Map<String, Object> message_i = (Map<String, Object>) dataSnapshot.child("Status").child(DB_num).getValue();
                                int like_num = Integer.parseInt(message_i.get("like").toString());

                                // 좋아요 +1 업데이트
                                Post temp = posts.get(position);
                                temp.setLike(like_num+1);
                                setItem(temp,position);
                                like.setText(temp.getLike()+"명이 좋아합니다.");

                                Map<String, Object> childUpdates1 = new HashMap<>();
                                Map<String, Object> childUpdates2 = new HashMap<>();

                                childUpdates1.put(temp.getDB_num()+"/like",temp.getLike());
                                childUpdates2.put(temp.getDB_num()+"/who_liked/"+restoreState(), restoreState());

                                DatabaseReference reference_upload = FirebaseDatabase.getInstance().getReference().child("SKKU").child("Status");
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

                                Map<String, Object> message_i = (Map<String, Object>) dataSnapshot.child("Status").child(DB_num).getValue();
                                int like_num = Integer.parseInt(message_i.get("like").toString());

                                // 좋아요 -1 업데이트
                                Post temp = posts.get(position);
                                temp.setLike(like_num-1);
                                setItem(temp,position);
                                like.setText(temp.getLike()+"명이 좋아합니다.");

                                Map<String, Object> childUpdates1 = new HashMap<>();
                                Map<String, Object> childUpdates2 = new HashMap<>();

                                childUpdates1.put(temp.getDB_num()+"/like",temp.getLike());
                                DatabaseReference reference_upload = FirebaseDatabase.getInstance().getReference().child("SKKU").child("Status");
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

            Date date1 = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
            String current_date = format.format(date1);
            String then_date = post.getDate();
            int current_year = Integer.parseInt(current_date.substring(0,4));
            int then_year = Integer.parseInt(then_date.substring(0,4));
            int current_month = Integer.parseInt(current_date.substring(5,7));
            int then_month = Integer.parseInt(then_date.substring(5,7));
            int current_day = Integer.parseInt(current_date.substring(8,10));
            int then_day = Integer.parseInt(then_date.substring(8,10));
            int current_hour = Integer.parseInt(current_date.substring(11,13));
            int then_hour = Integer.parseInt(then_date.substring(11,13));
            int current_minute = Integer.parseInt(current_date.substring(14,16));
            int then_minute = Integer.parseInt(then_date.substring(14,16));
            int current_second = Integer.parseInt(current_date.substring(17,19));
            int then_second = Integer.parseInt(then_date.substring(17,19));

            if((current_year-then_year>=2) || (current_year - then_year == 1 && current_month - then_month >= 0)){
                date.setText(current_year-then_year+"년 전");
            } else if(current_year - then_year == 1 && current_month - then_month < 0){
                date.setText(current_month + 12 - then_month+"달 전");
            } else if((current_month-then_month>=2) || (current_month - then_month == 1 && current_day - then_day >= 0)){
                date.setText(current_month-then_month+"달 전");
            } else if(current_month - then_month == 1 && current_day - then_day < 0){
                date.setText(current_day + 30 - then_day+"일 전");
            } else if((current_day-then_day>=2) || (current_day - then_day == 1 && current_hour - then_hour >= 0)){
                date.setText(current_day-then_day+"일 전");
            } else if(current_day - then_day == 1 && current_hour - then_hour < 0){
                date.setText(current_hour + 24 - then_hour+"시간 전");
            } else if((current_hour-then_hour>=2) || (current_hour - then_hour == 1 && current_minute - then_minute >= 0)){
                date.setText(current_hour-then_hour+"시간 전");
            } else if(current_hour - then_hour == 1 && current_minute - then_minute < 0){
                date.setText(current_minute + 60 - then_minute+"분 전");
            } else if((current_minute-then_minute>=2) || (current_minute - then_minute == 1 && current_second - then_second >= 0)){
                date.setText(current_minute-then_minute+"분 전");
            } else{
                date.setText("방금 전");
            }

            user_text.setText(post.getUser_text());
            like.setText(Integer.toString(post.getLike()) + "명이 좋아합니다.");
            comment_num.setText(Integer.toString(post.getComment_num()));
        }
    }

    private class ReviewViewHolder extends RecyclerView.ViewHolder {
        //Review, Status, 댓글의 공통적인 요소
        TextView user_nickname, date, user_text;
        ImageView profile_photo;

        //Review, Status의 공통 요소
        TextView user_rank, restaurant, like, comment_num;
        EditText input_comment;
        Button comment_upload;
        ImageView like_button, post_option;
        Boolean liked = false;

        //Review의 요소
        ImageView user_photos;
        int doubleClickFlag = 0;
        final int CLICK_DELAY = 250;

        ReviewViewHolder(@NonNull final View itemView) {
            super(itemView);

            user_nickname = itemView.findViewById(R.id.user_nickname);
            user_rank = itemView.findViewById(R.id.user_rank);
            restaurant = itemView.findViewById(R.id.restaurant);
            date = itemView.findViewById(R.id.date);
            user_text = itemView.findViewById(R.id.user_text);
            like = itemView.findViewById(R.id.like);
            comment_num = itemView.findViewById(R.id.comment_num);
            input_comment = itemView.findViewById(R.id.input_comment);
            comment_upload = itemView.findViewById(R.id.comment_upload);
            like_button = itemView.findViewById(R.id.imageView);
            post_option = itemView.findViewById(R.id.post_option);
            user_photos = itemView.findViewById(R.id.user_photoes);

            profile_photo = itemView.findViewById(R.id.profile_photo);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    Log.d("debug", getAdapterPosition()+ "");
                }
            });

            user_nickname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    Toast.makeText(context, "어떤 사람일까요?", Toast.LENGTH_SHORT).show();
                }
            });

            post_option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    int pos = getAdapterPosition();
                    if(postOptionListener != null) {
                        boolean isWriter = false;
                        if(restoreState().equals(posts.get(pos).user_id)) {
                            isWriter = true;
                        }
                        postOptionListener.optionTouched(posts.get(pos).DB_num, isWriter);
                    }

                }
            });

            comment_upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION) {
                        imm.hideSoftInputFromWindow(input_comment.getWindowToken(), 0);
                        upload_num = posts.get(pos).DB_num;
                        upload_text = input_comment.getText().toString();
                        upload_pos = pos;
                        commentNum = posts.get(pos).getComment_num();
                        //자기 자신에 댓글이 1이상인지 본다.
                        //  1이상이면 lastComment_in_DB에 가장 최근에 쓴 댓글의 DB_num을 넘겨줘야해
                        //  아니면 그냥 -1을 넘긴다.
                        if(posts.get(pos).comment_num >= 1) {
                            lastComment_in_DB = Long.parseLong(posts.get(pos - 1).getDB_num());
                        } else {
                            lastComment_in_DB = -1;
                        }
                        if(upload_text.equals("")) {
                            Toast.makeText(context, "최소한 한 글자 이상 입력해주세요", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        reference.addListenerForSingleValueEvent(dataListener1);
                        input_comment.setText("");
                    }
                }
            });


        }

        private void bind(final int position){
            int photo_size = posts.get(position).photo.size();
            for(int i = 1 ; i <= photo_size ; i++){  // 사진 여러개 쓸 때 수정
                String file_path = posts.get(position).photo.get(i-1);
                StorageReference ref = storage.getReference().child(file_path);
                ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            Glide.with(HomeActivity.mContext).load(task.getResult()).into(user_photos);
                        }
                    }
                });
            }

            // 프로필 사진 적용
            Post post = posts.get(position);
            String file_path1 = "SKKU/profile_picture/profile_picture_" + post.getUser_id();
            StorageReference ref1 = storage.getReference().child(file_path1);
            ref1.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Glide.with(HomeActivity.mContext).load(task.getResult()).into(profile_photo);
                    }
                }
            });

            // Like 버튼 이미지 바인딩
            final String DB_num = post.getDB_num();
            DatabaseReference reference_like = FirebaseDatabase.getInstance().getReference().child("SKKU");
            reference_like.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Map<String, Object> message_i = (Map<String, Object>) dataSnapshot.child("Status").child(DB_num).getValue();
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

            // 사진 더블클릭 시 좋아요 기능
            user_photos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doubleClickFlag++;
                    Handler handler = new Handler();
                    Runnable clickRunnable = new Runnable() {
                        @Override
                        public void run() {
                            doubleClickFlag = 0;
                            // todo 클릭 이벤트
                        }
                    };
                    if( doubleClickFlag == 1 ) {
                        handler.postDelayed( clickRunnable, CLICK_DELAY );
                    }else if( doubleClickFlag == 2 ) {
                        doubleClickFlag = 0;
                        // todo 더블클릭 이벤트
                        if(liked == false){
                            like_button.setImageResource(R.drawable.like);
                            Toast.makeText(context, "좋아요!", Toast.LENGTH_SHORT).show();
                            liked = true;

                            DatabaseReference reference_like = FirebaseDatabase.getInstance().getReference().child("SKKU");
                            reference_like.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    Map<String, Object> message_i = (Map<String, Object>) dataSnapshot.child("Status").child(DB_num).getValue();
                                    int like_num = Integer.parseInt(message_i.get("like").toString());

                                    // 좋아요 +1 업데이트
                                    Post temp = posts.get(position);
                                    temp.setLike(like_num+1);
                                    setItem(temp,position);
                                    like.setText(temp.getLike()+"명이 좋아합니다.");

                                    Map<String, Object> childUpdates1 = new HashMap<>();
                                    Map<String, Object> childUpdates2 = new HashMap<>();


                                    childUpdates1.put(temp.getDB_num()+"/like",temp.getLike());
                                    childUpdates2.put(temp.getDB_num()+"/who_liked/"+restoreState(), restoreState());

                                    DatabaseReference reference_upload = FirebaseDatabase.getInstance().getReference().child("SKKU").child("Status");
                                    reference_upload.updateChildren(childUpdates1);
                                    reference_upload.updateChildren(childUpdates2);
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
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

                                Map<String, Object> message_i = (Map<String, Object>) dataSnapshot.child("Status").child(DB_num).getValue();
                                int like_num = Integer.parseInt(message_i.get("like").toString());

                                // 좋아요 +1 업데이트
                                Post temp = posts.get(position);
                                temp.setLike(like_num+1);
                                setItem(temp,position);
                                like.setText(temp.getLike()+"명이 좋아합니다.");

                                Map<String, Object> childUpdates1 = new HashMap<>();
                                Map<String, Object> childUpdates2 = new HashMap<>();


                                childUpdates1.put(temp.getDB_num()+"/like",temp.getLike());
                                childUpdates2.put(temp.getDB_num()+"/who_liked/"+restoreState(), restoreState());

                                DatabaseReference reference_upload = FirebaseDatabase.getInstance().getReference().child("SKKU").child("Status");
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

                                Map<String, Object> message_i = (Map<String, Object>) dataSnapshot.child("Status").child(DB_num).getValue();
                                int like_num = Integer.parseInt(message_i.get("like").toString());

                                // 좋아요 -1 업데이트
                                Post temp = posts.get(position);
                                temp.setLike(like_num-1);
                                setItem(temp,position);
                                like.setText(temp.getLike()+"명이 좋아합니다.");

                                Map<String, Object> childUpdates1 = new HashMap<>();
                                Map<String, Object> childUpdates2 = new HashMap<>();
                                childUpdates1.put(temp.getDB_num()+"/like",temp.getLike());

                                DatabaseReference reference_upload = FirebaseDatabase.getInstance().getReference().child("SKKU").child("Status");
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

            user_nickname.setText(post.getUser_nickname());
            user_rank.setText(post.getUser_rank());
            if(post.getRestaurant().equals("NO")){
                restaurant.setText("");
            } else{
                restaurant.setText(post.getRestaurant());
            }

            Date date1 = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
            String current_date = format.format(date1);
            String then_date = post.getDate();
            int current_year = Integer.parseInt(current_date.substring(0,4));
            int then_year = Integer.parseInt(then_date.substring(0,4));
            int current_month = Integer.parseInt(current_date.substring(5,7));
            int then_month = Integer.parseInt(then_date.substring(5,7));
            int current_day = Integer.parseInt(current_date.substring(8,10));
            int then_day = Integer.parseInt(then_date.substring(8,10));
            int current_hour = Integer.parseInt(current_date.substring(11,13));
            int then_hour = Integer.parseInt(then_date.substring(11,13));
            int current_minute = Integer.parseInt(current_date.substring(14,16));
            int then_minute = Integer.parseInt(then_date.substring(14,16));
            int current_second = Integer.parseInt(current_date.substring(17,19));
            int then_second = Integer.parseInt(then_date.substring(17,19));

            if((current_year-then_year>=2) || (current_year - then_year == 1 && current_month - then_month >= 0)){
                date.setText(current_year-then_year+"년 전");
            } else if(current_year - then_year == 1 && current_month - then_month < 0){
                date.setText(current_month + 12 - then_month+"달 전");
            } else if((current_month-then_month>=2) || (current_month - then_month == 1 && current_day - then_day >= 0)){
                date.setText(current_month-then_month+"달 전");
            } else if(current_month - then_month == 1 && current_day - then_day < 0){
                date.setText(current_day + 30 - then_day+"일 전");
            } else if((current_day-then_day>=2) || (current_day - then_day == 1 && current_hour - then_hour >= 0)){
                date.setText(current_day-then_day+"일 전");
            } else if(current_day - then_day == 1 && current_hour - then_hour < 0){
                date.setText(current_hour + 24 - then_hour+"시간 전");
            } else if((current_hour-then_hour>=2) || (current_hour - then_hour == 1 && current_minute - then_minute >= 0)){
                date.setText(current_hour-then_hour+"시간 전");
            } else if(current_hour - then_hour == 1 && current_minute - then_minute < 0){
                date.setText(current_minute + 60 - then_minute+"분 전");
            } else if((current_minute-then_minute>=2) || (current_minute - then_minute == 1 && current_second - then_second >= 0)){
                date.setText(current_minute-then_minute+"분 전");
            } else{
                date.setText("방금 전");
            }

            user_text.setText(post.getUser_text());
            like.setText(Integer.toString(post.getLike()) + "명이 좋아합니다.");
            comment_num.setText(Integer.toString(post.getComment_num()));
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == REVIEW) {
            return new ReviewViewHolder(LayoutInflater.from(context).inflate(R.layout.review_item, parent,false));
        } else if(viewType == STATUS) {
            return new StatusViewHolder(LayoutInflater.from(context).inflate(R.layout.status_item, parent,false));
        } else if(viewType == COMMENT) {
            return new CommentViewHolder(LayoutInflater.from(context).inflate(R.layout.comment_item, parent,false));
        }

        Log.d("debug", "예외 상황 발생");
        return null;
    }

    @NonNull
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = posts.get(position).type;
        if(viewType == REVIEW) {
            ((ReviewViewHolder) holder).bind(position);
        } else if(viewType == STATUS) {
            ((StatusViewHolder) holder).bind(position);
        } else if(viewType == COMMENT) {
            ((CommentViewHolder) holder).bind(position);
        } else {
            Log.d("debug", "예외 상황 발생");
        }
    }

    @Override
    public int getItemViewType(int position) {  //xml 파일 두 개 쓰는 방법
        return posts.get(position).type;
        // here you can get decide from your model's ArrayList, which type of view you need to load. Like
        //if (list.get(position).type == Something) { // put your condition, according to your requirements
        //    return VIEW_TYPE_ONE;
        //}
        //return VIEW_TYPE_TWO;
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    //댓글 달기 구현하는 데이터 리스너
    ValueEventListener dataListener1 = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            //Map<String, Object> message1 = (Map<String, Object>) dataSnapshot.getValue();
            //리사이클러뷰에서 몇번째 게시물: upload_num

            Map<String, Object> childUpdates1 = new HashMap<>();
            Map<String, Object> postValues = new HashMap<>();

            //현재 시간을 구해서 넣는다(DB: date).
            Date time = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
            SimpleDateFormat format2 = new SimpleDateFormat("yyyy"+"MM"+"dd"+"HH"+"mm"+"ss"); //comments에 제목으로 들어감
            postValues.put("date", ""+format.format(time));
            //Log.d("datasnap", "현재 시각은 "+format.format(time)+"입니다.");

            //자기 아이디를 찾아서 넣는다(DB: id).
            postValues.put("id", ""+restoreState());
            //Log.d("snap", postValues.values().toString());

            //자기 번호를 찾아서 넣는다(DB: user_num)
            postValues.put("user_num", restoreState2());

            //입력한 댓글 내용을 찾아서 넣는다(DB: text)
            postValues.put("text", ""+upload_text);

            childUpdates1.put(upload_num + "/comments/" + format2.format(time), postValues);
            //childUpdates1.put(upload_num + "/comments/num", target_comment);
            reference.updateChildren(childUpdates1);

            if(itemAddListener!=null) {
                itemAddListener.itemAdded(lastComment_in_DB, upload_pos, upload_num);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    public void setItem(Post post,int index){
        posts.set(index,post);
    }
    public void addItem(Post post) {
        posts.add(post);
    }

    public void addItem(Post post, int index) { posts.add(index, post); }

    public void comment_add(int position) {
        posts.get(position).comment_num += 1;
    }

    public void deleteAllItem(){  // 리프레쉬를 위한 어댑터 리셋
        while(posts.size()>=1){
            posts.remove(0);
        }
    }

    public String restoreState() {
        SharedPreferences pref = ((HomeActivity)HomeActivity.mContext).getSharedPreferences("pref", Activity.MODE_PRIVATE);
        return pref.getString("id","");
    }

    public int restoreState2() {
        SharedPreferences pref = ((HomeActivity)HomeActivity.mContext).getSharedPreferences("pref", Activity.MODE_PRIVATE);
        return pref.getInt("user_num",0);
    }

    public String getFirst_DB_num() { return posts.get(0).DB_num; }
    public String getLast_DB_num() {
        return  posts.get(posts.size() - 1).getDB_num();
    }
}