package com.webtoapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.*;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
                // পেজ লোড শেষ — loading overlay সরাও
                loadingOverlay.animate()
                    .alpha(0f)
                    .setDuration(300)
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
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    return false;
                }
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

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }

    @Override protected void onResume() { super.onResume(); webView.onResume(); }
    @Override protected void onPause()  { super.onPause();  webView.onPause();  }
}
