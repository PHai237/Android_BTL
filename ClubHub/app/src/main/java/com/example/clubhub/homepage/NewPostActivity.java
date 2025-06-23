package com.example.clubhub.homepage;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.clubhub.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.*;

public class NewPostActivity extends AppCompatActivity {
    private Spinner spinnerClub;
    private EditText etContent, etImageUrl;
    private ImageView imgPreview;
    private Button btnFindImage, btnPost;
    private String email;

    // Lưu tạm thông tin các CLB user tham gia
    private List<Club> clubList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        spinnerClub = findViewById(R.id.spinner_club);
        etContent = findViewById(R.id.et_content);
        etImageUrl = findViewById(R.id.et_image_url);
        imgPreview = findViewById(R.id.img_preview);
        btnFindImage = findViewById(R.id.btn_find_image);
        btnPost = findViewById(R.id.btn_post);

        // Nút back
        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        email = getIntent().getStringExtra("email");

        // Preview ảnh khi dán link
        etImageUrl.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String url = s.toString().trim();
                if (!url.isEmpty() && (url.startsWith("http://") || url.startsWith("https://"))) {
                    imgPreview.setVisibility(View.VISIBLE);
                    Glide.with(NewPostActivity.this).load(url).into(imgPreview);
                } else {
                    imgPreview.setVisibility(View.GONE);
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnFindImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://unsplash.com/s/photos"));
            startActivity(intent);
        });

        // Lấy danh sách CLB user đã tham gia từ Firestore
        loadUserClubs();

        btnPost.setOnClickListener(v -> {
            String content = etContent.getText().toString().trim();
            String imageUrl = etImageUrl.getText().toString().trim();

            // Nếu chưa có CLB: Hiện popup, không đăng
            if (clubList.isEmpty()) {
                new AlertDialog.Builder(this)
                        .setTitle("Bạn chưa tham gia CLB nào")
                        .setMessage("Bạn cần tham gia ít nhất một câu lạc bộ để đăng bài!")
                        .setPositiveButton("OK", null)
                        .setNegativeButton("Khám phá CLB", (dialog, which) -> {
                            // TODO: Mở màn hình khám phá CLB nếu muốn
                            // startActivity(new Intent(this, ExploreClubActivity.class));
                        })
                        .show();
                return;
            }

            if (content.isEmpty()) {
                etContent.setError("Vui lòng nhập nội dung");
                return;
            }

            int selectedIdx = spinnerClub.getSelectedItemPosition();
            if (selectedIdx < 0) {
                Toast.makeText(this, "Bạn cần chọn câu lạc bộ để đăng bài!", Toast.LENGTH_SHORT).show();
                return;
            }

            Club selectedClub = clubList.get(selectedIdx);
            uploadPost(content, imageUrl, selectedClub);
        });
    }

    private void loadUserClubs() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Clubs")
                .whereArrayContains("members", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    clubList.clear();
                    List<String> clubNameList = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String clubName = doc.getString("clubName");
                        String clubId = doc.getId();
                        String clubAvatar = doc.getString("clubAvatarUrl");
                        clubList.add(new Club(clubId, clubName, clubAvatar));
                        clubNameList.add(clubName != null ? clubName : "(Không tên)");
                    }

                    if (clubList.isEmpty()) {
                        // Hiện chỉ 1 dòng spinner, vẫn enable để hiển thị cho đẹp
                        clubNameList = Collections.singletonList("Bạn chưa tham gia câu lạc bộ nào");
                        spinnerClub.setEnabled(false);
                    } else {
                        spinnerClub.setEnabled(true);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, clubNameList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerClub.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi lấy danh sách CLB: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    spinnerClub.setEnabled(false);
                });
    }

    private void uploadPost(String content, String imageUrl, Club club) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String postId = db.collection("posts").document().getId();

        // Lấy user info để lưu vào bài post (lấy avatar, tên...)
        db.collection("Users").document(email)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String userName = documentSnapshot.getString("fullName");
                    String userAvatarUrl = documentSnapshot.getString("photoUrl");

                    Map<String, Object> post = new HashMap<>();
                    post.put("postId", postId);
                    post.put("authorEmail", email);
                    post.put("userName", userName != null ? userName : "");
                    post.put("userAvatarUrl", userAvatarUrl != null ? userAvatarUrl : "");
                    post.put("content", content);
                    post.put("imageUrl", imageUrl);
                    post.put("createdAt", new Date());

                    // Thông tin club
                    post.put("clubId", club.clubId);
                    post.put("clubName", club.clubName);
                    post.put("clubAvatarUrl", club.clubAvatarUrl);

                    db.collection("posts").document(postId).set(post)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(NewPostActivity.this, "Đăng bài thành công!", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(NewPostActivity.this, "Lỗi đăng bài: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                            );
                });
    }

    // Class đại diện cho Club
    private static class Club {
        String clubId, clubName, clubAvatarUrl;
        Club(String id, String name, String avatar) {
            clubId = id; clubName = name; clubAvatarUrl = avatar;
        }
    }
}
