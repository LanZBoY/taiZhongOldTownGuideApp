package com.usrProject.taizhongoldtownguideapp.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TimePicker;

import com.google.firebase.database.FirebaseDatabase;
import com.usrProject.taizhongoldtownguideapp.R;
import com.usrProject.taizhongoldtownguideapp.activity.ChangeMarkerIcon;
import com.usrProject.taizhongoldtownguideapp.model.User.User;
import com.usrProject.taizhongoldtownguideapp.schema.UserSchema;

import java.util.HashMap;
import java.util.Map;

public class CreateNewMarker extends AppCompatActivity {

    private TimePicker picker;
    private EditText editText;
    private Switch aSwitch;

    private Boolean setNotice;
    private Button confirmButton;
    private double longitude;
    private double latitude;
    final int PICK_IMAGE_REQUEST = 2;
    private ImageView markerIcon;
    private String markerPath;
    //    private String teamID;
    //    private SharedPreferences pref;
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_marker);

        Intent intent = getIntent();
        User user = (User) intent.getSerializableExtra(UserSchema.USER_DATA);
        latitude =  intent.getDoubleExtra("latitude",0);
        longitude = intent.getDoubleExtra("longitude", 0);
        mDatabase = FirebaseDatabase.getInstance();

//        pref = getSharedPreferences(UserSchema.SharedPreferences.USER_DATA, MODE_PRIVATE);
//        teamID = pref.getString("teamID","error");
        markerPath = "location_icon";

        markerIcon = findViewById(R.id.addIcon_iconView);
        editText = findViewById(R.id.addIcon_editText);
        aSwitch = findViewById(R.id.setNotice_switch);
        confirmButton = findViewById(R.id.addLocation_button);
        picker = (TimePicker)findViewById(R.id.timePicker);
        picker.setIs24HourView(true);
        picker.setEnabled(false);

        //提醒功能尚在開發中
        aSwitch.setEnabled(false);
        //提醒功能尚在開發中
        /*
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    picker.setEnabled(true);
                }
                else{
                    picker.setEnabled(false);
                }
            }
        });
        */

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> newMark = new HashMap<>();

                Intent intent = new Intent();
                intent.putExtra("markContext", editText.getText().toString());
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                setResult(RESULT_OK, intent);

                newMark.put("markContext",editText.getText().toString());
                newMark.put("markLatitude",latitude);
                newMark.put("markLongitude",longitude);
                newMark.put("setRemind",true);
                newMark.put("markSetTime",picker.getHour()+" "+picker.getMinute());
                newMark.put("markPath",markerPath);

                mDatabase.getReference().child("team").child(user.teamId).child("marker").push().setValue(newMark);
                finish();
            }
        });
    }

    public void changeIcon(View view) {
        Intent intent = new Intent(this, ChangeMarkerIcon.class);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK){
            markerPath = data.getStringExtra("userPickedMarker");
            int imageResource = getResources().getIdentifier("@drawable/" + markerPath, null, getPackageName());
            markerIcon.setImageResource(imageResource);
        }
    }
}
