package com.example.administrator.weischool.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2017/6/28.
 */

public class SQLite_db extends SQLiteOpenHelper {
    private static final String DB_NAME = "WEISCHOOL.DB";//数据库名称
    private static final int DB_VERSION = 1;//数据库版本号

    public SQLite_db(Context context) {
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_STUDENT="CREATE TABLE "+ Student.TABLE+"("
                +Student.KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT ,"
                +Student.KEY_name+" TEXT, "
                +Student.KEY_age+" INTEGER, "
                +Student.KEY_email+" TEXT)";
        db.execSQL(CREATE_TABLE_STUDENT);
    }

 @Override
 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
     db.execSQL("DROP TABLE IF EXISTS "+ Student.TABLE);
     //再次创建表
     onCreate(db);
         }
}
