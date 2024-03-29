package com.example.android.aime;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private final String DATABASE_PATH = "/data/data/com.example.android.aime/databases/";
    private final String DATABASE_FILENAME = "ime.db";

    public EditText etWord; // 英文词输入
    public Button btInsert, btSearch; // 将输入的英文词插入数据库的按钮, 查询输入的英文词的按钮
    public TextView tvResult; // 显示查询结果

    public EditText etcnWord, zwWord; // 中文拼音输入, 对应的中文字词输入
    public Button btcnInsert, btcnSearch; // 将输入的中文拼音和对应字词插入数据库的按钮, 查询输入的拼音的按钮
    public TextView tvcnResult; // 显示查询结果

    public MyDbHelper dbHelper; // 数据库帮助对象

    public Context context; // 当前活动引用

    public Button test; // 按此按钮转入测试各种输入框的页面


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化数据库文件
        try{
            createDatabase();
        }catch(Exception e){
            e.printStackTrace();
        }

        context = this;

        // 定位UI组件
        etWord = (EditText) findViewById(R.id.word);
        btInsert = (Button) findViewById(R.id.insert);
        btSearch = (Button) findViewById(R.id.search);
        tvResult = (TextView) findViewById(R.id.result);

        etcnWord = (EditText) findViewById(R.id.cn_word);
        zwWord = (EditText) findViewById(R.id.cn_zhongwen);
        btcnInsert = (Button) findViewById(R.id.cn_insert);
        btcnSearch = (Button) findViewById(R.id.cn_search);
        tvcnResult = (TextView) findViewById(R.id.cn_result);

        test = (Button) findViewById(R.id.test);

        // 插入新词到数据库的英文词库中去
        btInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                String word = etWord.getText().toString().trim();
                if(! word.equals("")) {
                    values.put("txt", word);
                    db.insert("en", null, values);
                    Toast.makeText(context, "Insert ok", Toast.LENGTH_SHORT).show();
                }
                tvResult.setText("");
            }
        });

        // 从数据库中查询输入的英文词并显示
        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = "";
                String word = etWord.getText().toString().trim();
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery("select txt from en where txt like ? order by txt", new String[]{ word + "%"});
                while(cursor.moveToNext()){
                    String t = cursor.getString(cursor.getColumnIndex("txt"));
                    str = str + t + "\n";
                }
                tvResult.setText(str);
                cursor.close();

            }
        });

        // 将输入的拼音和对应字词插入数据库的中文词库中去
        btcnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values  = new ContentValues();
                String word = etcnWord.getText().toString().trim();
                String zhongwen = zwWord.getText().toString().trim();
                if((! word.equals("")) && (! zhongwen.equals(""))){
                    values.put("txt", word);
                    values.put("word", zhongwen);
                    long n = db.insert("cn", null, values); System.out.println("lines: " + n);
                    Toast.makeText(context, "Insert cn ok", Toast.LENGTH_SHORT).show();
                }
                tvcnResult.setText("");
            }
        });

        // 从数据库中查询输入的拼音, 并显示对应的字词
        btcnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = "";
                String word = etcnWord.getText().toString().trim();
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery("select word from cn where txt like ? order by txt", new String[]{ word + "%"});
                while(cursor.moveToNext()){
                    String t = cursor.getString(cursor.getColumnIndex("word"));
                    str = str + t + "\n";
                }
                tvcnResult.setText(str);
                cursor.close();
            }
        });

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 打开测试页面, 上有几种输入框
                Intent intent = new Intent(MainActivity.this, TestActivity.class);
                startActivity(intent);
            }
        });

        // 初始化数据库帮助对象
        dbHelper = new MyDbHelper(this, "ime.db", null, 2);
    }

    public void createDatabase() throws IOException {
        try{
            // 数据库文件全路径
            String databaseFilename = DATABASE_PATH + DATABASE_FILENAME;
            File dir = new File(DATABASE_PATH);
            // 不存在该路径则创建
            if(! dir.exists()){
                dir.mkdir();
            }
            // 不存在该数据库文件则从res/raw/下复制
            if(! (new File(databaseFilename)).exists()){
                FileOutputStream fos = new FileOutputStream(databaseFilename);
                InputStream is = getResources().openRawResource(R.raw.ime);
                byte[] buffer = new byte[4096];
                int count = 0;
                // 循环读取复制
                while((count = is.read(buffer)) > 0){
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
