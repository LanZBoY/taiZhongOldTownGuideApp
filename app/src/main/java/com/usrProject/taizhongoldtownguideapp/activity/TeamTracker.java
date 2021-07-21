package com.usrProject.taizhongoldtownguideapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.usrProject.taizhongoldtownguideapp.Loading;
import com.usrProject.taizhongoldtownguideapp.R;
import com.usrProject.taizhongoldtownguideapp.component.popupwin.CheckInOnCompletePopUpWin;
import com.usrProject.taizhongoldtownguideapp.component.popupwin.CheckInPopUpWin;
import com.usrProject.taizhongoldtownguideapp.component.CustomInfoWindowAdapter;
import com.usrProject.taizhongoldtownguideapp.component.popupwin.LocationInfoPopUpWin;
import com.usrProject.taizhongoldtownguideapp.component.popupwin.PersonInfoPopUpWin;
import com.usrProject.taizhongoldtownguideapp.component.popupwin.SwitchLayerPopUpWin;
import com.usrProject.taizhongoldtownguideapp.model.CheckIn.CheckInMarkerObject;
import com.usrProject.taizhongoldtownguideapp.model.CheckIn.CurrentTaskProcess;
import com.usrProject.taizhongoldtownguideapp.schema.TaskSchema;
import com.usrProject.taizhongoldtownguideapp.schema.type.MarkTask;
import com.usrProject.taizhongoldtownguideapp.schema.type.PopWindowType;
import com.usrProject.taizhongoldtownguideapp.schema.UserSchema;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class TeamTracker extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Location mCurrentLocation = null;
    FusedLocationProviderClient mFusedLocationProviderClient;
    private WindowManager.LayoutParams params;
    private String teamID;
    private String userID;
    private FirebaseDatabase mDatabase;
    private DatabaseReference teamRef;
    private DatabaseReference usersRef;
    private DatabaseReference markersRef;
    private DatabaseReference checkInMarkerRef;
    private Timer timer;
    private SharedPreferences pref;
    private static final int ADD_LOCATION_ACTIVITY_REQUEST_CODE = 0;
    private Handler messageHandler = null;
    private String responseJsonString = "";
    HashMap<String, Marker> hashMapMarker = new HashMap<>();
    HashMap<String, Marker> foodMarkerHashMap = new HashMap<>();
    HashMap<String, Marker> shoppingMarkerHashMap = new HashMap<>();
    HashMap<String, Marker> roomMarkerHashMap = new HashMap<>();
    HashMap<String, Marker> historyMarkerHashMap = new HashMap<>();
    HashMap<String, Marker> playMarkerHashMap = new HashMap<>();
    HashMap<String, Marker> trafficMarkerHashMap = new HashMap<>();
    HashMap<String, Marker> serviceMarkerHashMap = new HashMap<>();
    HashMap<String, Marker> religionMarkerHashMap = new HashMap<>();
    private String url = "http://140.134.48.76/USR/API/API/Default/APPGetData?name=point&token=2EV7tVz0Pv6bLgB/aXRURg==";
    private Button switchLayerBtn;
    private Button checkInRecordBtn;
    private Button checkInProcessBotton;
    Set<String> checkedLayerSet = new HashSet<>();
    private String roomType;
    private Button locationInfoButton;
    private Button personInfoButton;
    private Boolean isExiting = false;//判斷使用者是否正在退出團隊
    private CurrentTaskProcess currentTaskProcess;
    private Marker currentTaskMarker;
    private boolean isCheckPopUp = false;

    private TextView checkTestDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_tracker);

//      TODO:  for testing
        checkTestDistance = findViewById(R.id.checkDistanceTextView);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        pref = getSharedPreferences(UserSchema.SharedPreferences.USER_DATA, MODE_PRIVATE);

        teamID = pref.getString("teamID", "000000");
        userID = pref.getString("userID", "null");

        personInfoButton = findViewById(R.id.whereIsMyFriend_person_btn);
        locationInfoButton = findViewById(R.id.whereIsMyFriend_location_btn);
        switchLayerBtn = findViewById(R.id.layer_btn);
        checkInRecordBtn = findViewById(R.id.checkIn_record_btn);
        checkInProcessBotton = findViewById(R.id.checkInBotton);


        //roomType 分"singleUser"和"multiUsers"用來區別是單人使用或者多人使用的地圖
        roomType = pref.getString("roomType", "multiUsers");

        //如果是單人地圖的話，需要處理按鈕佈局
        if (roomType != null) {
            if (roomType.equals("singleUser")) {
                personInfoButton.setBackgroundResource(R.drawable.exit_icon);
            }
        }
