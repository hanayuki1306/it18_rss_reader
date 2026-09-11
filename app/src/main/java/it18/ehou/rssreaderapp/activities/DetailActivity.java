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
    String articleLink, articleTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        webView = findViewById(R.id.webView);
        btnShare = findViewById(R.id.btnShare);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);

        dbHelper = new DatabaseHelper(this);

        // Lấy dữ liệu Intent từ MainActivity truyền sang[cite: 5]
        articleLink = getIntent().getStringExtra("ARTICLE_LINK");
        articleTitle = getIntent().getStringExtra("ARTICLE_TITLE");

        // Cấu hình WebView hiển thị báo
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(articleLink);

        // Nút Quay lại
        btnBack.setOnClickListener(v -> finish());

        // Nút Chia sẻ dùng Implicit Intent[cite: 5]
        btnShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, articleTitle);
            shareIntent.putExtra(Intent.EXTRA_TEXT, articleLink);
            startActivity(Intent.createChooser(shareIntent, "Chia sẻ bài viết qua..."));
        });

        // Nút Lưu tin dùng SQLite[cite: 8]
        btnSave.setOnClickListener(v -> {
            boolean isSaved = dbHelper.saveNews(articleTitle, articleLink);
            if(isSaved) {
                Toast.makeText(this, "Đã lưu thành công!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Lỗi khi lưu!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}