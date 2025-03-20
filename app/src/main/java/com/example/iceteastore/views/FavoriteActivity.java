package com.example.iceteastore.views;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iceteastore.R;
import com.example.iceteastore.adapters.FavoriteAdapter;
import com.example.iceteastore.daos.FavoriteDAO;
import com.example.iceteastore.models.Product;

import java.util.List;

public class FavoriteActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FavoriteAdapter favoriteAdapter;
    private FavoriteDAO favoriteDAO;
    private String username = "current_user"; // Thay bằng username thực tế
    private ImageButton btnBack;
    private TextView tvEmptyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        recyclerView = findViewById(R.id.recyclerViewFavorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        favoriteDAO = new FavoriteDAO(this);
        List<Product> favoriteProducts = favoriteDAO.getFavoriteProducts(username);

        favoriteAdapter = new FavoriteAdapter(this, favoriteProducts);
        recyclerView.setAdapter(favoriteAdapter);

        // Khai báo TextView
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);

        // Kiểm tra danh sách rỗng hay không
        if (favoriteProducts.isEmpty()) {
            tvEmptyMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmptyMessage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            finish();
        });
    }
}
