package com.example.clubhub.profile;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.clubhub.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Nếu có truyền dữ liệu user, lấy ở đây
        // Ví dụ lấy qua Intent (nếu cần)
        // String userName = getIntent().getStringExtra("name");

        // Thiết lập bottom nav sáng ở PROFILE
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.nav_profile);

        // Hiển thị thông tin user (fake/demo)
        TextView tvName = findViewById(R.id.tv_name);
        TextView tvEmail = findViewById(R.id.tv_email);
        TextView tvPhone = findViewById(R.id.tv_phone);
        ImageView imgAvatar = findViewById(R.id.img_avatar);

        tvName.setText("Bruce Lee");
        tvEmail.setText("user@gmail.com");
        tvPhone.setText("0913789654");
        // imgAvatar.setImageResource(R.drawable.your_avatar_drawable); // Nếu dùng ảnh local
        // Hoặc dùng Glide nếu dùng link online
    }
}


