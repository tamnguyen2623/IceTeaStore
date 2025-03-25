package com.example.iceteastore.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.iceteastore.R;
import com.example.iceteastore.daos.UserDAO;
import com.example.iceteastore.models.User;
import com.example.iceteastore.utils.SessionManager;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    private TextView tvFavourite, btnEditProfile, tvBirthday, tvUsername;
    //            tvCart, tvSignOut;
    private SessionManager sessionManager;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Ánh xạ view
        btnEditProfile = findViewById(R.id.btnEditProfile);
        tvFavourite = findViewById(R.id.ivFavoriteList);
        //tvCart = findViewById(R.id.tvCart);
        //tvSignOut = findViewById(R.id.tvSignOut);
        tvBirthday = findViewById(R.id.tvBirthday);
        tvUsername = findViewById(R.id.tvUsername);
        sessionManager = new SessionManager(this);
        userDAO = new UserDAO(this);
        String loggedInUsername = sessionManager.getLoggedInUser();

        if (loggedInUsername != null) {
            loadUserProfile(loggedInUsername);
        }
        // Chuyển sang trang EditProfileActivity khi bấm nút Edit Profile
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        // Chuyển sang trang Favourite
        tvFavourite.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, FavoriteActivity.class);
            startActivity(intent);
        });
//
//        // Chuyển sang trang Cart
//        tvCart.setOnClickListener(v -> {
//            Intent intent = new Intent(ProfileActivity.this, CartActivity.class);
//            startActivity(intent);
//        });

        // Đăng xuất (quay lại màn hình đăng nhập)
//        tvSignOut.setOnClickListener(v -> {
//            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Xóa ngăn xếp activity
//            startActivity(intent);
//            finish();
//        });
    }

    private void loadUserProfile(String username) {
        User user = userDAO.getUserByUsername(username);
        if (user != null) {
            tvBirthday.setText(user.getBirthday());
            tvUsername.setText(user.getUsername());
        }
    }
}
