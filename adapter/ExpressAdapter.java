package com.example.campusexpress.adapter;

import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusexpress.R;
import com.example.campusexpress.bean.Express;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpressAdapter extends RecyclerView.Adapter<ExpressAdapter.ViewHolder> {
    private List<Express> expressList;
    private OnItemClickListener listener;
    private OnPickupClickListener pickupListener;
    private boolean isHistoryMode;
    // 添加成员变量
    private boolean isMultiSelectMode = false;
    private SparseBooleanArray selectedPositions = new SparseBooleanArray();

    public interface OnItemClickListener {
        void onDeleteClick(int position);
    }

    public interface OnPickupClickListener {
        void onPickupClick(int position);
    }

    public interface OnItemLongClickListener {
        boolean onLongClick(int position);
    }

    // 添加选中数量变化监听接口
    public interface OnSelectionChangeListener {
        void onSelectionChanged(int count);
    }

    private OnItemLongClickListener longClickListener;
    private OnSelectionChangeListener selectionChangeListener;

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

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    public void setOnSelectionChangeListener(OnSelectionChangeListener listener) {
        this.selectionChangeListener = listener;
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

        // 批量模式下隐藏操作按钮，避免混淆
        if (isMultiSelectMode) {
            holder.btnPickup.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        } else {
            // 正常模式下的按钮显示逻辑
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

            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(position);
                }
            });
        }

        // 设置多选模式的背景色和复选框
        if (isMultiSelectMode) {
            holder.itemView.setBackgroundColor(selectedPositions.get(position) ?
                    Color.parseColor("#E3F2FD") : Color.TRANSPARENT);
            // 显示复选框
            if (holder.checkBox != null) {
                holder.checkBox.setVisibility(View.VISIBLE);
                holder.checkBox.setChecked(selectedPositions.get(position));
            }
        } else {
            if (holder.checkBox != null) {
                holder.checkBox.setVisibility(View.GONE);
            }
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        // 添加点击监听支持多选
        holder.itemView.setOnClickListener(v -> {
            if (isMultiSelectMode) {
                toggleSelection(position);
            }
        });

        // 设置长按监听
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null && !isMultiSelectMode) {
                return longClickListener.onLongClick(position);
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return expressList.size();
    }

    public void updateList(List<Express> newList) {
        this.expressList = newList;
        // 清除选中状态，避免位置错乱
        clearSelections();
        notifyDataSetChanged();
    }

    public void setMultiSelectMode(boolean enabled) {
        this.isMultiSelectMode = enabled;
        if (!enabled) {
            clearSelections();
        }
        notifyDataSetChanged();
    }

    public boolean isMultiSelectMode() {
        return isMultiSelectMode;
    }

    public void toggleSelection(int position) {
        if (selectedPositions.get(position, false)) {
            selectedPositions.delete(position);
        } else {
            selectedPositions.put(position, true);
        }
        notifyItemChanged(position);
        // 通知选中数量变化
        if (selectionChangeListener != null) {
            selectionChangeListener.onSelectionChanged(getSelectedCount());
        }
    }

    public void clearSelections() {
        selectedPositions.clear();
    }

    public List<Express> getSelectedExpresses(List<Express> fullList) {
        List<Express> selected = new ArrayList<>();
        for (int i = 0; i < selectedPositions.size(); i++) {
            int position = selectedPositions.keyAt(i);
            if (position < fullList.size()) {
                selected.add(fullList.get(position));
            }
        }
        return selected;
    }

    public int getSelectedCount() {
        return selectedPositions.size();
    }

    // ViewHolder 类
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTrackingNumber, tvCompany, tvPickupCode, tvExpectedTime, tvPickupTime;
        Button btnPickup, btnDelete;
        CheckBox checkBox;

        ViewHolder(View itemView) {
            super(itemView);
            tvTrackingNumber = itemView.findViewById(R.id.tv_tracking_number);
            tvCompany = itemView.findViewById(R.id.tv_company);
            tvPickupCode = itemView.findViewById(R.id.tv_pickup_code);
            tvExpectedTime = itemView.findViewById(R.id.tv_expected_time);
            tvPickupTime = itemView.findViewById(R.id.tv_pickup_time);
            btnPickup = itemView.findViewById(R.id.btn_pickup);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            checkBox = itemView.findViewById(R.id.checkbox_select);
        }
    }
}
