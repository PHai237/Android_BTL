package com.example.clubhub.event;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.clubhub.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreateEventActivity extends AppCompatActivity {

    private EditText etEventName, etEventDate, etEventPlace, etEventTime, etEventImageUrl;
    private Button btnCreateEvent;
    private FirebaseFirestore db;
    private ImageView imgPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        db = FirebaseFirestore.getInstance();

        // Ánh xạ các trường và nút
        etEventName = findViewById(R.id.et_event_name);
        etEventDate = findViewById(R.id.et_event_date);
        etEventPlace = findViewById(R.id.et_event_place);
        etEventTime = findViewById(R.id.et_event_time);
        etEventImageUrl = findViewById(R.id.et_image_url);
        btnCreateEvent = findViewById(R.id.btn_create_event);
        imgPreview = findViewById(R.id.img_preview);

        btnCreateEvent.setOnClickListener(v -> {
            String eventName = etEventName.getText().toString();
            String eventDate = etEventDate.getText().toString();
            String eventPlace = etEventPlace.getText().toString();
            String eventTime = etEventTime.getText().toString();
            String eventImageUrl = etEventImageUrl.getText().toString();

            // Kiểm tra thông tin nhập vào
            if (eventName.isEmpty() || eventDate.isEmpty() || eventPlace.isEmpty() || eventTime.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo sự kiện mới và lưu vào Firestore
            Event newEvent = new Event(eventName, eventDate, eventTime, eventPlace, eventImageUrl, "", "", 0, 0);

            // Lưu sự kiện vào Firestore và lấy documentId làm eventId
            db.collection("events")
                    .add(newEvent)
                    .addOnSuccessListener(documentReference -> {
                        // Sử dụng documentId làm eventId
                        newEvent.setEventId(documentReference.getId());

                        // Cập nhật lại event với eventId
                        documentReference.update("eventId", documentReference.getId())
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Event created successfully", Toast.LENGTH_SHORT).show();
                                    finish();  // Quay lại trang trước sau khi tạo sự kiện
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error updating eventId: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error creating event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
