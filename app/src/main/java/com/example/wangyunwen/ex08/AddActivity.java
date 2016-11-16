package com.example.wangyunwen.ex08;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Button addItem = (Button) findViewById(R.id.addItem);
        final EditText getName = (EditText) findViewById(R.id.getName);
        EditText getBirthday = (EditText) findViewById(R.id.getBirthday);
        EditText getPresent = (EditText) findViewById(R.id.getPresent);

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = getName.getText().toString();
                if (name.equals("")) {
                    Toast.makeText(AddActivity.this, "名字为空，请完善",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
