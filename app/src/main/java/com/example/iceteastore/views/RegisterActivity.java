package com.example.iceteastore.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iceteastore.R;
import com.example.iceteastore.adapters.BillAdapter;
import com.example.iceteastore.daos.BillDAO;
import com.example.iceteastore.daos.UserDAO;
import com.example.iceteastore.database_helper.DatabaseHelper;
import com.example.iceteastore.models.Bill;
import com.example.iceteastore.models.User;

public class RegisterActivity extends AppCompatActivity {
    private EditText inputUsername, inputFullname, inputPhoneNumber, inputAddress, inputDateOfBirth, inputPassword;
    private Button submitSignup;
    private ImageButton btnBack;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Ánh xạ view
        inputUsername = findViewById(R.id.inputUsername);
        inputFullname = findViewById(R.id.inputFullname);
        inputPhoneNumber = findViewById(R.id.inputPhoneNumber);
        inputAddress = findViewById(R.id.inputAddress);
        inputDateOfBirth = findViewById(R.id.inputDateOfBirth);
        inputPassword = findViewById(R.id.inputPassword);
        submitSignup = findViewById(R.id.submit_signup);
        btnBack = findViewById(R.id.btnBack);

        // Khởi tạo UserDAO
        userDAO = new UserDAO(this);

        // Xử lý khi nhấn nút đăng ký
        submitSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        btnBack.setOnClickListener(v -> {
            finish(); // Đóng RegisterActivity và quay về LoginActivity
        });
    }

    private void registerUser() {
        String username = inputUsername.getText().toString().trim();
        String fullName = inputFullname.getText().toString().trim();
        String phoneNumber = inputPhoneNumber.getText().toString().trim();
        String address = inputAddress.getText().toString().trim();
        String birthday = inputDateOfBirth.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        // Kiểm tra dữ liệu hợp lệ
        if (username.isEmpty() || fullName.isEmpty() || phoneNumber.isEmpty() ||
                address.isEmpty() || birthday.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
        } else if (!phoneNumber.matches("^0\\d{9}$")) {
            Toast.makeText(this, "Invalid phone number!", Toast.LENGTH_SHORT).show();
        } else if (!isValidDate(birthday)) {
            Toast.makeText(this, "Invalid date format!", Toast.LENGTH_SHORT).show();
        } else if (userDAO.isUsernameExists(username)) {
            Toast.makeText(this, "Username already exists!", Toast.LENGTH_SHORT).show();
        } else if (userDAO.isPhoneNumberExists(phoneNumber)) {
            Toast.makeText(this, "Phone number already exists!", Toast.LENGTH_SHORT).show();
        } else {
            // Lưu vào SQLite
            User newUser = new User(username, password, fullName, birthday, phoneNumber, address, "admin");
            boolean isInserted = userDAO.insertUser(newUser);

            if (isInserted) {
                saveUserToSharedPreferences(username);
                Toast.makeText(this, "Sign-up successfully!", Toast.LENGTH_SHORT).show();

                // Điều hướng sang màn hình đăng nhập
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Sign-up failed. Please try again!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isValidDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        sdf.setLenient(false); // Ngăn chặn tự động chỉnh sửa ngày sai

        try {
            sdf.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }



    // Lưu thông tin người dùng vào SharedPreferences
    private void saveUserToSharedPreferences(String username) {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("role", "user");
        editor.apply();
    }

    public static class AdminBillActivity extends AppCompatActivity {

        private RecyclerView recyclerView;
        private BillAdapter billAdapter;
        private List<Bill> billList;
        private DatabaseHelper databaseHelper;
        private BillDAO billDAO;

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

            try (SQLiteDatabase db = databaseHelper.getReadableDatabase();
                 Cursor cursor = db.rawQuery("SELECT id, date, total, username, status FROM bills", null)) {

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
}
