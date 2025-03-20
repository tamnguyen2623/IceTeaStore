package com.example.iceteastore.adapters;

import android.content.Context;
import android.content.SharedPreferences;
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

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvProductName.setText(product.getName());
        holder.tvRating.setText("⭐ " + product.getRating() + " (" + product.getReviews() + " reviews)");
        holder.tvPrice.setText("$" + product.getPrice());

        // Load ảnh sản phẩm
        String imagePath = product.getImage();
        if (!imagePath.isEmpty()) {
            int imageResource = context.getResources().getIdentifier(imagePath, "drawable", context.getPackageName());
            if (imageResource != 0) {
                holder.ivProductImage.setImageResource(imageResource);
            } else {
                holder.ivProductImage.setImageResource(R.drawable.placeholder_image);
            }
        }

        // Kiểm tra sản phẩm có trong danh sách yêu thích không
        FavoriteDAO favoriteDAO = new FavoriteDAO(context);
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginSession", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);
        boolean isFavorite = favoriteDAO.isFavorite(username, product.getId());

        // Cập nhật UI của icon trái tim
        holder.ivFavorite.setImageResource(isFavorite ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border);

        // Xử lý khi nhấn vào icon yêu thích
        holder.ivFavorite.setOnClickListener(v -> {
            if (favoriteDAO.isFavorite(username, product.getId())) {
                favoriteDAO.removeFromFavorites(username, product.getId());
                holder.ivFavorite.setImageResource(R.drawable.ic_favorite_border);
            } else {
                favoriteDAO.addToFavorites(username, product.getId());
                holder.ivFavorite.setImageResource(R.drawable.ic_favorite_filled);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void filterList(List<Product> filteredList) {
        this.productList = new ArrayList<>(filteredList);
        notifyDataSetChanged();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvRating, tvPrice;
        ImageView ivProductImage, ivFavorite;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            ivFavorite = itemView.findViewById(R.id.ivFavorite); // Nút yêu thích
        }
    }
}
