package it18.ehou.rssreaderapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

import it18.ehou.rssreaderapp.R;
import it18.ehou.rssreaderapp.adapters.NewsAdapter;
import it18.ehou.rssreaderapp.database.DatabaseHelper;
import it18.ehou.rssreaderapp.models.NewsItem;

public class SavedNewsActivity extends AppCompatActivity {
    ListView listViewSaved;
    DatabaseHelper dbHelper;
    NewsAdapter adapter;
    List<NewsItem> savedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_news);

        // Khởi tạo Toolbar và kích hoạt nút quay lại
        Toolbar toolbar = findViewById(R.id.toolbarSaved);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        listViewSaved = findViewById(R.id.listViewSaved);
        dbHelper = new DatabaseHelper(this);

        // Lấy danh sách tin từ SQLite
        savedList = dbHelper.getAllSavedNews();

        // Sử dụng lại NewsAdapter để hiển thị danh sách
        adapter = new NewsAdapter(this, savedList);
        listViewSaved.setAdapter(adapter);

        // Bắt sự kiện click để đọc lại tin đã lưu
        listViewSaved.setOnItemClickListener((parent, view, position, id) -> {
            NewsItem item = savedList.get(position);

            Intent intent = new Intent(SavedNewsActivity.this, DetailActivity.class);
            intent.putExtra("ARTICLE_LINK", item.link);
            intent.putExtra("ARTICLE_TITLE", item.title);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật lại danh sách nếu người dùng hủy lưu tin từ màn hình chi tiết rồi quay lại
        if (dbHelper != null && adapter != null) {
            savedList.clear();
            savedList.addAll(dbHelper.getAllSavedNews());
            adapter.notifyDataSetChanged();
        }
    }
}