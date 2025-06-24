package com.example.clubhub.homepage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.clubhub.R;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> commentList;
    public CommentAdapter(List<Comment> commentList) {
        this.commentList = commentList;
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        Comment cmt = commentList.get(position);
        holder.tvName.setText(cmt.getUserName());
        holder.tvContent.setText(cmt.getContent());
        if (cmt.getUserAvatarUrl() != null && !cmt.getUserAvatarUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext()).load(cmt.getUserAvatarUrl()).into(holder.imgAvatar);
        } else {
            holder.imgAvatar.setImageResource(R.drawable.ic_user_avt_default);
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView tvName, tvContent;
        public CommentViewHolder(View v) {
            super(v);
            imgAvatar = v.findViewById(R.id.img_comment_avatar);
            tvName = v.findViewById(R.id.tv_comment_name);
            tvContent = v.findViewById(R.id.tv_comment_content);
        }
    }
}
