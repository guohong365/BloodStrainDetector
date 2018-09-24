package com.uc.libtest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ItemEditActivity extends AppCompatActivity {

    EditText edt_id;
    EditText edt_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_edit);
        edt_id=findViewById(R.id.edt_group_id);
        edt_text=findViewById(R.id.edt_text);
        Button button=findViewById(R.id.btn_ok);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.putExtra("ID", edt_id.getText().toString());
                intent.putExtra("TEXT", edt_text.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