//      打卡任務列表
        checkInRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CheckInTasksView.class);
                startActivity(intent);
            }
        });
//        TODO:打卡任務進度確認
        checkInProcessBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pref.contains(MarkTask.CURRENT_TASK.key)){
//                    popWindow(PopWindowType.CHECK_IN_ON_COMPLETE);
                    popWindow(PopWindowType.CHECK_IN_COMPLETED);
                }else{
                    Toast.makeText(getApplicationContext(),"你尚未接取打卡任務，請至任務列表選擇並接取打卡任務",Toast.LENGTH_SHORT).show();
                }

            }
        });

        locationInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow(PopWindowType.LOCATION_INFO);
            }
        });

        personInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (roomType.equals("singleUser")) {
                    exitTeam();
                } else {
                    popWindow(PopWindowType.PERSON_INFO);
                }
            }
        });

        //預設popupwin裡的checkbox history元件是已經勾選的
        checkedLayerSet.add("history");
        //存起來，別的layout會用到
        pref.edit().putStringSet("checkedLayer", checkedLayerSet).apply();

        timer = new Timer();
        //固定每5秒檢查用戶坐標是否有移動
        timer.schedule(checkTask, 1000, 5000);

        mDatabase = FirebaseDatabase.getInstance();
        teamRef = mDatabase.getReference("team").child(teamID);
