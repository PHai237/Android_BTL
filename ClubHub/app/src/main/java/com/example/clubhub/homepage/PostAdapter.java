package com.example.clubhub.homepage;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.clubhub.R;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private final List<Post> postList;
    private final Context context;

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
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
        holder.tvClubName.setText(post.clubName != null ? post.clubName : "");
        holder.tvUserName.setText(post.userName != null ? post.userName : "");
        holder.tvContent.setText(post.content != null ? post.content : "");

        // Load avatar CLB
        if (post.clubAvatarUrl != null && !post.clubAvatarUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(post.clubAvatarUrl)
                    .placeholder(R.drawable.ic_club_logo_default)
                    .into(holder.imgClub);
        } else {
            holder.imgClub.setImageResource(R.drawable.ic_club_logo_default);
        }

        // Load avatar user
        if (post.userAvatarUrl != null && !post.userAvatarUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(post.userAvatarUrl)
                    .placeholder(R.drawable.ic_user_avt_default)
                    .into(holder.imgUser);
        } else {
            holder.imgUser.setImageResource(R.drawable.ic_user_avt_default);
        }

        // Load ảnh bài post
        if (post.imageUrl != null && !post.imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(post.imageUrl)
                    .placeholder(R.drawable.sample_img_default)
                    .into(holder.imgPost);
        } else {
            holder.imgPost.setVisibility(View.GONE);
        }

        // Nhấn vào bài post để mở chi tiết và comment
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PostDetailActivity.class);
            intent.putExtra("postId", post.getPostId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView imgClub, imgUser, imgPost;
        TextView tvClubName, tvUserName, tvContent;
        // Nếu bạn muốn có nút comment thì khai báo thêm ở đây

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            imgClub = itemView.findViewById(R.id.img_club);
            tvClubName = itemView.findViewById(R.id.tv_club_name);
            imgUser = itemView.findViewById(R.id.img_user);
            tvUserName = itemView.findViewById(R.id.tv_username);
            tvContent = itemView.findViewById(R.id.tv_content);
            imgPost = itemView.findViewById(R.id.img_post);
        }
    }

    public void updateData(List<Post> newPostList) {
        postList.clear();
        postList.addAll(newPostList);
        notifyDataSetChanged();
    }
}
