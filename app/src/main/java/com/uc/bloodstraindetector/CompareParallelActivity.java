package com.uc.bloodstraindetector;

import android.view.View;
import android.widget.TextView;

import com.uc.bloodstraindetector.view.CompareFrameLayout;

public class CompareParallelActivity extends CompareActivityBase {

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_compare_parallel;
    }

    @Override
    protected int getOptionsMenuResId() {
        return R.menu.options_menu_compare_parallel;
    }

    @Override
    protected int getActivityTitleRes() {
        return R.string.title_compare_parallel;
    }

    View.OnClickListener commandListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.action_take_1:
                    startTakePhoto(0);
                    break;
                case R.id.action_take_2:
                    startTakePhoto(1);
                    break;
                case R.id.action_choose_1:
                    startChooser(0);
                    break;
                case R.id.action_choose_2:
                    startChooser(1);
                    break;
                case R.id.action_swap_to_left:
                case R.id.action_swap_to_right:
                    compareFrameLayout.swapImage(CompareFrameLayout.SWAP_LEFT_TO_RIGHT);
                    break;
            }
        }
    };
    @Override
    protected void setupCommandBars(){
        TextView textView=findViewById(R.id.action_take_1);
        textView.setOnClickListener(commandListener);
        textView=findViewById(R.id.action_take_2);
        textView.setOnClickListener(commandListener);
        textView=findViewById(R.id.action_choose_1);
        textView.setOnClickListener(commandListener);
        textView=findViewById(R.id.action_choose_2);
        textView.setOnClickListener(commandListener);
        textView=findViewById(R.id.action_swap_to_left);
        textView.setOnClickListener(commandListener);
        textView=findViewById(R.id.action_swap_to_right);
        textView.setOnClickListener(commandListener);
    }

    @Override
    protected void onSwapImage() {
        compareFrameLayout.swapImage(CompareFrameLayout.SWAP_LEFT_TO_RIGHT);
    }
}
