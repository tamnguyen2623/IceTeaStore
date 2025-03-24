//orderActivity
package com.example.iceteastore.views;



import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iceteastore.R;
import com.example.iceteastore.adapters.OrderAdapter;
import com.example.iceteastore.daos.OrderDAO;
import com.example.iceteastore.models.Order;

import java.util.List;

public class orderActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private OrderDAO orderDAO;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        recyclerView = findViewById(R.id.recyclerViewOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderDAO = new OrderDAO(this);
        List<Order> orderList = orderDAO.getAllOrders();

        if (orderList.isEmpty()) {
            Log.d("ORDER", "Không có đơn hàng nào!");
        }

        orderAdapter = new OrderAdapter(orderList);
        recyclerView.setAdapter(orderAdapter);
    }
}
