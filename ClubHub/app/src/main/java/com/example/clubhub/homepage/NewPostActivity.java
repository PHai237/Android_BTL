package com.example.clubhub.homepage;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.clubhub.R;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NewPostActivity extends AppCompatActivity {
    private EditText etContent, etImageUrl;
    private ImageView imgPreview;
    private Button btnFindImage, btnPost;

    // Các trường cần nhận từ Intent
    private String email, userName, userAvatarUrl;
    private String clubId, clubName, clubAvatarUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        etContent = findViewById(R.id.et_content);
        etImageUrl = findViewById(R.id.et_image_url);
        imgPreview = findViewById(R.id.img_preview);
        btnFindImage = findViewById(R.id.btn_find_image);
        btnPost = findViewById(R.id.btn_post);

        // Lấy dữ liệu user và club từ Intent
        email = getIntent().getStringExtra("email");
        userName = getIntent().getStringExtra("userName");
        userAvatarUrl = getIntent().getStringExtra("userAvatarUrl");
        clubId = getIntent().getStringExtra("clubId");
        clubName = getIntent().getStringExtra("clubName");
        clubAvatarUrl = getIntent().getStringExtra("clubAvatarUrl");

        if (userName == null) userName = "";
        if (userAvatarUrl == null) userAvatarUrl = "";
        if (clubName == null) clubName = "";
        if (clubAvatarUrl == null) clubAvatarUrl = "";

        btnFindImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://unsplash.com/s/photos"));
            startActivity(intent);
        });

        etImageUrl.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String url = s.toString().trim();
                if (!url.isEmpty() && (url.startsWith("http://") || url.startsWith("https://"))) {
                    Glide.with(NewPostActivity.this).load(url).into(imgPreview);
                } else {
                    imgPreview.setImageResource(R.drawable.sample_img_default);
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnPost.setOnClickListener(v -> {
            String content = etContent.getText().toString().trim();
            String imageUrl = etImageUrl.getText().toString().trim();

            if (content.isEmpty()) {
                etContent.setError("Vui lòng nhập nội dung");
                return;
            }
            uploadPost(content, imageUrl);
        });

        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

    }

    private void uploadPost(String content, String imageUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String postId = db.collection("posts").document().getId();

        Map<String, Object> post = new HashMap<>();
        post.put("postId", postId);
        post.put("authorEmail", email);
        post.put("userName", userName);
        post.put("userAvatarUrl", userAvatarUrl);
        post.put("content", content);
        post.put("createdAt", new Date());
        post.put("imageUrl", imageUrl);
        post.put("clubId", clubId);
        post.put("clubName", clubName);
        post.put("clubAvatarUrl", clubAvatarUrl);

        db.collection("posts").document(postId).set(post)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(NewPostActivity.this, "Đăng bài thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(NewPostActivity.this, "Lỗi đăng bài: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
