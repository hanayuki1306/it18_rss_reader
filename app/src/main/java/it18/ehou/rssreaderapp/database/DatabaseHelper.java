package it18.ehou.rssreaderapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;
import it18.ehou.rssreaderapp.models.NewsItem;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "SavedNews.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "news";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng lưu trữ tin tức[cite: 8]
        String createTable = "CREATE TABLE " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, link TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Hàm lưu tin bài[cite: 8]
    public boolean saveNews(String title, String link) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("link", link);
        long result = db.insert(TABLE_NAME, null, cv);
        return result != -1;
    }
    public List<NewsItem> getAllSavedNews() {
        List<NewsItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Truy vấn tất cả dữ liệu, sắp xếp theo id giảm dần (tin mới lưu lên đầu)[cite: 8]
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, "id DESC");

        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String link = cursor.getString(cursor.getColumnIndexOrThrow("link"));

                // Vì database cơ bản chỉ lưu Tiêu đề và Link, ta truyền rỗng cho Ảnh và Ngày
                list.add(new NewsItem(title, link, "", ""));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}