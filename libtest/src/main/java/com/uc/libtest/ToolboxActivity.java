package com.uc.libtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.uc.model.ToolboxButtonItem;
import com.uc.model.ToolboxItem;
import com.uc.widget.ToolboxLayout;

public class ToolboxActivity extends AppCompatActivity {

    ToolboxLayout toolboxLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbox);
        toolboxLayout=findViewById(R.id.tool_box);
        int size=getResources().getDimensionPixelSize(R.dimen.sizeButton);
        ToolboxItem toolboxItem=new ToolboxItem(R.drawable.ic_app_button, size,size, LinearLayout.VERTICAL);
        toolboxItem.addButton(new ToolboxButtonItem(R.drawable.ic_arrow_back_black_24dp));
        toolboxItem.addButton(new ToolboxButtonItem(R.drawable.ic_arrow_downward_black_24dp));
        toolboxLayout.setToolBoxItem(toolboxItem);
    }
}
