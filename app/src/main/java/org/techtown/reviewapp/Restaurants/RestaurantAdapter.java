package org.techtown.reviewapp.Restaurants;

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

import de.hdodenhof.circleimageview.CircleImageView;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder>{
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private ArrayList<Restaurant> items = new ArrayList<>();
    Context context;

    public RestaurantAdapter(Context context) { // 생성자를 만들 때 Context를 안 넘겨주면 프래그먼트 나갔다가 들어올 때 마다 리사이클러뷰에 중복으로 쌓이는 버그 생김
        this.context = context;
    }

    @NonNull
    @Override
    public RestaurantAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView;

        itemView = inflater.inflate(R.layout.restaurant_item,parent,false);
        return new RestaurantAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantAdapter.ViewHolder holder, int position) {
        Restaurant restaurant = items.get(position);
        holder.setItem(restaurant);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        ImageView imageView;
        TextView textView,textView2,textView3;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.textView);
            textView2 = itemView.findViewById(R.id.textView2);
            textView3 = itemView.findViewById(R.id.textView3);
            circleImageView = itemView.findViewById(R.id.circleImageView);
            imageView = itemView.findViewById(R.id.imageView);
        }

        public void setItem(Restaurant restaurant) {
            String file_path = restaurant.getPicture();
            StorageReference ref = storage.getReference().child(file_path);
            ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Glide.with(RestaurantListActivity.mContext).load(task.getResult()).into(circleImageView);
                        itemView.setVisibility(View.VISIBLE);
                        if(((RestaurantListActivity)RestaurantListActivity.mContext).restaurants_num == getItemCount()){  // 마지막 음식점의 사진이 불러오진 후에야 프로그레스바가 사라지고 화면이 뜬다.
                            ((RestaurantListActivity)RestaurantListActivity.mContext).textView2.setVisibility(View.VISIBLE);
                            ((RestaurantListActivity)RestaurantListActivity.mContext).recyclerView.setVisibility(View.VISIBLE);
                            ((RestaurantListActivity)RestaurantListActivity.mContext).progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            });

            float rating = restaurant.getRating();
            textView.setText(restaurant.getName());
            textView2.setText(Float.toString(rating));
            textView3.setText("리뷰 "+restaurant.getNum_of_reviews()+"개");

            if(rating < 0.25){

            } else if(rating < 0.75){
                imageView.setImageResource(R.drawable.star0_5);
            } else if(rating < 1.25){
                imageView.setImageResource(R.drawable.star1);
            } else if(rating < 1.75){
                imageView.setImageResource(R.drawable.star1_5);
            } else if(rating < 2.25){
                imageView.setImageResource(R.drawable.star2);
            } else if(rating < 2.75){
                imageView.setImageResource(R.drawable.star2_5);
            } else if(rating < 3.25){
                imageView.setImageResource(R.drawable.star3);
            } else if(rating < 3.75){
                imageView.setImageResource(R.drawable.star3_5);
            } else if(rating < 4.25){
                imageView.setImageResource(R.drawable.star4);
            } else if(rating < 4.75){
                imageView.setImageResource(R.drawable.star4_5);
            } else{
                imageView.setImageResource(R.drawable.star5);
            }
        }
    }

    public void addRestaurant(Restaurant restaurant) {
        items.add(restaurant);
    }
}
