package com.example.clubhub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class IntroducePage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce_page); // Đảm bảo layout đúng

        // Tìm nút Explore trong IntroductPage
        Button exploreButton = findViewById(R.id.btn_explore);

        // Sự kiện khi người dùng nhấn nút
        exploreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến MainActivity khi nhấn nút Explore
                Intent intent = new Intent(IntroducePage.this, MainActivity.class);
                startActivity(intent);
                finish();  // Đảm bảo đóng IntroductPage khi chuyển đến MainActivity
            }
        });
    }
}