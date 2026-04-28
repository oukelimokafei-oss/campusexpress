package com.example.campusexpress;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
    }

    private void initData() {
        expressDao = new ExpressDao(this);
        loadData();
    }

    private void loadData() {
        expressList = expressDao.getPendingExpresses();
        if (adapter == null) {
            adapter = new ExpressAdapter(expressList, false);
            adapter.setOnPickupClickListener(position -> markAsPicked(position));
            adapter.setOnItemClickListener(position -> deleteExpress(position));
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateList(expressList);
        }

        if (expressList.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    //为三个按钮设置点击监听器,实现跳转
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

    @Override
    //触发时机：跳转的Activity关闭后返回
    //接收AddExpressActivity添加成功后的回调，实时更新列表
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadData();
            Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    //核心功能：调用loadData()刷新数据
    //保证每次回到主界面时列表都是最新数据（兜底刷新机制）
    protected void onResume() {
        super.onResume();
        loadData();
    }
}
