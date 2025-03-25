package com.example.iceteastore.database_helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "IceTeaStore.db";
    private static final int DATABASE_VERSION = 3;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng User
        db.execSQL("CREATE TABLE users (" +
                "username TEXT PRIMARY KEY, " +
                "password TEXT, " +
                "fullName TEXT, " +
                "birthday TEXT, " +
                "phoneNumber TEXT, " +
                "address TEXT, " +
                "role TEXT)");

        // Tạo bảng Product
        db.execSQL("CREATE TABLE products (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "description TEXT, " +
                "image TEXT, " +
                "quantity INTEGER, " +
                "price REAL)");

        // Tạo bảng Bill
        db.execSQL("CREATE TABLE bills (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "date TEXT, " +
                "total REAL, " +
                "username TEXT, " +
                "status TEXT, " +
                "FOREIGN KEY(username) REFERENCES users(username))");

        // Tạo bảng Order
        db.execSQL("CREATE TABLE orders (" +
                "billId INTEGER, " +
                "productId INTEGER, " +
                "quantity INTEGER, " +
                "price REAL, " +
                "PRIMARY KEY(billId, productId), " +
                "FOREIGN KEY(billId) REFERENCES bills(id), " +
                "FOREIGN KEY(productId) REFERENCES products(id))");

        // Tạo bảng ShoppingCart
        db.execSQL("CREATE TABLE shopping_cart (" +
                "username TEXT, " +
                "productId INTEGER, " +
                "quantity INTEGER, " +
                "price REAL, " +
                "PRIMARY KEY(username, productId), " +
                "FOREIGN KEY(username) REFERENCES users(username), " +
                "FOREIGN KEY(productId) REFERENCES products(id))");

        // Tạo bảng Favorite
        db.execSQL("CREATE TABLE favorites (" +
                "username TEXT, " +
                "productId INTEGER, " +
                "PRIMARY KEY(username, productId), " +
                "FOREIGN KEY(username) REFERENCES users(username), " +
                "FOREIGN KEY(productId) REFERENCES products(id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS products");
        db.execSQL("DROP TABLE IF EXISTS bills");
        db.execSQL("DROP TABLE IF EXISTS orders");
        db.execSQL("DROP TABLE IF EXISTS shopping_cart");
        db.execSQL("DROP TABLE IF EXISTS favorites");
        onCreate(db);
    }
}
