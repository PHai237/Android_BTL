package com.example.clubhub.club;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.clubhub.R;
import com.google.firebase.firestore.FirebaseFirestore;
import android.widget.ArrayAdapter;

public class EditClubActivity extends AppCompatActivity {

    private EditText etClubName, etClubDesc;
    private AutoCompleteTextView actvVisibility, actvType;
    private Button btnSaveClub;
    private String clubId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_club);

        // Nhận thông tin từ Intent
        clubId = getIntent().getStringExtra("clubId");
        if (clubId == null) {
            Toast.makeText(this, "Không tìm thấy câu lạc bộ!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etClubName = findViewById(R.id.et_club_name);
        etClubDesc = findViewById(R.id.et_club_desc);
        actvVisibility = findViewById(R.id.actv_visibility);
        actvType = findViewById(R.id.actv_type);
        btnSaveClub = findViewById(R.id.btn_save_club);

        // Điền dữ liệu cũ vào các trường
        loadClubData();

        // Lưu câu lạc bộ khi nhấn nút Save
        btnSaveClub.setOnClickListener(v -> saveClubInfo());

        // Quay lại màn hình ClubDetail khi nhấn Back
        findViewById(R.id.btn_back).setOnClickListener(v -> onBackPressed());

        // Thiết lập Adapter cho Visibility và Type
        setupVisibilityAdapter();
        setupTypeAdapter();
    }

    // Lấy dữ liệu câu lạc bộ từ Firestore và điền vào các trường
    private void loadClubData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Clubs").document(clubId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String clubName = doc.getString("name");
                        String clubDesc = doc.getString("description");
                        String visibility = doc.getBoolean("isPublic") ? "Public" : "Private";
                        String type = doc.getString("type");

                        etClubName.setText(clubName);
                        etClubDesc.setText(clubDesc);
                        actvVisibility.setText(visibility);
                        actvType.setText(type);
                    } else {
                        Toast.makeText(this, "Không tìm thấy câu lạc bộ!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Không thể tải dữ liệu câu lạc bộ!", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    // Cập nhật thông tin câu lạc bộ đã chỉnh sửa
    private void saveClubInfo() {
        String clubName = etClubName.getText().toString().trim();
        String clubDesc = etClubDesc.getText().toString().trim();
        String visibility = actvVisibility.getText().toString().trim();
        String type = actvType.getText().toString().trim();

        if (clubName.isEmpty() || clubDesc.isEmpty() || visibility.isEmpty() || type.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật thông tin câu lạc bộ vào Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Clubs").document(clubId)
                .update("name", clubName, "description", clubDesc, "isPublic", visibility.equals("Public"), "type", type)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditClubActivity.this, "Câu lạc bộ đã được chỉnh sửa!", Toast.LENGTH_SHORT).show();

                    // Quay lại ClubDetailActivity với dữ liệu câu lạc bộ đã chỉnh sửa
                    Intent intent = new Intent(EditClubActivity.this, ClubDetailActivity.class);
                    intent.putExtra("clubId", clubId);
                    startActivity(intent);
                    finish(); // Quay lại màn hình trước
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditClubActivity.this, "Không thể lưu thông tin câu lạc bộ!", Toast.LENGTH_SHORT).show();
                });
    }

    // Thiết lập Adapter cho Visibility (Public / Private)
    private void setupVisibilityAdapter() {
        String[] visibilityOptions = {"Public", "Private"};
        ArrayAdapter<String> visibilityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, visibilityOptions);
        actvVisibility.setAdapter(visibilityAdapter);

        // Đảm bảo người dùng có thể chọn giá trị từ dropdown khi bấm
        actvVisibility.setOnClickListener(v -> actvVisibility.showDropDown());
    }

    // Thiết lập Adapter cho Type (Football, IT)
    private void setupTypeAdapter() {
        String[] typeOptions = {"Football", "IT"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, typeOptions);
        actvType.setAdapter(typeAdapter);

        // Đảm bảo người dùng có thể chọn giá trị từ dropdown khi bấm
        actvType.setOnClickListener(v -> actvType.showDropDown());
    }
}
