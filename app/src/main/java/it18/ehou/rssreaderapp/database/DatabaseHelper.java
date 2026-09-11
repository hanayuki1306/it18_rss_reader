package it18.ehou.rssreaderapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import it18.ehou.rssreaderapp.models.NewsItem;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "SavedNews.db";
    private static final int DATABASE_VERSION = 2; // Tăng lên 2 để cập nhật cấu trúc bảng
    private static final String TABLE_NAME = "news";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng đầy đủ 4 trường thông tin
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "link TEXT, " +
                "image TEXT, " +
                "date TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa bảng cũ nếu nâng cấp cấu trúc
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Hàm lưu tin bài kèm ảnh và ngày đăng
    public boolean saveNews(String title, String link, String image, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("link", link);
        cv.put("image", image);
        cv.put("date", date);
        long result = db.insert(TABLE_NAME, null, cv);
        return result != -1;
    }

    // Hàm lấy danh sách tin đã lưu
    public List<NewsItem> getAllSavedNews() {
        List<NewsItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Sắp xếp theo id giảm dần (tin mới lưu lên đầu)
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, "id DESC");

        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String link = cursor.getString(cursor.getColumnIndexOrThrow("link"));
                String image = cursor.getString(cursor.getColumnIndexOrThrow("image"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));

                list.add(new NewsItem(title, link, image, date));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}