package com.example.iceteastore.daos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.iceteastore.database_helper.DatabaseHelper;
import com.example.iceteastore.models.ShoppingCart;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCartDAO {
    private DatabaseHelper dbHelper;

    public ShoppingCartDAO(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    // Thêm sản phẩm vào giỏ hàng
    public void addToCart(String username, ShoppingCart item) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("productId", item.getProductId()); // ✅ Thêm productId
        values.put("name", item.getName());
        values.put("imageResource", item.getImageResource());
        values.put("quantity", item.getQuantity());
        values.put("price", item.getPrice());

        db.insert("shopping_cart", null, values);
        db.close();
    }

    // Lấy danh sách sản phẩm trong giỏ hàng của user
    public List<ShoppingCart> getCartItems(String username) {
        List<ShoppingCart> cartList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM shopping_cart WHERE username=?", new String[]{username});

        if (cursor.moveToFirst()) {
            do {
                int productId = cursor.getInt(cursor.getColumnIndexOrThrow("productId")); // ✅ Thêm productId
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

    // Cập nhật số lượng sản phẩm
    public void updateQuantity(String username, int productId, int quantity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (quantity <= 0) {
            removeItem(username, productId);
        } else {
            ContentValues values = new ContentValues();
            values.put("quantity", quantity);
            db.update("shopping_cart", values, "username=? AND productId=?", new String[]{username, String.valueOf(productId)});
        }
        db.close();
    }

    // Xóa sản phẩm khỏi giỏ hàng
    public void removeItem(String username, int productId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("shopping_cart", "username=? AND productId=?", new String[]{username, String.valueOf(productId)});
        db.close();
    }

    // Xóa toàn bộ giỏ hàng của user
    public void clearCart(String username) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("shopping_cart", "username=?", new String[]{username});
        db.close();
    }
}
