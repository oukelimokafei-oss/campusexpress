package com.example.campusexpress;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.campusexpress.bean.Express;
import com.example.campusexpress.db.ExpressDao;
import com.example.campusexpress.db.ExpressDao;
import com.example.campusexpress.bean.Express;
import com.example.campusexpress.receiver.RemindReceiver;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddExpressActivity extends AppCompatActivity {
    private EditText etTrackingNumber, etPickupCode;
    private TextView tvSelectedDateTime;           // 新增：显示选中时间
    private Button btnSelectDate, btnSelectTime;   // 新增：日期时间选择按钮
    private Spinner spCompany;
    private Button btnSave, btnCancel, btnTestReminder;
    private ExpressDao expressDao;
    private String[] companies = {"顺丰速运", "中通快递", "圆通速递", "韵达快递", "申通快递", "京东物流", "邮政EMS", "极兔速递", "其他"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_express);

        initViews();
        // 设置默认时间为当前时间后1小时
        selectedCalendar.add(Calendar.HOUR_OF_DAY, 1);
        updateDateTimeDisplay();

        setupSpinner();
        setupDateTimePickers();  // 新增这行

        btnSave.setOnClickListener(v -> saveExpress());
        btnCancel.setOnClickListener(v -> finish());
        btnTestReminder.setOnClickListener(v -> setTestReminder());
    }

    private void initViews() {
        etTrackingNumber = findViewById(R.id.et_tracking_number);
        spCompany = findViewById(R.id.sp_company);
        etPickupCode = findViewById(R.id.et_pickup_code);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
        btnTestReminder = findViewById(R.id.btn_test_reminder);

        // 新增：绑定新控件
        btnSelectDate = findViewById(R.id.btn_select_date);
        btnSelectTime = findViewById(R.id.btn_select_time);
        tvSelectedDateTime = findViewById(R.id.tv_selected_datetime);

        expressDao = new ExpressDao(this);
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, companies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCompany.setAdapter(adapter);
    }
    //添加日期时间选择器初始化方法
    private void setupDateTimePickers() {
        btnSelectDate.setOnClickListener(v -> showDatePicker());
        btnSelectTime.setOnClickListener(v -> showTimePicker());
    }
    //添加日期选择、时间选择、更新时间显示的方法
    private Calendar selectedCalendar = Calendar.getInstance();  // 放在成员变量区

    private void showDatePicker() {
        int year = selectedCalendar.get(Calendar.YEAR);
        int month = selectedCalendar.get(Calendar.MONTH);
        int day = selectedCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    selectedCalendar.set(Calendar.YEAR, year1);
                    selectedCalendar.set(Calendar.MONTH, month1);
                    selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateTimeDisplay();
                }, year, month, day);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void showTimePicker() {
        int hour = selectedCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = selectedCalendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute1) -> {
                    selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedCalendar.set(Calendar.MINUTE, minute1);
                    updateDateTimeDisplay();
                }, hour, minute, true);
        timePickerDialog.show();
    }

    private void updateDateTimeDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        tvSelectedDateTime.setText("已选择时间：" + sdf.format(selectedCalendar.getTime()));
        //tvSelectedDateTime.setTextColor(getColor(android.R.color.black));
    }

    private void saveExpress() {
        String trackingNumber = etTrackingNumber.getText().toString().trim();
        String company = companies[spCompany.getSelectedItemPosition()];
        String pickupCode = etPickupCode.getText().toString().trim();
        //（修改前）手动输入:String expectedTime = etExpectedTime.getText().toString().trim();
        //（修改后）控件选择：
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String expectedTime = sdf.format(selectedCalendar.getTime());

        if (trackingNumber.isEmpty()) {
            etTrackingNumber.setError("请输入快递单号");
            return;
        }

        Express express = new Express(trackingNumber, company, pickupCode, expectedTime);
        long id = expressDao.addExpress(express);

        if (id > 0) {
            // 根据预计到达时间设置提醒
            setupReminderByTime(trackingNumber, company, pickupCode, expectedTime);
            Toast.makeText(this, "添加成功，已设置到达提醒", Toast.LENGTH_LONG).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "添加失败", Toast.LENGTH_SHORT).show();
        }
    }

    // 根据用户设置的时间设置提醒
    private void setupReminderByTime(String trackingNumber, String company, String pickupCode, String expectedTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date expectedDate = sdf.parse(expectedTime);
            long triggerTime = expectedDate.getTime();

            // 如果设置的时间已经过了，默认设置为1分钟后提醒（用于演示）
            if (triggerTime <= System.currentTimeMillis()) {
                triggerTime = System.currentTimeMillis() + 60 * 1000; // 1分钟后
                Toast.makeText(this, "预计时间已过，将设置为1分钟后提醒测试", Toast.LENGTH_SHORT).show();
            }

            setAlarm(triggerTime, trackingNumber, company, pickupCode, 1);

        } catch (Exception e) {
            e.printStackTrace();
            // 解析失败，默认5分钟后提醒
            long triggerTime = System.currentTimeMillis() + 5 * 60 * 1000;
            setAlarm(triggerTime, trackingNumber, company, pickupCode, 1);
            Toast.makeText(this, "时间格式错误，已设置为5分钟后提醒", Toast.LENGTH_SHORT).show();
        }
    }

    // 测试提醒：2分钟后提醒
    private void setTestReminder() {
        String trackingNumber = etTrackingNumber.getText().toString().trim();
        String company = companies[spCompany.getSelectedItemPosition()];
        String pickupCode = etPickupCode.getText().toString().trim();

        if (trackingNumber.isEmpty()) {
            etTrackingNumber.setError("请先输入快递单号");
            return;
        }

        long triggerTime = System.currentTimeMillis() + 2 * 60 * 1000; // 2分钟后
        setAlarm(triggerTime, trackingNumber, company, pickupCode, 2);

        Toast.makeText(this, "已设置2分钟后发送取件提醒", Toast.LENGTH_LONG).show();
    }

    private void setAlarm(long triggerTime, String trackingNumber, String company, String pickupCode, int requestId) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, RemindReceiver.class);
        intent.putExtra("tracking_number", trackingNumber);
        intent.putExtra("company", company);
        intent.putExtra("pickup_code", pickupCode);
        intent.setAction("REMINDER_" + requestId + "_" + System.currentTimeMillis());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // 显示提醒时间
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String timeStr = sdf.format(new Date(triggerTime));
        Toast.makeText(this, "提醒时间: " + timeStr, Toast.LENGTH_LONG).show();

        // 设置闹钟
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
    }
}
