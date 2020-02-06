package org.techtown.reviewapp.review;

import android.content.Context;
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
import androidx.recyclerview.widget.LinearLayoutManager;
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
import org.techtown.reviewapp.comment.Comment;
import org.techtown.reviewapp.comment.CommentAdapter;
import org.techtown.reviewapp.home.HomeActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("SKKU").child("Status");
    private ArrayList<Review> reviews = new ArrayList<Review>();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    Context context;
    static int view_num = 0;
    RecyclerView recyclerView;
    CommentAdapter adapter;
    int upload_num;
    String upload_text;

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
        TextView user_nickname, user_rank, restaurant, date, user_text, like, comment_num;
        EditText input_comment;
        Button comment_upload;
        ImageView user_photoes, profile_photo;
        Boolean already_loaded = false; // 리사이클러뷰 안에 리사이클러뷰 넣었을 때 계속 add 되는 문제 해결

        public ViewHolder(@NonNull final View itemView) {
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
            recyclerView = itemView.findViewById(R.id.comments);
            recyclerView.setLayoutManager(new LinearLayoutManager((HomeActivity) HomeActivity.mContext,LinearLayoutManager.VERTICAL,false)) ;
            adapter = new CommentAdapter((HomeActivity)HomeActivity.mContext);

            profile_photo = itemView.findViewById(R.id.profile_photo);

            if(view_num == 1) {
                user_photoes = itemView.findViewById(R.id.user_photoes);
            }

            comment_upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION) {
                        upload_num = pos;
                        upload_text = input_comment.getText().toString();
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

        public void setItem(Review review){

            user_nickname.setText(review.getUser_nickname());
            user_rank.setText(review.getUser_rank());
            restaurant.setText(review.getRestaurant());
            date.setText(review.getDate());
            user_text.setText(review.getUser_text());
            like.setText(Integer.toString(review.getLike()));
            comment_num.setText(Integer.toString(review.comments.size()));

            if(already_loaded == false){ // 리사이클러뷰 안에 리사이클러뷰 넣었을 때 계속 add 되는 문제 해결
                for(int i=1 ; i<=review.comments.size();i++){ Log.d("asd",Integer.toString(review.comments.size()));
                    adapter.addComment(review.comments.get(i-1));
                }
                recyclerView.setAdapter(adapter);
            }
            already_loaded = true;  // 리사이클러뷰 안에 리사이클러뷰 넣었을 때 계속 add 되는 문제 해결


            if(view_num == 1){
                for(int i = 1 ; i <= review.photo.size() ; i++){  // 사진 여러개 쓸 때 수정
                    String file_path = review.photo.get(i-1);
                    StorageReference ref = storage.getReference().child(file_path);
                    ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()){
                                Glide.with(HomeActivity.mContext).load(task.getResult()).into(user_photoes);
                            }
                        }
                    });
                }

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

    //댓글 달기 구현하는 데이터 리스너
    ValueEventListener dataListener1 = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Map<String, Object> message1 = (Map<String, Object>) dataSnapshot.getValue();
            //리사이클러뷰에서 몇번째 게시물: upload_num

            //DB상에서 num값(게시물의 총 개수)을 찾는다.
            //      target_num = Status 밑의 num값 - 포지션 = 댓글을 달았던 Status 번호
            int target_num = (Integer.parseInt(message1.get("num").toString()) - upload_num);

            //DB상에서 target_num 밑의 num을 찾는다.
            //      target_comment에 넣는다.
            Map<String, Object> message2 = (Map<String, Object>)dataSnapshot.child(Integer.toString(target_num)).child("comments").getValue();

            int target_comment = Integer.parseInt(message2.get("num").toString());
            target_comment++;
            //target_comment를 1 올리고,
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

            childUpdates1.put(target_num + "/comments/" + target_comment, postValues);
            childUpdates1.put(target_num + "/comments/num", target_comment);
            reference.updateChildren(childUpdates1);

            //끝났으면 받아오기
            Map<String, Object> message3 = (Map<String, Object>) dataSnapshot.child(Integer.toString(target_num)).child("comments").getValue();
            int newlyUpdate = Integer.parseInt(message3.get("num").toString());
            for(int i=target_comment; i<=newlyUpdate; i++) {
                Map<String, Object> message4 = (Map<String, Object>) message_comment.get(Integer.toString(i));
                String date = (String)message4.get("date");
                String id = (String)message4.get("id");
                String text = (String)message4.get("text");
                String nickname = (String)"bestowing";
                //reviews.get(upload_num).comments.add(new Comment(id,nickname,text,date));
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    public void addReview(Review review) {
        reviews.add(review);
    }

    public String restoreState() {
        return "bestowing";
    }

    public String restoreState2() {
        return "2";
    }

}
