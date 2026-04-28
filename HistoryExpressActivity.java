package com.example.campusexpress;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusexpress.adapter.ExpressAdapter;
import com.example.campusexpress.bean.Express;
import com.example.campusexpress.db.ExpressDao;

import java.util.List;

public class HistoryExpressActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ExpressAdapter adapter;
    private ExpressDao expressDao;
    private List<Express> historyList;
    private LinearLayout emptyView;

    // 批量操作相关成员变量
    private View batchActionBar;
    private TextView tvSelectedCount;
    private Button btnBatchDelete, btnBatchCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        initViews();
        initData();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
        emptyView = findViewById(R.id.empty_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        expressDao = new ExpressDao(this);

        // 批量操作栏控件
        batchActionBar = findViewById(R.id.batch_action_bar);
        tvSelectedCount = findViewById(R.id.tv_selected_count);
        btnBatchDelete = findViewById(R.id.btn_batch_delete);
        btnBatchCancel = findViewById(R.id.btn_batch_cancel);

        if (btnBatchDelete != null) {
            btnBatchDelete.setOnClickListener(v -> batchDeleteHistory());
        }
        if (btnBatchCancel != null) {
            btnBatchCancel.setOnClickListener(v -> exitBatchMode());
        }
    }

    private void initData() {
        loadData();
        setupBatchModeEntry();
    }

    private void loadData() {
        historyList = expressDao.getHistoryExpresses();

        if (adapter == null) {
            adapter = new ExpressAdapter(historyList, true);
            adapter.setOnItemClickListener(position -> deleteHistory(position));
            // 设置长按监听，进入批量模式
            adapter.setOnItemLongClickListener(position -> {
                if (!adapter.isMultiSelectMode()) {
                    enterBatchMode();
                    adapter.toggleSelection(position);
                    updateSelectedCount();
                }
                return true;
            });
            // 设置选中数量变化监听
            adapter.setOnSelectionChangeListener(count -> {
                updateSelectedCount();
            });
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateList(historyList);
        }

        // 控制空视图显示
        if (historyList.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        // 如果当前处于批量模式且数据变为空，自动退出批量模式
        if (adapter.isMultiSelectMode() && historyList.isEmpty()) {
            exitBatchMode();
        }
    }

    // 单个删除历史记录
    private void deleteHistory(int position) {
        Express express = historyList.get(position);
        new AlertDialog.Builder(this)
                .setTitle("删除确认")
                .setMessage("确定删除该历史记录吗？")
                .setPositiveButton("确认", (dialog, which) -> {
                    if (expressDao.deleteExpress(express.getId())) {
                        Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
                        loadData();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    // 批量删除历史记录
    private void batchDeleteHistory() {
        List<Express> selected = adapter.getSelectedExpresses(historyList);
        if (selected.isEmpty()) {
            Toast.makeText(this, "请先勾选要删除的记录", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("批量删除")
                .setMessage("确定删除选中的 " + selected.size() + " 条历史记录吗？")
                .setPositiveButton("确认", (dialog, which) -> {
                    int successCount = 0;
                    for (Express express : selected) {
                        if (expressDao.deleteExpress(express.getId())) {
                            successCount++;
                        }
                    }
                    Toast.makeText(this, "成功删除 " + successCount + " 条记录", Toast.LENGTH_SHORT).show();
                    exitBatchMode();
                    loadData();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    // 进入批量模式
    private void enterBatchMode() {
        adapter.setMultiSelectMode(true);
        batchActionBar.setVisibility(View.VISIBLE);
        updateSelectedCount();
    }

    // 退出批量模式
    private void exitBatchMode() {
        adapter.setMultiSelectMode(false);
        batchActionBar.setVisibility(View.GONE);
    }

    // 更新选中数量
    private void updateSelectedCount() {
        if (tvSelectedCount != null && adapter != null) {
            tvSelectedCount.setText("已选择 " + adapter.getSelectedCount() + " 项");
        }
    }

    // 设置长按进入批量模式
    private void setupBatchModeEntry() {
        // 长按监听已经在 loadData() 中创建 adapter 时设置
    }
}
