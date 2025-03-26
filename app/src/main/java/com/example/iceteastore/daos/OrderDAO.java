//OderDAO
package com.example.iceteastore.daos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.iceteastore.database_helper.DatabaseHelper;
import com.example.iceteastore.models.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public OrderDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // Thêm order vào database
    public boolean addOrder(Order order) {
        open();
        ContentValues values = new ContentValues();
        values.put("billId", order.getBillId());
        values.put("productId", order.getProductId());
        values.put("quantity", order.getQuantity());
        values.put("price", order.getPrice());

        long result = database.insert("orders", null, values);
        close();
        return result != -1;
    }

    // Lấy danh sách tất cả orders
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        open();
        Cursor cursor = database.rawQuery("SELECT * FROM orders", null);

        if (cursor.moveToFirst()) {
            do {
                Order order = new Order();
                order.setBillId(cursor.getInt(0));
                order.setProductId(cursor.getInt(1));
                order.setQuantity(cursor.getInt(2));
                order.setPrice(cursor.getDouble(3));

                orders.add(order);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return orders;
    }

}