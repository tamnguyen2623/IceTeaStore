package com.example.iceteastore.daos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.iceteastore.database_helper.DatabaseHelper;
import com.example.iceteastore.models.Product;

import java.util.ArrayList;
import java.util.List;

public class FavoriteDAO {
    private DatabaseHelper dbHelper;

    public FavoriteDAO(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    // ✅ Thêm sản phẩm vào danh sách yêu thích
    public boolean addToFavorites(String username, int productId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("productId", productId);

        long result = db.insert("favorites", null, values);
        db.close();

        if (result == -1) {
            Log.e("FavoriteDAO", "Lỗi khi thêm vào favorites: " + username + " - " + productId);
        } else {
            Log.d("FavoriteDAO", "Thêm thành công: " + username + " - " + productId);
        }

        return result != -1;
    }

    // ✅ Xóa sản phẩm khỏi danh sách yêu thích
    public boolean removeFromFavorites(String username, int productId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete("favorites", "username = ? AND productId = ?",
                new String[]{username, String.valueOf(productId)});
        db.close();

        if (result > 0) {
            Log.d("FavoriteDAO", "Xóa thành công sản phẩm ID: " + productId + " khỏi danh sách yêu thích.");
        } else {
            Log.e("FavoriteDAO", "Lỗi: Không thể xóa sản phẩm ID: " + productId);
        }

        return result > 0;
    }

    // ✅ Kiểm tra sản phẩm có trong danh sách yêu thích không
    public boolean isFavorite(String username, int productId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT 1 FROM favorites WHERE username = ? AND productId = ?",
                new String[]{username, String.valueOf(productId)});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    // ✅ Lấy danh sách sản phẩm yêu thích của một người dùng
    public List<Product> getFavoriteProducts(String username) {
        List<Product> favoriteProducts = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT p.* FROM products p " +
                "INNER JOIN favorites f ON p.id = f.productId " +
                "WHERE f.username = ?";

        Cursor cursor = db.rawQuery(query, new String[]{username});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Product product = new Product(
                            cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                            cursor.getString(cursor.getColumnIndexOrThrow("name")),
                            cursor.getString(cursor.getColumnIndexOrThrow("description")),
                            cursor.getString(cursor.getColumnIndexOrThrow("image")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("quantity")),
                            cursor.getDouble(cursor.getColumnIndexOrThrow("price")),
                            4.5f,  // Giả định rating
                            50     // Giả định số lượng reviews
                    );
                    favoriteProducts.add(product);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();
        return favoriteProducts;
    }
}
