package org.techtown.reviewapp.post;

import android.content.Context;
import android.net.Uri;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.techtown.reviewapp.R;
import org.techtown.reviewapp.home.HomeActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //DB
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("SKKU").child("Status");
    int upload_num, upload_pos, commentNum;
    String upload_text;

    //viewType
    public int REVIEW = 0; //사진 있는 리뷰
    public int STATUS = 1; //사진 없는 리뷰
    public int COMMENT = 2; //댓글

    //context
    private final Context context;

    //data
    private ArrayList<Post> posts = new ArrayList<>();

    //etc
    static int view_num = 0;

    InputMethodManager imm;

    public ItemAddListener itemAddListener;

    public interface ItemAddListener {
        void itemAdded(int prev_num, int position, int DB_num);
    }

    //생성자
    public PostAdapter(Context context) {
        this.context = context;
        imm = (InputMethodManager)context.getSystemService(INPUT_METHOD_SERVICE);
    }

    private class CommentViewHolder extends RecyclerView.ViewHolder {
        //Review, Status, 댓글의 공통적인 요소
        TextView user_nickname, date, user_text;
        ImageView profile_photo;

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
        }

        private void bind(int position) {
            // This method will be called anytime a list post is created or update its data
            //Do your stuff here
            Post post = posts.get(position);
            user_nickname.setText(post.getUser_nickname());
            date.setText(post.getDate());
            user_text.setText(post.getUser_text());
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
        ImageView like_button;
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
            like_button = itemView.findViewById(R.id.imageView);

            profile_photo = itemView.findViewById(R.id.profile_photo);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
                        if(upload_text.equals("")) {
                            Toast.makeText(context, "최소한 한 글자 이상 입력해주세요", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        reference.addListenerForSingleValueEvent(dataListener1);
                        input_comment.setText("");
                    }
                }
            });

            like_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(liked == false){
                        like_button.setImageResource(R.drawable.like);
                        Toast.makeText(context, "좋아요!", Toast.LENGTH_SHORT).show();
                        liked = true;
                    } else{
                        like_button.setImageResource(R.drawable.no_like);
                        Toast.makeText(context, "좋아요 취소해요..", Toast.LENGTH_SHORT).show();
                        liked = false;
                    }

                }
            });
        }

        private void bind(int position){
            Post post = posts.get(position);
            user_nickname.setText(post.getUser_nickname());
            user_rank.setText(post.getUser_rank());
            if(post.getRestaurant().equals("NO")){
                restaurant.setText("");
            } else{
                restaurant.setText(post.getRestaurant());
            }

            date.setText(post.getDate());
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
        ImageView like_button;
        Boolean liked = false;

        //Review의 요소
        ImageView user_photos = itemView.findViewById(R.id.user_photoes);

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

            profile_photo = itemView.findViewById(R.id.profile_photo);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
                        if(upload_text.equals("")) {
                            Toast.makeText(context, "최소한 한 글자 이상 입력해주세요", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        reference.addListenerForSingleValueEvent(dataListener1);
                        input_comment.setText("");
                    }
                }
            });

            like_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(liked == false){
                        like_button.setImageResource(R.drawable.like);
                        Toast.makeText(context, "좋아요!", Toast.LENGTH_SHORT).show();
                        liked = true;
                    } else{
                        like_button.setImageResource(R.drawable.no_like);
                        Toast.makeText(context, "좋아요 취소해요..", Toast.LENGTH_SHORT).show();
                        liked = false;
                    }

                }
            });
        }

        private void bind(int position){
            Post post = posts.get(position);
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
            } else if((current_month-then_month>=2) || (current_month - then_month == 1 && current_day - then_day >= 0)){
                date.setText(current_month-then_month+"달 전");
            } else if((current_day-then_day>=2) || (current_day - then_day == 1 && current_hour - then_hour >= 0)){
                date.setText(current_day-then_day+"일 전");
            } else if((current_hour-then_hour>=2) || (current_hour - then_hour == 1 && current_minute - then_minute >= 0)){
                date.setText(current_hour-then_hour+"시간 전");
            } else if((current_minute-then_minute>=2) || (current_minute - then_minute == 1 && current_second - then_second >= 0)){
                date.setText(current_minute-then_minute+"분 전");
            } else{
                date.setText("방금 전");
            }



            user_text.setText(post.getUser_text());
            like.setText(Integer.toString(post.getLike()) + "명이 좋아합니다.");
            comment_num.setText(Integer.toString(post.getComment_num()));

            if(posts.get(position).photo_num == 1) {
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

            }
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
            Map<String, Object> message1 = (Map<String, Object>) dataSnapshot.getValue();
            //리사이클러뷰에서 몇번째 게시물: upload_num

            //DB상에서 upload_num 밑의 num을 찾는다.
            //      target_comment에 넣는다.
            Map<String, Object> message2 = (Map<String, Object>)dataSnapshot.child(Integer.toString(upload_num)).child("comments").getValue();
            int target_comment = Integer.parseInt(message2.get("num").toString());

            //target_comment를 1 올리고,
            target_comment++;

            Map<String, Object> childUpdates1 = new HashMap<>();
            Map<String, Object> postValues = new HashMap<>();

            //현재 시간을 구해서 넣는다(DB: date).
            Date time = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
            postValues.put("date", ""+format.format(time));
            //Log.d("datasnap", "현재 시각은 "+format.format(time)+"입니다.");

            //자기 아이디를 찾아서 넣는다(DB: id).
            postValues.put("id", ""+restoreState());
            //Log.d("snap", postValues.values().toString());

            //자기 번호를 찾아서 넣는다(DB: user_num)
            postValues.put("user_num", ""+restoreState2());

            //입력한 댓글 내용을 찾아서 넣는다(DB: text)
            postValues.put("text", ""+upload_text);

            childUpdates1.put(upload_num + "/comments/" + target_comment, postValues);
            childUpdates1.put(upload_num + "/comments/num", target_comment);
            reference.updateChildren(childUpdates1);

            if(itemAddListener!=null) {
                itemAddListener.itemAdded(commentNum, upload_pos, upload_num);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    public void addItem(Post post) {
        posts.add(post);
    }

    public void addItem(Post post, int index) { posts.add(index, post); }

    public void comment_add(int position) {
        posts.get(position).comment_num += 1;
    }

    public String restoreState() {
        return "bestowing";
    }

    public String restoreState2() {
        return "2";
    }

}