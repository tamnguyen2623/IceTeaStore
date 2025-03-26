package com.example.iceteastore.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.iceteastore.R;
import com.example.iceteastore.models.Bill;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class BillOfUserAdapter extends RecyclerView.Adapter<BillOfUserAdapter.BillViewHolder> {
    private List<Bill> billList;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public BillOfUserAdapter(List<Bill> billList) {
        this.billList = billList;
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bill, parent, false);
        return new BillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {
        Bill bill = billList.get(position);
        holder.billId.setText("Bill ID: " + bill.getId());
        holder.date.setText("Date: " + sdf.format(bill.getDate()));
        holder.status.setText("Status: " + bill.getStatus());
        holder.tvPrice.setText("Total: $" + bill.getTotal());
    }

    @Override
    public int getItemCount() {
        return billList.size();
    }

    public static class BillViewHolder extends RecyclerView.ViewHolder {
        TextView billId, date, status, tvPrice;

        public BillViewHolder(@NonNull View itemView) {
            super(itemView);
            billId = itemView.findViewById(R.id.billId);
            date = itemView.findViewById(R.id.date);
            status = itemView.findViewById(R.id.status);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}
