package com.example.administrator.weischool;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.administrator.weischool.RegisterActivity;

public class MyDBHelper extends SQLiteOpenHelper {
    public static final String CREATE_USERDATA="create table userData(" +
            "id integer primary key autoincrement,"
            +"nametext,"
            +"password text)";


    public MyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_USERDATA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
