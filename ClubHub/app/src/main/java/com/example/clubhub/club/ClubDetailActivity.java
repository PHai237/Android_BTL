package com.example.clubhub.club;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.clubhub.R;

public class ClubDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_detail);
        // TODO: Hiển thị chi tiết club từ Firestore dựa trên clubId nhận từ intent
    }
}
