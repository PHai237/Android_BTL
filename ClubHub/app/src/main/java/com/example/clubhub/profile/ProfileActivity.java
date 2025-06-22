package com.example.clubhub.profile;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.clubhub.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {
    private EditText tvName, tvEmail, tvPhone;
    private Button btnSavePhone, btnLogout;

    private String email, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvName = findViewById(R.id.tv_name);
        tvEmail = findViewById(R.id.tv_email);
        tvPhone = findViewById(R.id.tv_phone);
//        btnSavePhone = findViewById(R.id.btnSavePhone);
        btnLogout = findViewById(R.id.btn_logout);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.nav_profile);

        // Nhận thông tin từ LoginActivity
        email = getIntent().getStringExtra("email");
        name = getIntent().getStringExtra("name");
        String phone = getIntent().getStringExtra("phone");

        tvName.setText(name);
        tvEmail.setText(email);
        tvPhone.setText(phone == null ? "" : phone);

        btnSavePhone.setOnClickListener(v -> {
            String newPhone = tvPhone.getText().toString().trim();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Users").document(email)
                    .update("phoneNumber", newPhone)
                    .addOnSuccessListener(unused -> Toast.makeText(ProfileActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        btnLogout.setOnClickListener(v -> {
            // Quay về Login
            finish();
        });
    }
}
