package com.example.clubhub.homepage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.clubhub.R;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;

    public PostAdapter(List<Post> postList) {
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new PostViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.tvClubName.setText(post.clubName);
        holder.tvUserName.setText(post.userName);
        holder.tvContent.setText(post.content);
        holder.tvComment.setText(post.comment);

        // Load avatar CLB
        Glide.with(holder.itemView.getContext())
                .load(post.clubAvatarUrl)
                .placeholder(R.drawable.ic_club_logo_default)
                .into(holder.imgClub);

        // Load avatar user
        Glide.with(holder.itemView.getContext())
                .load(post.userAvatarUrl)
                .placeholder(R.drawable.ic_user_avt_default)
                .into(holder.imgUser);

        // Load ảnh bài post
        Glide.with(holder.itemView.getContext())
                .load(post.imageUrl)
                .placeholder(R.drawable.sample_img_default)
                .into(holder.imgPost);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView imgClub, imgUser, imgPost;
        TextView tvClubName, tvUserName, tvContent, tvComment;
        ImageButton btnDelete;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            imgClub = itemView.findViewById(R.id.img_club);
            tvClubName = itemView.findViewById(R.id.tv_club_name);
            imgUser = itemView.findViewById(R.id.img_user);
            tvUserName = itemView.findViewById(R.id.tv_username);
            tvContent = itemView.findViewById(R.id.tv_content);
            imgPost = itemView.findViewById(R.id.img_post);
            tvComment = itemView.findViewById(R.id.tv_comment);
        }
    }

    public void updateData(List<Post> newPostList) {
        postList.clear();
        postList.addAll(newPostList);
        notifyDataSetChanged();
    }
}
