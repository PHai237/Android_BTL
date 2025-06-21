package com.example.clubhub;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> posts;

    public PostAdapter(List<Post> posts) {
        this.posts = posts;
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView tvClub, tvAuthor, tvContent;
        ImageView imgPhoto;

        public PostViewHolder(View itemView) {
            super(itemView);
            tvClub = itemView.findViewById(R.id.tvClub);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvContent = itemView.findViewById(R.id.tvContent);
            imgPhoto = itemView.findViewById(R.id.imgPhoto);
        }
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.tvClub.setText(post.clubName);
        holder.tvAuthor.setText(post.author);
        holder.tvContent.setText(post.content);
        if (post.imageResId != 0) {
            holder.imgPhoto.setImageResource(post.imageResId);
        } else {
            holder.imgPhoto.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
