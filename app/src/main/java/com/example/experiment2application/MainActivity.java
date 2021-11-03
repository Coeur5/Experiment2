package com.example.experiment2application;

import static android.media.CamcorderProfile.getAll;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        switch (id){
            case R.id.action_search:
                //查找
                SearchDialog();
                return true;
            case R.id.action_insert:
                //新增单词
                InsertDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,ContextMenu.ContextMenuInfo menuInfo){
        getMenuInflater().inflate(R.menu.contextmenu_wordslistview,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        TextView textId=null;
        TextView textWord=null;
        TextView textMeaning=null;
        TextView textSample=null;
        AdapterView.AdapterContextMenuInfo info=null;
        View itemview=null;
        switch(item.getItemId()){
            case R.id.action_delete:
                //删除单词
                info=(AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                itemview=info.targetView;
                textId=(TextView)itemview.findViewById(R.id.textId);
                if(textId!=null){
                    String strId=textId.getText().toString();
                    DeleteDialog(strId);
                }
                break;
            case R.id.action_update:
                //修改单词
                info=(AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                itemview=info.targetView;
                textId=(TextView)itemview.findViewById(R.id.textId);
                textWord=(TextView)itemview.findViewById(R.id.textviewWord);
                textMeaning=(TextView)itemview.findViewById(R.id.textviewMeaning);
                textSample=(TextView)itemview.findViewById(R.id.textviewSample);
                if(textId!=null&&textWord!=null&&textMeaning!=null&&textSample!=null){
                    String strId=textId.getText().toString();
                    String strWord=textWord.getText().toString();
                    String strMeaning=textMeaning.getText().toString();
                    String strSample=textSample.getText().toString();
                    UpdateDialog(strId,strWord,strMeaning,strSample);
                }
                break;
        }
        return true;
    }
    private void setWordsListView(ArrayList<Map<String,String>> item){
        SimpleAdapter adapter=new SimpleAdapter(this,item,R.layout.item,new String[]{
                Words.Word._ID,Words.Word.COLUMN_WORD,
                Words.Word.COLUMN_MEANING,
                Words.Word.COLUMN_SAMPLE},
                new int[]{R.id.textId,R.id.textviewWord,R.id.textviewMeaning,R.id.textviewSample});
        ListView list=(ListView)findViewById(R.id.IsWords);
        list.setAdapter(adapter);
    }
    //使用SQL语句插入单词
    private void InsertUserSql(String strWord,String strMeaning,String strSample){
        String sql="insert into words(word,meaning,sample)values(?,?,?)";

        SQLiteDatabase db=mDbHelper.getWritableDatabase();
        db.execSQL(sql,new String[]{strWord,strMeaning,strSample});
    }

    //使用insert方法增加单词
    private void Insert(String strWord,String strMeaning,String strSample){
        SQLiteDatabase db=mDbHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(Words.Word.COLUMN_WORD,strWord);
        values.put(Words.Word.COLUMN_MEANING,strMeaning);
        values.put(Words.Word.COLUMN_SAMPLE,strSample);

        long newRowId;
        newRowId=db.insert(Words.Word.TABLE_NAME,null,values);
    }
    /*增加单词的对话框*/
    private void InsertDialog(){
        final TableLayout tableLayout=(TableLayout) getLayoutInflater().inflate(R.layout.insert,null);
        new AlertDialog.Builder(this)
                .setTitle("新增单词")
                .setView(tableLayout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String strWord=((EditText)tableLayout.findViewById(R.id.txtWord)).getText().toString();
                        String strMeaning=((EditText)tableLayout.findViewById(R.id.txtMeaning)).getText().toString();
                        String strSample=((EditText)tableLayout.findViewById(R.id.txtSample)).getText().toString();
                        //既可以使用sql语句插入，也可以使用insert方法插入
                        Insert(strWord,strMeaning,strSample);

                        ArrayList<Map<String,String>> items=getAll();
                        setWordsListView(items);
                    }
                })
                //取消
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()//创建
                .show();//显示
    }
    /*使用sql语句删除单词*/
    private void DeleteUseSql(String strId){
        String sql="delete from words where _id='"+strId+"'";
        SQLiteDatabase db=mDbHelper.getReadableDatabase();
        db.execSQL(sql);
    }
    /*删除单词*/
    private void Dalete(String strId){
        SQLiteDatabase db=mDbHelper.getReadableDatabase();
        //定义where子句
        String selection= Words.Word._ID+"=?";
        //指定占位符对应的实际参数
        String[] selectionArgs={strId};

        db.delete(Words.Word.TABLE_NAME,selection,selectionArgs);

    }
    /*删除单词的对话框*/
    private void DeleteDialog(final String strId){
        new AlertDialog.Builder(this)
                .setTitle("删除单词")
                .setMessage("确认删除单词？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //既可以用sql语句删除单词，也可以使用dalete方法删除单词
                        DeleteUseSql(strId);
                        setWordsListView(getAll());
                    }
                })
                .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).create()
                .show();
    }
    private void UpdateUseSql(String strId,String strWord,String strMeaning,String strSample){
        SQLiteDatabase db=mDbHelper.getReadableDatabase();
        String sql="update words set word=?,meaning=?,sample=?where _id=?";
        db.execSQL(sql,new String[]{strWord,strMeaning,strSample,strId});

    }
    /*使用方法更新*/
    private void Update(String strId,String strWord,String strMeaning,String strSample){
        SQLiteDatabase db=mDbHelper.getReadableDatabase();

        ContentValues values=new ContentValues();
        values.put(Words.Word.COLUMN_WORD,strWord);
        values.put(Words.Word.COLUMN_MEANING,strMeaning);
        values.put(Words.Word.COLUMN_SAMPLE,strSample);

        String selection=Words.Word._ID+"=?";
        String[] selectionArgs={strId};

        int count=db.update(Words.Word.TABLE_NAME,values,selection,selectionArgs);

    }
    /*修改对话框*/
    private void UpdateDialog(final String strId,final String strWord,final String strMeaning,final String strSample){
        final TableLayout tableLayout=(TableLayout) getLayoutInflater().inflate(R.layout.insert,null);
        ((EditText)tableLayout.findViewById(R.id.txtWord)).setText(strWord);
        ((EditText)tableLayout.findViewById(R.id.txtMeaning)).setText(strMeaning);
        ((EditText)tableLayout.findViewById(R.id.txtWord)).setText(strSample);
        new AlertDialog.Builder(this)
                .setTitle("修改单词")
                .setView(tableLayout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String strNewWord=((EditText)tableLayout.findViewById(R.id.txtWord)).getText().toString();
                        String strNewMeaning=((EditText)tableLayout.findViewById(R.id.txtMeaning)).getText().toString();
                        String strNewSample=((EditText)tableLayout.findViewById(R.id.txtSample)).getText().toString();
                        /*既可以使用sql语句，也可以使用update方法*/
                        UpdateUseSql(strId,strNewWord,strNewMeaning,strNewSample);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).create()
                .show();
    }
    /*使用sql语句查找*/
    private ArrayList<Map<String,String>> SearchUseSql(String strWordSearch){
        SQLiteDatabase db=mDbHelper.getReadableDatabase();

        String sql="select * from words where word like ? order by word desc";
        Cursor c=db.rawQuery(sql,new String[]{"%"+strWordSearch+"%"});

        return ConvertCursor2List(c);
    }
    /*使用query方法查找*/
    private ArrayList<Map<String,String>>Search(String strWordSearch){
        SQLiteDatabase db=mDbHelper.getReadableDatabase();
        String[] projection={
                Words.Word._ID,
                Words.Word.COLUMN_WORD,
                Words.Word.COLUMN_MEANING,
                Words.Word.COLUMN_SAMPLE
        };
        String sortOrder=Words.Word.COLUMN_WORD+"DESC";
        String selection=Words.Word.COLUMN_WORD+"LIKE ?";
        String[] selectionArgs={"%"+strWordSearch+"%"};

        Cursor c=db.query(Words.Word.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);

        return ConvertCursor2List(c);
    }
    //查询的对话框
    private void SearchDialog(){
        final TableLayout tableLayout=(TableLayout) getLayoutInflater().inflate(R.layout.searchterm,null);
        new AlertDialog.Builder(this)
                .setTitle("增加单词")
                .setView(tableLayout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String txtSearchWord = ((EditText) tableLayout.findViewById(R.id.txtSearchWord)).getText().toString();

                        ArrayList<Map<String, String>> items = null;

                        //既可以使用sql语句增加单词，也可以使用query方法
                        items = SearchUseSql(txtSearchWord);

                        if (items.size() > 0) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("result", items);
                            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                            intent.putExtras(bundle)
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "没有找到", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()
                .show();

    }
}