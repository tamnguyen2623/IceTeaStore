package com.example.iceteastore.views;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iceteastore.R;
import com.example.iceteastore.adapters.BillAdapter;
import com.example.iceteastore.daos.BillDAO;
import com.example.iceteastore.database_helper.DatabaseHelper;
import com.example.iceteastore.models.Bill;
import com.example.iceteastore.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminBillActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BillAdapter billAdapter;
    private List<Bill> billList;
    private DatabaseHelper databaseHelper;
    private BillDAO billDAO;
    private SessionManager sessionManager;


    private Spinner spinnerStatus;
    private String selectedStatus = "All"; // Mặc định hiển thị tất cả

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_bill);

        sessionManager = new SessionManager(this);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        // Đánh dấu Drink là item được chọn
        bottomNavigationView.setSelectedItemId(R.id.order);
        // Xử lý chuyển trang khi bấm vào item navbar
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.product) {
                    startActivity(new Intent(AdminBillActivity.this, ProductManagementListActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.logout) {
                    sessionManager.logout();
                    startActivity(new Intent(AdminBillActivity.this, LoginActivity.class));
                    overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                }
                return false;
            }
        });

        recyclerView = findViewById(R.id.list_bill);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo databaseHelper và BillDAO
        databaseHelper = new DatabaseHelper(this);
        billDAO = new BillDAO(this);

        billList = new ArrayList<>();
        billAdapter = new BillAdapter(this, billList, bill -> {
            Intent intent = new Intent(AdminBillActivity.this, AdminUpdateBillActivity.class);
            intent.putExtra("billId", bill.getId());
            startActivity(intent);
        });

        recyclerView.setAdapter(billAdapter);

        // Kiểm tra nếu không có dữ liệu thì thêm dữ liệu mẫu
        if (!hasBills()) {
            insertSampleData();
        } else {
            loadBills();
        }

        spinnerStatus = findViewById(R.id.spinnerStatus);

// Danh sách trạng thái
        List<String> statusList = new ArrayList<>();
        statusList.add("All");
        statusList.add("Pending");
        statusList.add("Completed");
        statusList.add("Cancelled");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);

        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedStatus = parent.getItemAtPosition(position).toString();
                loadBills(); // Tải lại danh sách khi chọn trạng thái mới
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


    }

    // Kiểm tra xem bảng bills có dữ liệu hay không
    private boolean hasBills() {
        try (SQLiteDatabase db = databaseHelper.getReadableDatabase();
             Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM bills", null)) {

            boolean hasData = false;
            if (cursor.moveToFirst()) {
                hasData = cursor.getInt(0) > 0;
            }
            Log.d("DB_CHECK", "Có dữ liệu trong bills: " + hasData);
            return hasData;
        }
    }

    private void insertSampleData() {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.beginTransaction();

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = sdf.parse("2025-03-26");

            // Thêm dữ liệu vào bảng bills
            String insertBillSQL = "INSERT INTO bills (date, total, username, status) VALUES (?, ?, ?, ?)";
            db.execSQL(insertBillSQL, new Object[]{sdf.format(date), 20000, "An", "Pending"});
            db.execSQL(insertBillSQL, new Object[]{sdf.format(date), 40000, "Bích", "Pending"});
            db.execSQL(insertBillSQL, new Object[]{sdf.format(date), 80000, "Minh", "Completed"});
            db.execSQL(insertBillSQL, new Object[]{sdf.format(date), 60000, "Khanh", "Cancelled"});

            // Lấy ID của các bills vừa thêm vào
            Cursor cursor = db.rawQuery("SELECT id FROM bills ORDER BY id DESC", null);
            List<Integer> billIds = new ArrayList<>();
            while (cursor.moveToNext()) {
                billIds.add(cursor.getInt(0));
            }
            cursor.close();

            // Thêm dữ liệu vào bảng products (nếu chưa có)
            String insertProductSQL = "INSERT INTO products (name, description, image, quantity, price) VALUES (?, ?, ?, ?, ?)";
            db.execSQL(insertProductSQL, new Object[]{"Trà Sữa Trân Châu", "Ngon tuyệt vời", "image1.jpg", 50, 25000});
            db.execSQL(insertProductSQL, new Object[]{"Hồng Trà", "Thơm ngon đậm vị", "image2.jpg", 40, 20000});
            db.execSQL(insertProductSQL, new Object[]{"Matcha Latte", "Hương vị Nhật Bản", "image3.jpg", 30, 30000});

            // Lấy ID của các products vừa thêm vào
            Cursor productCursor = db.rawQuery("SELECT id FROM products ORDER BY id DESC LIMIT 3", null);
            List<Integer> productIds = new ArrayList<>();
            while (productCursor.moveToNext()) {
                productIds.add(productCursor.getInt(0));
            }
            productCursor.close();

            // Thêm dữ liệu vào bảng orders
            String insertOrderSQL = "INSERT INTO orders (billId, productId, quantity, price) VALUES (?, ?, ?, ?)";
            db.execSQL(insertOrderSQL, new Object[]{billIds.get(0), productIds.get(0), 2, 50000});
            db.execSQL(insertOrderSQL, new Object[]{billIds.get(0), productIds.get(1), 1, 20000});
            db.execSQL(insertOrderSQL, new Object[]{billIds.get(1), productIds.get(2), 3, 90000});
            db.execSQL(insertOrderSQL, new Object[]{billIds.get(2), productIds.get(0), 1, 25000});
            db.execSQL(insertOrderSQL, new Object[]{billIds.get(3), productIds.get(1), 2, 40000});

            db.setTransactionSuccessful();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }

        loadBills();
    }


    // Load danh sách bills
    private void loadBills() {
        billList.clear();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor;

        if (selectedStatus.equals("All")) {
            cursor = db.rawQuery("SELECT id, date, total, username, status FROM bills", null);
        } else {
            cursor = db.rawQuery("SELECT id, date, total, username, status FROM bills WHERE status = ?", new String[]{selectedStatus});
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String dateString = cursor.getString(1);
            double total = cursor.getDouble(2);
            String username = cursor.getString(3);
            String status = cursor.getString(4);
            Date date;

            try {
                date = dateFormat.parse(dateString);
            } catch (ParseException e) {
                date = new Date();
            }

            // Lấy danh sách sản phẩm từ BillDAO
            List<String> productNames = billDAO.getProductNamesByBillId(id);

            // Thêm bill với danh sách sản phẩm
            billList.add(new Bill(id, date, total, username, status, productNames));
        }

        cursor.close();
        db.close();
        billAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBills();
    }
}
