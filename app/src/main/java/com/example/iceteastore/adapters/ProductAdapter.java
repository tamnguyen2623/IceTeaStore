package com.example.iceteastore.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.iceteastore.R;
import com.example.iceteastore.daos.FavoriteDAO;
import com.example.iceteastore.daos.ShoppingCartDAO;
import com.example.iceteastore.models.Product;
import com.example.iceteastore.models.ShoppingCart;
import com.example.iceteastore.utils.SessionManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    private SessionManager sessionManager; // Khai báo SessionManager

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
        this.sessionManager = new SessionManager(context); // Khởi tạo SessionManager trong constructor
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
        String username = sessionManager.getLoggedInUser(); // Lấy username từ session trong onBindViewHolder

        // Kiểm tra nếu username null thì thông báo
        if (username == null) {
            Toast.makeText(context, "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }

        holder.tvProductName.setText(product.getName());
        holder.tvPrice.setText("$" + product.getPrice());

        String imageResource = product.getImage();
        if (imageResource != null && !imageResource.isEmpty()) {
            Glide.with(context).load(imageResource).into(holder.ivProductImage);
        } else {
            holder.ivProductImage.setImageResource(R.drawable.placeholder_image);
        }


        // Kiểm tra sản phẩm có trong danh sách yêu thích không
        FavoriteDAO favoriteDAO = new FavoriteDAO(context);
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginSession", Context.MODE_PRIVATE);
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

        // Xử lý khi bấm vào tên sản phẩm
        holder.tvProductName.setOnClickListener(v -> {
            View dialogView = LayoutInflater.from(context).inflate(R.layout.activity_product_detail, null);
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
            bottomSheetDialog.setContentView(dialogView);

            BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from((View) dialogView.getParent());
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            bottomSheetBehavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);

            ImageView ivDialogProductImage = dialogView.findViewById(R.id.ivProductImage);
            TextView tvDialogProductName = dialogView.findViewById(R.id.tvProductName);
            TextView tvDialogRating = dialogView.findViewById(R.id.tvRating);
            TextView tvDialogPrice = dialogView.findViewById(R.id.tvPrice);

            tvDialogProductName.setText(product.getName());
            tvDialogRating.setText("⭐ " + product.getRating() + " (" + product.getReviews() + " reviews)");
            tvDialogPrice.setText("$" + product.getPrice());



            Button btnAddToOrder = dialogView.findViewById(R.id.btn_order);

            btnAddToOrder.setOnClickListener(v1 -> {
                ShoppingCartDAO shoppingCartDAO = new ShoppingCartDAO(context);
                List<ShoppingCart> cartItems = shoppingCartDAO.getCartItems(username);

                boolean itemExists = false;

                for (ShoppingCart cartItem : cartItems) {
                    if (cartItem.getProductId() == product.getId()) {
                        // Sản phẩm đã có trong giỏ hàng -> Tăng số lượng
                        int newQuantity = cartItem.getQuantity() + 1;
                        shoppingCartDAO.updateQuantity(username, product.getId(), newQuantity);
                        Toast.makeText(context, "Tăng số lượng " + product.getName() + " lên " + newQuantity, Toast.LENGTH_SHORT).show();
                        itemExists = true;
                        break;
                    }
                }

                if (!itemExists) {
                    // Nếu chưa có, thêm mới vào giỏ hàng
                    ShoppingCart newItem = new ShoppingCart(product.getId(), product.getName(), product.getImage(), 1, product.getPrice());
                    shoppingCartDAO.addToCart(username, newItem);
                    Toast.makeText(context, "Đã thêm " + product.getName() + " vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                }

                bottomSheetDialog.dismiss();
            });



            bottomSheetDialog.show();
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
        Button btnOrder;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            ivFavorite = itemView.findViewById(R.id.ivFavorite); // Nút yêu thích
            btnOrder = itemView.findViewById(R.id.btn_order);
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