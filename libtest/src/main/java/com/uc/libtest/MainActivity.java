package com.uc.libtest;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.uc.model.Selectable;
import com.uc.utils.ActivityHelper;
import com.uc.widget.adapter.OnRecyclerViewItemLongClickListener;

import java.util.ArrayList;
import java.util.List;

import com.uc.libtest.adapter.GroupAdapter;
import com.uc.libtest.model.DataItem;
import com.uc.libtest.model.GroupItem;

public class MainActivity extends AppCompatActivity {
    static final String TAG="MainActivity";
    static final int REQUEST_ITEM=100;
    GroupAdapter groupAdapter;
    List<GroupItem> groupItems=new ArrayList<>();
    int currentItem=-1;
    GroupItem currentGroup=null;
    View.OnClickListener btnClicked=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_add_group:
                    doInsertGroup();
                    break;
                case R.id.btn_add_item:
                    startEditor(null);
                    break;
                case R.id.btn_landscape:
                    ActivityHelper.forceLayoutOrientation(MainActivity.this, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    break;
                case R.id.btn_portrait:
                    ActivityHelper.forceLayoutOrientation(MainActivity.this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    break;
                case R.id.btn_toolbox:
                    Log.d(TAG, "onClick: tool box");
                    Intent intent=new Intent();
                    intent.setClass(MainActivity.this, ToolboxActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button=findViewById(R.id.btn_add_group);
        button.setOnClickListener(btnClicked);
        button=findViewById(R.id.btn_add_item);
        button.setOnClickListener(btnClicked);
        button=findViewById(R.id.btn_landscape);
        button.setOnClickListener(btnClicked);
        button=findViewById(R.id.btn_portrait);
        button.setOnClickListener(btnClicked);
        button=findViewById(R.id.btn_toolbox);
        button.setOnClickListener(btnClicked);
        RecyclerView view=findViewById(R.id.ctrl_view);
        view.setLayoutManager(new LinearLayoutManager(this));
        groupAdapter=new GroupAdapter(this, new ArrayList<Selectable>());
        view.setAdapter(groupAdapter);
        groupAdapter.setOnRecyclerViewItemLongClickListener(new OnRecyclerViewItemLongClickListener() {
            @Override
            public boolean onLongClicked(View view, Object tag, int position) {
                currentGroup= (GroupItem) tag;
                currentItem=position;
                return false;
            }
        });
    }

    void startEditor(Long groupId){
        Intent intent=new Intent();
        if(groupId!=null){
            intent.putExtra("ID", groupId);
        }
        intent.setClass(this, ItemEditActivity.class);
        startActivityForResult(intent, REQUEST_ITEM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode!=RESULT_OK) return;
        String id=data.getExtras().getString("ID");
        Log.d(TAG, "onActivityResult: ID=" + id);
        String text=data.getExtras().getString("TEXT");
        Log.d(TAG, "onActivityResult: TEXT=" + text);
        doInsertItem(new DataItem(Long.parseLong(id), text));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_group:
                doDeleteGroup();
                break;
            case R.id.delete_item:
                doDeleteItem();
                break;
            case R.id.add_item:
                startEditor(currentGroup!=null ? currentGroup.getId():null);
                break;
        }
        return true;
    }
    public void doDeleteGroup(){

    }
    void doDeleteItem(){

    }
    void doInsertGroup(){
        GroupItem groupItem=new GroupItem();
        groupAdapter.insertItem(groupItem, 0);
    }
    void doInsertItem(DataItem item){
//        GroupItem groupItem= (GroupItem) groupAdapter.findGroupByName("GROUP " + item.getGroupId());
//        if(groupItem==null){
//            Log.d(TAG, "doInsertItem: group [] not found. data was ignored");
//            return;
//        }
//        Object tag=groupItem.getTag();
//        Log.d(TAG, "doInsertItem: TAG=" + (tag==null ? "NULL" : tag.getClass().getName()));
//        GroupItemHolder holder= (GroupItemHolder) tag;
//        assert holder != null;
//        ItemAdapter adapter = holder.getAdapter();
//        adapter.insertItem(item, 0);
        groupAdapter.insertSubItem(item, 0);
    }
}
