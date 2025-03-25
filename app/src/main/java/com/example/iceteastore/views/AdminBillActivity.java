package com.example.iceteastore.views;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iceteastore.R;
import com.example.iceteastore.adapters.BillAdapter;
import com.example.iceteastore.daos.BillDAO;
import com.example.iceteastore.database_helper.DatabaseHelper;
import com.example.iceteastore.models.Bill;

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


    private Spinner spinnerStatus;
    private String selectedStatus = "All"; // Mặc định hiển thị tất cả

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_bill);

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
            Date date = sdf.parse("2000-12-12");

            String insertSQL = "INSERT INTO bills (date, total, username, status) VALUES (?, ?, ?, ?)";
            db.execSQL(insertSQL, new Object[]{sdf.format(date), 20000, "ca", "Pending"});
            db.execSQL(insertSQL, new Object[]{sdf.format(date), 40000, "da", "Pending"});
            db.execSQL(insertSQL, new Object[]{sdf.format(date), 80000, "an", "Pending"});
            db.execSQL(insertSQL, new Object[]{sdf.format(date), 60000, "khang", "Pending"});

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
                billList.add(new Bill(id, date, total, username, status));
            }


        Log.d("DB_CHECK", "Tổng số bills: " + billList.size());

        billAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBills();
    }
}
