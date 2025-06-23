package com.example.clubhub.homepage;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.clubhub.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NewPostActivity extends AppCompatActivity {
    private EditText etContent;
    private ImageView imgPreview;
    private Button btnChooseImg, btnPost;
    private Uri imageUri = null;
    private String email;

    private static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        etContent = findViewById(R.id.et_content);
        imgPreview = findViewById(R.id.img_preview);
        btnChooseImg = findViewById(R.id.btn_choose_img);
        btnPost = findViewById(R.id.btn_post);

        email = getIntent().getStringExtra("email");

        btnChooseImg.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE);
        });

        btnPost.setOnClickListener(v -> {
            String content = etContent.getText().toString().trim();
            if (content.isEmpty()) {
                etContent.setError("Vui lòng nhập nội dung");
                return;
            }
            uploadPost(content, imageUri);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imgPreview.setImageURI(imageUri);
        }
    }

    // Upload post lên Firestore (và Storage nếu có ảnh)
    private void uploadPost(String content, Uri imageUri) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String postId = db.collection("posts").document().getId();

        Map<String, Object> post = new HashMap<>();
        post.put("postId", postId);
        post.put("authorEmail", email);
        post.put("content", content);
        post.put("createdAt", new Date());

        if (imageUri != null) {
            // Lưu ảnh lên Firebase Storage, rồi lưu URL vào post
            FirebaseStorage.getInstance().getReference("post_images/" + postId + ".jpg")
                    .putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot ->
                            taskSnapshot.getStorage().getDownloadUrl()
                                    .addOnSuccessListener(uri -> {
                                        post.put("imageUrl", uri.toString());
                                        db.collection("posts").document(postId).set(post)
                                                .addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(this, "Đăng bài thành công!", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                });
                                    }))
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Lỗi tải ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            post.put("imageUrl", "");
            db.collection("posts").document(postId).set(post)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Đăng bài thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Lỗi đăng bài: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}
