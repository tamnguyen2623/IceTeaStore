package com.example.iceteastore.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

import com.example.iceteastore.R;
import com.example.iceteastore.daos.UserDAO;
import com.example.iceteastore.models.User;

public class RegisterActivity extends AppCompatActivity {
    private EditText inputUsername, inputFullname, inputPhoneNumber, inputAddress, inputDateOfBirth, inputPassword;
    private Button submitSignup;
    private ImageButton btnBack;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Ánh xạ view
        inputUsername = findViewById(R.id.inputUsername);
        inputFullname = findViewById(R.id.inputFullname);
        inputPhoneNumber = findViewById(R.id.inputPhoneNumber);
        inputAddress = findViewById(R.id.inputAddress);
        inputDateOfBirth = findViewById(R.id.inputDateOfBirth);
        inputPassword = findViewById(R.id.inputPassword);
        submitSignup = findViewById(R.id.submit_signup);
        btnBack = findViewById(R.id.btnBack);

        // Khởi tạo UserDAO
        userDAO = new UserDAO(this);

        // Xử lý khi nhấn nút đăng ký
        submitSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        btnBack.setOnClickListener(v -> {
            finish(); // Đóng RegisterActivity và quay về LoginActivity
        });
    }

    private void registerUser() {
        String username = inputUsername.getText().toString().trim();
        String fullName = inputFullname.getText().toString().trim();
        String phoneNumber = inputPhoneNumber.getText().toString().trim();
        String address = inputAddress.getText().toString().trim();
        String birthday = inputDateOfBirth.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        // Kiểm tra dữ liệu hợp lệ
        if (username.isEmpty() || fullName.isEmpty() || phoneNumber.isEmpty() ||
                address.isEmpty() || birthday.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
        } else if (!phoneNumber.matches("^0\\d{9}$")) {
            Toast.makeText(this, "Invalid phone number!", Toast.LENGTH_SHORT).show();
        } else if (!isValidDate(birthday)) {
            Toast.makeText(this, "Invalid date format!", Toast.LENGTH_SHORT).show();
        } else if (userDAO.isUsernameExists(username)) {
            Toast.makeText(this, "Username already exists!", Toast.LENGTH_SHORT).show();
        } else if (userDAO.isPhoneNumberExists(phoneNumber)) {
            Toast.makeText(this, "Phone number already exists!", Toast.LENGTH_SHORT).show();
        } else {
            // Lưu vào SQLite
            User newUser = new User(username, password, fullName, birthday, phoneNumber, address, "admin");
            boolean isInserted = userDAO.insertUser(newUser);

            if (isInserted) {
                saveUserToSharedPreferences(username);
                Toast.makeText(this, "Sign-up successfully!", Toast.LENGTH_SHORT).show();

                // Điều hướng sang màn hình đăng nhập
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Sign-up failed. Please try again!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isValidDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        sdf.setLenient(false); // Ngăn chặn tự động chỉnh sửa ngày sai

        try {
            sdf.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }



    // Lưu thông tin người dùng vào SharedPreferences
    private void saveUserToSharedPreferences(String username) {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("role", "admin");
        editor.apply();
    }
}
