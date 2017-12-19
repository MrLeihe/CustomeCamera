package com.code.library.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by yue on 15/11/30.
 */
public class BaseDao {

    protected DBHelper dbHelper;
    protected SQLiteDatabase sqLiteDatabase;

    public BaseDao(Context context) {
        dbHelper = new DBHelper(context);
    }

    protected void openRDB() {
        sqLiteDatabase = dbHelper.getReadableDatabase();
    }

    protected void openWDB() {
        sqLiteDatabase = dbHelper.getWritableDatabase();
    }

    protected void closeDB() {
        if (sqLiteDatabase != null) {
            sqLiteDatabase.close();
            sqLiteDatabase = null;
        }
    }

    /**
     * 查询数据库
     */
    protected Cursor queryDB(String sql) {
        return sqLiteDatabase.rawQuery(sql, null);
    }

    /**
     * 执行插入、删除数据库语句
     */
    protected void execSQL(String sql, Object[] objects) {
        sqLiteDatabase.execSQL(sql, objects);
    }

    /**
     * 执行插入、删除数据库语句
     */
    protected void execSQL(String sql) {
        sqLiteDatabase.execSQL(sql);
    }
}
