package com.example.iceteastore.views;


import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iceteastore.R;
import com.example.iceteastore.database_helper.DatabaseHelper;

public class AdminUpdateBillActivity extends AppCompatActivity {

    private TextView txtBillId, txtPrice, tvUser;
    Spinner edtStatus;
    private ImageButton btnSave, btnBack;
    private DatabaseHelper databaseHelper;
    private int billId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_update_bill);

        txtBillId = findViewById(R.id.txtBillId);
        txtPrice = findViewById(R.id.txtPrice);
        tvUser = findViewById(R.id.tv_user);
        edtStatus = findViewById(R.id.spinnerStatus);
        btnSave = findViewById(R.id.btn_save);

        // Thiết lập Adapter cho Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.status_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        edtStatus.setAdapter(adapter);

        databaseHelper = new DatabaseHelper(this);
        billId = getIntent().getIntExtra("billId", -1);
        String status = getIntent().getStringExtra("status");

        loadBillDetails();
        // Đặt trạng thái ban đầu cho Spinner
        if (status != null) {
            int position = adapter.getPosition(status);
            if (position >= 0) {
                edtStatus.setSelection(position);
            }
        }

        btnSave.setOnClickListener(v -> updateBillStatus());

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            finish();
        });
    }

    private void loadBillDetails() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM bills WHERE id = ?", new String[]{String.valueOf(billId)});

        if (cursor.moveToFirst()) {
            txtBillId.setText("Bill ID: " + cursor.getInt(0));
            txtPrice.setText("Total: $" + cursor.getDouble(2));
            tvUser.setText(cursor.getString(3));
//            edtStatus.set("Status: "+cursor.getString(4));

        }
        cursor.close();
    }

    private void updateBillStatus() {
        String newStatus = edtStatus.getSelectedItem().toString();
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", newStatus);

        db.update("bills", values, "id=?", new String[]{String.valueOf(billId)});
        db.close();

        Toast.makeText(this, "Bill Updated!", Toast.LENGTH_SHORT).show();
        finish();
    }

}
