package com.example.iceteastore.daos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.example.iceteastore.database_helper.DatabaseHelper;
import com.example.iceteastore.models.Bill;

import java.util.ArrayList;
import java.util.List;

public class BillDAO {
    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;

    public BillDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }



    public long insertBill(String date, double total, String username, String status) {
        db = dbHelper.getWritableDatabase(); // 🔥 Đảm bảo db không null

        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("total", total);
        values.put("username", username);
        values.put("status", status);

        long result = db.insert("bills", null, values);

        if (result == -1) {
            Log.e("BillDAO", "❌ Thêm bill thất bại");
        } else {
            Log.d("BillDAO", "✅ Thêm bill thành công: ID = " + result);
        }

        db.close();
        return result;
    }

    // Lấy danh sách tên sản phẩm từ billId
    public List<String> getProductNamesByBillId(int billId) {
        List<String> productNames = new ArrayList<>();
        db = dbHelper.getReadableDatabase();

        String query = "SELECT p.name FROM orders o " +
                "JOIN products p ON o.productId = p.id " +
                "WHERE o.billId = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(billId)});

        while (cursor.moveToNext()) {
            productNames.add(cursor.getString(0)); // Lấy tên sản phẩm
        }
        cursor.close();
        db.close();
        return productNames;
    }

    public List<Bill> getAllBills() {
        List<Bill> billList = new ArrayList<>();
        db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM bills", null);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String dateString = cursor.getString(1);
                double total = cursor.getDouble(2);
                String username = cursor.getString(3);
                String status = cursor.getString(4);

                // Chuyển đổi String -> Date
                Date date = null;
                try {
                    date = sdf.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                billList.add(new Bill(id, date, total, username, status, null));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return billList;
    }

    // ✅ Phương thức xóa bill
    public boolean deleteBill(int billId) {
        db = dbHelper.getWritableDatabase();
        int result = db.delete("bills", "id=?", new String[]{String.valueOf(billId)});
        db.close();
        return result > 0;
    }
}

