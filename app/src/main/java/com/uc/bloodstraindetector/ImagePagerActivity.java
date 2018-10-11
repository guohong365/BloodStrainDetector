package com.uc.bloodstraindetector;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.PersistableBundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.uc.activity.ActivityBase;
import com.uc.bloodstraindetector.model.CaseItem;
import com.uc.bloodstraindetector.model.DataManager;
import com.uc.bloodstraindetector.model.ImageItem;
import com.uc.bloodstraindetector.model.ImageItemUtils;
import com.uc.bloodstraindetector.view.adapter.ImagePagerAdapter;

import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ImagePagerActivity extends ActivityBase {
    public static final String KEY_CASE_ID="caseId";
    public static final String KEY_POSITION="position";
    public static final String KEY_VIEW_ONLY="view_only";
    private static final int REQUEST_EDIT_PHOTO = 0;
    private CaseItem caseItem;
    private List<ImageItem> imageItems;
    private ViewPager viewPager;
    private ImagePagerAdapter adapter;
    private int current;
    @Override
    protected void onCreateTasks(Bundle savedInstanceState) {
        long caseId=getIntent().getLongExtra(KEY_CASE_ID, -1);
        if(caseId!=-1) {
            caseItem = DataManager.getInstance().findCaseById(caseId);
        } else {
            caseItem=null;
        }
        boolean viewOnly=getIntent().getBooleanExtra(KEY_VIEW_ONLY, false);
        setContentView(R.layout.activity_image_pager);

        if(!viewOnly) {
            View commandBar = findViewById(R.id.ctrl_command_bar);
            commandBar.setVisibility(View.VISIBLE);
            TextView textView = findViewById(R.id.action_edit);
            textView.setOnClickListener(commandButtonClicked);
            textView = findViewById(R.id.action_delete);
            textView.setOnClickListener(commandButtonClicked);
        }
        if(caseId!=-1){
            TextView textView = findViewById(R.id.t_case_title);
            textView.setVisibility(View.VISIBLE);
            textView.setText(caseItem.getTitle());
        }
        current=getIntent().getIntExtra(KEY_POSITION, 0);
        viewPager=findViewById(R.id.ctrl_view_pager);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt(KEY_POSITION, viewPager.getCurrentItem());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        current=savedInstanceState.getInt(KEY_POSITION);
    }

    @Override
    protected void onResumeTasks() {
        if (caseItem != null) {
            imageItems = caseItem.getImages();
        } else {
            imageItems = DataManager.getInstance().getImages();
        }
        adapter=new ImagePagerAdapter(this, imageItems);
        viewPager.setAdapter(adapter);
        current = adapter.getCount() < 0 ? 0 : ( current > adapter.getCount()-1 ? adapter.getCount() -1 : current);
        viewPager.setCurrentItem(current);
    }

    private View.OnClickListener commandButtonClicked=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.action_edit:
                    startImageEditor();
                    break;
                case R.id.action_delete:
                    new AlertDialog.Builder(ImagePagerActivity.this)
                            .setTitle(R.string.dialog_title_warning)
                            .setMessage(R.string.message_if_delete_image)
                            .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    doDeleteImage();
                                }
                            })
                            .setNegativeButton(R.string.dialog_no, null)
                            .show();
                    break;
            }
        }
    };

    private ImageItem getCurrentItem(){
        int position=viewPager.getCurrentItem();
        ImageItem imageItem=imageItems.get(position);
        Log.d(TAG, "getCurrentItem: current=" + imageItem==null ? "NULL" : imageItem.toString());
        return imageItem;
    }
    private void startImageEditor(){
        Log.d(TAG, "startImageEditor: .....................");
        ImageItem imageItem=getCurrentItem();
        Intent intent=new Intent();
        intent.putExtra(PhotoEditorActivity.EXTRA_INPUT, imageItem.getUri());
        intent.setClass(this, PhotoEditorActivity.class);
        startActivityForResult(intent, REQUEST_EDIT_PHOTO);
    }

    private void doDeleteImage() {
        int position = viewPager.getCurrentItem();
        Log.d(TAG, "deleteItem: delete at " + position);
        ImageItem imageItem = imageItems.get(position);
        DataManager.getInstance().deleteImageItem(imageItem);
        if(imageItems.size()==0) {
            finish();
        }
        viewPager.getAdapter().notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK) return;
        switch (requestCode) {
            case REQUEST_EDIT_PHOTO:
                Uri uri=data.getParcelableExtra(PhotoEditorActivity.EXTRA_OUTPUT);
                if(uri!=null) {
                    DataManager.getInstance().insertImageItem(ImageItemUtils.New(caseItem.getId(), uri));
                }
                adapter.notifyDataSetChanged();
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }


}
