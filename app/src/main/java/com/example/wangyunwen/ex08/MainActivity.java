package com.example.wangyunwen.ex08;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.*;

public class MainActivity extends AppCompatActivity {
    public ListView list;
    private SimpleAdapter simpleAdapter;
    private String DB_NAME = "BirthdayMemo";
    private static final String TABLE_NAME = "BirthdayMemo";
    private com.example.wangyunwen.ex08.myDB myDB = new myDB(this, DB_NAME, null, 1);
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //set list adapter
        setListAdapter();

        // add item button
        Button add = (Button) findViewById(R.id.add);
        add.setOnClickListener(onAddButtonClick);

        //list item onclick
        list.setOnItemClickListener(onItemClickListener);
        list.setOnItemLongClickListener(onLongClick);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null) {
            bundle = intent.getExtras();
            updateListAndDB(bundle.get("name").toString(), bundle.get("birthday").toString(),
                    bundle.get("present").toString());
        }
    }

    // 增加数据库内容和list条目
    private void updateListAndDB(String name, String birthday, String present) {
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("birthday", birthday);
        cv.put("present", present);
        db.insert(DB_NAME, null, cv);
        db.close();
        setListAdapter();
    }

    public View.OnClickListener onAddButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, AddActivity.class);
            startActivity(intent);
        }
    };

    public AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            View view1 = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_layout, null);
            final Map<String, Object> mMap = (Map<String, Object>) simpleAdapter.getItem(position);

            TextView showName = (TextView) view1.findViewById(R.id.showName);
            showName.setText(mMap.get("name").toString());
            final EditText setBirthday = (EditText) view1.findViewById(R.id.setBirthday);
            setBirthday.setText(mMap.get("birthday").toString());
            final EditText setPresent = (EditText) view1.findViewById(R.id.setPresent);
            setPresent.setText(mMap.get("present").toString());

            Dialog dialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("编辑信息")
                    .setPositiveButton("保存修改", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db = myDB.getWritableDatabase();
                            ContentValues cv = new ContentValues();
                            cv.put("name", mMap.get("name").toString());
                            cv.put("birthday", setBirthday.getText().toString());
                            cv.put("present", setPresent.getText().toString());
                            String whereClause = "name=?";
                            String[] whereArgs = {mMap.get("name").toString()};
                            db.update(TABLE_NAME, cv, whereClause, whereArgs);
                            setListAdapter();
                        }
                    })
                    .setNegativeButton("放弃修改", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //
                        }
                    })
                    .setView(view1)
                    .create();
            dialog.show();
        }
    };
    public AdapterView.OnItemLongClickListener onLongClick = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick (AdapterView < ? > parent, View view,int position, long id){
            final Map<String, Object> mMap = (Map<String, Object>) simpleAdapter.getItem(position);
            Dialog dialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("是否删除")
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String name = mMap.get("name").toString();
                            db = myDB.getWritableDatabase();
                            String whereClause = "name=?";
                            String[] whereArgs = {name};
                            db.delete(TABLE_NAME, whereClause, whereArgs);
                            setListAdapter();
                        }
                    })
                    .setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //
                        }
                    })
                    .create();
            dialog.show();
            return false;
        }
    };

    public void setListAdapter() {
        List<Map<String, String>> data = new ArrayList<>();

        Map<String, String> header = new LinkedHashMap<>();
        header.put("name", "姓名");
        header.put("birthday", "生日");
        header.put("present", "礼物");
        data.add(header);

        db = myDB.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[] {"name", "birthday", "present"},
                null, null, null, null, null);
        while (cursor.moveToNext()) {
            Map<String, String> temp = new LinkedHashMap<>();
            int nameCol = cursor.getColumnIndex("name");
            int birthdayCol = cursor.getColumnIndex("birthday");
            int presentCol = cursor.getColumnIndex("present");
            String name = cursor.getString(nameCol);
            String birthday = cursor.getString(birthdayCol);
            String present = cursor.getString(presentCol);
            temp.put("name", name);
            temp.put("birthday", birthday);
            temp.put("present", present);
            data.add(temp);
        }

        list = (ListView) findViewById(R.id.list);
        simpleAdapter = new SimpleAdapter(this, data, R.layout.item,
                new String[] {"name", "birthday", "present"}, new int[] {R.id.name, R.id.birthday, R.id.present});
        list.setAdapter(simpleAdapter);

        db.close();
    }
}
