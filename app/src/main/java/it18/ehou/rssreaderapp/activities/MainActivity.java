package it18.ehou.rssreaderapp.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import it18.ehou.rssreaderapp.R;
import it18.ehou.rssreaderapp.adapters.NewsAdapter;
import it18.ehou.rssreaderapp.models.NewsItem;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<NewsItem> newsList;
    NewsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        newsList = new ArrayList<>();
        adapter = new NewsAdapter(this, newsList);
        listView.setAdapter(adapter);

        // Bắt sự kiện người dùng click vào 1 tin[cite: 3]
        listView.setOnItemClickListener((parent, view, position, id) -> {
            NewsItem selectedItem = newsList.get(position);

            // Dùng Explicit Intent chuyển sang màn hình đọc chi tiết[cite: 5]
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("ARTICLE_LINK", selectedItem.link);
            intent.putExtra("ARTICLE_TITLE", selectedItem.title);
            startActivity(intent);
        });

        // Bắt đầu tải dữ liệu RSS mặc định từ VnExpress
        fetchRssData("https://vnexpress.net/rss/tin-moi-nhat.rss");
    }

    private void fetchRssData(String rssUrl) {
        // Tạo một luồng chạy ngầm để không làm đơ giao diện chính
        new Thread(() -> {
            try {
                Document doc = Jsoup.connect(rssUrl).get();
                Elements items = doc.select("item");

                for (Element item : items) {
                    String title = item.select("title").text();
                    String link = item.select("link").text();
                    String pubDate = item.select("pubDate").text();

                    // Bóc tách ảnh từ thẻ description
                    String description = item.select("description").text();
                    Document descDoc = Jsoup.parse(description);
                    String imgUrl = descDoc.select("img").attr("src");

                    newsList.add(new NewsItem(title, link, imgUrl, pubDate));
                }

                // Cập nhật lại giao diện (ListView) trên luồng chính
                runOnUiThread(() -> adapter.notifyDataSetChanged());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Nạp Menu vào màn hình chính[cite: 4]
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // Xử lý sự kiện khi ấn các nút trên Menu[cite: 4]
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_saved_news) {
            // Chuyển sang màn hình tin đã lưu[cite: 5]
            Intent intent = new Intent(MainActivity.this, SavedNewsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_add_rss) {
            // Hiển thị hộp thoại thêm RSS khi ấn nút[cite: 4]
            showAddRssDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Hàm tạo và hiển thị hộp thoại Dialog thêm RSS[cite: 4]
    private void showAddRssDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm Link RSS");

        // Nạp giao diện dialog_add_rss.xml vào hộp thoại
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_rss, null);
        builder.setView(dialogView);

        EditText edtRssLink = dialogView.findViewById(R.id.edtRssLink);

        // Nút Xác nhận
        builder.setPositiveButton("Tải tin", (dialog, which) -> {
            String newLink = edtRssLink.getText().toString().trim();
            if (!newLink.isEmpty()) {
                // Xóa danh sách cũ và tải lại dữ liệu từ link mới
                newsList.clear();
                adapter.notifyDataSetChanged();
                fetchRssData(newLink);
                Toast.makeText(this, "Đang tải RSS mới...", Toast.LENGTH_SHORT).show();
            }
        });

        // Nút Hủy
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        builder.show();
    }
}