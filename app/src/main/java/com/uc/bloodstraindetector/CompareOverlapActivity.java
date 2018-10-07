package com.uc.bloodstraindetector;

import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.uc.bloodstraindetector.view.CompareFrameLayout;

public class CompareOverlapActivity extends CompareActivityBase {
    protected SeekBar alphaSeekBar;
    protected TextView t_alpha;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_compare_overlap;
    }

    @Override
    protected int getOptionsMenuResId() {
        return R.menu.options_menu_compare_overlap;
    }

    @Override
    protected int getActivityTitleRes() {
        return R.string.title_compare_overlap;
    }

    @Override
    protected void setupViews() {
        super.setupViews();
        alphaSeekBar = findViewById(R.id.alphaSeekBar);
        t_alpha=findViewById(R.id.t_alpha);
        if(alphaSeekBar!=null){
            alphaSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    Log.d(TAG, "onProgressChanged: seek bar changed=" + progress + " fromUser=" + fromUser);
                    if (fromUser) {
                        compareFrameLayout.setImageAlpha(1 - progress / 100.f);
                        t_alpha.setText(progress + "%");
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            t_alpha.setText("50%");
        }
    }
    View.OnClickListener commandListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.action_take_photo:
                    startTakePhoto(0);
                    break;
                case R.id.action_choose_photo:
                    startChooser(0);
                    break;
                case R.id.action_swap_up_down:
                    compareFrameLayout.swapImage(CompareFrameLayout.SWAP_UP_TO_DOWN);
                    break;
            }
        }
    };

    @Override
    protected void setupCommandBars(){
        TextView textView=findViewById(R.id.action_take_photo);
        textView.setOnClickListener(commandListener);
        textView=findViewById(R.id.action_choose_photo);
        textView.setOnClickListener(commandListener);
        textView=findViewById(R.id.action_swap_up_down);
        textView.setOnClickListener(commandListener);
    }

    @Override
    protected void onSwapImage() {
        compareFrameLayout.swapImage(CompareFrameLayout.SWAP_UP_TO_DOWN);
    }
}
