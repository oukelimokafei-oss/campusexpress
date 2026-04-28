package com.example.campusexpress;

import android.os.Bundle;
import android.widget.ExpandableListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpressCompanyActivity extends AppCompatActivity {
    private ExpandableListView expandableListView;
    private List<String> companyNames;
    private HashMap<String, List<String>> companyDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_express_company);

        initData();
        setupExpandableListView();
    }

    private void initData() {
        companyNames = new ArrayList<>();
        companyDetails = new HashMap<>();

        // 顺丰速运
        companyNames.add("顺丰速运");
        List<String> sfDetails = new ArrayList<>();
        sfDetails.add("客服电话：95338");
        sfDetails.add("网点地址：校园快递服务中心1号柜");
        sfDetails.add("服务时间：8:00-20:00");
        companyDetails.put("顺丰速运", sfDetails);

        // 中通快递
        companyNames.add("中通快递");
        List<String> ztDetails = new ArrayList<>();
        ztDetails.add("客服电话：95311");
        ztDetails.add("网点地址：校园快递服务中心2号柜");
        ztDetails.add("服务时间：9:00-19:00");
        companyDetails.put("中通快递", ztDetails);

        // 圆通速递
        companyNames.add("圆通速递");
        List<String> ytDetails = new ArrayList<>();
        ytDetails.add("客服电话：95554");
        ytDetails.add("网点地址：校园快递服务中心3号柜");
        ytDetails.add("服务时间：9:00-19:00");
        companyDetails.put("圆通速递", ytDetails);

        // 韵达快递
        companyNames.add("韵达快递");
        List<String> ydDetails = new ArrayList<>();
        ydDetails.add("客服电话：95546");
        ydDetails.add("网点地址：校园快递服务中心4号柜");
        ydDetails.add("服务时间：9:00-19:00");
        companyDetails.put("韵达快递", ydDetails);

        // 申通快递
        companyNames.add("申通快递");
        List<String> stDetails = new ArrayList<>();
        stDetails.add("客服电话：95543");
        stDetails.add("网点地址：校园快递服务中心5号柜");
        stDetails.add("服务时间：9:00-19:00");
        companyDetails.put("申通快递", stDetails);

        // 京东物流
        companyNames.add("京东物流");
        List<String> jdDetails = new ArrayList<>();
        jdDetails.add("客服电话：950616");
        jdDetails.add("网点地址：校园快递服务中心6号柜");
        jdDetails.add("服务时间：8:00-20:00");
        companyDetails.put("京东物流", jdDetails);

        // 邮政EMS
        companyNames.add("邮政EMS");
        List<String> yzDetails = new ArrayList<>();
        yzDetails.add("客服电话：11183");
        yzDetails.add("网点地址：校园邮政服务点");
        yzDetails.add("服务时间：9:00-17:00");
        companyDetails.put("邮政EMS", yzDetails);
    }

    private void setupExpandableListView() {
        expandableListView = findViewById(R.id.expandable_list_view);
        ExpandableListAdapter adapter = new ExpandableListAdapter(this, companyNames, companyDetails);
        expandableListView.setAdapter(adapter);

        // 默认展开所有组
        for (int i = 0; i < companyNames.size(); i++) {
            expandableListView.expandGroup(i);
        }
    }
}
