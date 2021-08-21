package com.usrProject.taizhongoldtownguideapp.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TimePicker;

import com.google.firebase.database.FirebaseDatabase;
import com.usrProject.taizhongoldtownguideapp.R;
import com.usrProject.taizhongoldtownguideapp.model.User.User;
import com.usrProject.taizhongoldtownguideapp.model.User.UserMarker;
import com.usrProject.taizhongoldtownguideapp.schema.UserSchema;

import org.apache.commons.lang3.StringUtils;

public class CreateNewMarker extends AppCompatActivity {

    private TimePicker picker;
    private EditText markTitleEditText;
    private Switch aSwitch;

    private Boolean setNotice;
    private Button confirmButton;

    final int PICK_IMAGE_REQUEST = 2;
    private ImageView markerIcon;
    private FirebaseDatabase mDatabase;

    private User user;
    private UserMarker userMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_marker);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra(UserSchema.USER_DATA);
        userMarker = (UserMarker) intent.getSerializableExtra(UserSchema.USER_MARKER);
        mDatabase = FirebaseDatabase.getInstance();
        markerIcon = findViewById(R.id.addIcon_iconView);
        markTitleEditText = findViewById(R.id.addIcon_editText);
        switch (userMarker.markType){
            case TASK:
                markerIcon.setClickable(false);
                userMarker.iconId = R.drawable.check_in_record_icon;
                markerIcon.setImageResource(userMarker.iconId);
                break;
            case CUSTOMIZE:
                markerIcon.setClickable(true);
                userMarker.iconId = R.drawable.location_icon;
                markerIcon.setImageResource(userMarker.iconId);
                break;
        }
        if(StringUtils.isNotBlank(userMarker.title)){
            markTitleEditText.setText(userMarker.title);
        }
        aSwitch = findViewById(R.id.setNotice_switch);
        confirmButton = findViewById(R.id.addLocation_button);
        picker = findViewById(R.id.timePicker);
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

        confirmButton.setOnClickListener(v -> {
            userMarker.title = markTitleEditText.getText().toString();
            intent.putExtra(UserSchema.USER_MARKER, userMarker);
            setResult(RESULT_OK, intent);
            mDatabase.getReference().child("team").child(user.teamId).child("marker").push().setValue(userMarker);
            finish();
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
            assert data != null;
            userMarker.iconId = data.getIntExtra("userPickedMarker",R.drawable.location_icon);
            markerIcon.setImageResource(userMarker.iconId);
        }
    }
}
