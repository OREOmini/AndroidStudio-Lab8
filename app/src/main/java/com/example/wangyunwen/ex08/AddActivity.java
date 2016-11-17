package com.example.wangyunwen.ex08;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.LinkedHashMap;
import java.util.Map;

public class AddActivity extends AppCompatActivity {
    private String DB_NAME = "BirthdayMemo";
    private static final String TABLE_NAME = "BirthdayMemo";
    private com.example.wangyunwen.ex08.myDB myDB = new myDB(this, DB_NAME, null, 1);
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Button addItem = (Button) findViewById(R.id.addItem);
        final EditText getName = (EditText) findViewById(R.id.getName);
        final EditText getBirthday = (EditText) findViewById(R.id.getBirthday);
        final EditText getPresent = (EditText) findViewById(R.id.getPresent);

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = getName.getText().toString();
                if (name.equals("")) {
                    Toast.makeText(AddActivity.this, "名字为空，请完善",Toast.LENGTH_SHORT).show();
                } else if(nameExist(name)) {
                    Toast.makeText(AddActivity.this, "名字重复，请核查",Toast.LENGTH_SHORT).show();
                }
                else {
                    Bundle bundle = new Bundle();
                    bundle.putString("name", name);
                    bundle.putString("birthday", getBirthday.getText().toString());
                    bundle.putString("present", getPresent.getText().toString());
                    Intent intent = new Intent(AddActivity.this, MainActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
    }

    private boolean nameExist(String name) {
        boolean f = false;
        db = myDB.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[] {"name", "birthday", "present"},
                null, null, null, null, null);
        while (cursor.moveToNext()) {
            int nameCol = cursor.getColumnIndex("name");
            String nameTemp = cursor.getString(nameCol);
            if(name.equals(nameTemp))
                f = true;
        }
        return f;
    }
}
