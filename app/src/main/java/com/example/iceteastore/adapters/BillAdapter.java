package com.example.iceteastore.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iceteastore.R;
import com.example.iceteastore.daos.BillDAO;
import com.example.iceteastore.models.Bill;
import com.example.iceteastore.models.Product;

import java.util.List;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.BillViewHolder> {
    private Context context;
    private List<Bill> billList;
    private OnBillClickListener listener;

    public interface OnBillClickListener {
        void onUpdateClick(Bill bill);
    }


    public BillAdapter(Context context, List<Bill> billList, OnBillClickListener listener) {
        this.context = context;
        this.billList = billList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_bill, parent, false);
        return new BillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {
        Bill bill = billList.get(position);
        holder.tvUser.setText("Customer: " + bill.getUsername());
        holder.txtBillId.setText("Bill ID: " + bill.getId());
        holder.txtPrice.setText("Total: $" + bill.getTotal());
        holder.txtStatus.setText("Status: " + bill.getStatus());

        holder.btnUpdate.setOnClickListener(v -> listener.onUpdateClick(bill));

        // Xử lý khi nhấn nút "Delete"
        holder.btnDelete.setOnClickListener(v -> {
            showDeleteConfirmationDialog(position);
        });
    }

    //Hiển thị hộp thoại xác nhận xóa sản phẩm

    private void showDeleteConfirmationDialog(int position) {
        Bill item = billList.get(position);

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setTitle("Confirm Delete");
        builder.setMessage("Are you sure you want to delete \"" + item.getId() + "\"?");

        // Nút xác nhận xóa
        builder.setPositiveButton("Yes", (dialog, id) -> {
            BillDAO billDAO = new BillDAO(context);
            boolean isDeleted = billDAO.deleteBill(item.getId());
            if (isDeleted) {
                billList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, billList.size());
                Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Delete failed", Toast.LENGTH_SHORT).show();
            }
        });

        // Nút hủy bỏ
        builder.setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());

        // Hiển thị hộp thoại
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }



    @Override
    public int getItemCount() {
        return billList.size();
    }
    @Override
    public int getItemViewType(int position) {
        return position; // Đảm bảo RecyclerView hiển thị đầy đủ
    }


    public static class BillViewHolder extends RecyclerView.ViewHolder {
        TextView tvUser, txtBillId, txtPrice, txtStatus;
        ImageButton btnUpdate, btnDelete;

        public BillViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUser = itemView.findViewById(R.id.tv_user);
            txtBillId = itemView.findViewById(R.id.txtBillId);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            btnUpdate = itemView.findViewById(R.id.btn_update);
            btnDelete = itemView.findViewById(R.id.btn_delete);

        }
    }

}
