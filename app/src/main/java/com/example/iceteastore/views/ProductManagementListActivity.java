package com.example.iceteastore.views;

import android.content.Intent;
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
import com.example.iceteastore.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

public class ProductManagementListActivity extends AppCompatActivity {
    private ArrayList<Product> productList;
    private ProductManagementAdapter adapter;
    private RecyclerView recyclerView;
    private ProductDAO productDAO;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        sessionManager = new SessionManager(this);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        // Đánh dấu Drink là item được chọn
        bottomNavigationView.setSelectedItemId(R.id.product);
        // Xử lý chuyển trang khi bấm vào item navbar
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.order) {
//                    startActivity(new Intent(HomeActivity.this, ProductManagementActivity.class));
//                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.logout) {
                    sessionManager.logout();
                    startActivity(new Intent(ProductManagementListActivity.this, LoginActivity.class));
                    overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
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
