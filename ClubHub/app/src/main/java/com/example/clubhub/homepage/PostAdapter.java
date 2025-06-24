package com.example.clubhub.homepage;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.clubhub.R;
import com.google.firebase.firestore.FirebaseFirestore;

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

        // Lấy clubId từ bài đăng
        String clubId = post.clubId;

        // Truy vấn thông tin câu lạc bộ (nếu bạn muốn hiển thị thêm thông tin câu lạc bộ)
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Clubs").document(clubId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String clubName = documentSnapshot.getString("name");
                        String clubAvatarUrl = documentSnapshot.getString("logoUrl");

                        holder.tvClubName.setText(clubName);
                        if (clubAvatarUrl != null && !clubAvatarUrl.isEmpty()) {
                            Glide.with(holder.itemView.getContext())
                                    .load(clubAvatarUrl)
                                    .placeholder(R.drawable.ic_club_logo_default)
                                    .into(holder.imgClub);
                        } else {
                            holder.imgClub.setImageResource(R.drawable.ic_club_logo_default);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("PostAdapter", "Error getting club info: ", e);
                });

        // Load user info (user avatar, user name)
        holder.tvUserName.setText(post.userName);
        holder.tvContent.setText(post.content);

        // Load image for post if it exists
        if (post.imageUrl != null && !post.imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(post.imageUrl)
                    .placeholder(R.drawable.sample_img_default)
                    .into(holder.imgPost);
        } else {
            holder.imgPost.setVisibility(View.GONE);
        }

        // Handle click to open PostDetailActivity
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
