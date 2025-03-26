//OrderAdapter
package com.example.iceteastore.adapters;




import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iceteastore.models.Order;
import com.example.iceteastore.R;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orderList;

    public OrderAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.txtBillId.setText("Bill ID: " + order.getBillId());
//        holder.txtProductId.setText("Product ID: " + order.getProductId());
        holder.txtQuantity.setText("Quantity: " + order.getQuantity());
        holder.txtPrice.setText("Price: $" + order.getPrice());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView txtBillId, txtProductId, txtQuantity, txtPrice;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            txtBillId = itemView.findViewById(R.id.txtBillId);
//            txtProductId = itemView.findViewById(R.id.txtProductId);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            txtPrice = itemView.findViewById(R.id.txtPrice);
        }
    }
}
