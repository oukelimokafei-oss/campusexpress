package com.example.campusexpress.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.example.campusexpress.MainActivity;
import com.example.campusexpress.R;

public class RemindReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "express_arrival_channel";
    private static final int NOTIFICATION_ID = 1001;

    @Override
    public void onReceive(Context context, Intent intent) {
        String trackingNumber = intent.getStringExtra("tracking_number");
        String company = intent.getStringExtra("company");
        String pickupCode = intent.getStringExtra("pickup_code");

        createNotificationChannel(context);
        showArrivalNotification(context, trackingNumber, company, pickupCode);
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "快递到达提醒",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("当快递到达驿站时，会发送此通知");
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 500, 200, 500});

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void showArrivalNotification(Context context, String trackingNumber, String company, String pickupCode) {
        // 点击通知打开应用
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("📦 快递到达提醒")
                .setContentText(company + " 快递已到达驿站")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("快递公司：" + company + "\n" +
                                "快递单号：" + trackingNumber + "\n" +
                                "取件码：" + pickupCode + "\n\n" +
                                "请及时前往驿站取件！"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
