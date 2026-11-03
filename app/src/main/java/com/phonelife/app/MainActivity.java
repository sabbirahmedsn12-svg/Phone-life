package com.phonelife.app;

import android.app.Activity;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {

    LinearLayout layout;
    TextView report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(30, 40, 30, 30);
        layout.setBackgroundColor(0xFF080F1C);

        TextView title = new TextView(this);
        title.setText("PhoneLife");
        title.setTextSize(28);
        title.setTextColor(0xFF45E5A0);
        title.setGravity(Gravity.CENTER);
        layout.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("আপনার ফোন ব্যবহারের বাস্তব রিপোর্ট");
        subtitle.setTextColor(0xFFFFFFFF);
        subtitle.setTextSize(17);
        subtitle.setGravity(Gravity.CENTER);
        layout.addView(subtitle);

        Button permissionButton = new Button(this);
        permissionButton.setText("Usage Access অনুমতি দিন");
        layout.addView(permissionButton);

        permissionButton.setOnClickListener(v -> {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        });

        Button refreshButton = new Button(this);
        refreshButton.setText("রিপোর্ট দেখুন");
        layout.addView(refreshButton);

        report = new TextView(this);
        report.setTextColor(0xFFFFFFFF);
        report.setTextSize(16);
        report.setPadding(0, 25, 0, 0);
        layout.addView(report);

        setContentView(layout);

        refreshButton.setOnClickListener(v -> showReport());

        showReport();
    }

    private boolean hasUsagePermission() {
        AppOpsManager appOps =
            (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);

        int mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            getPackageName()
        );

        return mode == AppOpsManager.MODE_ALLOWED;
    }

    private void showReport() {

        if (!hasUsagePermission()) {
            report.setText(
                "প্রথমে Usage Access অনুমতি দিন।\n\n" +
                "অনুমতি দেওয়ার পর ফিরে এসে রিপোর্ট দেখুন চাপুন।"
            );
            return;
        }

        UsageStatsManager manager =
            (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long start = calendar.getTimeInMillis();
        long end = System.currentTimeMillis();

        List<UsageStats> stats = manager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            start,
            end
        );

        if (stats == null || stats.isEmpty()) {
            report.setText("এখনো ব্যবহারের তথ্য পাওয়া যায়নি।");
            return;
        }

        StringBuilder result = new StringBuilder();
        result.append("আজকের অ্যাপ ব্যবহারের রিপোর্ট\n\n");

        long totalTime = 0;

        String[] targetApps = {
            "com.google.android.youtube",
            "com.facebook.katana",
            "com.zhiliaoapp.musically",
            "com.whatsapp",
            "com.google.android.gm",
            "com.imo.android.imoim"
        };

        String[] names = {
            "YouTube",
            "Facebook",
            "TikTok",
            "WhatsApp",
            "Gmail",
            "imo"
        };

        for (int i = 0; i < targetApps.length; i++) {

            long appTime = 0;

            for (UsageStats item : stats) {
                if (targetApps[i].equals(item.getPackageName())) {
                    appTime += item.getTotalTimeInForeground();
                }
            }

            totalTime += appTime;

            result.append(names[i])
                  .append(": ")
                  .append(formatTime(appTime))
                  .append("\n");
        }

        result.append("\nমোট ব্যবহারের সময়: ")
              .append(formatTime(totalTime));

        report.setText(result.toString());
    }

    private String formatTime(long millis) {

        long minutes = millis / 60000;
        long hours = minutes / 60;
        long remaining = minutes % 60;

        return String.format(
            Locale.getDefault(),
            "%d ঘণ্টা %d মিনিট",
            hours,
            remaining
        );
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (report != null) {
            showReport();
        }
    }
          }
