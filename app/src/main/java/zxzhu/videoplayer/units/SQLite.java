package zxzhu.videoplayer.units;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import zxzhu.videoplayer.beans.Data;

/**
 * Created by Zhuzzzzzzx on 2017/5/21.
 */

public class SQLite {
    private final String TAG = "SQUtils";
    private MyDataBaseHelper dataBaseHelper = null;
    private Context mContext = null;
    private SQLiteDatabase db = null;

    public SQLite(Context context) {
        mContext = context;
        dataBaseHelper = new MyDataBaseHelper(context, "Recently.db", null, 3);
        db = dataBaseHelper.getWritableDatabase();
        Log.d(TAG, "创建数据库成功");
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    public Cursor getCursor() {
        return db.query("Recently", null, null, null, null, null, null);
    }

    /**
     * 数据库的插入方法
     *
     * @param item
     */
    public void insert(Data.ShowapiResBodyBean.PagebeanBean.ContentlistBean item) {
        if (db != null) {
            ContentValues values = new ContentValues();
                values.put("time", item.getCreate_time());
                values.put("title", item.getText());
                values.put("url", item.getVideo_uri());
                values.put("video_id", item.getId());
                values.put("share", item.getWeixin_url());
                db.insert("Recently", null, values);
                values.clear();
            Log.d(TAG, "数据库插入没有问题");
        } else Log.d(TAG, "数据库错误：db == null");
    }

//    public void insertImg(String url) {
//        if (db != null) {
//            ContentValues values = new ContentValues();
//            values.put("img_url", url);
//            db.insert("Recently", null, values);
//            values.clear();
//            Log.d(TAG, "数据库插入图片URl没有问题");
//        } else Log.d(TAG, "数据库错误：db == null");
//    }

    /**
     * 数据库的删除方法（全部删除）
     */
    public void deleteAll() {
        db.delete("Recently", null, null);
        Toast.makeText(mContext, "历史记录已清除", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "数据库中数据全部删除");
    }

    /**
     * 更新是否阅读的状态
     *
     * @param title 传入标题
     */
    public void isRead(String title) {
        ContentValues values = new ContentValues();
        values.put("isRead", 1);
        db.update("Recently", values, "title = ?", new String[]{title});
    }

    public boolean query(String obj) {
        Cursor cursor = db.query("Recently", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Log.d(TAG, "开始查询");
                String title = cursor.getString(cursor.getColumnIndex("title"));
                if (title.contains(obj)) {
                    //返回日期，从SQ中取出当天的新闻;
                    return true;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return false;
    }


    class MyDataBaseHelper extends SQLiteOpenHelper {
        /**
         * 建表
         */
        public static final String CREATE_DATA = "create table Recently(" +
                "id integer primary key autoincrement," +
                "time text," +
                "title text," +
//                "img_url text," +
                "video_id," +
                "share text," +
                "url text)";

        Context mContext;

        public MyDataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
            mContext = context;
        }

        /**
         * 数据库开始就调用此方法
         *
         * @param sqLiteDatabase
         */
        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_DATA);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }
}
