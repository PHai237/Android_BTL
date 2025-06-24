package com.example.clubhub.homepage;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.clubhub.R;
import com.google.firebase.firestore.*;
import java.util.*;

public class PostDetailActivity extends AppCompatActivity {
    private TextView tvContent;
    private ImageView imgPost;
    private RecyclerView recyclerComments;
    private EditText etComment;
    private Button btnSendComment;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList = new ArrayList<>();
    private String postId, email, userName, userAvatarUrl;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        tvContent = findViewById(R.id.tv_content);
        imgPost = findViewById(R.id.img_post);
        recyclerComments = findViewById(R.id.recycler_comments);
        etComment = findViewById(R.id.et_comment);
        btnSendComment = findViewById(R.id.btn_send_comment);

        db = FirebaseFirestore.getInstance();

        postId = getIntent().getStringExtra("postId");

        // Load post info
        db.collection("posts").document(postId).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        tvContent.setText(document.getString("content"));
                        String imageUrl = document.getString("imageUrl");
                        if (imageUrl != null && !imageUrl.isEmpty())
                            Glide.with(this).load(imageUrl).into(imgPost);
                        else imgPost.setVisibility(View.GONE);
                    }
                });

        // Lấy session
        SharedPreferences prefs = getSharedPreferences("USER_SESSION", MODE_PRIVATE);
        email = prefs.getString("email", null);

        // Lấy thông tin user (nếu đã login)
        if (email != null) {
            db.collection("Users").document(email).get()
                    .addOnSuccessListener(doc -> {
                        userName = doc.getString("fullName");
                        userAvatarUrl = doc.getString("photoUrl");
                    });
        } else {
            etComment.setEnabled(false);
            etComment.setHint("Hãy đăng nhập để bình luận");
            btnSendComment.setEnabled(false);
        }

        // List comment
        commentAdapter = new CommentAdapter(commentList);
        recyclerComments.setLayoutManager(new LinearLayoutManager(this));
        recyclerComments.setAdapter(commentAdapter);

        // Load comment mỗi khi có thay đổi
        loadComments();

        btnSendComment.setOnClickListener(v -> {
            String cmt = etComment.getText().toString().trim();
            if (cmt.isEmpty()) {
                etComment.setError("Bạn chưa nhập nội dung!");
                return;
            }
            addComment(cmt);
        });
    }

    private void loadComments() {
        db.collection("comments")
                .whereEqualTo("postId", postId)
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(this, "Lỗi khi tải comment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    commentList.clear();
                    if (snapshots != null) {
                        for (DocumentSnapshot doc : snapshots) {
                            Comment c = doc.toObject(Comment.class);
                            commentList.add(c);
                        }
                    }
                    commentAdapter.notifyDataSetChanged();
                });
    }

    private void addComment(String content) {
        if (email == null) {
            Toast.makeText(this, "Bạn cần đăng nhập để bình luận!", Toast.LENGTH_SHORT).show();
            return;
        }
        String commentId = db.collection("comments").document().getId();
        Map<String, Object> cmt = new HashMap<>();
        cmt.put("commentId", commentId);
        cmt.put("postId", postId);
        cmt.put("userEmail", email);
        cmt.put("userName", userName != null ? userName : "");
        cmt.put("userAvatarUrl", userAvatarUrl != null ? userAvatarUrl : "");
        cmt.put("content", content);
        cmt.put("createdAt", new Date());

        db.collection("comments").document(commentId).set(cmt)
                .addOnSuccessListener(aVoid -> {
                    etComment.setText("");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi gửi comment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
