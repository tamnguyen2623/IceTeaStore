package com.example.iceteastore.daos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.iceteastore.database_helper.DatabaseHelper;
import com.example.iceteastore.models.Bill;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BillDAO {
    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;
    private static final String TABLE_NAME = "bills";
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"; // Định dạng ngày lưu vào DB
    private SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    public BillDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // ✅ Phương thức thêm Bill bằng model `Bill`
    public long insertBill(Bill bill) {
        db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("date", sdf.format(bill.getDate())); // Chuyển Date -> String
        values.put("total", bill.getTotal());
        values.put("username", bill.getUsername());
        values.put("status", bill.getStatus());

        long result = db.insert(TABLE_NAME, null, values);
        db.close();

        if (result == -1) {
            Log.e("BillDAO", "❌ Thêm bill thất bại");
        } else {
            Log.d("BillDAO", "✅ Bill đã được thêm: ID = " + result);
        }

        return result;
    }

    // ✅ Lấy danh sách tất cả hóa đơn
    public List<Bill> getAllBills() {
        List<Bill> billList = new ArrayList<>();
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

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

                billList.add(new Bill(id, date, total, username, status));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return billList;
    }

    // ✅ Xóa bill theo ID
    public boolean deleteBill(int billId) {
        db = dbHelper.getWritableDatabase();
        int result = db.delete(TABLE_NAME, "id=?", new String[]{String.valueOf(billId)});
        db.close();
        return result > 0;
    }
}