//        打卡系統
        checkInMarkerRef = mDatabase.getReference("checkInMarker");
        usersRef = teamRef.child("userData");
        markersRef = teamRef.child("marker");

        switchLayerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow(PopWindowType.SWITCH_LAYER);
            }
        });

    }

    @SuppressLint("HandlerLeak")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        checkPermission();
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(TeamTracker.this));
        mMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
            @Override
            public void onInfoWindowLongClick(Marker marker) {
                LatLng position = marker.getPosition();
                addLocation(position.latitude, position.longitude);
            }
        });

        messageHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    String jsonString = JsonParser.parseString(responseJsonString).getAsString();

                    try {
                        JSONArray jsonArray = new JSONArray(jsonString);
                        Double xPoint = 0.0;
                        Double yPoint = 0.0;
                        String title = "";
                        String type = "";
                        String content = "";
                        String id = "";
                        float markerColor = 0;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            xPoint = Double.parseDouble(jsonObject.get("PO_X").toString());
                            yPoint = Double.parseDouble(jsonObject.get("PO_Y").toString());
                            title = jsonObject.get("PO_TITLE").toString();
                            type = jsonObject.get("PO_TYPES").toString();
                            content = jsonObject.get("PO_CONTENT").toString();
                            id = jsonObject.get("PO_ID").toString();
                            Marker marker = null;

                            switch (Integer.parseInt(type)) {
                                case 0://美食
                                    markerColor = BitmapDescriptorFactory.HUE_AZURE;
                                    marker = mMap.addMarker(new MarkerOptions().position(new LatLng(yPoint, xPoint)).title(title).icon(BitmapDescriptorFactory.defaultMarker(markerColor)).snippet(content));
                                    marker.setVisible(false);
                                    marker.setTag("food");
                                    foodMarkerHashMap.put(id, marker);
                                    break;
                                case 1://購物
                                    markerColor = BitmapDescriptorFactory.HUE_BLUE;
                                    marker = mMap.addMarker(new MarkerOptions().position(new LatLng(yPoint, xPoint)).title(title).icon(BitmapDescriptorFactory.defaultMarker(markerColor)).snippet(content));
                                    marker.setVisible(false);
                                    marker.setTag("shopping");
                                    shoppingMarkerHashMap.put(id, marker);
                                    break;
                                case 2://住宿
                                    markerColor = BitmapDescriptorFactory.HUE_CYAN;
                                    marker = mMap.addMarker(new MarkerOptions().position(new LatLng(yPoint, xPoint)).title(title).icon(BitmapDescriptorFactory.defaultMarker(markerColor)).snippet(content));
                                    marker.setVisible(false);
                                    marker.setTag("room");
                                    roomMarkerHashMap.put(id, marker);
                                    break;
                                case 3://歷史
                                    markerColor = BitmapDescriptorFactory.HUE_RED;
                                    marker = mMap.addMarker(new MarkerOptions().position(new LatLng(yPoint, xPoint)).title(title).icon(BitmapDescriptorFactory.defaultMarker(markerColor)).snippet(content));
                                    marker.setTag("history");
                                    historyMarkerHashMap.put(id, marker);
                                    break;
                                case 4://遊憩
                                    markerColor = BitmapDescriptorFactory.HUE_MAGENTA;
                                    marker = mMap.addMarker(new MarkerOptions().position(new LatLng(yPoint, xPoint)).title(title).icon(BitmapDescriptorFactory.defaultMarker(markerColor)).snippet(content));
                                    marker.setVisible(false);
                                    marker.setTag("play");
                                    playMarkerHashMap.put(id, marker);
                                    break;
                                case 5://交通
                                    markerColor = BitmapDescriptorFactory.HUE_ORANGE;
                                    marker = mMap.addMarker(new MarkerOptions().position(new LatLng(yPoint, xPoint)).title(title).icon(BitmapDescriptorFactory.defaultMarker(markerColor)).snippet(content));
                                    marker.setVisible(false);
                                    marker.setTag("traffic");
                                    trafficMarkerHashMap.put(id, marker);
                                    break;
                                case 6://服務
                                    markerColor = BitmapDescriptorFactory.HUE_GREEN;
                                    marker = mMap.addMarker(new MarkerOptions().position(new LatLng(yPoint, xPoint)).title(title).icon(BitmapDescriptorFactory.defaultMarker(markerColor)).snippet(content));
                                    marker.setVisible(false);
                                    marker.setTag("service");
                                    serviceMarkerHashMap.put(id, marker);
                                    break;
                                case 7://宗教
                                    markerColor = BitmapDescriptorFactory.HUE_ROSE;
                                    marker = mMap.addMarker(new MarkerOptions().position(new LatLng(yPoint, xPoint)).title(title).icon(BitmapDescriptorFactory.defaultMarker(markerColor)).snippet(content));
                                    marker.setVisible(false);
                                    marker.setTag("religion");
                                    religionMarkerHashMap.put(id, marker);
                                    break;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("seeMarkerLoad", "markerLoadFail");
                    }
                }
                ;
            }
        };
        //獲得自己裝置的位置
        getDeviceLocation();
        //使用坐標資料api
        getPointJson(url);

        if(pref.contains(MarkTask.CURRENT_TASK.key)){
            Log.d(TaskSchema.TASK_SYSTEM, "觸發任務系統");
            if(currentTaskProcess == null){
                currentTaskProcess = new Gson().fromJson(pref.getString(MarkTask.CURRENT_TASK.key, null), CurrentTaskProcess.class);
            }
            if(currentTaskProcess.contents != null &&!currentTaskProcess.contents.isEmpty() && !currentTaskProcess.doneFlag){
                setTaskMark(currentTaskProcess.contents.get(currentTaskProcess.currentTask));
            }
        }

        //每次fireBase裡朋友資料更新時，更新本地朋友資料
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Marker marker = null;
                for (DataSnapshot data : snapshot.getChildren()) {
                    String userName = data.child("userName").getValue(String.class);
                    Integer userIconPath = data.child("userIconPath").getValue(Integer.class);
                    String userID = data.getKey();

                    if (userName != null && userIconPath != null && userID != null) {
                        Bitmap userBitmap = new BitmapFactory().decodeResource(getResources(), userIconPath);
                        Double userLatitude = data.child("userLatitude").getValue(Double.class);
                        Double userLongitude = data.child("userLongitude").getValue(Double.class);

                        if (userLatitude != null && userLongitude != null) {
                            marker = mMap.addMarker(new MarkerOptions().position(new LatLng(userLatitude, userLongitude)).title(userName).icon(BitmapDescriptorFactory.fromBitmap(userBitmap)));
                            marker.setTag("user");
                            if (hashMapMarker.containsKey(userID)) {
                                Marker delMarker = hashMapMarker.get(userID);
                                delMarker.remove();
                                hashMapMarker.remove(userID);
                            }
                            hashMapMarker.put(userID, marker);
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //每次firebase裡有marker更新時，更新本地所有marker資料
        markersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        String markContext = data.child("markContext").getValue(String.class);
                        //String userIconPath = data.child("userIconPath").getValue(String.class);
                        Double markLatitude = data.child("markLatitude").getValue(Double.class);
                        Double markLongitude = data.child("markLongitude").getValue(Double.class);

                        Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(markLatitude, markLongitude)).title(markContext));
                        marker.setTag("customize");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private void setTaskMark(CheckInMarkerObject checkTask){
        if(checkTask.markLatitude == null || checkTask.markLongitude == null){
            return;
        }
        currentTaskMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(checkTask.markLatitude,checkTask.markLongitude)).title(checkTask.markTitle));
    }

    //等待使用者在createNewMarker頁面把增加marker的資訊返回
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_LOCATION_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String returnString = data.getStringExtra("markContext");
                Double latitude = data.getDoubleExtra("latitude", 0);
                Double longitude = data.getDoubleExtra("longitude", 0);
                Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(returnString).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                marker.setTag("customize");
            }
        }
    }

    //獲取使用者裝置現在的位置
    private void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        checkPermission();
        final Task<Location> location = mFusedLocationProviderClient.getLastLocation();
        if (!isExiting) {
            location.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    Map<String, Object> userLocations = new HashMap<>();
                    DatabaseReference myRef = usersRef.child(userID);
                    mCurrentLocation = (Location) location;

                    if (mCurrentLocation != null) {

                        //檢查user有沒有移動
                        userLocations.put("userLatitude", mCurrentLocation.getLatitude());
                        userLocations.put("userLongitude", mCurrentLocation.getLongitude());

                        myRef.updateChildren(userLocations);
                        //地圖addMarker時可以使用到
                        pref.edit().putLong("mLatitude", Double.doubleToLongBits(location.getLatitude())).apply();
                        pref.edit().putLong("mLongitude", Double.doubleToLongBits(location.getLongitude())).apply();

                        moveCamera(new LatLng(location.getLatitude(), location.getLongitude()), 15f);



                    } else {
                        //如果用戶進入app後才開啟GPS定位的話，會需要重啟location的資料才會正常
                        finish();
                        Intent intent = new Intent(getApplicationContext(), Loading.class);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    private void checkPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        }
    }
