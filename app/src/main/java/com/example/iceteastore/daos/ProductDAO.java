package com.example.iceteastore.daos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.iceteastore.database_helper.DatabaseHelper;
import com.example.iceteastore.models.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private DatabaseHelper dbHelper;

    public ProductDAO(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    // Lấy danh sách tất cả sản phẩm từ SQLite
    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM products", null);

        if (cursor.moveToFirst()) {
            do {
                Product product = new Product(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4),
                        cursor.getDouble(5),
                        4.8f,
                        50
                );
                productList.add(product);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return productList;
    }

    // Thêm sản phẩm mới vào SQLite
    public boolean insertProduct(Product product) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", product.getName());
        values.put("description", product.getDescription());
        values.put("image", product.getImage());
        values.put("quantity", product.getQuantity());
        values.put("price", product.getPrice());

        long result = db.insert("products", null, values);
        db.close();
        return result != -1;
    }

    // update product
    public boolean updateProduct(Product product) {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("name", product.getName());
        values.put("description", product.getDescription());
        values.put("image", product.getImage());
        values.put("quantity", product.getQuantity());
        values.put("price", product.getPrice());

        int rowsAffected = db.update("products", values, "id=?", new String[]{String.valueOf(product.getId())});
        db.close();

        return rowsAffected > 0;
    }

    public boolean deleteProduct(int productId) {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        int result = db.delete("products", "id = ?", new String[]{String.valueOf(productId)});
        db.close();
        return result > 0;
    }


    public Product getProductById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Product product = null;

        Cursor cursor = db.rawQuery("SELECT * FROM products WHERE id = ?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            product = new Product(
                    cursor.getInt(0),  // id
                    cursor.getString(1),  // name
                    cursor.getString(2),  // description
                    cursor.getString(3),  // image
                    cursor.getInt(4),  // quantity
                    cursor.getDouble(5),  // price
                    4.8f,  // Giả định rating
                    50   // Giả định số lượng reviews
            );
        }
        cursor.close();
        db.close();
        return product;
    }

}