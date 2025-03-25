package com.example.iceteastore.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.iceteastore.R;
import com.example.iceteastore.daos.ProductDAO;
import com.example.iceteastore.models.Product;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditProductAdminActivity extends AppCompatActivity {
    private EditText edtName, edtQuantity, edtPrice, edtDescription;
    private ImageView imgProduct;
    private Button btnUpdate, btnSelectImage;
    private ProductDAO productDAO;
    private Product currentProduct;
    private String imageBase64 = "";
    private Uri imageUri;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    try {
                        Bitmap bitmap;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), imageUri);
                            bitmap = ImageDecoder.decodeBitmap(source);
                        } else {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        }
                        imgProduct.setImageBitmap(bitmap);
                        imageBase64 = convertBitmapToBase64(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product_admin);

        productDAO = new ProductDAO(this);

        edtName = findViewById(R.id.edtName);
        edtQuantity = findViewById(R.id.edtQuantity);
        edtPrice = findViewById(R.id.edtPrice);
        edtDescription = findViewById(R.id.edtDescription);
        imgProduct = findViewById(R.id.imgProduct);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnSelectImage = findViewById(R.id.btnSelectImage);

        // Nhận dữ liệu sản phẩm
        currentProduct = (Product) getIntent().getSerializableExtra("Product");
        if (currentProduct != null) {
            edtName.setText(currentProduct.getName());
            edtQuantity.setText(String.valueOf(currentProduct.getQuantity()));
            edtPrice.setText(String.valueOf(currentProduct.getPrice()));
            edtDescription.setText(currentProduct.getDescription());
            imageBase64 = currentProduct.getImage();

            if (!imageBase64.isEmpty()) {
                Bitmap bitmap = decodeBase64ToBitmap(imageBase64);
                imgProduct.setImageBitmap(bitmap);
            }
        }

        btnSelectImage.setOnClickListener(v -> openImageChooser());

        btnUpdate.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String description = edtDescription.getText().toString().trim();
            int quantity = Integer.parseInt(edtQuantity.getText().toString().trim());
            double price = Double.parseDouble(edtPrice.getText().toString().trim());

            currentProduct.setName(name);
            currentProduct.setDescription(description);
            currentProduct.setQuantity(quantity);
            currentProduct.setPrice(price);
            currentProduct.setImage(imageBase64);

//                boolean isUpdated = productDAO.updateProduct(currentProduct);

            Log.d("EditProduct", "Before Update: " + currentProduct.toString());
            boolean isUpdated = productDAO.updateProduct(currentProduct);
            Log.d("EditProduct", "After Update: " + productDAO.getAllProducts().toString());



            if (isUpdated) {
                Toast.makeText(this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update product", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        imagePickerLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private Bitmap decodeBase64ToBitmap(String base64) {
        byte[] decodedBytes = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
