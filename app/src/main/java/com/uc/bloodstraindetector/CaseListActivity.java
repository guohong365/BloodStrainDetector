package com.uc.bloodstraindetector;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.uc.activity.ActivityBase;
import com.uc.activity.RequestParams;
import com.uc.bloodstraindetector.model.CaseItem;
import com.uc.bloodstraindetector.model.DataManager;
import com.uc.bloodstraindetector.model.RequestParamsImpl;
import com.uc.bloodstraindetector.view.adapter.CaseListAdapter;
import com.uc.widget.adapter.LinearDecoration;
import com.uc.widget.adapter.OnRecyclerViewItemClickListener;
import com.uc.widget.adapter.OnRecyclerViewItemLongClickListener;

import java.util.ArrayList;
import java.util.List;

public class CaseListActivity extends ActivityBase {
    private static final String CASE_ITEM = "caseItem";
    public static final int REQUEST_CREATE_NEW_CASE = 0;
    public static final int REQUEST_MODIFY_CASE = 1;
    public static final int REQUEST_VIEW_CASE_DETAIL=2;

    private SearchView searchView;
    private RecyclerView recyclerView;
    private CaseListAdapter caseListAdapter;

    @Override
    protected void onCreateTasks(Bundle savedInstanceState) {
        super.onCreateTasks(savedInstanceState);
        setContentView(R.layout.activity_case_list);

        //TODO: implement search feature
        searchView = findViewById(R.id.ctrl_search);
        searchView.setVisibility(View.GONE);
        recyclerView= findViewById(R.id.ctrl_case_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new LinearDecoration(
                getResources().getDimensionPixelSize(R.dimen.height_decoration_case_list),
                getColor(R.color.gray)));
        caseListAdapter=new CaseListAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(caseListAdapter);
        caseListAdapter.setOnRecyclerViewItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onClicked(View view, Object tag, int position) {
                startViewCaseDetail(position);
            }
        });
        caseListAdapter.setOnRecyclerViewItemLongClickListener(new OnRecyclerViewItemLongClickListener() {
            @Override
            public boolean onLongClicked(View view, Object tag, int position) {
                caseListAdapter.setCurrent(position);
                return false;
            }
        });
        setupToolbar();
        setupCommandBar();
    }
    protected void setupToolbar(){
        final Toolbar toolbar = findViewById(R.id.tool_bar);
        Drawable stateButtonDrawable = ContextCompat.getDrawable(this, R.drawable.ic_arrow_left);
        toolbar.setNavigationIcon(stateButtonDrawable);
        TextView title=findViewById(R.id.title_tool_bar);
        title.setText(R.string.title_case_list);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu_case_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.option_menu_add_case:
                startCaseEditor(-1,null, REQUEST_CREATE_NEW_CASE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupCommandBar(){
        TextView view=findViewById(R.id.action_add_case);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCaseEditor(-1,null, REQUEST_CREATE_NEW_CASE);
            }
        });
    }

    @Override
    protected void onResumeTasks() {
        super.onResumeTasks();
        List<CaseItem> caseItems= DataManager.getInstance().getCases();
        caseListAdapter.getItems().clear();
        caseListAdapter.getItems().addAll(caseItems);
        caseListAdapter.notifyDataSetChanged();
    }

    void startCaseEditor(int position, CaseItem caseItem, int requestCode){
        Intent intent=new Intent();
        RequestParamsImpl params=new RequestParamsImpl(requestCode, position, caseItem);
        intent.putExtra(RequestParamsImpl.KEY_REQUEST, params);
        intent.setClass(this,CaseEditorActivity.class);
        startActivityForResult(intent, requestCode);
    }

    void startViewCaseDetail(int position){
        Intent intent=new Intent();
        RequestParamsImpl params=new RequestParamsImpl(REQUEST_VIEW_CASE_DETAIL, position, caseListAdapter.getItem(position));
        intent.putExtra(RequestParamsImpl.KEY_REQUEST, params);
        intent.setClass(this, CaseContentActivity.class);
        startActivityForResult(intent, REQUEST_VIEW_CASE_DETAIL);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position=caseListAdapter.getCurrent();
        CaseItem caseItem=caseListAdapter.getItem(position);
        if(caseItem!=null) { //maybe always true
            switch (item.getItemId()){
                case R.id.action_edit:
                    startCaseEditor(position, caseItem, REQUEST_MODIFY_CASE);
                    break;
                case R.id.action_delete:
                    doDeleteCase(position, caseItem);
                    break;
            }
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode !=RESULT_OK) return;
        RequestParamsImpl params=data.getParcelableExtra(RequestParams.KEY_REQUEST);
        switch (requestCode){
            case REQUEST_CREATE_NEW_CASE:
                caseListAdapter.insertItem(params.getCaseItem(), 0);
                startViewCaseDetail(0);
                break;
            case REQUEST_MODIFY_CASE:
                if(params.getCaseItem().isDirty()){
                    caseListAdapter.replaceItem(params.getPosition(),params.getCaseItem());
                }
                break;
            case REQUEST_VIEW_CASE_DETAIL:
                /*
                if(params.getCaseItem().isDirty()){
                    Log.d(TAG, "onActivityResult: data is dirty");
                    CaseItem item=DataManager.getInstance().findCaseById(params.getCaseItem().getId());
                    Log.d(TAG, "onActivityResult: data in db=" + item.toString());
                    item=caseListAdapter.getItem(params.getPosition());
                    Log.d(TAG, "onActivityResult: data in adapter=" + item.toString());
                    caseListAdapter.notifyItemChanged(params.getPosition());
                }
                */
                break;
        }
    }
    private void doDeleteCase(int position, CaseItem caseItem){
        new AlertDialog.Builder(this)
                .setTitle(R.string.system_dialog_title_warning)
                .setMessage(R.string.message_if_delete_case)
                .setPositiveButton(R.string.action_name_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        caseListAdapter.deleteItem(position);
                    }
                })
                .setNegativeButton(R.string.action_name_cancel, null)
                .show();
    }
}
