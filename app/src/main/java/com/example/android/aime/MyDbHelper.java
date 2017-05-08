package com.example.android.aime;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by TRY on 2017/5/6.
 */

public class MyDbHelper extends SQLiteOpenHelper {
    public Context mContext;

    // 建英文词表
    public static String CREATE_TABLE_EN = "create table en (" +
            "txt text)";

    // 建中文词表, txt放拼音, word放对应的中文字词
    public static String CREATE_TABLE_CN = "create table cn (" +
            "txt text," +
            "word text)";

    public MyDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_TABLE_EN);
        db.execSQL(CREATE_TABLE_CN);
        Toast.makeText(mContext, "Create table ok", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
    }
}
