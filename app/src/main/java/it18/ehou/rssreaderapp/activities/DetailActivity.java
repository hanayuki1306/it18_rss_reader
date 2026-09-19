package it18.ehou.rssreaderapp.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;

import it18.ehou.rssreaderapp.R;
import it18.ehou.rssreaderapp.database.DatabaseHelper;

public class DetailActivity extends AppCompatActivity {
    private WebView webView;
    private ProgressBar progressBar;
    private MaterialButton btnShare, btnSave, btnBack;
    private DatabaseHelper dbHelper;
    private String articleLink, articleTitle, articleImage, articleDate;
    private boolean isCurrentlySaved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBarDetail);
        btnShare = findViewById(R.id.btnShare);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);

        dbHelper = new DatabaseHelper(this);

        // Nhận đầy đủ thông tin bài viết truyền sang
        articleLink = getIntent().getStringExtra("ARTICLE_LINK");
        articleTitle = getIntent().getStringExtra("ARTICLE_TITLE");
        articleImage = getIntent().getStringExtra("ARTICLE_IMAGE");
        articleDate = getIntent().getStringExtra("ARTICLE_DATE");

        // Cấu hình WebView hiển thị báo
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        // Theo dõi tiến trình tải trang để hiển thị ProgressBar
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (progressBar != null) {
                    if (newProgress < 100) {
                        progressBar.setVisibility(View.VISIBLE);
                        progressBar.setProgress(newProgress);
                    } else {
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
        });

        if (articleLink != null && !articleLink.trim().isEmpty()) {
            webView.loadUrl(articleLink);
        }

        // Kiểm tra xem bài viết đã được lưu trước đó hay chưa
        isCurrentlySaved = dbHelper.isNewsSaved(articleLink);
        updateSaveButtonState(isCurrentlySaved);

        // Xử lý nút Quay lại (Back hardware & software)
        OnBackPressedCallback backCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (webView != null && webView.canGoBack()) {
                    webView.goBack();
                } else {
                    finish();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, backCallback);

        btnBack.setOnClickListener(v -> {
            if (webView != null && webView.canGoBack()) {
                webView.goBack();
            } else {
                finish();
            }
        });

        // Nút Chia sẻ dùng Implicit Intent
        btnShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, articleTitle);
            shareIntent.putExtra(Intent.EXTRA_TEXT, articleLink);
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_title)));
        });

        // Nút Lưu / Bỏ lưu tin dùng SQLite
        btnSave.setOnClickListener(v -> {
            if (isCurrentlySaved) {
                boolean deleted = dbHelper.deleteNews(articleLink);
                if (deleted) {
                    isCurrentlySaved = false;
                    updateSaveButtonState(false);
                    Toast.makeText(this, R.string.msg_unsaved_success, Toast.LENGTH_SHORT).show();
                }
            } else {
                boolean saved = dbHelper.saveNews(articleTitle, articleLink, articleImage, articleDate);
                if (saved) {
                    isCurrentlySaved = true;
                    updateSaveButtonState(true);
                    Toast.makeText(this, R.string.msg_saved_success, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.msg_save_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateSaveButtonState(boolean saved) {
        if (btnSave == null) return;
        if (saved) {
            btnSave.setText(R.string.btn_unsave);
            btnSave.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.color_saved)));
        } else {
            btnSave.setText(R.string.btn_save);
            btnSave.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.primary_green)));
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.stopLoading();
            webView.clearHistory();
            webView.destroy();
        }
        super.onDestroy();
    }
}