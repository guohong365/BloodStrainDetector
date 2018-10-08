package com.uc.bloodstraindetector;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.uc.activity.ActivityBase;
import com.uc.activity.CameraActivity;
import com.uc.activity.RequestParams;
import com.uc.android.camera.app.CameraApp;
import com.uc.bloodstraindetector.model.CaseItem;
import com.uc.bloodstraindetector.model.CompareParams;
import com.uc.bloodstraindetector.model.DataManager;
import com.uc.bloodstraindetector.model.ImageItem;
import com.uc.bloodstraindetector.model.ImageItemUtils;
import com.uc.bloodstraindetector.model.RequestParamsImpl;
import com.uc.bloodstraindetector.utils.FileHelper;
import com.uc.bloodstraindetector.view.CompareFrameLayout;

import com.uc.task.BitmapSaveTask;
import com.uc.utils.ViewUtils;
import com.yalantis.ucrop.view.TransformImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class CompareActivityBase extends ActivityBase {
    protected static final int REQUEST_TAKE_PHOTO =0;
    protected static final int REQUEST_CHOOSE_PHOTO=1;

    protected CompareParams compareParams;
    protected CompareFrameLayout compareFrameLayout;
    protected View blockingView;
    protected int loadCount;
    protected Uri takenPhotoUri;
    protected int requestPhotoIndex=-1;
    protected abstract @LayoutRes int getLayoutResId();
    protected abstract @MenuRes int getOptionsMenuResId();
    protected abstract @StringRes int getActivityTitleRes();
    protected void setupLayout()
    {
        setContentView(getLayoutResId());
        setupToolbar();
        //@LayoutRes int actionBarId = getLayoutResId();
        //ViewGroup rootView = findViewById(R.id.compare_activity_root);
        //View.inflate(this, actionBarId, rootView);
    }
    protected void setupToolbar(){
        final Toolbar toolbar = findViewById(R.id.tool_bar);
        Drawable stateButtonDrawable = ContextCompat.getDrawable(this, R.drawable.ic_arrow_left);
        toolbar.setNavigationIcon(stateButtonDrawable);
        TextView title=findViewById(R.id.title_tool_bar);
        title.setText(getActivityTitleRes());
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    protected void setupViews(){
        compareFrameLayout = findViewById(R.id.compare_root_view);
        compareFrameLayout.setTransformImageListener(new TransformImageView.TransformImageListener() {
            @Override
            public void onLoadComplete() {
                synchronized (this){
                    if(loadCount>0){
                        loadCount --;
                    }
                }
                if(loadCount==0){
                    blockingView.setClickable(false);
                }
            }

            @Override
            public void onLoadFailure(@NonNull Exception e) {

            }

            @Override
            public void onRotate(float currentAngle) {

            }

            @Override
            public void onScale(float currentScale) {

            }
        });
        blockingView=findViewById(R.id.blocking_view);
    }
    protected abstract void setupCommandBars();

    @Override
    protected void onCreateTasks(Bundle savedInstanceState) {
        super.onCreateTasks(savedInstanceState);
        setupLayout();
        setupViews();
        setupCommandBars();
        compareParams = getIntent().getParcelableExtra(RequestParams.KEY_REQUEST);
    }

    @Override
    protected void onResumeTasks() {
        super.onResumeTasks();
        hideNavigationBarAndStatusBar();
        Log.d(TAG, "onResumeTasks: ............");
        loadImageData();
    }

    protected void loadImageData(){
        loadCount=2;
        compareFrameLayout.setCompareParams(compareParams);
    }

    protected void startTakePhoto(int requestPhotoIndex){
        this.requestPhotoIndex=requestPhotoIndex;
        Intent intent=new Intent();
        intent.setClass(this,CameraActivity.class);
        Log.d(TAG, "startTakePhoto...... ");
        File file=FileHelper.getImageFile(this, CameraApp.getImageFileName(FileHelper.IMAGE_EXT));
        takenPhotoUri=Uri.fromFile(file);
        Log.d(TAG, "startTakePhoto: takenPhotoUri=" + takenPhotoUri.toString());
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, takenPhotoUri);
        startActivityForResult(intent, REQUEST_TAKE_PHOTO);
    }
    protected void startChooser(int requestPhotoIndex){
        this.requestPhotoIndex=requestPhotoIndex;
        Intent intent=new Intent();
        CaseItem caseItem= DataManager.getInstance().findCaseById(compareParams.caseId);
        RequestParams requestParams=new RequestParamsImpl(
                requestPhotoIndex,
                -1,
                caseItem,
                getResources().getString(R.string.message_choose_and_replace_for_compare),
                1);
        intent.putExtra(RequestParams.KEY_REQUEST, requestParams);
        intent.setClass(this,PhotoSelectActivity.class);
        startActivityForResult(intent, REQUEST_CHOOSE_PHOTO);
    }
    protected void afterPhotoRequested(int requestPhotoIndex, Uri photoUri){
        compareParams.images[requestPhotoIndex]=photoUri.toString();
        Log.d(TAG, "afterPhotoRequested: ...........");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode!=RESULT_OK) return;
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                ImageItem imageItem= ImageItemUtils.New(compareParams.caseId, takenPhotoUri);
                DataManager.getInstance().insertImageItem(imageItem);
                break;
            case REQUEST_CHOOSE_PHOTO:
                RequestParamsImpl params=data.getParcelableExtra(RequestParams.KEY_REQUEST);
                takenPhotoUri=params.getImageItems().get(0).getUri();
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                return;
        }
        afterPhotoRequested(requestPhotoIndex, takenPhotoUri);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final int menuResId=getOptionsMenuResId();
        getMenuInflater().inflate(menuResId, menu);
        return true;
    }
    protected void onSaveImage(){
        blockingView.setClickable(true);
        Bitmap bitmap=ViewUtils.capture(compareFrameLayout);
        Uri file=FileHelper.getImageUri(getApplicationContext(), CameraApp.getImageFileName(FileHelper.IMAGE_EXT));
        new BitmapSaveTask(this, bitmap, file, null, new BitmapSaveTask.BitmapSaveCallback() {
            @Override
            public void onBitmapSaved(Uri outputUri) {
                CompareActivityBase.this.showToast("image saved.");
                blockingView.setClickable(false);
            }

            @Override
            public void onSaveFailure(Throwable throwable) {
                CompareActivityBase.this.showToast("image saved failed." + throwable.getMessage());
                blockingView.setClickable(false);
            }
        }).execute();
    }
    protected abstract void onSwapImage();
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.option_menu_save:
                onSaveImage();
                break;
            case R.id.option_menu_swap:
                onSwapImage();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
