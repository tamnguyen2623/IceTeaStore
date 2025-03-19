package com.example.iceteastore.daos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.iceteastore.database_helper.DatabaseHelper;
import com.example.iceteastore.models.User;

public class UserDAO {
    private SQLiteDatabase db;

    public UserDAO(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        this.db = dbHelper.getWritableDatabase();
    }

    // Thêm user (dùng để tạo tài khoản test)
    public boolean insertUser(User user) {
        ContentValues values = new ContentValues();
        values.put("username", user.getUsername());
        values.put("password", user.getPassword());
        values.put("fullName", user.getFullName());
        values.put("birthday", user.getBirthday());
        values.put("phoneNumber", user.getPhoneNumber());
        values.put("address", user.getAddress());
        values.put("role", user.getRole());

        long result = db.insert("users", null, values);
        return result != -1;  // Trả về true nếu thêm thành công
    }

    // Kiểm tra xem username đã tồn tại chưa
    public boolean isUsernameExists(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean isPhoneNumberExists(String phoneNumber) {
        String query = "SELECT * FROM users WHERE phoneNumber = ?";
        Cursor cursor = db.rawQuery(query, new String[]{phoneNumber});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public String getUserRole(String username, String password) {
        String query = "SELECT role FROM users WHERE username = ? AND password = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username, password});

        if (cursor.moveToFirst()) {
            String role = cursor.getString(0);
            cursor.close();
            return role;
        }
        cursor.close();
        return null;
    }

    // Kiểm tra password có đúng với username không
    public boolean isPasswordCorrect(String username, String password) {
        String query = "SELECT 1 FROM users WHERE username = ? AND password = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username, password});
        boolean isCorrect = cursor.getCount() > 0;
        cursor.close();
        return isCorrect;
    }

}
