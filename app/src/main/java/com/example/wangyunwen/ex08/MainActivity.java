package com.example.wangyunwen.ex08;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.*;

public class MainActivity extends AppCompatActivity {
    public ListView list;
    private SimpleAdapter simpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setListAdapter();

        Button add = (Button) findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivity(intent);
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View view1 = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_layout, null);
                Map<String, Object> mMap = (Map<String, Object>) simpleAdapter.getItem(position);
                TextView showName = (TextView) view1.findViewById(R.id.showName);
                //Log.d("----",showName.toString());
                showName.setText(mMap.get("name").toString());
                EditText setBirthday = (EditText) view1.findViewById(R.id.setBirthday);
                setBirthday.setText(mMap.get("birthday").toString());
                EditText setPresent = (EditText) view1.findViewById(R.id.setPresent);
                setPresent.setText(mMap.get("present").toString());
                Dialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("编辑信息")
                        .setPositiveButton("保存修改", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //
                            }
                        })
                        .setNegativeButton("放弃修改", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //
                            }
                        })
                        .setView(R.layout.dialog_layout)
                        .create();
                dialog.show();
            }
        });
    }


    public void setListAdapter() {
        List<Map<String, String>> data = new ArrayList<>();
        Map<String, String> header = new LinkedHashMap<>();
        header.put("name", "姓名");
        header.put("birthday", "生日");
        header.put("present", "礼物");
        data.add(header);

        Map<String, String> temp = new LinkedHashMap<>();
        temp.put("name", "brenda");
        temp.put("birthday", "1996/4/15");
        temp.put("present", "money");
        data.add(temp);

        list = (ListView) findViewById(R.id.list);
        simpleAdapter = new SimpleAdapter(this, data, R.layout.item,
                new String[] {"name", "birthday", "present"}, new int[] {R.id.name, R.id.birthday, R.id.present});
        list.setAdapter(simpleAdapter);
    }
}
