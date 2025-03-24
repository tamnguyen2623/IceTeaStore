package com.example.iceteastore.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.iceteastore.R;

public class OrdersuccessfulActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ordersuccessful);

    }

    // Nút Quay về trang chủ
    // Hàm xử lý khi nhấn nút "View Order"
    public void vieworder(View view) {
        Intent intent = new Intent(this, orderActivity.class);
        startActivity(intent);
    }

    // Hàm xử lý khi nhấn nút "Back to Home"
    public void backhome(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

}