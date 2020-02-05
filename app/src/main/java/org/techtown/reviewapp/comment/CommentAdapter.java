package org.techtown.reviewapp.comment;

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

import org.techtown.reviewapp.R;
import org.techtown.reviewapp.home.HomeActivity;
import org.techtown.reviewapp.review.Review;
import org.techtown.reviewapp.review.ReviewAdapter;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private ArrayList<Comment> comments = new ArrayList<>();
    Context context;

    public CommentAdapter(Context context) {
        this.context = context;
    }

    //코멘트는 뷰홀더 필요없음
    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("asdf","C");
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        Log.d("asdf","D");
        View itemView;
        itemView = inflater.inflate(R.layout.comment_item,parent,false);
        return new CommentAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.setItem(comment);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView user_nickname, user_comment, date;
        //RecyclerView comments;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            user_nickname = itemView.findViewById(R.id.user_nickname);
            user_comment = itemView.findViewById(R.id.user_comment);
            date = itemView.findViewById(R.id.date);

        }

        public void setItem(Comment comment){

            user_nickname.setText(comment.get_user_id());
            user_comment.setText(comment.get_comment_text());
            date.setText(comment.get_date());
        }
    }

    public void addComment(Comment comment) {
        Log.d("asdf","A");
        comments.add(comment);
        Log.d("asdf","B");
    }
}
