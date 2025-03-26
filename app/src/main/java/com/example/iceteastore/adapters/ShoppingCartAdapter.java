package com.example.iceteastore.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iceteastore.R;
import com.example.iceteastore.daos.ShoppingCartDAO;
import com.example.iceteastore.models.ShoppingCart;

import java.util.List;

public class ShoppingCartAdapter extends RecyclerView.Adapter<ShoppingCartAdapter.ViewHolder> {

    private Context context;
    private List<ShoppingCart> cartItems;
    private OnQuantityChangeListener listener;
    private ShoppingCartDAO cartDAO;
    private String username;  // Username của user hiện tại

    public interface OnQuantityChangeListener {
        void onQuantityChanged();
    }

    public ShoppingCartAdapter(Context context, List<ShoppingCart> cartItems, String username, OnQuantityChangeListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
        this.username = username;
        this.cartDAO = new ShoppingCartDAO(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShoppingCart item = cartItems.get(position);
        holder.txtName.setText(item.getName());
        holder.txtPrice.setText(String.format("$%.2f", item.getPrice()));
        holder.txtQuantity.setText(String.valueOf(item.getQuantity()));

        String imagePath = item.getImageResource();
        if (!imagePath.isEmpty()) {
            holder.imgProduct.setImageBitmap(convertBase64ToBitmap(item.getImageResource()));
        } else {
            holder.imgProduct.setImageResource(R.drawable.placeholder_image);
        }

        // Xử lý tăng số lượng
        holder.btnIncrease.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            cartDAO.updateQuantity(username, item.getName().hashCode(), item.getQuantity());  // Cập nhật vào SQLite
            notifyItemChanged(position);  // Chỉ cập nhật item thay vì toàn bộ danh sách
            listener.onQuantityChanged();
        });

        // Xử lý giảm số lượng
        holder.btnDecrease.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                cartDAO.updateQuantity(username, item.getProductId(), item.getQuantity());  // Cập nhật vào SQLite
                notifyItemChanged(position);
                listener.onQuantityChanged();
            } else {
                removeItem(position);  // Gọi hàm xóa sản phẩm
            }
        });


        // Xử lý xóa sản phẩm khỏi giỏ hàng
//        holder.btnRemove.setOnClickListener(v -> removeItem(position));
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    // Xóa sản phẩm khỏi giỏ hàng
    private void removeItem(int position) {
        ShoppingCart item = cartItems.get(position);
        cartDAO.removeItem(username, item.getProductId());  // Dùng productId thay vì hashCode()

        cartItems.remove(position);  // Xóa khỏi danh sách hiển thị

        if (cartItems.isEmpty()) {
            notifyDataSetChanged();  // Nếu danh sách trống, cập nhật toàn bộ giao diện
        } else {
            notifyItemRemoved(position);  // Nếu vẫn còn sản phẩm, cập nhật UI tối ưu hơn
        }

        listener.onQuantityChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView txtName, txtPrice, txtRating, txtQuantity;
        Button btnIncrease, btnDecrease;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtName = itemView.findViewById(R.id.tvProductName);
            txtRating = itemView.findViewById(R.id.tvRating);
            txtPrice = itemView.findViewById(R.id.tvPrice);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            txtQuantity = itemView.findViewById(R.id.tvQuantity);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
//            btnRemove = itemView.findViewById(R.id.); // Thêm nút xóa sản phẩm
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
}