//    TODO:打卡系統進入點 目前彈出視窗問題還沒解決
    private void checkTaskDone(Location mCurrentLocation) {
        if(currentTaskMarker == null){
            return;
        }
        LatLng currentPosition = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        Double distance = getDistanceMeterByLatLng(currentPosition, currentTaskMarker.getPosition());
        checkTestDistance.setText(String.valueOf(distance));
        if(distance < 15.0f && !isCheckPopUp && !currentTaskProcess.doneFlag){
            Log.d(TaskSchema.TASK_SYSTEM, "觸發任務");
            new AlertDialog.Builder(TeamTracker.this)
                    .setTitle("打卡提醒")
                    .setMessage(String.format("正在接近任務地點 %s ", currentTaskProcess.contents.get(currentTaskProcess.currentTask).markTitle))
                    .setPositiveButton("打卡", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            popWindow(PopWindowType.CHECK_IN_ON_COMPLETE);
                        }
                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            isCheckPopUp = false;
                        }
                    })
                    .create()
                    .show();
        }
        isCheckPopUp = true;
    }

    private Double getDistanceMeterByLatLng(LatLng p1, LatLng p2){
        Double EARTH_RADIUS = 6378.137;
        Double latr1 = p1.latitude * Math.PI / 180.0;
        Double latr2 = p2.latitude * Math.PI / 180.0;
        Double lngr1 = p1.longitude * Math.PI / 180.0;
        Double lngr2 = p2.longitude * Math.PI / 180.0;
        Double a = latr1 - latr2;
        Double b = lngr1 - lngr2;
        Double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(latr1) * Math.cos(latr2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS  * 1000;
        Log.i("距離",s+"公尺");
        return s;
    }

    //    TODO:打卡任務列表的東西
    private void checkLocationChange() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        checkPermission();
        final Task<Location> location = mFusedLocationProviderClient.getLastLocation();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        final float preLatitude = pref.getFloat("userLatitude",0);
        final float preLongitude = pref.getFloat("userLongitude",0);

        if(!isExiting){
            location.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    mCurrentLocation = location;
                    if(mCurrentLocation != null){
                        //檢查位置
                        if(preLatitude != (float)mCurrentLocation.getLatitude() || preLongitude != (float)mCurrentLocation.getLongitude()){
                            Map<String, Object> userLocations = new HashMap<>();
                            userLocations.put("userLatitude",mCurrentLocation.getLatitude());
                            userLocations.put("userLongitude",mCurrentLocation.getLongitude());
                            //地圖addMarker時可以使用到
                            pref.edit().putLong("mLatitude",Double.doubleToLongBits(location.getLatitude())).apply();
                            pref.edit().putLong("mLongitude",Double.doubleToLongBits(location.getLongitude())).apply();
                            usersRef.child(userID).updateChildren(userLocations);
                        }
//                      檢查下個任務點距離
                        checkTaskDone(mCurrentLocation);


                    }
                }
            });
        }
    }

    private TimerTask checkTask = new TimerTask() {
        @Override
        public void run() {
            checkLocationChange();
        }
    };

    //用來移動你的攝像機
    private void moveCamera(LatLng latLng, float zoom){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
    }

    public void popWindow(PopWindowType popWindowType) {
        if(popWindowType == PopWindowType.LOCATION_INFO){
            LocationInfoPopUpWin locationInfoPopWin = new LocationInfoPopUpWin(this, R.layout.location_info_pop_win, mMap, this);
            locationInfoPopWin.showAtLocation(findViewById(R.id.map), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
            params = getWindow().getAttributes();
            params.alpha = 0.7f;
            getWindow().setAttributes(params);
            locationInfoPopWin.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    params = getWindow().getAttributes();
                    params.alpha = 1f;
                    getWindow().setAttributes(params);
                }
            });
        } else if (popWindowType == PopWindowType.PERSON_INFO){
            PersonInfoPopUpWin personInfoPopWin = new PersonInfoPopUpWin(this, R.layout.person_info_pop_win, mMap);
            personInfoPopWin.showAtLocation(findViewById(R.id.map), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
            params = getWindow().getAttributes();
            params.alpha = 0.7f;
            getWindow().setAttributes(params);
           personInfoPopWin.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    params = getWindow().getAttributes();
                    params.alpha = 1f;
                    getWindow().setAttributes(params);
                }
            });
        } else if (popWindowType == PopWindowType.SWITCH_LAYER){
            SwitchLayerPopUpWin switchLayerPopUpWin = new SwitchLayerPopUpWin(this, R.layout.switch_layer_pop_up_win);
            switchLayerPopUpWin.showAtLocation(findViewById(R.id.map), Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
            params = getWindow().getAttributes();
            params.alpha = 0.7f;
            getWindow().setAttributes(params);
            switchLayerPopUpWin.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    params = getWindow().getAttributes();
                    params.alpha = 1f;
                    getWindow().setAttributes(params);
                }
            });
        }else if (popWindowType==PopWindowType.CHECK_IN_COMPLETED){
            CheckInPopUpWin checkInPopUpWin = new CheckInPopUpWin(this,R.layout.check_in_completed_pop_up_win);
            checkInPopUpWin.showAtLocation(findViewById(R.id.map), Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
            params = getWindow().getAttributes();
            params.alpha = 0.7f;
            getWindow().setAttributes(params);
            checkInPopUpWin.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    params = getWindow().getAttributes();
                    params.alpha = 1f;
                    getWindow().setAttributes(params);
                }
            });
        }else if(popWindowType == PopWindowType.CHECK_IN_ON_COMPLETE){
            CheckInOnCompletePopUpWin checkInOnCompletePopUpWin = new CheckInOnCompletePopUpWin(this,R.layout.check_in_oncomplete_win,false, currentTaskProcess);
            checkInOnCompletePopUpWin.showAtLocation(findViewById(R.id.map), Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
            params = getWindow().getAttributes();
            params.alpha = 0.7f;
            getWindow().setAttributes(params);
            checkInOnCompletePopUpWin.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    params = getWindow().getAttributes();
                    params.alpha = 1f;
                    getWindow().setAttributes(params);
//                  初始化所有狀態
                    currentTaskMarker.remove();
                    isCheckPopUp = false;

                    if(!currentTaskProcess.doneFlag){
                        pref.edit().putString(MarkTask.CURRENT_TASK.key, new Gson().toJson(currentTaskProcess)).apply();
                        setTaskMark(currentTaskProcess.contents.get(currentTaskProcess.currentTask));
                    }else{
//                      TODO:打卡任務完成
                        pref.edit().remove(MarkTask.CURRENT_TASK.key).apply();
                        currentTaskProcess = null;
                        currentTaskMarker = null;
                        checkTestDistance.setText("距離初始化中...");
                    }
                }
            });
        }
    }

    public void exitTeam() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("是否要退出團隊？");
        alert.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //isLeader ,roomnumber
                isExiting = true;
                pref.edit().putBoolean("inTeam",false).commit();
                usersRef.child(userID).removeValue();
                alert.setView(null);
                //TODO:這裡要去firebase刪掉相關用戶的資料，現在還沒實作
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        alert.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alert.create().show();
    }
    //給其他layout用的
    public void exitTeam(View view) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("是否要退出團隊？");
        alert.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isExiting = true;
                pref.edit().putBoolean("inTeam",false).commit();
                //這裡要去firebase刪掉相關用戶的資料
                usersRef.child(userID).removeValue();
                alert.setView(null);
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        alert.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.create().show();
    }

    public void addLocation(double latitude, double longitude) {
        //pref.edit().putFloat("Latitude",(float)mCurrentLocation.getLatitude()).putFloat("Longitude",(float)mCurrentLocation.getLongitude()).commit();
        params = getWindow().getAttributes();
        params.alpha=1f;
        getWindow().setAttributes(params);
        Intent intent = new Intent(this, CreateNewMarker.class);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        startActivityForResult(intent,ADD_LOCATION_ACTIVITY_REQUEST_CODE);
    }
    void getPointJson(String url){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Response response) throws IOException {
                if(response.isSuccessful()){
                    responseJsonString = response.body().string();
                    Message msg = new Message();
                    msg.what = 1;
                    messageHandler.sendMessage(msg);
                }
            }
        });

    }

    public void switchLayer(View view) {

        boolean checked = ((CheckBox) view).isChecked();
        String boxName = "";
        switch(view.getId()) {
            case R.id.foodCheckBox:
                boxName = "food";
                for(Map.Entry<String, Marker> entry : foodMarkerHashMap.entrySet()){ entry.getValue().setVisible(checked);}
                break;
            case R.id.shoppingCheckBox:
                boxName = "shopping";
                for(Map.Entry<String, Marker> entry : shoppingMarkerHashMap.entrySet()){ entry.getValue().setVisible(checked);}
                break;
            case R.id.roomCheckBox:
                boxName = "room";
                for(Map.Entry<String, Marker> entry : roomMarkerHashMap.entrySet()){ entry.getValue().setVisible(checked);}
                break;
            case R.id.historyCheckBox:
                boxName = "history";
                for(Map.Entry<String, Marker> entry : historyMarkerHashMap.entrySet()){ entry.getValue().setVisible(checked);}
                break;
            case R.id.playCheckBox:
                boxName = "play";
                for(Map.Entry<String, Marker> entry : playMarkerHashMap.entrySet()){ entry.getValue().setVisible(checked);}
                break;
            case R.id.trafficCheckBox:
                boxName = "traffic";
                for(Map.Entry<String, Marker> entry : trafficMarkerHashMap.entrySet()){ entry.getValue().setVisible(checked);}
                break;
            case R.id.serviceCheckBox:
                boxName = "service";
                for(Map.Entry<String, Marker> entry : serviceMarkerHashMap.entrySet()){ entry.getValue().setVisible(checked);}
                break;
            case R.id.religionCheckBox:
                boxName = "religion";
                for(Map.Entry<String, Marker> entry : religionMarkerHashMap.entrySet()){ entry.getValue().setVisible(checked);}
                break;
        }
        if(checked){
            checkedLayerSet.add(boxName);
        } else {
            checkedLayerSet.remove(boxName);
        }
        pref.edit().putStringSet("checkedLayer", checkedLayerSet).apply();

    }
}
