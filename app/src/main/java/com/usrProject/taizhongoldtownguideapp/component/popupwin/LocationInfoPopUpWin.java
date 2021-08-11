package com.usrProject.taizhongoldtownguideapp.component.popupwin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.usrProject.taizhongoldtownguideapp.activity.CreateNewMarker;
import com.usrProject.taizhongoldtownguideapp.R;
import com.usrProject.taizhongoldtownguideapp.component.LocationInfoPopUpWinRecycleViewAdapter;
import com.usrProject.taizhongoldtownguideapp.component.popupwin.CustomPopUpWin;
import com.usrProject.taizhongoldtownguideapp.model.User.User;
import com.usrProject.taizhongoldtownguideapp.model.User.UserMarker;
import com.usrProject.taizhongoldtownguideapp.schema.UserSchema;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class LocationInfoPopUpWin extends CustomPopUpWin {

    private List<UserMarker> locationList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private LocationInfoPopUpWinRecycleViewAdapter mAdapter;
//    private String teamID;
//    private SharedPreferences pref;
    private FirebaseDatabase mDatabase;
    private DatabaseReference teamMarkerRef;
    private Button createMarkerBtn;
//    private Double mLatitude;
//    private Double mLongitude;
    //private Context context;
    private Activity activity;
    private static final int ADD_LOCATION_ACTIVITY_REQUEST_CODE = 0;
    private User user;


    public LocationInfoPopUpWin(final Context mContext, int xmlLayout, final GoogleMap map, Activity activity, Bundle bundle) {
        super(mContext, xmlLayout,true);
        this.activity = activity;

        mDatabase = FirebaseDatabase.getInstance();
        user = (User) bundle.getSerializable(UserSchema.USER_DATA);
//        pref = mContext.getSharedPreferences(UserSchema.SharedPreferences.USER_DATA, mContext.MODE_PRIVATE);
//        teamID = pref.getString("teamID","error");

//        mLatitude = Double.longBitsToDouble(pref.getLong("mLatitude",0));
//        mLongitude = Double.longBitsToDouble(pref.getLong("mLongitude",0));

        createMarkerBtn = getView().findViewById(R.id.create_marker_btn);
        //必須在getDeviceLocation()後面，因為會需要用到getDeviceLocation獲取的使用者位置mCurrentLocation

        createMarkerBtn.setOnClickListener(v -> {
            addLocation(user.latitude, user.longitude);
            //Log.d("sayHello","sayHello");
        });

        mDatabase.getReference().child("team").child(user.teamId).child("marker");
        teamMarkerRef = mDatabase.getReference().child("team").child(user.teamId).child("marker");
        mRecyclerView = getView().findViewById(R.id.showLocation_recyclerView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new LocationInfoPopUpWinRecycleViewAdapter(mContext,locationList,teamMarkerRef,map, this);
        mRecyclerView.setAdapter(mAdapter);
        teamMarkerRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                UserMarker userMarker = snapshot.getValue(UserMarker.class);
                userMarker.id = snapshot.getKey();
                locationList.add(userMarker);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                removeLocation(locationList, snapshot.getKey());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void removeLocation(List<UserMarker> locationList, String key){
        for(int i = 0; i < locationList.size(); i++){
            UserMarker currentUserMarker = locationList.get(i);
            if(StringUtils.equals(currentUserMarker.id, key)){
                locationList.remove(i);
                return;
            }
        }
    }
    public void addLocation(double latitude, double longitude) {
        UserMarker userMarker = new UserMarker();
        userMarker.latitude = latitude;
        userMarker.longitude = longitude;
        Intent intent = new Intent(this.activity, CreateNewMarker.class);
        intent.putExtra(UserSchema.USER_DATA, user);
        intent.putExtra(UserSchema.USER_MARKER, userMarker);

        this.activity.startActivityForResult(intent,ADD_LOCATION_ACTIVITY_REQUEST_CODE);
    }

}