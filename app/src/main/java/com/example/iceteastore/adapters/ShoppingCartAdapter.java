package com.example.iceteastore.adapters;

import android.content.Context;
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

        holder.imgProduct.setImageResource(item.getImageResource());
        holder.txtName.setText(item.getName());
        holder.txtPrice.setText(String.format("$%.2f", item.getPrice()));
        holder.txtRating.setText(String.format("%.1f⭐", item.getRating()));
        holder.txtQuantity.setText(String.valueOf(item.getQuantity()));

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
                cartDAO.updateQuantity(username, item.getName().hashCode(), item.getQuantity());  // Cập nhật vào SQLite
                notifyItemChanged(position);
                listener.onQuantityChanged();
            } else {
                removeItem(position);
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
        cartDAO.removeItem(username, item.getName().hashCode());  // Xóa khỏi SQLite
        cartItems.remove(position);  // Xóa khỏi danh sách hiển thị
        notifyItemRemoved(position);  // Cập nhật UI tối ưu hơn
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
}
