package com.uc.bloodstraindetector;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.uc.activity.ActivityBase;
import com.uc.activity.CameraActivity;
import com.uc.activity.RequestParams;
import com.uc.android.camera.app.CameraApp;
import com.uc.bloodstraindetector.model.CaseItem;
import com.uc.bloodstraindetector.model.CompareParams;
import com.uc.bloodstraindetector.model.DataManager;
import com.uc.bloodstraindetector.model.ImageGroupItem;
import com.uc.bloodstraindetector.model.ImageItem;
import com.uc.bloodstraindetector.model.ImageItemUtils;
import com.uc.bloodstraindetector.model.RequestParamsImpl;
import com.uc.bloodstraindetector.utils.FileHelper;
import com.uc.bloodstraindetector.view.OnAfterPhotosChosenListener;
import com.uc.bloodstraindetector.view.adapter.ImageGroupListAdapter;
import com.uc.bloodstraindetector.view.adapter.holder.ImageGroupItemHolderFactory;
import com.uc.bloodstraindetector.view.adapter.holder.ImageItemHolderFactory;
import com.uc.model.DataGroup;
import com.uc.widget.adapter.OnRecyclerViewGroupedItemClickListener;
import com.uc.widget.adapter.OnRecyclerViewGroupedItemLongClickListener;
import com.uc.widget.adapter.GridRecyclerViewLayoutManagerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CaseContentActivity extends ActivityBase {
    private static final int REQUEST_SELECT_PHOTOS = 0;
    private static final int REQUEST_COMPARE_SPLIT = 1;
    private static final int REQUEST_COMPARE_OVERLAP =2;
    private static final int REQUEST_DELETE = 3;
    private static final int REQUEST_EXPORT = 4;
    private static final int REQUEST_TAKE_PHOTO=5;
    private static final int REQUEST_PHOTO_EDIT=6;
    private static final int REQUEST_PAGER_VIEW=7;

    private RequestParamsImpl request;
    private CaseItem caseItem;
    private ImageGroupListAdapter groupAdapter;
    private ImageGroupItem currentGroup=null;
    private int currentImageItem=-1;
    private File imageFile;

    @Override
    protected void onCreateTasks(Bundle savedInstanceState) {
        super.onCreateTasks(savedInstanceState);
        request = getIntent().getExtras().getParcelable(RequestParams.KEY_REQUEST);
        if(request==null || request.getPosition()<0 || request.getCaseItem()==null){
            Log.e(TAG, "empry request parameters or INVALID parameter");
            finish();
            return;
        }
        caseItem= DataManager.getInstance().findCaseById(request.getCaseItem().getId());
        setContentView(R.layout.activity_case_content);
        setupToolbar();
        setupCommandButtons();
        setupCaseInfo();
    }
    protected void setupToolbar(){
        final Toolbar toolbar = findViewById(R.id.tool_bar);
        Drawable stateButtonDrawable = ContextCompat.getDrawable(this, R.drawable.ic_arrow_left);
        toolbar.setNavigationIcon(stateButtonDrawable);
        TextView title=findViewById(R.id.title_tool_bar);
        title.setText(R.string.title_case_content);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu_case_content, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.option_menu_batch_delete:
                doChoosePhotos(REQUEST_DELETE, -1, -1, R.string.message_choose_for_delete);
                break;
            case R.id.option_menu_export:
                doChoosePhotos(REQUEST_EXPORT, -1, -1, R.string.message_choose_for_export);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    OnAfterPhotosChosenListener afterPhotosChosenListener=new OnAfterPhotosChosenListener() {
        @Override
        public void afterChosen(RequestParamsImpl params) {
            switch (params.getAction()){
                case REQUEST_COMPARE_SPLIT:
                case REQUEST_COMPARE_OVERLAP:
                    startCompare(params);
                    break;
                case REQUEST_DELETE:
                    doDeleteImages(params);
                    break;
                case REQUEST_EXPORT:
                    doExportImages(params);
                    break;
            }
        }
    };
    private void startCompare(RequestParamsImpl params){
        CompareParams compareParams=new CompareParams(
                params.getAction() == REQUEST_COMPARE_OVERLAP?
                        CompareParams.COMPARE_MODE_OVERLAP:
                        CompareParams.COMPARE_MODE_PARALLEL,
                caseItem.getId(),
                new String[]{
                        params.getImageItems().get(0).getUriString(),
                        params.getImageItems().get(1).getUriString()
                }
        );
        Intent intent=new Intent();
        intent.setClass(this,
                compareParams.compareMode== CompareParams.COMPARE_MODE_OVERLAP ?
                        CompareOverlapActivity.class:
                        CompareParallelActivity.class
        );
        intent.putExtra(RequestParams.KEY_REQUEST, compareParams);
        startActivityForResult(intent, params.getAction());
    }
    private void doChoosePhotos(int actionCode, int needCount, int position, int messageId){
        RequestParamsImpl params=new RequestParamsImpl(actionCode, position, caseItem, getResources().getString(messageId), needCount);
        Intent intent=new Intent();
        intent.setClass(this,PhotoSelectActivity.class);
        intent.putExtra(RequestParamsImpl.KEY_REQUEST, params);
        startActivityForResult(intent, REQUEST_SELECT_PHOTOS);
    }

    private View.OnClickListener commandButtonClicked=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.action_take_photo:
                    startTakePhoto();
                    break;
                case R.id.action_compare_split:
                    doChoosePhotos(REQUEST_COMPARE_SPLIT, 2, -1, R.string.message_choose_for_compare);
                    break;
                case R.id.action_compare_overlap:
                    doChoosePhotos(REQUEST_COMPARE_OVERLAP, 2, -1, R.string.message_choose_for_compare);
                    break;
                case R.id.action_delete:
                    doChoosePhotos(REQUEST_DELETE, -1, -1, R.string.message_choose_for_delete);
                    break;
                case R.id.action_export:
                    doChoosePhotos(REQUEST_EXPORT, -1, -1, R.string.message_choose_for_export);
                    break;
            }
        }
    };

    private void setupCommandButtons(){
        TextView textView=findViewById(R.id.action_take_photo);
        textView.setOnClickListener(commandButtonClicked);
        textView=findViewById(R.id.action_compare_split);
        textView.setOnClickListener(commandButtonClicked);
        textView=findViewById(R.id.action_compare_overlap);
        textView.setOnClickListener(commandButtonClicked);
        textView=findViewById(R.id.action_delete);
        textView.setOnClickListener(commandButtonClicked);
        textView=findViewById(R.id.action_export);
        textView.setOnClickListener(commandButtonClicked);
    }
    private void setupCaseInfo(){
        TextView name=findViewById(R.id.t_name);
        TextView operator=findViewById(R.id.t_operator);
        TextView description=findViewById(R.id.t_description);
        if(caseItem!=null) {
            operator.setText(caseItem.getOperator());
            name.setText(caseItem.getTitle());
            description.setText(caseItem.getDescription() == null ? "" : caseItem.getDescription());
        }
    }

    @Override
    protected void onResumeTasks() {
        super.onResumeTasks();
        setupRecyclerView(findViewById(R.id.ctrl_photo_group_list_view));
    }

    private void setupRecyclerView(RecyclerView recyclerView){
        List<ImageItem> imageItems=caseItem==null ? new ArrayList<>() : caseItem.getImages();
        List<ImageGroupItem> groupItems=DataManager.buildImageGroup(imageItems, caseItem);
        int column=getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE ?
                getResources().getInteger(R.integer.image_grid_horizontal_span) :
                getResources().getInteger(R.integer.image_grid_vertival_span);
        groupAdapter = new ImageGroupListAdapter(this, groupItems,
                new ImageGroupItemHolderFactory(this, new GridRecyclerViewLayoutManagerFactory(this, column, LinearLayoutManager.VERTICAL)),
                new ImageItemHolderFactory(this,R.menu.context_menu_image_list_item, column));
        recyclerView.setAdapter(groupAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        groupAdapter.setOnRecyclerViewGroupedItemClickListener(new OnRecyclerViewGroupedItemClickListener() {
            @Override
            public void onClicked(View groupView, View view, DataGroup group, int groupPosition, Object tag, int position) {
                ImageItem item= (ImageItem) tag;
                startImagePager(request.getCaseItem().getId(), position);
            }
        });
        groupAdapter.setOnRecyclerViewGroupedItemLongClickListener(new OnRecyclerViewGroupedItemLongClickListener() {
            @Override
            public boolean onLongClicked(View groupView, View view, DataGroup group, int groupPosition, Object tag, int position) {
                currentGroup= (ImageGroupItem) group;
                currentImageItem=position;
                return false;
            }
        });
    }

    private void startImagePager(Long caseId, int position){
        Intent intent=new Intent();
        intent.putExtra(ImagePagerActivity.KEY_CASE_ID, caseId);
        intent.putExtra(ImagePagerActivity.KEY_POSITION, position);
        intent.setClass(this,ImagePagerActivity.class);
        startActivityForResult(intent, REQUEST_PAGER_VIEW);
    }
    private void startTakePhoto(){
        Log.d(TAG, "startTakePhoto...... ");
        Intent intent =new Intent();
        intent.setClass(this,CameraActivity.class);
        imageFile=FileHelper.getImageFile(this, CameraApp.getImageFileName(FileHelper.IMAGE_EXT));
        Log.d(TAG, "startTakePhoto: path=" + imageFile.getAbsolutePath());
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
        startActivityForResult(intent, REQUEST_TAKE_PHOTO);
    }

    private void doSaveImageItem(){
        Log.d(TAG, "doSaveImageItem ..........");
        if(!imageFile.exists() || !imageFile.isFile()){
            Log.d(TAG, "doSaveImageItem: file is not exists nor a file[" + imageFile.getAbsolutePath() + "]");
            Toast.makeText(this, R.string.err_save_image_file_failed, Toast.LENGTH_LONG).show();
            return;
        }
        Log.d(TAG, "doSaveImageItem: taken photo path=" + imageFile.getAbsolutePath());
        ImageItem imageItem= ImageItemUtils.New(caseItem.getId(), Uri.fromFile(imageFile));
        ImageItemUtils.fillImageExif(this, imageItem);
        Log.d(TAG, "doSaveImageItem: imageItem prepared [" + imageItem.toString() + "]");
        groupAdapter.insertSubItem(imageItem, 0);
        Log.d(TAG, "doSaveImageItem: inserted.");
        caseItem.setDirty(true);
    }

    private  void startEditPhoto(ImageItem imageItem){
        Intent intent=new Intent();
        intent.setClass(this, PhotoEditorActivity.class);
        intent.putExtra(PhotoEditorActivity.EXTRA_INPUT, imageItem.getUri());
        File file=FileHelper.getImageFile(this, CameraApp.getImageFileName(FileHelper.IMAGE_EXT));
        intent.putExtra(PhotoEditorActivity.EXTRA_OUTPUT, Uri.fromFile(file));
        intent.putExtra(PhotoEditorActivity.EXTRA_CASE_ID, caseItem.getId());
        startActivityForResult(intent, REQUEST_PHOTO_EDIT);
    }

    private void doDeleteImage(ImageItem imageItem){
        new AlertDialog.Builder(this)
                .setTitle(R.string.system_dialog_title_warning)
                .setMessage(R.string.message_if_delete_image)
                .setNegativeButton(R.string.action_name_cancel, null)
                .setPositiveButton(R.string.action_name_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        groupAdapter.deleteSubItem(imageItem);
                        caseItem.setDirty(true);
                    }
                })
                .create().show();
    }

    private void doDeleteImages(RequestParamsImpl params){
        for(ImageItem item : params.getImageItems()){
            /*
            File file= com.uc.utils.FileHelper.getUriFile(this, item.getUri());
            Log.d(TAG, "doDeleteImages: uri=" + item.getUri().toString() + " path=" + file.getAbsolutePath());
            if(file.delete()){
                Log.d(TAG, "doDeleteImages: file deleted....");
            } else {
                Log.d(TAG, "doDeleteImages: delete file failed.");
            }
            */
            groupAdapter.deleteSubItem(item);
            caseItem.setDirty(true);
        }        
    }
    private void doExportImages(RequestParamsImpl params){

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode!=RESULT_OK) return;
        switch (requestCode){
            case REQUEST_SELECT_PHOTOS:
                RequestParamsImpl params=data.getExtras().getParcelable(RequestParamsImpl.KEY_REQUEST);
                afterPhotosChosenListener.afterChosen(params);
                break;
            case REQUEST_TAKE_PHOTO:
                doSaveImageItem();
                break;
            case REQUEST_PHOTO_EDIT:
                Uri uri=data.getParcelableExtra(PhotoEditorActivity.EXTRA_OUTPUT);
                if(uri!=null){
                    ImageItem imageItem= ImageItemUtils.New(caseItem.getId(), uri);
                    groupAdapter.insertSubItem(imageItem, 0);
                }
                break;
            case REQUEST_PAGER_VIEW:
                //TODO : update list if delete or edit in pager
                break;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(currentImageItem == -1) return true;
        ImageItem imageItem= (ImageItem) currentGroup.getSubAdapter().getItem(currentImageItem);
        switch (item.getItemId()){
            case R.id.action_edit:
                startEditPhoto(imageItem);
                break;
            case R.id.action_delete:
                doDeleteImage(imageItem);
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        request=new RequestParamsImpl(request.getAction(), request.getPosition(), caseItem);
        Intent intent=new Intent();
        intent.putExtra(RequestParams.KEY_REQUEST, request);
        setResult(RESULT_OK, intent);
        finish();
    }
}
