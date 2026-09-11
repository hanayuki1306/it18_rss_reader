package it18.ehou.rssreaderapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import it18.ehou.rssreaderapp.R;
import it18.ehou.rssreaderapp.database.DatabaseHelper;

public class DetailActivity extends AppCompatActivity {
    WebView webView;
    Button btnShare, btnSave, btnBack;
    DatabaseHelper dbHelper;
    String articleLink, articleTitle, articleImage, articleDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        webView = findViewById(R.id.webView);
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
        webView.setWebViewClient(new WebViewClient());
        if (articleLink != null) {
            webView.loadUrl(articleLink);
        }

        // Nút Quay lại
        btnBack.setOnClickListener(v -> finish());

        // Nút Chia sẻ dùng Implicit Intent
        btnShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, articleTitle);
            shareIntent.putExtra(Intent.EXTRA_TEXT, articleLink);
            startActivity(Intent.createChooser(shareIntent, "Chia sẻ bài viết qua..."));
        });

        // Nút Lưu tin dùng SQLite
        btnSave.setOnClickListener(v -> {
            boolean isSaved = dbHelper.saveNews(articleTitle, articleLink, articleImage, articleDate);
            if (isSaved) {
                Toast.makeText(this, "Đã lưu thành công!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Lỗi khi lưu!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}