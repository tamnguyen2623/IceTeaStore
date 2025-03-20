package com.example.iceteastore.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iceteastore.R;
import com.example.iceteastore.adapters.ProductManagementAdapter;
import com.example.iceteastore.daos.ProductDAO;
import com.example.iceteastore.models.Product;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class ProductManagementListActivity extends AppCompatActivity {
    private ArrayList<Product> productList;
    private ProductManagementAdapter adapter;
    private RecyclerView recyclerView;
    private ProductDAO productDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        SharedPreferences sharedPreferences = getSharedPreferences("LoginSession", Context.MODE_PRIVATE);
        String role = sharedPreferences.getString("role", null);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.product);

        // Ẩn/hiện menu theo role
        if ("admin".equals(role)) {
            bottomNavigationView.getMenu().findItem(R.id.home).setVisible(false);
            bottomNavigationView.getMenu().findItem(R.id.shopping_cart).setVisible(false);
            bottomNavigationView.getMenu().findItem(R.id.profile).setVisible(false);
            bottomNavigationView.getMenu().findItem(R.id.product).setVisible(true);
            bottomNavigationView.getMenu().findItem(R.id.order).setVisible(true);
        }

        // Xử lý chuyển trang khi bấm vào item navbar
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.order) {
//                    startActivity(new Intent(HomeActivity.this, ProductManagementActivity.class));
//                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        if (recyclerView == null) {
            throw new NullPointerException("RecyclerView is null. Check the layout file!");
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        productDAO = new ProductDAO(this);

        productList = (ArrayList<Product>) productDAO.getAllProducts();
        Log.d("ProductListActivity", "Number of products: " + productList.size());

        if (productList == null) {
            productList = new ArrayList<>();
        }
        Button btnAddProduct = findViewById(R.id.btnAddProduct);
        btnAddProduct.setOnClickListener(view -> {
            Intent intent = new Intent(ProductManagementListActivity.this, ProductManagementActivity.class);
            startActivity(intent);
        });

        adapter = new ProductManagementAdapter(this, productList);
        recyclerView.setAdapter(adapter);
    }

}
