package com.example.campusexpress;

import android.os.Bundle;
import android.view.View;
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

public class HistoryExpressActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ExpressAdapter adapter;
    private ExpressDao expressDao;
    private List<Express> historyList;
    private LinearLayout emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        initViews();
        loadData();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
        emptyView = findViewById(R.id.empty_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        expressDao = new ExpressDao(this);
    }

    private void loadData() {
        historyList = expressDao.getHistoryExpresses();

        if (adapter == null) {
            adapter = new ExpressAdapter(historyList, true);
            adapter.setOnItemClickListener(position -> deleteHistory(position));
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateList(historyList);
        }

        if (historyList.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

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
}
