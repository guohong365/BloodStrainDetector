package com.uc.bloodstraindetector;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.uc.activity.ActivityBase;
import com.uc.activity.RequestParams;
import com.uc.bloodstraindetector.model.CaseItem;
import com.uc.bloodstraindetector.model.DataManager;
import com.uc.bloodstraindetector.model.ImageItem;
import com.uc.bloodstraindetector.model.RequestParamsImpl;
import com.uc.bloodstraindetector.view.adapter.ImageListAdapter;
import com.uc.bloodstraindetector.view.adapter.holder.ImageItemHolderFactory;
import com.uc.model.Selectable;
import com.uc.widget.adapter.GridDecoration;
import com.uc.widget.adapter.OnRecyclerViewItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class PhotoSelectActivity extends ActivityBase {
    private RequestParamsImpl request;
    private CaseItem caseItem=null;
    private List<ImageItem> imageItems;
    private ImageListAdapter imageAdapter;
    private TextView btnDone;
    private View.OnClickListener onCommanderButtonClicked=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.action_done:
                    List<ImageItem> selected= imageAdapter.getSelectedItems();
                    request.setImageItems(selected);
                    Intent intent=new Intent();
                    intent.putExtra(RequestParams.KEY_REQUEST, request);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
                case R.id.action_cancel:
                    setResult(RESULT_CANCELED);
                    finish();
                    break;
            }
            setSelectAll(false);
            setSelectableAll(false);
            finish();
        }
    };

    @Override
    public void onBackPressed() {
        setSelectAll(false);
        setSelectableAll(false);
        super.onBackPressed();
    }

    @Override
    protected void onCreateTasks(Bundle savedInstanceState) {
        setContentView(R.layout.activity_photo_select);
        request=getIntent().getParcelableExtra(RequestParams.KEY_REQUEST);
        Log.d(TAG, "onCreateTasks: request=" + (request==null ? "" : request));
        if(request.getCaseItem()==null){
            Log.d(TAG, "onCreate: null case, select photo in all images.");
            imageItems=DataManager.getInstance().getImages();
        } else {
            caseItem= DataManager.getInstance().findCaseById(request.getCaseItem().getId());
            Log.d(TAG, "onCreate: select photo in case [" + caseItem.getTitle() +  "]");
            imageItems=caseItem.getImages();
        }
        setSelectableAll(true);
        TextView textView=findViewById(R.id.t_message);
        textView.setText(request.getMessage());
        btnDone=findViewById(R.id.action_done);
        btnDone.setOnClickListener(onCommanderButtonClicked);
        btnDone.setEnabled(false);
        findViewById(R.id.action_cancel).setOnClickListener(onCommanderButtonClicked);
        CheckBox checkBox=findViewById(R.id.ctrl_select_all);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setSelectAll(isChecked);
                imageAdapter.notifyDataSetChanged();
                int count=imageAdapter.getSelectedCount();
                Log.d(TAG, "onClicked: checked count=" + count);
                if(count==request.getRequestCount() ||(request.getRequestCount()<=0 && count>0)){
                    btnDone.setEnabled(true);
                } else {
                    btnDone.setEnabled(false);
                }
            }
        });
        RecyclerView recyclerView=findViewById(R.id.ctrl_photo_list_view);
        int column=getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE ?
                getResources().getInteger(R.integer.image_grid_horizontal_span) :
                getResources().getInteger(R.integer.image_grid_vertival_span);
        recyclerView.setLayoutManager(new GridLayoutManager(this, column, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new GridDecoration(getResources().getDimensionPixelSize(R.dimen.thick_image_item_grid_decoration),getColor(R.color.black_overlay)));
        imageAdapter=new ImageListAdapter(this, new ArrayList<>(imageItems), new ImageItemHolderFactory(this,-1, column));
        imageAdapter.setOnRecyclerViewItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onClicked(View view, Object tag, int position) {
                Selectable item=imageAdapter.getItem(position);
                item.setSelected(!item.isSelected());
                imageAdapter.notifyItemChanged(position);
                int count=imageAdapter.getSelectedCount();
                Log.d(TAG, "onClicked: checked count=" + count);
                if(count==request.getRequestCount() ||(request.getRequestCount()<=0 && count>0)){
                    btnDone.setEnabled(true);
                } else {
                    btnDone.setEnabled(false);
                }
            }
        });
        recyclerView.setAdapter(imageAdapter);
    }
    private void setSelectAll(boolean selected){
        for(ImageItem item : imageItems){
            item.setSelected(selected);
        }
    }
    private void setSelectableAll(boolean selectable){
        for(ImageItem item : imageItems){
            item.setSelectable(selectable);
        }
    }
}
