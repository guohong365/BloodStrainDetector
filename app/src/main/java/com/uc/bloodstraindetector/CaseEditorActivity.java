package com.uc.bloodstraindetector;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.uc.bloodstraindetector.model.CaseItem;
import com.uc.bloodstraindetector.model.DataManager;
import com.uc.bloodstraindetector.model.RequestParamsImpl;

import java.util.Date;

public class CaseEditorActivity extends AppCompatActivity {
    private RequestParamsImpl request;
    private CaseItem caseItem;
    private EditText title;
    private EditText operator;
    private EditText description;
    private ImageView[] levels;
    private int currentLevel=0;
    private TextView date;
    private TextView longitude;
    private TextView latitude;
    private ImageView location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        request = getIntent().getExtras().getParcelable(RequestParamsImpl.KEY_REQUEST);
        if (request == null) {
            finish();
            return;
        }
        if (request.getAction() == CaseListActivity.REQUEST_CREATE_NEW_CASE) {
            caseItem = new CaseItem();
            Date currentDate = new Date();
            caseItem.setDirty(true);
            caseItem.setLevel(0);
            caseItem.setCreateTime(currentDate);
            Location currentLocation = BloodStrainDetectorApp.getInstance().getLocation();
            if (currentLocation != null) {
                caseItem.setLongitude(currentLocation.getLongitude());
                caseItem.setLatitude(currentLocation.getLatitude());
            }
            request.setCaseItem(caseItem);
        } else {
            caseItem = DataManager.getInstance().findCaseById(request.getCaseItem().getId());
        }
        setContentView(R.layout.activity_case_editor);
        title = findViewById(R.id.edt_name);
        operator = findViewById(R.id.edt_operator);
        description = findViewById(R.id.edt_comment);
        levels = new ImageView[5];
        levels[0] = findViewById(R.id.ctrl_case_level_0);
        levels[1] = findViewById(R.id.ctrl_case_level_1);
        levels[2] = findViewById(R.id.ctrl_case_level_2);
        levels[3] = findViewById(R.id.ctrl_case_level_3);
        levels[4] = findViewById(R.id.ctrl_case_level_4);
        for (int i = 0; i < 5; i++) {
            levels[i].setTag(i);
            levels[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(currentLevel!=(int) v.getTag()){
                        currentLevel= (int) v.getTag();
                        checkLevel();
                    }
                }
            });
        }
        date = findViewById(R.id.t_date);
        longitude = findViewById(R.id.t_longitude);
        latitude = findViewById(R.id.t_latitude);
        location = findViewById(R.id.ctrl_location);
        title.setText(caseItem.getTitle());
        operator.setText(caseItem.getOperator());
        description.setText(caseItem.getDescription());
        currentLevel=caseItem.getLevel();
        checkLevel();
        date.setText(caseItem.getCreateDate());
        latitude.setText(caseItem.getLatitude() == null ? "" : caseItem.getLatitude().toString());
        longitude.setText(caseItem.getLongitude() == null ? "" : caseItem.getLongitude().toString());
        setupCommandButtons();
    }

    private View.OnClickListener commandButtonClicked=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.action_done:
                    EditText view=retrieve();
                    if(view!=null){
                        Toast.makeText(v.getContext(), R.string.err_field_empty, Toast.LENGTH_LONG).show();
                        view.requestFocus();
                        return;
                    }
                    if(DataManager.getInstance().isTitleDuplicated(caseItem)){
                        Toast.makeText(v.getContext(), R.string.err_duplicated_case_title, Toast.LENGTH_LONG).show();
                        title.requestFocus();
                        return;
                    }
                    Intent data=new Intent();
                    data.putExtra(RequestParamsImpl.KEY_REQUEST, request);
                    setResult(RESULT_OK, data);
                    break;
                case R.id.action_cancel:
                    setResult(RESULT_CANCELED);
                    break;
            }
            finish();
        }
    };

    private EditText retrieve(){
        String value=title.getText().toString();
        boolean dirty=false;
        if(!value.isEmpty()){
            if(!value.equals(request.getCaseItem().getTitle())){
                request.getCaseItem().setTitle(value);
                dirty=true;
            }
        } else {
            return  title;
        }
        value=operator.getText().toString();
        if(!value.isEmpty()){
            if(!value.equals(request.getCaseItem().getOperator())){
                request.getCaseItem().setOperator(value);
                dirty=true;
            }
        } else {
            return operator;
        }
        value=description.getText().toString();
        if(!value.isEmpty()){
            if(!value.equals(request.getCaseItem().getDescription())){
                request.getCaseItem().setDescription(value);
                dirty=true;
            }
        } else if(request.getCaseItem().getDescription()!=null && !request.getCaseItem().getDescription().isEmpty()) {
            request.getCaseItem().setDescription(null);
            dirty = true;
        }

        if(currentLevel!=request.getCaseItem().getLevel()){
            request.getCaseItem().setLevel(currentLevel);
            dirty=true;
        }
        request.getCaseItem().setDirty(dirty);
        return null;
    }

    private void setupCommandButtons(){
        TextView view=findViewById(R.id.action_done);
        view.setOnClickListener(commandButtonClicked);
        view=findViewById(R.id.action_cancel);
        view.setOnClickListener(commandButtonClicked);
    }
    private void checkLevel(){
        for(int i=0; i<5; i++){
            levels[i].setSelected(currentLevel==i);
        }
    }

}
