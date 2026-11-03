
package com.phonelife.app;

import android.app.Activity;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {

    private LinearLayout root;
    private TextView totalTime, statusText;
    private LinearLayout appList;

    private final String[] packages = {
        "com.google.android.youtube",
        "com.facebook.katana",
        "com.zhiliaoapp.musically",
        "com.whatsapp",
        "com.imo.android.imoim",
        "com.google.android.gm"
    };

    private final String[] names = {
        "YouTube", "Facebook", "TikTok",
        "WhatsApp", "imo", "Gmail"
    };

    private final String[] icons = {
        "▶️", "📘", "🎵", "🟢", "📞", "✉️"
    };

    private final int BG = Color.rgb(8, 15, 28);
    private final int CARD = Color.rgb(17, 30, 46);
    private final int GREEN = Color.rgb(69, 229, 160);
    private final int MUTED = Color.rgb(130, 148, 169);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildInterface();
        showReport();
    }

    private int dp(float value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density);
    }

    private GradientDrawable rounded(int color, int radius) {
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(color);
        shape.setCornerRadius(dp(radius));
        return shape;
    }

    private TextView text(String value, int size, int color) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
        return t;
    }

    private LinearLayout vertical() {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        return box;
    }

    private void buildInterface() {

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(BG);

        root = vertical();
        root.setPadding(dp(18), dp(20), dp(18), dp(25));
        scroll.addView(root);

        setContentView(scroll);

        // Header
        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setOrientation(LinearLayout.HORIZONTAL);

        TextView logo = text("Phone", 27, GREEN);
        logo.setTypeface(null, Typeface.BOLD);

        TextView logo2 = text("Life", 27, Color.WHITE);
        logo2.setTypeface(null, Typeface.BOLD);

        LinearLayout logoBox = new LinearLayout(this);
        logoBox.addView(logo);
        logoBox.addView(logo2);

        header.addView(logoBox,
                new LinearLayout.LayoutParams(0, -2, 1));

        TextView privacy = text("🔒 ব্যক্তিগত", 12, GREEN);
        privacy.setPadding(dp(12), dp(8), dp(12), dp(8));
        privacy.setBackground(rounded(
                Color.rgb(25, 61, 54), 20));

        header.addView(privacy);
        root.addView(header);

        addSpace(root, 25);

        // Welcome
        TextView welcome = text(
                "স্বাগতম PhoneLife-এ 👋", 23, Color.WHITE);
        welcome.setTypeface(null, Typeface.BOLD);
        root.addView(welcome);

        TextView subtitle = text(
                "আপনার ফোন ব্যবহারের ব্যক্তিগত ড্যাশবোর্ড",
                13, MUTED);
        root.addView(subtitle);

        addSpace(root, 22);

        // Summary card
        LinearLayout summary = vertical();
        summary.setPadding(dp(20), dp(22), dp(20), dp(22));
        summary.setBackground(rounded(
                Color.rgb(21, 62, 54), 20));

        summary.addView(text(
                "📱 আজকের মোট ব্যবহারের সময়",
                14, Color.rgb(168, 197, 186)));

        addSpace(summary, 8);

        totalTime = text("ডাটা অপেক্ষমাণ", 30, Color.WHITE);
        totalTime.setTypeface(null, Typeface.BOLD);
        summary.addView(totalTime);

        addSpace(summary, 6);

        summary.addView(text(
                "নির্বাচিত অ্যাপগুলোর ব্যবহার",
                12, Color.rgb(145, 181, 168)));

        root.addView(summary);

        addSpace(root, 18);

        // Permission card
        LinearLayout permissionCard = vertical();
        permissionCard.setPadding(
                dp(16), dp(16), dp(16), dp(16));
        permissionCard.setBackground(rounded(CARD, 16));

        permissionCard.addView(text(
                "🔐 Usage Access অনুমতি",
                17, Color.WHITE));

        addSpace(permissionCard, 5);

        permissionCard.addView(text(
                "নিজের ফোনের অ্যাপ ব্যবহারের সময় দেখতে এই অনুমতি প্রয়োজন।",
                12, MUTED));

        Button permissionButton = new Button(this);
        permissionButton.setText("অনুমতি সেটিংস খুলুন");
        permissionButton.setTextColor(Color.BLACK);
        permissionButton.setBackground(rounded(GREEN, 12));

        LinearLayout.LayoutParams bp =
                new LinearLayout.LayoutParams(-1, dp(48));
        bp.topMargin = dp(12);
        permissionCard.addView(permissionButton, bp);

        permissionButton.setOnClickListener(v -> {
            startActivity(new Intent(
                    Settings.ACTION_USAGE_ACCESS_SETTINGS));
        });

        root.addView(permissionCard);

        addSpace(root, 22);

        // Report heading
        TextView heading = text(
                "📊 অ্যাপ ব্যবহারের হিসাব", 19, Color.WHITE);
        heading.setTypeface(null, Typeface.BOLD);
        root.addView(heading);

        addSpace(root, 12);

        appList = vertical();
        root.addView(appList);

        addSpace(root, 15);

        Button refresh = new Button(this);
        refresh.setText("↻  রিপোর্ট রিফ্রেশ করুন");
        refresh.setTextColor(Color.BLACK);
        refresh.setBackground(rounded(GREEN, 14));

        root.addView(refresh, new LinearLayout.LayoutParams(
                -1, dp(50)));

        refresh.setOnClickListener(v -> showReport());

        addSpace(root, 15);

        statusText = text(
                "বাস্তব রিপোর্টের জন্য Usage Access প্রয়োজন।",
                12, MUTED);
        root.addView(statusText);

        addSpace(root, 25);

        LinearLayout note = vertical();
        note.setPadding(dp(15), dp(15), dp(15), dp(15));
        note.setBackground(rounded(
                Color.rgb(23, 38, 55), 14));

        note.addView(text(
                "🔒 আপনার তথ্য আপনারই",
                15, GREEN));

        addSpace(note, 6);

        note.addView(text(
                "PhoneLife আপনার ফোনের নির্বাচিত অ্যাপগুলোর ব্যবহার সময় দেখায়। এই সংস্করণে তথ্য ফোনের মধ্যেই হিসাব করা হয়।",
                12, MUTED));

        root.addView(note);

        addSpace(root, 25);

        TextView footer = text(
                "PhoneLife © 2026\nYour Phone, Your Data, Your Privacy\nDeveloper: সাব্বির আহমদ",
                12, MUTED);
        footer.setGravity(Gravity.CENTER);
        root.addView(footer);
    }

    private void addSpace(LinearLayout parent, int height) {
        View space = new View(this);
        parent.addView(space, new LinearLayout.LayoutParams(
                1, dp(height)));
    }

    private boolean hasUsagePermission() {
        AppOpsManager ops =
                (AppOpsManager) getSystemService(
                        Context.APP_OPS_SERVICE);

        int mode = ops.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                getPackageName());

        return mode == AppOpsManager.MODE_ALLOWED;
    }

    private void showReport() {

        appList.removeAllViews();

        if (!hasUsagePermission()) {
            totalTime.setText("অনুমতি প্রয়োজন");
            statusText.setText(
                    "Usage Access অনুমতি চালু করে আবার রিপোর্ট দেখুন।");

            for (int i = 0; i < names.length; i++) {
                addAppCard(i, "অনুমতি অপেক্ষমাণ");
            }
            return;
        }

        UsageStatsManager manager =
                (UsageStatsManager) getSystemService(
                        Context.USAGE_STATS_SERVICE);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        long start = cal.getTimeInMillis();
        long end = System.currentTimeMillis();

        List<UsageStats> stats = manager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                start, end);

        if (stats == null || stats.isEmpty()) {
            totalTime.setText("ডাটা পাওয়া যায়নি");
            statusText.setText(
                    "আজকের ব্যবহারের তথ্য এখনো পাওয়া যায়নি।");
            for (int i = 0; i < names.length; i++) {
                addAppCard(i, "ডাটা পাওয়া যায়নি");
            }
            return;
        }

        long total = 0;

        for (int i = 0; i < names.length; i++) {

            long duration = 0;

            for (UsageStats item : stats) {
                if (packages[i].equals(item.getPackageName())) {
                    duration += item.getTotalTimeInForeground();
                }
            }

            total += duration;
            addAppCard(i, formatTime(duration));
        }

        totalTime.setText(formatTime(total));
        statusText.setText(
                "সর্বশেষ আপডেট: " +
                new java.text.SimpleDateFormat(
                        "hh:mm a", Locale.getDefault())
                        .format(System.currentTimeMillis()));
    }

    private void addAppCard(int index, String value) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(15), dp(14), dp(15), dp(14));
        card.setBackground(rounded(CARD, 15));

        LinearLayout.LayoutParams cp =
                new LinearLayout.LayoutParams(-1, -2);
        cp.bottomMargin = dp(10);
        appList.addView(card, cp);

        TextView icon = text(icons[index], 24, Color.WHITE);
        icon.setGravity(Gravity.CENTER);
        icon.setBackground(rounded(
                Color.rgb(28, 44, 64), 12));

        card.addView(icon, new LinearLayout.LayoutParams(
                dp(48), dp(48)));

        LinearLayout details = vertical();
        details.setPadding(dp(12), 0, 0, 0);

        TextView name = text(names[index], 15, Color.WHITE);
        name.setTypeface(null, Typeface.BOLD);
        details.addView(name);

        details.addView(text(
                "আজকের ব্যবহার", 11, MUTED));

        card.addView(details,
                new LinearLayout.LayoutParams(0, -2, 1));

        TextView time = text(value, 13, GREEN);
        time.setGravity(Gravity.CENTER);
        card.addView(time);
    }

    private String formatTime(long millis) {
        long minutes = millis / 60000;
        long hours = minutes / 60;
        long remaining = minutes % 60;

        return String.format(
                Locale.getDefault(),
                "%d ঘণ্টা %d মিনিট",
                hours, remaining);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (root != null) {
            showReport();
        }
    }
                         }
            
