package com.example.iceteastore.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.iceteastore.R;
import com.example.iceteastore.daos.FavoriteDAO;
import com.example.iceteastore.models.Product;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private Context context;
    private List<Product> favoriteList;

    public FavoriteAdapter(Context context, List<Product> favoriteList) {
        this.context = context;
        this.favoriteList = favoriteList;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        Product product = favoriteList.get(position);
        holder.tvProductName.setText(product.getName());
        holder.tvRating.setText("⭐ " + product.getRating() + " (" + product.getReviews() + " reviews)");
        holder.tvPrice.setText("$" + product.getPrice());

        // Load ảnh sản phẩm
        String imagePath = product.getImage();
        if (!imagePath.isEmpty()) {
            holder.ivProductImage.setImageBitmap(convertBase64ToBitmap(product.getImage()));
        } else {
            holder.ivProductImage.setImageResource(R.drawable.placeholder_image);
        }

        // Xử lý xóa khỏi danh sách yêu thích
        holder.ivFavorite.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = context.getSharedPreferences("LoginSession", Context.MODE_PRIVATE);
            String username = sharedPreferences.getString("username", null);

            if (username != null) {
                // Xóa sản phẩm khỏi database trước khi cập nhật UI
                FavoriteDAO favoriteDAO = new FavoriteDAO(context);
                boolean isDeleted = favoriteDAO.removeFromFavorites(username, product.getId()); // Sửa lỗi truyền username

                if (isDeleted) {
                    favoriteList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, favoriteList.size());
                    Toast.makeText(context, "Removed from favorites list successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("FavoriteAdapter", "Error when removing product ID: " + product.getId());
                }
            } else {
                Toast.makeText(context, "User not logged in!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvRating, tvPrice;
        ImageView ivProductImage, ivFavorite;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            ivFavorite = itemView.findViewById(R.id.ivFavorite);
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
