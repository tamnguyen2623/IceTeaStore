package com.example.iceteastore.daos;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.iceteastore.database_helper.DatabaseHelper;

public class BillDAO {
    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;

    public BillDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }



    public void insertBill(String date, double total, String username, String status) {
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
    }

    // ✅ Phương thức xóa bill
    public boolean deleteBill(int billId) {
        db = dbHelper.getWritableDatabase();
        int result = db.delete("bills", "id=?", new String[]{String.valueOf(billId)});
        db.close();
        return result > 0;
    }
}

