package com.example.clubhub.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.clubhub.homepage.HomePage;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide; // Để load avatar nếu có link (thêm thư viện Glide vào gradle nếu dùng)
import com.example.clubhub.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
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

        imgAvatar = findViewById(R.id.img_avatar);
        imgEdit = findViewById(R.id.img_edit);
        tvName = findViewById(R.id.tv_name);
        tvEmail = findViewById(R.id.tv_email);
        tvPhone = findViewById(R.id.tv_phone);
        btnLogout = findViewById(R.id.btn_logout);

        // Lấy email truyền sang từ Login/SignUp
        email = getIntent().getStringExtra("email");

        // Load thông tin user từ Firestore
        loadUserProfile(email);

        // Nút Edit: sang EditProfileActivity
        imgEdit.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            // Xoá session nếu có
            getSharedPreferences("USER_SESSION", MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply();

            // Chuyển về màn Login
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });



        // Navigation bottom (tuỳ bạn setup logic chuyển tab)
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.nav_profile);

        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_post) {
                Intent intent = new Intent(ProfileActivity.this, HomePage.class);
                startActivity(intent);
                return true;
            }

            return false; // Mặc định không xử lý các tab khác
        });
    }

    // Load profile từ Firestore
    private void loadUserProfile(String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(email)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("fullName");
                        String phone = documentSnapshot.getString("phoneNumber");
                        String avatar = documentSnapshot.getString("photoUrl");
                        tvName.setText(name != null ? name : "");
                        tvEmail.setText(email);
                        tvPhone.setText(phone != null ? phone : "");

                        if (avatar != null && !avatar.isEmpty()) {
                            Glide.with(this).load(avatar).into(imgAvatar);
                        } else {
                            imgAvatar.setImageResource(R.drawable.ic_user_avt_default);
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load profile: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // Khi trở lại từ EditProfile sẽ load lại user info
    @Override
    protected void onResume() {
        super.onResume();
        loadUserProfile(email);
    }

}
