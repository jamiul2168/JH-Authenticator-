package com.webtoapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.*;
import android.widget.*;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.app.Dialog;
import android.graphics.Bitmap;

public class MainActivity extends Activity {

    private WebView webView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefresh;
    private FrameLayout loadingOverlay;
    private static final String APP_URL = "https://jh.auth.jhtone.site/";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.WHITE);
        window.setNavigationBarColor(Color.WHITE);
        window.getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        );

        setContentView(R.layout.activity_main);

        progressBar    = findViewById(R.id.progressBar);
        swipeRefresh   = findViewById(R.id.swipeRefresh);
        webView        = findViewById(R.id.webView);
        loadingOverlay = findViewById(R.id.loadingOverlay);

        swipeRefresh.setColorSchemeColors(Color.BLACK);
        swipeRefresh.setProgressBackgroundColorSchemeColor(Color.WHITE);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setAllowFileAccess(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webView.setBackgroundColor(Color.WHITE);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
                swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                loadingOverlay.animate()
                    .alpha(0f).setDuration(300)
                    .withEndAction(() -> loadingOverlay.setVisibility(View.GONE))
                    .start();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                progressBar.setVisibility(View.GONE);
                loadingOverlay.setVisibility(View.GONE);
                webView.loadData(getOfflinePage(), "text/html", "UTF-8");
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (url.startsWith("http://") || url.startsWith("https://")) return false;
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Cannot open link", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
                if (newProgress == 100) progressBar.setVisibility(View.GONE);
            }
        });

        swipeRefresh.setOnRefreshListener(() -> {
            loadingOverlay.setAlpha(1f);
            loadingOverlay.setVisibility(View.VISIBLE);
            webView.reload();
        });

        webView.loadUrl(APP_URL);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            showExitDialog();
        }
    }

    private void showExitDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);

        // ── Root card ──
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundColor(Color.WHITE);
        card.setPadding(dp(28), dp(32), dp(28), dp(24));

        // Rounded background via outline
        card.setClipToOutline(true);
        android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable();
        bg.setColor(Color.WHITE);
        bg.setCornerRadius(dp(24));
        card.setBackground(bg);

        // ── Icon circle ──
        FrameLayout iconCircle = new FrameLayout(this);
        LinearLayout.LayoutParams circleParams = new LinearLayout.LayoutParams(dp(64), dp(64));
        circleParams.gravity = Gravity.CENTER_HORIZONTAL;
        circleParams.bottomMargin = dp(20);
        iconCircle.setLayoutParams(circleParams);

        android.graphics.drawable.GradientDrawable circleBg = new android.graphics.drawable.GradientDrawable();
        circleBg.setShape(android.graphics.drawable.GradientDrawable.OVAL);
        circleBg.setColor(Color.parseColor("#F3F4F6"));
        iconCircle.setBackground(circleBg);

        TextView iconEmoji = new TextView(this);
        iconEmoji.setText("👋");
        iconEmoji.setTextSize(28);
        FrameLayout.LayoutParams emojiParams = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        emojiParams.gravity = Gravity.CENTER;
        iconEmoji.setLayoutParams(emojiParams);
        iconCircle.addView(iconEmoji);
        card.addView(iconCircle);

        // ── Title ──
        TextView title = new TextView(this);
        title.setText("Exit now?");
        title.setTextColor(Color.parseColor("#111111"));
        title.setTextSize(19);
        title.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        titleParams.bottomMargin = dp(8);
        title.setLayoutParams(titleParams);
        card.addView(title);

        // ── Subtitle ──
        TextView subtitle = new TextView(this);
        subtitle.setText("Confirm if you want to exit\nJH Auth.");
        subtitle.setTextColor(Color.parseColor("#888888"));
        subtitle.setTextSize(13);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setLineSpacing(0, 1.4f);
        LinearLayout.LayoutParams subParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        subParams.bottomMargin = dp(28);
        subtitle.setLayoutParams(subParams);
        card.addView(subtitle);

        // ── Divider ──
        View divider = new View(this);
        divider.setBackgroundColor(Color.parseColor("#F0F0F0"));
        LinearLayout.LayoutParams divParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp(1));
        divParams.bottomMargin = dp(16);
        divider.setLayoutParams(divParams);
        card.addView(divider);

        // ── Button row ──
        LinearLayout btnRow = new LinearLayout(this);
        btnRow.setOrientation(LinearLayout.HORIZONTAL);
        btnRow.setWeightSum(2f);

        // Cancel button
        TextView btnCancel = new TextView(this);
        btnCancel.setText("Stay");
        btnCancel.setTextColor(Color.parseColor("#111111"));
        btnCancel.setTextSize(15);
        btnCancel.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        btnCancel.setGravity(Gravity.CENTER);
        btnCancel.setPadding(0, dp(14), 0, dp(14));

        android.graphics.drawable.GradientDrawable cancelBg = new android.graphics.drawable.GradientDrawable();
        cancelBg.setColor(Color.parseColor("#F3F4F6"));
        cancelBg.setCornerRadius(dp(14));
        btnCancel.setBackground(cancelBg);

        LinearLayout.LayoutParams cancelParams = new LinearLayout.LayoutParams(0,
            LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        cancelParams.rightMargin = dp(8);
        btnCancel.setLayoutParams(cancelParams);
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        // Exit button
        TextView btnExit = new TextView(this);
        btnExit.setText("Exit");
        btnExit.setTextColor(Color.WHITE);
        btnExit.setTextSize(15);
        btnExit.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        btnExit.setGravity(Gravity.CENTER);
        btnExit.setPadding(0, dp(14), 0, dp(14));

        android.graphics.drawable.GradientDrawable exitBg = new android.graphics.drawable.GradientDrawable();
        exitBg.setColor(Color.parseColor("#111111"));
        exitBg.setCornerRadius(dp(14));
        btnExit.setBackground(exitBg);

        LinearLayout.LayoutParams exitParams = new LinearLayout.LayoutParams(0,
            LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        exitParams.leftMargin = dp(8);
        btnExit.setLayoutParams(exitParams);
        btnExit.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });

        btnRow.addView(btnCancel);
        btnRow.addView(btnExit);
        card.addView(btnRow);

        dialog.setContentView(card);

        // Dialog window sizing
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.85f);
            params.gravity = Gravity.CENTER;
            dialog.getWindow().setAttributes(params);

            // Dim background
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog.getWindow().setDimAmount(0.5f);
        }

        // Animate in
        card.setAlpha(0f);
        card.setScaleX(0.92f);
        card.setScaleY(0.92f);
        card.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(200).start();

        dialog.show();
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }

    private String getOfflinePage() {
        return "<!DOCTYPE html><html><head><meta name='viewport' content='width=device-width,initial-scale=1'>"
            + "<style>body{background:#fff;color:#111;font-family:sans-serif;display:flex;align-items:center;"
            + "justify-content:center;height:100vh;margin:0;flex-direction:column;text-align:center;padding:20px}"
            + "h2{font-size:22px;margin-bottom:10px}p{color:#888;font-size:14px}"
            + ".btn{background:#111;color:#fff;border:none;padding:12px 32px;"
            + "font-size:14px;margin-top:20px;cursor:pointer;border-radius:50px}"
            + "</style></head><body>"
            + "<h2>⚠️ No Connection</h2>"
            + "<p>ইন্টারনেট সংযোগ নেই।<br>Check your connection and try again.</p>"
            + "<button class='btn' onclick='window.location.reload()'>🔄 Try Again</button>"
            + "</body></html>";
    }

    @Override protected void onResume() { super.onResume(); webView.onResume(); }
    @Override protected void onPause()  { super.onPause();  webView.onPause();  }
}
