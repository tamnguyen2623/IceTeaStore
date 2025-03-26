package com.example.iceteastore.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.iceteastore.R;
import com.example.iceteastore.daos.UserDAO;
import com.example.iceteastore.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {
    private EditText inputUsername, inputPassword;
    private Button btnLogin, linkSignup;
    private UserDAO userDAO;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputUsername = findViewById(R.id.inputUsername);
        inputPassword = findViewById(R.id.inputPassword);
        btnLogin = findViewById(R.id.btnLogin);
        linkSignup = findViewById(R.id.linkSignup);

        userDAO = new UserDAO(this);
        sessionManager = new SessionManager(this);

        // Kiểm tra nếu đã đăng nhập trước đó
        String username = sessionManager.getLoggedInUser();
        String role = sessionManager.getUserRole();

        if (username != null && role != null) {
            if ("admin".equals(role)) {
                navigateToAdmin();
            } else {
                navigateToHome();
            }
        }

        btnLogin.setOnClickListener(v -> handleLogin());
        linkSignup.setOnClickListener(v -> navigateToSignup());
    }

    private void handleLogin() {
        String username = inputUsername.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter all required information!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy role từ database khi login thành công
        String role = userDAO.getUserRole(username, password);

        if (role != null) {
            sessionManager.saveLogin(username, role); // Lưu username và role

            Toast.makeText(this, "Sign-in successfully!", Toast.LENGTH_SHORT).show();

            if ("admin".equals(role)) {
                navigateToAdmin();
            } else {
                navigateToHome();
            }
        } else {
            if(!userDAO.isUsernameExists(username)) {
                Toast.makeText(this, "Username does not exist!", Toast.LENGTH_SHORT).show();
            } else if(!userDAO.isPasswordCorrect(username, password)) {
                Toast.makeText(this, "Password is not correct!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void navigateToSignup() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void navigateToAdmin() {
        Intent intent = new Intent(this, ProductManagementListActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
