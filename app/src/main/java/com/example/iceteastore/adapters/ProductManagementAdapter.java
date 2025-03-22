package com.example.iceteastore.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.iceteastore.R;

import com.example.iceteastore.daos.ProductDAO;
import com.example.iceteastore.models.Product;
import com.example.iceteastore.views.EditProductAdminActivity;
import com.example.iceteastore.views.FavoriteActivity;
import com.example.iceteastore.views.ProductManagementListActivity;

import java.util.List;

public class ProductManagementAdapter extends RecyclerView.Adapter<ProductManagementAdapter.ViewHolder> {
    private Context context;
    private List<Product> productList;
    private ProductDAO productDAO;
    private ProductManagementAdapter productManagementAdapter;

    private static final int EDIT_PRODUCT_REQUEST = 1;


    public ProductManagementAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
        this.productDAO = new ProductDAO(context); // ⚡ Sửa lỗi: khởi tạo ProductDAO

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_management, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.txtName.setText("Name: " + product.getName());
        holder.txtPrice.setText("Price: " + product.getPrice());
        holder.txtDescription.setText("Description: " + product.getDescription());
        holder.txtQuantity.setText("Quantity: " + product.getQuantity());
        holder.imgProduct.setImageBitmap(convertBase64ToBitmap(product.getImage()));

// Edit
        holder.btn_edit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditProductAdminActivity.class);
            intent.putExtra("Product", product);
            ((ProductManagementListActivity) context).startActivityForResult(intent, EDIT_PRODUCT_REQUEST
            );
        });

        // Delete Product with Confirmation Dialog
        holder.btn_delete.setOnClickListener(v -> {
            showDeleteConfirmationDialog(position);
        });
    }

    /**
     * Hiển thị hộp thoại xác nhận xóa sản phẩm
     */
    private void showDeleteConfirmationDialog(int position) {
        Product item = productList.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirm Delete");
        builder.setMessage("Are you sure you want to delete \"" + item.getName() + "\"?");

        // Nút xác nhận xóa
        builder.setPositiveButton("Yes", (dialog, id) -> {
            boolean isDeleted = productDAO.deleteProduct(item.getId());
            if (isDeleted) {
                productList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, productList.size());
                Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Delete failed", Toast.LENGTH_SHORT).show();
            }
        });

        // Nút hủy bỏ
        builder.setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());

        // Hiển thị hộp thoại
        AlertDialog dialog = builder.create();
        dialog.show();
    }



    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton btn_edit, btn_delete; // Thêm nút chỉnh sửa
        ImageView imgProduct;
        TextView txtName, txtPrice, txtQuantity, txtDescription;

        public ViewHolder(View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtName = itemView.findViewById(R.id.txtName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            btn_edit = itemView.findViewById(R.id.btn_edit); // 💡 Thêm dòng này để tránh NullPointerException
            btn_delete = itemView.findViewById(R.id.btn_delete); // ⚡ Sửa lỗi thiếu gán ID cho btn_delete

        }
    }


    private Bitmap convertBase64ToBitmap(String base64String) {
        try {
            byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
//edit
    public void updateData(List<Product> newProductList) {
        this.productList = newProductList;
        notifyDataSetChanged(); // Cập nhật lại RecyclerView
    }
    private void loadProducts() {
        productList = productDAO.getAllProducts();
        productManagementAdapter.updateData(productList);
    }


}


