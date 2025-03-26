package com.example.iceteastore.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.iceteastore.R;
import com.example.iceteastore.adapters.BillOfUserAdapter;
import com.example.iceteastore.daos.BillDAO;
import com.example.iceteastore.models.Bill;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import java.util.List;

public class orderActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BillOfUserAdapter billAdapter;
    private BillDAO billDAO;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.bill);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.shopping_cart) {
                    startActivity(new Intent(orderActivity.this, ShoppingCartActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.home) {
                    startActivity(new Intent(orderActivity.this, HomeActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.profile) {
                    startActivity(new Intent(orderActivity.this, ProfileActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        });

        recyclerView = findViewById(R.id.recyclerViewOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        billDAO = new BillDAO(this);
        List<Bill> billList = billDAO.getAllBills();

        if (billList.isEmpty()) {
            Log.d("BILL", "No bills found!");
        }

        billAdapter = new BillOfUserAdapter(billList);
        recyclerView.setAdapter(billAdapter);
    }
}
