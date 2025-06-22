package com.example.clubhub.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.clubhub.R;
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

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String name = etName.getText().toString().trim();
                String password = etPassword.getText().toString();

                boolean valid = true;

                // Email validate
                if (email.isEmpty() || !email.contains("@")) {
                    tvErrorEmail.setText("Invalid email address. Missing '@' symbol.");
                    valid = false;
                } else {
                    tvErrorEmail.setText("");
                }

                // Name validate: không có số, không rỗng
                if (name.isEmpty() || !name.matches("^[^0-9]+$")) {
                    tvErrorName.setText("Invalid name. Names cannot contain numbers.");
                    valid = false;
                } else {
                    tvErrorName.setText("");
                }

                // Password validate
                if (!isValidPassword(password)) {
                    tvErrorPassword.setText("Use at least 8 characters, including a capital letter, number, and symbol.");
                    valid = false;
                } else {
                    tvErrorPassword.setText("");
                }

                if (!valid) return;

                Map<String, Object> user = new HashMap<>();
                user.put("email", email);
                user.put("passwordHash", password); // Demo, không nên lưu plain text!
                user.put("fullName", name);
                user.put("phoneNumber", "");
                user.put("photoUrl", "");
                user.put("createdAt", new Date());

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Users")
                        .document(email)
                        .set(user)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(SignUpActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        })
                        .addOnFailureListener(e -> Toast.makeText(SignUpActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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
