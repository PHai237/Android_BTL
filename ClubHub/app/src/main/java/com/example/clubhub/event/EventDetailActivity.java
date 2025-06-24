package com.example.clubhub.event;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.clubhub.R;

public class EventDetailActivity extends AppCompatActivity {

    private ImageView imgEventDetail;
    private TextView tvEventName, tvEventDateTime, tvEventPlace, tvRegisteredUsers, tvClubName, tvEventDescription;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        // Mapping Views
        imgEventDetail = findViewById(R.id.img_event_detail);
        tvEventName = findViewById(R.id.tv_event_name);
        tvEventDateTime = findViewById(R.id.tv_event_date_time);
        tvEventPlace = findViewById(R.id.tv_event_place);
        tvRegisteredUsers = findViewById(R.id.tv_registered_users);
        tvClubName = findViewById(R.id.tv_club_name);
        tvEventDescription = findViewById(R.id.tv_event_description);
        btnRegister = findViewById(R.id.btn_register);

        // Nhận dữ liệu event từ Intent
        Intent intent = getIntent();
        Event event = (Event) intent.getSerializableExtra("eventDetails");

        // Kiểm tra nếu event không phải là null
        if (event != null) {
            // Cập nhật dữ liệu vào các view
            tvEventName.setText(event.getEventName());
            tvEventDateTime.setText(event.getEventDate() + " | " + event.getEventTime());
            tvEventPlace.setText(event.getEventPlace());
            tvRegisteredUsers.setText(event.getRegisteredUsers() + " of " + event.getTotalUsers() + " registered");
            tvClubName.setText(event.getClubName());
            tvEventDescription.setText(event.getEventDescription());

            // Tải ảnh
            if (event.getEventImageUrl() != null && !event.getEventImageUrl().isEmpty()) {
                Glide.with(this).load(event.getEventImageUrl()).into(imgEventDetail);
            } else {
                imgEventDetail.setImageResource(R.drawable.sample_img_default); // Default image
            }

            // Xử lý nút đăng ký
            btnRegister.setOnClickListener(v -> {
                // Logic đăng ký sự kiện
            });
        } else {
            // Nếu không có dữ liệu event, có thể hiển thị thông báo lỗi hoặc chuyển hướng
            tvEventName.setText("Error: Event details not available");
        }

        // Xử lý nút back
        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            onBackPressed();  // Quay lại màn hình trước
        });

    }
}
