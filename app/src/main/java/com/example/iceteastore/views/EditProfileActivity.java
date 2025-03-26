package com.example.iceteastore.views;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.iceteastore.R;

import androidx.appcompat.app.AppCompatActivity;

import com.example.iceteastore.daos.UserDAO;
import com.example.iceteastore.models.User;
import com.example.iceteastore.utils.SessionManager;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Locale;

public class EditProfileActivity extends AppCompatActivity {
    private TextInputLayout layoutFullName, layoutBirthday, layoutPhone, layoutAddress;
    private EditText edtFullName, edtBirthday, edtPhone, edtAddress, edtUserName;
    private Button btnSave;
    private UserDAO userDAO;
    private SessionManager sessionManager;
    private String loggedInUsername;
    private ImageButton btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        edtUserName = findViewById(R.id.edtUserName);
        edtFullName = findViewById(R.id.edtFullName);
        edtBirthday = findViewById(R.id.edtBirthday);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);
        edtBirthday.setOnClickListener(v -> showDatePickerDialog());
        edtBirthday.setFocusable(false); // Ngăn không cho nhập tay vào EditText

        layoutFullName = findViewById(R.id.layoutFullName);
        layoutBirthday = findViewById(R.id.layoutBirthday);
        layoutPhone = findViewById(R.id.layoutPhone);
        layoutAddress = findViewById(R.id.layoutAddress);

        btnBack = findViewById(R.id.btnBack);
        btnSave = findViewById(R.id.btnSave);

        btnBack.setOnClickListener(v -> finish());
        userDAO = new UserDAO(this);
        sessionManager = new SessionManager(this);
        loggedInUsername = sessionManager.getLoggedInUser(); // Lấy username từ session

        if (loggedInUsername != null) {
            loadUserProfile(loggedInUsername);
        } else {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        btnSave.setOnClickListener(v -> saveUserProfile());
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year1, month1, dayOfMonth);

            Calendar ninetyYearsAgo = Calendar.getInstance();
            ninetyYearsAgo.add(Calendar.YEAR, -90); // 90 năm trước

            if (selectedDate.after(calendar)) {
                layoutBirthday.setError("Ngày sinh không được là ngày trong tương lai");
            } else if (selectedDate.before(ninetyYearsAgo)) {
                layoutBirthday.setError("Tuổi không được quá 90");
            } else {
                layoutBirthday.setError(null); // Xóa lỗi nếu hợp lệ
                String formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month1 + 1, year1);
                edtBirthday.setText(formattedDate);
            }
        }, year, month, day);

        datePickerDialog.show();
    }


    private void loadUserProfile(String username) {
        User user = userDAO.getUserByUsername(username);
        if (user != null) {
            edtUserName.setText(user.getUsername());
            edtFullName.setText(user.getFullName());
            edtBirthday.setText(user.getBirthday());
            edtPhone.setText(user.getPhoneNumber());
            edtAddress.setText(user.getAddress());
        } else {
            Toast.makeText(this, "Failed to load user profile", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUserProfile() {
        String fullName = edtFullName.getText().toString().trim();
        String birthday = edtBirthday.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        boolean isValid = true;

        // Kiểm tra Họ và tên
        if (fullName.isEmpty()) {
            layoutFullName.setError("Họ và tên không được để trống");
            isValid = false;
        } else {
            layoutFullName.setError(null);
        }

        // Kiểm tra Ngày sinh
        if (birthday.isEmpty()) {
            layoutBirthday.setError("Vui lòng chọn ngày sinh");
            isValid = false;
        } else {
            layoutBirthday.setError(null);
        }

        // Kiểm tra số điện thoại
        if (!phone.matches("\\d{10}")) { // Chỉ chấp nhận đúng 10 số
            layoutPhone.setError("Số điện thoại phải có đúng 10 chữ số");
            isValid = false;
        } else {
            layoutPhone.setError(null);
        }

        // Kiểm tra Địa chỉ
        if (address.isEmpty()) {
            layoutAddress.setError("Địa chỉ không được để trống");
            isValid = false;
        } else {
            layoutAddress.setError(null);
        }

        // Nếu có lỗi, không tiếp tục lưu
        if (!isValid) return;

        boolean success = userDAO.updateUserProfile(loggedInUsername, fullName, birthday, phone, address);
        if (success) {
            Toast.makeText(this, "Cập nhật hồ sơ thành công!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
        }
    }
}
