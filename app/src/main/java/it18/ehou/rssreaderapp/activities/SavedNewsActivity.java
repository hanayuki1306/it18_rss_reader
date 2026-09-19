package it18.ehou.rssreaderapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it18.ehou.rssreaderapp.R;
import it18.ehou.rssreaderapp.adapters.NewsAdapter;
import it18.ehou.rssreaderapp.database.DatabaseHelper;
import it18.ehou.rssreaderapp.models.NewsItem;

public class SavedNewsActivity extends AppCompatActivity {
    private RecyclerView recyclerViewSaved;
    private TextView txtEmptySaved;
    private DatabaseHelper dbHelper;
    private NewsAdapter adapter;
    private final List<NewsItem> savedList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_news);

        // Khởi tạo Toolbar và kích hoạt nút quay lại
        Toolbar toolbar = findViewById(R.id.toolbarSaved);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerViewSaved = findViewById(R.id.recyclerViewSaved);
        txtEmptySaved = findViewById(R.id.txtEmptySaved);
        dbHelper = new DatabaseHelper(this);

        recyclerViewSaved.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NewsAdapter(this, savedList);
        recyclerViewSaved.setAdapter(adapter);

        // Bắt sự kiện click để đọc lại tin đã lưu (truyền đủ 4 trường thông tin)
        adapter.setOnItemClickListener((item, position) -> {
            Intent intent = new Intent(SavedNewsActivity.this, DetailActivity.class);
            intent.putExtra("ARTICLE_LINK", item.getLink());
            intent.putExtra("ARTICLE_TITLE", item.getTitle());
            intent.putExtra("ARTICLE_IMAGE", item.getImageUrl());
            intent.putExtra("ARTICLE_DATE", item.getPubDate());
            startActivity(intent);
        });

        // Nhấn giữ bài viết để xác nhận xóa khỏi mục đã lưu
        adapter.setOnItemLongClickListener((item, position) -> {
            new AlertDialog.Builder(SavedNewsActivity.this)
                    .setTitle(R.string.saved_news_title)
                    .setMessage(R.string.msg_confirm_delete)
                    .setPositiveButton(R.string.dialog_delete, (dialog, which) -> {
                        boolean deleted = dbHelper.deleteNews(item.getLink());
                        if (deleted) {
                            adapter.removeItem(position);
                            checkEmptyState();
                            Toast.makeText(SavedNewsActivity.this, R.string.msg_deleted_success, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton(R.string.dialog_cancel, null)
                    .show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSavedNews();
    }

    private void loadSavedNews() {
        if (dbHelper != null && adapter != null) {
            List<NewsItem> latestSaved = dbHelper.getAllSavedNews();
            adapter.updateData(latestSaved);
            checkEmptyState();
        }
    }

    private void checkEmptyState() {
        if (adapter.getItemCount() == 0) {
            txtEmptySaved.setVisibility(View.VISIBLE);
            recyclerViewSaved.setVisibility(View.GONE);
        } else {
            txtEmptySaved.setVisibility(View.GONE);
            recyclerViewSaved.setVisibility(View.VISIBLE);
        }
    }
}