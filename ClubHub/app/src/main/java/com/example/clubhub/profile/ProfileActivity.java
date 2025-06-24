package com.example.clubhub.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.clubhub.homepage.HomePage;
import com.example.clubhub.profile.LoginActivity; // Import LoginActivity
import com.example.clubhub.club.ClubListActivity; // Import ClubListActivity
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.clubhub.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {
    private ImageView imgAvatar, imgEdit;
    private TextView tvName, tvEmail, tvPhone;
    private Button btnLogout;

    private String email; // Sẽ truyền email từ Login/SignUp

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Kết nối các view
        imgAvatar = findViewById(R.id.img_avatar);
        imgEdit = findViewById(R.id.img_edit);
        tvName = findViewById(R.id.tv_name);
        tvEmail = findViewById(R.id.tv_email);
        tvPhone = findViewById(R.id.tv_phone);
        btnLogout = findViewById(R.id.btn_logout);

        // Lấy email từ SharedPreferences để kiểm tra đăng nhập
        email = getSharedPreferences("USER_SESSION", MODE_PRIVATE).getString("email", null);

        if (email == null) {
            // Nếu không có email trong session (chưa đăng nhập), chuyển về LoginActivity
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return; // Dừng tiếp tục xử lý
        }

        // Load thông tin người dùng từ Firestore
        loadUserProfile(email);

        // Nút Edit: chuyển sang EditProfileActivity
        imgEdit.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        // Nút Đăng xuất
        btnLogout.setOnClickListener(v -> {
            // Xóa session khi đăng xuất
            getSharedPreferences("USER_SESSION", MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply();

            // Chuyển về màn hình đăng nhập
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear tất cả các Activity trước đó
            startActivity(intent);
            finish();
        });

        // Setup BottomNavigationView
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.nav_profile);

        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_club) {
                // Kiểm tra xem người dùng đã đăng nhập chưa
                if (email == null) {
                    // Nếu chưa đăng nhập, yêu cầu đăng nhập và chuyển về LoginActivity
                    Toast.makeText(ProfileActivity.this, "Bạn cần đăng nhập để xem câu lạc bộ", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Nếu đã đăng nhập, chuyển sang ClubListActivity
                    Intent intent = new Intent(ProfileActivity.this, ClubListActivity.class);
                    startActivity(intent);
                }
                return true;
            } else if (item.getItemId() == R.id.nav_post) {
                // Chuyển sang HomePage nếu chọn mục "Post"
                Intent intent = new Intent(ProfileActivity.this, HomePage.class);
                startActivity(intent);
                return true;
            }
            return false; // Mặc định không xử lý các tab khác
        });
    }

    // Load thông tin user từ Firestore
    private void loadUserProfile(String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(email)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("fullName");
                        String phone = documentSnapshot.getString("phoneNumber");
                        String avatar = documentSnapshot.getString("photoUrl");

                        // Cập nhật các thông tin user vào các view
                        tvName.setText(name != null ? name : "No name");
                        tvEmail.setText(email);
                        tvPhone.setText(phone != null ? phone : "No phone");

                        // Nếu có URL ảnh avatar, dùng Glide để load ảnh
                        if (avatar != null && !avatar.isEmpty()) {
                            Glide.with(this).load(avatar).into(imgAvatar);
                        } else {
                            imgAvatar.setImageResource(R.drawable.ic_user_avt_default); // Default avatar nếu không có
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load profile: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // Khi trở lại từ EditProfile, load lại thông tin người dùng
    @Override
    protected void onResume() {
        super.onResume();
        loadUserProfile(email);
    }
}
