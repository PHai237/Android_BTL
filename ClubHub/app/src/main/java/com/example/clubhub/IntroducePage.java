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
        setContentView(R.layout.activity_introduce_page);

        Button exploreButton = findViewById(R.id.btn_explore);

        exploreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang HomePage thay vì MainActivity
                Intent intent = new Intent(IntroducePage.this, HomePage.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
