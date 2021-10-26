package com.example.experiment2application;

import static android.media.CamcorderProfile.getAll;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    WordsDBHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //为ListView注册上下文菜单
        ListView list=(ListView) findViewById(R.id.IsWords);
        registerForContextMenu(list);

        //创建SQLiteOpenHelper对象，注意第一次运行时，数据库并没有被创建
        mDbHelper=new WordsDBHelper(this);

        //在列表显示全部单词
        ArrayList<Map<String,String>> items=getAll();
        setWordsListView(items);
    }

    @Override
    protected void onDestroy() {//关闭数据库
        super.onDestroy();
        mDbHelper.close();
    }
}