package com.example.clubhub.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.clubhub.R;
import com.example.clubhub.homepage.HomePage;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private EditText etEmail, etName, etPassword;
    private TextView tvErrorEmail, tvErrorName, tvErrorPassword;
    private MaterialButton btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etEmail = findViewById(R.id.etEmail);
        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        tvErrorEmail = findViewById(R.id.tvErrorEmail);
        tvErrorName = findViewById(R.id.tvErrorName);
        tvErrorPassword = findViewById(R.id.tvErrorPassword);
        btnSignUp = findViewById(R.id.btnSignUp);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.nav_profile);

        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_post) {
                Intent intent = new Intent(SignUpActivity.this, HomePage.class);
                startActivity(intent);
                return true;
            }

            return false; // Mặc định không xử lý các tab khác
        });
        TextView tvLogin = findViewById(R.id.tv_login);
        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Tùy, có thể để hoặc bỏ (nên để để back không quay lại SignUp nữa)
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String name = etName.getText().toString().trim();
                String password = etPassword.getText().toString();

                boolean valid = true;

                // Validate email
                if (email.isEmpty() || !email.contains("@")) {
                    tvErrorEmail.setText("Invalid email address. Missing '@' symbol.");
                    tvErrorEmail.setVisibility(View.VISIBLE);
                    valid = false;
                } else {
                    tvErrorEmail.setText("");
                    tvErrorEmail.setVisibility(View.GONE);
                }

                // Validate name: không chứa số và không rỗng
                if (name.isEmpty() || !name.matches("^[^0-9]+$")) {
                    tvErrorName.setText("Invalid name. Names cannot contain numbers.");
                    tvErrorName.setVisibility(View.VISIBLE);
                    valid = false;
                } else {
                    tvErrorName.setText("");
                    tvErrorName.setVisibility(View.GONE);
                }

                // Validate password
                if (!isValidPassword(password)) {
                    tvErrorPassword.setText("Use at least 8 characters, including a capital letter, number, and symbol.");
                    tvErrorPassword.setVisibility(View.VISIBLE);
                    valid = false;
                } else {
                    tvErrorPassword.setText("");
                    tvErrorPassword.setVisibility(View.GONE);
                }

                if (!valid) return;

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // Kiểm tra email đã tồn tại chưa
                db.collection("Users").document(email).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                tvErrorEmail.setText("Email already exists.");
                                tvErrorEmail.setVisibility(View.VISIBLE);
                            } else {
                                // 1. Lấy userID lớn nhất
                                db.collection("Users")
                                        .orderBy("userID", com.google.firebase.firestore.Query.Direction.DESCENDING)
                                        .limit(1)
                                        .get()
                                        .addOnSuccessListener(queryDocumentSnapshots -> {
                                            int nextId = 1; // Nếu chưa có ai
                                            if (!queryDocumentSnapshots.isEmpty()) {
                                                String maxUserIdStr = queryDocumentSnapshots.getDocuments().get(0).getString("userID");
                                                try {
                                                    nextId = Integer.parseInt(maxUserIdStr) + 1;
                                                } catch (Exception e) {
                                                    nextId = 1;
                                                }
                                            }
                                            String newUserId = String.format("%04d", nextId);

                                            // 2. Tạo dữ liệu user mới
                                            Map<String, Object> user = new HashMap<>();
                                            user.put("email", email);
                                            user.put("passwordHash", password); // KHÔNG dùng plain text ngoài demo!
                                            user.put("fullName", name);
                                            user.put("phoneNumber", "");
                                            user.put("photoUrl", "");
                                            user.put("createdAt", new Date());
                                            user.put("userID", newUserId);

                                            // 3. Lưu vào Firestore
                                            db.collection("Users")
                                                    .document(email)
                                                    .set(user)
                                                    .addOnSuccessListener(aVoid -> {
                                                        Toast.makeText(SignUpActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

                                                        // Lưu email vào SharedPreferences (Session)
                                                        SharedPreferences prefs = getSharedPreferences("USER_SESSION", MODE_PRIVATE);
                                                        prefs.edit().putString("email", email).apply();

                                                        // Sang ProfileActivity
                                                        Intent intent = new Intent(SignUpActivity.this, ProfileActivity.class);
                                                        intent.putExtra("email", email);
                                                        intent.putExtra("name", name);
                                                        intent.putExtra("phone", ""); // Mặc định rỗng khi đăng ký mới
                                                        intent.putExtra("photoUrl", ""); // Mặc định rỗng
                                                        startActivity(intent);
                                                        finish();
                                                    })

                                                    .addOnFailureListener(e -> Toast.makeText(SignUpActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(SignUpActivity.this, "Lỗi khi lấy userID: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                            }
                        })
                        .addOnFailureListener(e -> Toast.makeText(SignUpActivity.this, "Lỗi kiểm tra email: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    // Password: >=8 ký tự, có chữ hoa, số, ký tự đặc biệt
    private boolean isValidPassword(String password) {
        if (password.length() < 8) return false;
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\",.<>/?].*");
        return hasUpper && hasDigit && hasSpecial;
    }
}
