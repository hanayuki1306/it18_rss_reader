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
    private static final int DATABASE_VERSION = 3; // Nâng lên 3 để hỗ trợ UNIQUE link
    private static final String TABLE_NAME = "news";

    public static final String COL_ID = "id";
    public static final String COL_TITLE = "title";
    public static final String COL_LINK = "link";
    public static final String COL_IMAGE = "image";
    public static final String COL_DATE = "date";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng đầy đủ 4 trường thông tin, link là UNIQUE để chống nhân bản dữ liệu
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TITLE + " TEXT, " +
                COL_LINK + " TEXT UNIQUE, " +
                COL_IMAGE + " TEXT, " +
                COL_DATE + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Hàm lưu tin bài kèm ảnh và ngày đăng (tránh trùng lặp với CONFLICT_REPLACE)
    public boolean saveNews(String title, String link, String image, String date) {
        if (link == null || link.trim().isEmpty()) return false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TITLE, title != null ? title : "");
        cv.put(COL_LINK, link);
        cv.put(COL_IMAGE, image != null ? image : "");
        cv.put(COL_DATE, date != null ? date : "");

        long result = db.insertWithOnConflict(TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        return result != -1;
    }

    // Hàm kiểm tra bài viết đã được lưu hay chưa
    public boolean isNewsSaved(String link) {
        if (link == null || link.trim().isEmpty()) return false;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COL_ID},
                COL_LINK + " = ?", new String[]{link}, null, null, null);
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) {
            cursor.close();
        }
        return exists;
    }

    // Hàm xóa tin bài theo link
    public boolean deleteNews(String link) {
        if (link == null || link.trim().isEmpty()) return false;
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_NAME, COL_LINK + " = ?", new String[]{link});
        return rows > 0;
    }

    // Hàm lấy danh sách tin đã lưu
    public List<NewsItem> getAllSavedNews() {
        List<NewsItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Sắp xếp theo id giảm dần (tin mới lưu lên đầu)
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, COL_ID + " DESC");

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE));
                    String link = cursor.getString(cursor.getColumnIndexOrThrow(COL_LINK));
                    String image = cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE));

                    list.add(new NewsItem(title, link, image, date));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return list;
    }
}