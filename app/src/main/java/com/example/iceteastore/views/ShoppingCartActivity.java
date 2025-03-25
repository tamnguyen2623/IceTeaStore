package com.example.iceteastore.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iceteastore.R;
import com.example.iceteastore.adapters.ShoppingCartAdapter;
import com.example.iceteastore.daos.ShoppingCartDAO;
import com.example.iceteastore.models.ShoppingCart;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ShoppingCartAdapter adapter;
    private List<ShoppingCart> cartItems;
    private TextView txtTotalPrice, txtEmptyCart;
    private Button btnSendOrder;
    private ImageButton btnBack;
    private ShoppingCartDAO cartDAO;
    private String username = "user123";  // Giả định username (có thể lấy từ SharedPreferences)

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.recyclerViewCart);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        txtEmptyCart = findViewById(R.id.txtEmptyCart);
        btnSendOrder = findViewById(R.id.btnSendOrder);
        btnBack = findViewById(R.id.btnBack);
        cartDAO = new ShoppingCartDAO(this);

        loadCartData();

        adapter = new ShoppingCartAdapter(this, cartItems, username, this::calculateTotalPrice);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        calculateTotalPrice();

        btnBack.setOnClickListener(v -> finish());
        btnSendOrder.setOnClickListener(v -> sendOrder());
    }

    // Lấy dữ liệu giỏ hàng từ SQLite
    private void loadCartData() {
        cartItems = new ArrayList<>();
        cartItems.add(new ShoppingCart("Trà Sữa Truyền Thống", R.drawable.food3, 2, 4.5, 4.8f));
        cartItems.add(new ShoppingCart("Trà Sữa Matcha", R.drawable.food3, 1, 5.0, 4.7f));
        cartItems.add(new ShoppingCart("Trà Đào Cam Sả", R.drawable.food3, 3, 3.8, 4.6f));

        updateEmptyCartView();
    }


    // Cập nhật tổng giá trị đơn hàng
    private void calculateTotalPrice() {
        double total = 0;
        for (ShoppingCart item : cartItems) {
            total += item.getPrice() * item.getQuantity();
        }
        txtTotalPrice.setText(String.format("Total: $%.2f", total));
        updateEmptyCartView();
    }

    // Kiểm tra giỏ hàng có rỗng hay không
    private void updateEmptyCartView() {
        if (cartItems.isEmpty()) {
            txtEmptyCart.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            txtEmptyCart.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    // Xử lý gửi đơn hàng
    private void sendOrder() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng trống! Vui lòng thêm sản phẩm trước khi đặt hàng.", Toast.LENGTH_SHORT).show();
        } else {
            cartDAO.clearCart(username);
            cartItems.clear();
            adapter.notifyDataSetChanged();
            calculateTotalPrice();
            Toast.makeText(this, "Đơn hàng đã gửi thành công!", Toast.LENGTH_SHORT).show();
        }
    }


}
