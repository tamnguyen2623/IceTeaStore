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
                        cursor.getInt(0),  // id
                        cursor.getString(1),  // name
                        cursor.getString(2),  // description
                        cursor.getString(3),  // image
                        cursor.getInt(4),  // quantity
                        cursor.getDouble(5),  // price
                        4.8f,  // Giả định rating
                        50   // Giả định số lượng reviews
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
}
