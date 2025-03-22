package com.example.iceteastore.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iceteastore.R;
import com.example.iceteastore.adapters.ProductManagementAdapter;
import com.example.iceteastore.daos.ProductDAO;
import com.example.iceteastore.models.Product;

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

    private void reloadProductList() {
        List<Product> updatedList = productDAO.getAllProducts(); // Lấy danh sách sản phẩm mới từ DB

        if (updatedList != null) {
            productList.clear();
            productList.addAll(updatedList);
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged(); // Cập nhật RecyclerView
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        reloadProductList(); // Load lại danh sách sản phẩm khi quay về màn hình này
    }


}