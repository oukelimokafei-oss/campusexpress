package com.example.campusexpress;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
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

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ExpressAdapter adapter;
    private ExpressDao expressDao;
    private List<Express> expressList;
    private Button btnAdd, btnCompany, btnHistory;
    private LinearLayout emptyView;
    // 添加成员变量
    private View batchActionBar;
    private TextView tvSelectedCount;
    private Button btnBatchPickup, btnBatchDelete, btnBatchCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 请求通知权限（Android 13+）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 100);
            }
        }

        initViews();
        initData();
        setupListeners();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
        btnAdd = findViewById(R.id.btn_add);
        btnCompany = findViewById(R.id.btn_company);
        btnHistory = findViewById(R.id.btn_history);
        emptyView = findViewById(R.id.empty_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 批量操作栏
        batchActionBar = findViewById(R.id.batch_action_bar);
        tvSelectedCount = findViewById(R.id.tv_selected_count);
        btnBatchPickup = findViewById(R.id.btn_batch_pickup);
        btnBatchDelete = findViewById(R.id.btn_batch_delete);
        btnBatchCancel = findViewById(R.id.btn_batch_cancel);

        if (btnBatchPickup != null) {
            btnBatchPickup.setOnClickListener(v -> batchMarkAsPicked());
        }
        if (btnBatchDelete != null) {
            btnBatchDelete.setOnClickListener(v -> batchDeleteExpresses());
        }
        if (btnBatchCancel != null) {
            btnBatchCancel.setOnClickListener(v -> exitBatchMode());
        }
    }

    private void initData() {
        expressDao = new ExpressDao(this);
        loadData();
        setupBatchModeEntry();  // 设置长按进入批量模式
    }

    private void loadData() {
        expressList = expressDao.getPendingExpresses();
        if (adapter == null) {
            adapter = new ExpressAdapter(expressList, false);
            adapter.setOnPickupClickListener(position -> markAsPicked(position));
            adapter.setOnItemClickListener(position -> deleteExpress(position));
            adapter.setOnItemLongClickListener(position -> {
                if (!adapter.isMultiSelectMode()) {
                    enterBatchMode();
                    adapter.toggleSelection(position);
                    updateSelectedCount();
                }
                return true;
            });
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateList(expressList);
        }

        // 控制空视图显示
        if (expressList.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        // 如果当前处于批量模式且数据变为空，自动退出批量模式
        if (adapter.isMultiSelectMode() && expressList.isEmpty()) {
            exitBatchMode();
        }
    }

    // 为三个按钮设置点击监听器，实现跳转
    private void setupListeners() {
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddExpressActivity.class);
            startActivityForResult(intent, 1);
        });

        btnCompany.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ExpressCompanyActivity.class);
            startActivity(intent);
        });

        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoryExpressActivity.class);
            startActivity(intent);
        });
    }

    private void markAsPicked(int position) {
        Express express = expressList.get(position);
        new AlertDialog.Builder(this)
                .setTitle("确认取件")
                .setMessage("确定已取件吗？")
                .setPositiveButton("确认", (dialog, which) -> {
                    if (expressDao.markAsPicked(express.getId())) {
                        Toast.makeText(this, "已标记取件", Toast.LENGTH_SHORT).show();
                        loadData();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void deleteExpress(int position) {
        Express express = expressList.get(position);
        new AlertDialog.Builder(this)
                .setTitle("删除确认")
                .setMessage("确定删除该快递信息吗？")
                .setPositiveButton("确认", (dialog, which) -> {
                    if (expressDao.deleteExpress(express.getId())) {
                        Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
                        loadData();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    // 批量标记取件方法
    private void batchMarkAsPicked() {
        List<Express> selected = adapter.getSelectedExpresses(expressList);
        if (selected.isEmpty()) {
            Toast.makeText(this, "请选择要标记的快递", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("批量标记取件")
                .setMessage("确定标记选中的 " + selected.size() + " 个快递为已取件吗？")
                .setPositiveButton("确认", (dialog, which) -> {
                    int successCount = 0;
                    for (Express express : selected) {
                        if (expressDao.markAsPicked(express.getId())) {
                            successCount++;
                        }
                    }
                    Toast.makeText(this, "成功标记 " + successCount + " 个快递", Toast.LENGTH_SHORT).show();
                    exitBatchMode();
                    loadData();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    // 批量删除方法
    private void batchDeleteExpresses() {
        List<Express> selected = adapter.getSelectedExpresses(expressList);
        if (selected.isEmpty()) {
            Toast.makeText(this, "请先勾选要删除的快递", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("批量删除")
                .setMessage("确定删除选中的 " + selected.size() + " 个快递吗？")
                .setPositiveButton("确认", (dialog, which) -> {
                    int successCount = 0;
                    for (Express express : selected) {
                        if (expressDao.deleteExpress(express.getId())) {
                            successCount++;
                        }
                    }
                    Toast.makeText(this, "成功删除 " + successCount + " 个快递", Toast.LENGTH_SHORT).show();
                    exitBatchMode();  // 退出批量模式
                    loadData();       // 刷新列表
                })
                .setNegativeButton("取消", null)
                .show();
    }

    // 进入批量模式
    private void enterBatchMode() {
        adapter.setMultiSelectMode(true);
        batchActionBar.setVisibility(View.VISIBLE);
        // 隐藏其他按钮
        btnAdd.setVisibility(View.GONE);
        btnCompany.setVisibility(View.GONE);
        btnHistory.setVisibility(View.GONE);
        updateSelectedCount();
    }

    // 退出批量模式
    private void exitBatchMode() {
        adapter.setMultiSelectMode(false);
        batchActionBar.setVisibility(View.GONE);
        btnAdd.setVisibility(View.VISIBLE);
        btnCompany.setVisibility(View.VISIBLE);
        btnHistory.setVisibility(View.VISIBLE);
    }

    // 更新选中数量
    private void updateSelectedCount() {
        if (tvSelectedCount != null && adapter != null) {
            tvSelectedCount.setText("已选择 " + adapter.getSelectedCount() + " 项");
        }
    }

    // 设置长按进入批量模式
    private void setupBatchModeEntry() {
        // 注意：长按监听已经在 adapter 创建时设置，这里只是确保 adapter 不为空
        // 实际的长按逻辑在 loadData() 中创建 adapter 时已经设置
    }

    @Override
    // 触发时机：跳转的Activity关闭后返回
    // 接收AddExpressActivity添加成功后的回调，实时更新列表
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadData();
            Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    // 核心功能：调用loadData()刷新数据
    // 保证每次回到主界面时列表都是最新数据（兜底刷新机制）
    protected void onResume() {
        super.onResume();
        loadData();
    }
}
