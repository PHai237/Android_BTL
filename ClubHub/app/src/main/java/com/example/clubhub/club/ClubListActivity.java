package com.example.clubhub.club;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.clubhub.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ClubListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_list);

        // Nút thêm club (FrameLayout ở góc phải trên header, id: btn_add_club)
        FrameLayout btnAddClub = findViewById(R.id.btn_add_club);

        // Lấy email từ Intent hoặc SharedPreferences (ưu tiên intent trước)
        String email = getIntent().getStringExtra("email");
        if (email == null) {
            SharedPreferences prefs = getSharedPreferences("USER_SESSION", MODE_PRIVATE);
            email = prefs.getString("email", null);
        }

        if (email == null) {
            btnAddClub.setVisibility(View.GONE);
        } else {
            btnAddClub.setVisibility(View.VISIBLE);
            String finalEmail = email;
            btnAddClub.setOnClickListener(v -> {
                Intent intent = new Intent(ClubListActivity.this, CreateClubActivity.class);
                // Đúng chuẩn, phải truyền key là "userId"
                intent.putExtra("userId", finalEmail);
                startActivity(intent);
            });
        }


        // Xử lý bottom navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.nav_club);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_club) {
                // Đã ở trang Club, không làm gì cả
                return true;
            }
            if (itemId == R.id.nav_post) {
                Intent intent = new Intent(ClubListActivity.this, com.example.clubhub.homepage.HomePage.class);
                startActivity(intent);
                finish();
                return true;
            }
            if (itemId == R.id.nav_profile) {
                SharedPreferences prefs = getSharedPreferences("USER_SESSION", MODE_PRIVATE);
                String emailPref = prefs.getString("email", null);
                Intent intent;
                if (emailPref == null) {
                    intent = new Intent(ClubListActivity.this, com.example.clubhub.profile.LoginActivity.class);
                } else {
                    intent = new Intent(ClubListActivity.this, com.example.clubhub.profile.ProfileActivity.class);
                    intent.putExtra("email", emailPref);
                }
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });

        // TODO: Viết code load danh sách club và hiển thị trong layout của bạn
    }
}
