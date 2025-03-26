package com.example.iceteastore.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.iceteastore.R;
import com.example.iceteastore.adapters.OrderConfirmationAdapter;
import com.example.iceteastore.daos.BillDAO;
import com.example.iceteastore.daos.ShoppingCartDAO;
import com.example.iceteastore.models.Bill;
import com.example.iceteastore.models.ShoppingCart;
import com.example.iceteastore.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderConfirmationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderConfirmationAdapter adapter;
    private TextView tvTotalPrice;
    private Button btnConfirmOrder;
    private List<ShoppingCart> cartItems;
    private double totalPrice;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);

        recyclerView = findViewById(R.id.recyclerView);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnConfirmOrder = findViewById(R.id.btnConfirmOrder);

        // Nhận danh sách sản phẩm từ Intent
        cartItems = (List<ShoppingCart>) getIntent().getSerializableExtra("cartItems");

        if (cartItems == null || cartItems.isEmpty()) {
            Toast.makeText(this, "There are no products in the order!", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Hiển thị danh sách sản phẩm
        adapter = new OrderConfirmationAdapter(cartItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Tính tổng tiền
        totalPrice = calculateTotalPrice();
        tvTotalPrice.setText("Total: $" + totalPrice);

        // Xác nhận đơn hàng
        btnConfirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmOrder();
            }
        });

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            finish(); // Đóng RegisterActivity và quay về LoginActivity
        });
    }

    // Lấy username từ session
    private String getLoggedInUsername() {
        SessionManager sessionManager = new SessionManager(this);
        return sessionManager.getLoggedInUser();
    }

    // Tính tổng tiền
    private double calculateTotalPrice() {
        double total = 0;
        for (ShoppingCart item : cartItems) {
            total += item.getPrice() * item.getQuantity();
        }
        return total;
    }

    // Xác nhận đơn hàng
    // Xác nhận đơn hàng
    private void confirmOrder() {
        String username = getLoggedInUsername(); // Lấy username từ session

        if (username == null) {
            Toast.makeText(this, "You must sign in!", Toast.LENGTH_SHORT).show();
            return;
        }
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(currentDate);
            Bill bill = new Bill(0, date, totalPrice, username, "Pending", null);
            BillDAO billDAO = new BillDAO(this);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            long billId = billDAO.insertBill(sdf.format(date), totalPrice, username, "Pending");

            if (billId != -1) {
                // Xóa giỏ hàng sau khi đặt hàng thành công
                ShoppingCartDAO shoppingCartDAO = new ShoppingCartDAO(this);
                shoppingCartDAO.clearCart(username);

                Toast.makeText(this, "Order confirmed!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OrderConfirmationActivity.this, OrdersuccessfulActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Order failed!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating order!", Toast.LENGTH_SHORT).show();
        }
    }

}
