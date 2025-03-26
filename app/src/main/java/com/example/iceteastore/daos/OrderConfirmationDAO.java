package com.example.iceteastore.daos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.iceteastore.database_helper.DatabaseHelper;
import com.example.iceteastore.models.ShoppingCart;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderConfirmationDAO {
    private DatabaseHelper dbHelper;

    public OrderConfirmationDAO(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }


    public List<ShoppingCart> getCartItems(String username) {
        List<ShoppingCart> cartList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM shopping_cart WHERE username=?", new String[]{username});

        if (cursor.moveToFirst()) {
            do {
                int productId = cursor.getInt(cursor.getColumnIndexOrThrow("productId"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String imageResource = cursor.getString(cursor.getColumnIndexOrThrow("imageResource"));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));

                cartList.add(new ShoppingCart(productId, name, imageResource, quantity, price));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return cartList;
    }


    public double calculateTotalPrice(String username) {
        double total = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(quantity * price) AS total FROM shopping_cart WHERE username=?", new String[]{username});

        if (cursor.moveToFirst()) {
            total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
        }

        cursor.close();
        db.close();
        return total;
    }

    // Lưu đơn hàng vào bảng bills và orders
    public boolean saveOrder(String username) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();

        try {
            List<ShoppingCart> cartList = getCartItems(username);
            if (cartList.isEmpty()) {
                return false;
            }

            double totalPrice = calculateTotalPrice(username);
            String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

            ContentValues billValues = new ContentValues();
            billValues.put("date", currentDate);
            billValues.put("total", totalPrice);
            billValues.put("username", username);
            billValues.put("status", "Pending");

            long billId = db.insert("bills", null, billValues);
            if (billId == -1) {
                return false;
            }

            for (ShoppingCart item : cartList) {
                ContentValues orderValues = new ContentValues();
                orderValues.put("billId", billId);
                orderValues.put("productId", item.getProductId());
                orderValues.put("quantity", item.getQuantity());
                orderValues.put("price", item.getPrice());

                long orderId = db.insert("orders", null, orderValues);
                if (orderId == -1) {
                    return false;
                }
            }

            db.delete("shopping_cart", "username=?", new String[]{username});

            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.endTransaction();
            db.close();
        }
    }
}
