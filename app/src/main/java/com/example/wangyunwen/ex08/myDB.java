package com.example.wangyunwen.ex08;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wangyunwen on 16/11/16.
 */
public class myDB extends SQLiteOpenHelper {

    private static final String DB_NAME = "BirthdayMemo";
    private static final String TABLE_NAME = "BirthdayMemo";

    public myDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context,name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CTREATE_TABLE = "CREATE TABLE if not exists "
                + TABLE_NAME
                + "(id integer primary key, name TEXT, birthday TEXT, present TEXT)";
        sqLiteDatabase.execSQL(CTREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //
    }
}
