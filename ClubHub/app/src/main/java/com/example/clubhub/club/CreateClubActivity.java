package com.example.clubhub.club;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.clubhub.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class CreateClubActivity extends AppCompatActivity {
    private TextInputEditText etName, etDesc;
    private AutoCompleteTextView actvVisibility, actvType;
    private Button btnCreate;
    private ImageView btnBack;
    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_club);

        // Lấy userId từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("USER_SESSION", MODE_PRIVATE);
        userId = prefs.getString("userId", null);

        if (userId == null) {
            Toast.makeText(this, "Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etName = findViewById(R.id.et_club_name);
        etDesc = findViewById(R.id.et_club_desc);
        actvVisibility = findViewById(R.id.actv_visibility);
        actvType = findViewById(R.id.actv_type);
        btnCreate = findViewById(R.id.btn_create_club);
        btnBack = findViewById(R.id.btn_back);

        // Adapter cho Visibility (dropdown)
// Adapter cho Visibility (dropdown)
        String[] visibilities = {"Public", "Private"};
        ArrayAdapter<String> visAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, visibilities
        );
        actvVisibility.setAdapter(visAdapter);
        actvVisibility.setOnClickListener(v -> actvVisibility.showDropDown());

// Adapter cho Type (dropdown)
        String[] types = {"Football", "IT"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, types
        );
        actvType.setAdapter(typeAdapter);
        actvType.setOnClickListener(v -> actvType.showDropDown());


        // Bấm back
        btnBack.setOnClickListener(v -> onBackPressed());

        btnCreate.setOnClickListener(v -> createClub());
    }

    private void createClub() {
        String name = etName.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();
        String visibility = actvVisibility.getText().toString();
        String type = actvType.getText().toString();

        if (name.isEmpty() || visibility.isEmpty() || type.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> club = new HashMap<>();
        club.put("name", name);
        club.put("description", desc);
        club.put("isPublic", visibility.equals("Public"));
        club.put("type", type);
        club.put("createdById", userId);

        db.collection("Clubs")
                .add(club)
                .addOnSuccessListener(documentReference -> {
                    String clubId = documentReference.getId();
                    addUserRole(userId, clubId);

                    Toast.makeText(this, "Tạo club thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CreateClubActivity.this, ClubDetailActivity.class);
                    intent.putExtra("clubId", clubId);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Tạo club thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void addUserRole(String userId, String clubId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> userRole = new HashMap<>();
        userRole.put("userId", userId);
        userRole.put("roleId", "adminCLB");
        userRole.put("clubID", clubId);

        String docId = userId + "_adminCLB_" + clubId;
        db.collection("UserRoles")
                .document(docId)
                .set(userRole);
    }
}
