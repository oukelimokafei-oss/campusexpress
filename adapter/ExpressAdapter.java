package com.example.campusexpress.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusexpress.R;
import com.example.campusexpress.bean.Express;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpressAdapter extends RecyclerView.Adapter<ExpressAdapter.ViewHolder> {
    private List<Express> expressList;
    private OnItemClickListener listener;
    private OnPickupClickListener pickupListener;
    private boolean isHistoryMode;

    public interface OnItemClickListener {
        void onDeleteClick(int position);
    }

    public interface OnPickupClickListener {
        void onPickupClick(int position);
    }

    public ExpressAdapter(List<Express> expressList, boolean isHistoryMode) {
        this.expressList = expressList;
        this.isHistoryMode = isHistoryMode;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnPickupClickListener(OnPickupClickListener listener) {
        this.pickupListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_express, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Express express = expressList.get(position);

        holder.tvTrackingNumber.setText("单号: " + express.getTrackingNumber());
        holder.tvCompany.setText("快递: " + express.getCompany());
        holder.tvPickupCode.setText("取件码: " + (express.getPickupCode() != null ? express.getPickupCode() : "无"));
        holder.tvExpectedTime.setText("预计到达: " + express.getExpectedTime());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        if (express.getStatus() == 1 && express.getPickupTime() > 0) {
            holder.tvPickupTime.setText("取件时间: " + sdf.format(new Date(express.getPickupTime())));
            holder.tvPickupTime.setVisibility(View.VISIBLE);
        } else {
            holder.tvPickupTime.setVisibility(View.GONE);
        }

        if (isHistoryMode) {
            holder.btnPickup.setVisibility(View.GONE);
        } else {
            holder.btnPickup.setVisibility(View.VISIBLE);
            holder.btnPickup.setText("标记取件");
            holder.btnPickup.setOnClickListener(v -> {
                if (pickupListener != null) {
                    pickupListener.onPickupClick(position);
                }
            });
        }

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return expressList.size();
    }

    public void updateList(List<Express> newList) {
        this.expressList = newList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTrackingNumber, tvCompany, tvPickupCode, tvExpectedTime, tvPickupTime;
        Button btnPickup, btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            tvTrackingNumber = itemView.findViewById(R.id.tv_tracking_number);
            tvCompany = itemView.findViewById(R.id.tv_company);
            tvPickupCode = itemView.findViewById(R.id.tv_pickup_code);
            tvExpectedTime = itemView.findViewById(R.id.tv_expected_time);
            tvPickupTime = itemView.findViewById(R.id.tv_pickup_time);
            btnPickup = itemView.findViewById(R.id.btn_pickup);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
