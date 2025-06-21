package com.example.clubhub.homepage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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
        holder.imgClub.setImageResource(post.clubAvatarRes);
        holder.tvClubName.setText(post.clubName);
        holder.imgUser.setImageResource(post.userAvatarRes);
        holder.tvUserName.setText(post.userName);
        holder.tvContent.setText(post.content);
        holder.imgPost.setImageResource(post.imageRes);
        // Xử lý nút xóa, comment... tại đây nếu muốn
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
}
