package com.usrProject.taizhongoldtownguideapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.usrProject.taizhongoldtownguideapp.R;

public class ChangeMarkerIcon extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_marker_icon);
    }

    public void chooseMarker1(View view) {
        Intent intent = new Intent();
        intent.putExtra("userPickedMarker", R.drawable.bed_icon);
        setResult(RESULT_OK, intent);
        finish();
    }
    public void chooseMarker2(View view) {
        Intent intent = new Intent();
        intent.putExtra("userPickedMarker", R.drawable.food_icon);
        setResult(RESULT_OK, intent);
        finish();
    }
    public void chooseMarker3(View view) {
        Intent intent = new Intent();
        intent.putExtra("userPickedMarker", R.drawable.location_icon);
        setResult(RESULT_OK, intent);
        finish();
    }

}
