package com.example.iceteastore.views;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.iceteastore.R;
import com.example.iceteastore.daos.ProductDAO;
import com.example.iceteastore.daos.ShoppingCartDAO;
import com.example.iceteastore.daos.UserDAO;
import com.example.iceteastore.models.Product;
import com.example.iceteastore.models.ShoppingCart;
import com.example.iceteastore.models.User;
import com.example.iceteastore.utils.SessionManager;

public class DetailActivity extends AppCompatActivity {
    private ImageView ivProductImage;
    private TextView tvProductName, tvRating, tvPrice, tvDescription;
    private Button btnOrder;
    private ProductDAO productDAO;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Ánh xạ UI
        ivProductImage = findViewById(R.id.ivProductImage);
        tvProductName = findViewById(R.id.tvProductName);
        tvRating = findViewById(R.id.tvRating);
        tvPrice = findViewById(R.id.tvPrice);
        tvDescription = findViewById(R.id.tvDescription);
        btnOrder = findViewById(R.id.btn_order);

        productDAO = new ProductDAO(this);

        // Nhận ID sản phẩm từ Intent
        int productId = getIntent().getIntExtra("product_id", -1);
        Log.d("DetailActivity", "Received product ID: " + productId);

        if (productId != -1) {
            product = productDAO.getProductById(productId);
            if (product != null) {
                tvProductName.setText(product.getName());
                tvRating.setText("⭐ " + product.getRating());
//                tvPrice.setText("$" + product.getPrice());
                tvDescription.setText(product.getDescription());
            } else {
                Toast.makeText(this, "Product not found!", Toast.LENGTH_SHORT).show();
                finish(); // Đóng activity nếu không tìm thấy sản phẩm
            }
        }
        // Xử lý khi nhấn nút đặt hàng
        btnOrder.setOnClickListener(v -> {
            Toast.makeText(this, "Added to cart: " + product.getName(), Toast.LENGTH_SHORT).show();
        });
    }

}
