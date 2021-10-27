package com.usrProject.taizhongoldtownguideapp.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.usrProject.taizhongoldtownguideapp.Loading;
import com.usrProject.taizhongoldtownguideapp.R;
import com.usrProject.taizhongoldtownguideapp.component.CheckInTasksView;
import com.usrProject.taizhongoldtownguideapp.component.CustomInfoWindowAdapter;
import com.usrProject.taizhongoldtownguideapp.component.popupwin.CheckInOnCompletePopUpWin;
import com.usrProject.taizhongoldtownguideapp.component.popupwin.CheckInPopUpWin;
import com.usrProject.taizhongoldtownguideapp.component.popupwin.LocationInfoPopUpWin;
import com.usrProject.taizhongoldtownguideapp.component.popupwin.PersonInfoPopUpWin;
import com.usrProject.taizhongoldtownguideapp.component.popupwin.SwitchLayerPopUpWin;
import com.usrProject.taizhongoldtownguideapp.model.CheckIn.CheckInMarkerObject;
import com.usrProject.taizhongoldtownguideapp.model.CheckIn.CurrentTaskProcess;
import com.usrProject.taizhongoldtownguideapp.model.User.User;
import com.usrProject.taizhongoldtownguideapp.model.User.UserMarker;
import com.usrProject.taizhongoldtownguideapp.model.service.LocationDTO;
import com.usrProject.taizhongoldtownguideapp.schema.ServiceSchema;
import com.usrProject.taizhongoldtownguideapp.schema.TaskSchema;
import com.usrProject.taizhongoldtownguideapp.schema.UserSchema;
import com.usrProject.taizhongoldtownguideapp.schema.type.MarkType;
import com.usrProject.taizhongoldtownguideapp.schema.type.PopWindowType;
import com.usrProject.taizhongoldtownguideapp.schema.type.TeamType;
import com.usrProject.taizhongoldtownguideapp.utils.LocationUtils;
import com.usrProject.taizhongoldtownguideapp.utils.SharedPreferencesManager;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The type Team tracker.
 */
public class TeamTracker extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    /**
     * The M current location.
     */
    Location mCurrentLocation = null;
    /**
     * The M fused location provider client.
     */
    FusedLocationProviderClient mFusedLocationProviderClient;
    private WindowManager.LayoutParams params;
    private DatabaseReference usersRef;
    private DatabaseReference markersRef;
    private Timer timer;

    private static final int ADD_LOCATION_ACTIVITY_REQUEST_CODE = 0;
    private Handler messageHandler = null;
    //    private String responseJsonString = "";
    HashMap<String, Marker> userMarkerMap = new HashMap<>();
    HashMap<String, Marker> customMarkerMap = new HashMap<>();
    HashMap<String, Marker> foodMarkerHashMap = new HashMap<>();
    HashMap<String, Marker> shoppingMarkerHashMap = new HashMap<>();
    HashMap<String, Marker> roomMarkerHashMap = new HashMap<>();
    HashMap<String, Marker> historyMarkerHashMap = new HashMap<>();
    HashMap<String, Marker> playMarkerHashMap = new HashMap<>();
    HashMap<String, Marker> trafficMarkerHashMap = new HashMap<>();
    HashMap<String, Marker> serviceMarkerHashMap = new HashMap<>();
    HashMap<String, Marker> religionMarkerHashMap = new HashMap<>();
    Set<String> checkedLayerSet = new HashSet<>();

    private Boolean isExiting = false;//判斷使用者是否正在退出團隊
    private CurrentTaskProcess currentTaskProcess;
    private Marker currentTaskMarker;
    private boolean isCheckPopUp = false;
    private boolean isStopped;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_tracker);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        user = (User) getIntent().getSerializableExtra(UserSchema.USER_DATA);
        currentTaskProcess = (CurrentTaskProcess) getIntent().getSerializableExtra(TaskSchema.CURRENT_TASK);
        if (user == null) {
            user = SharedPreferencesManager.getUser(this);
        }
        if (currentTaskProcess == null) {
            currentTaskProcess = SharedPreferencesManager.getCurrentTaskProcess(this);
        }

        Button personInfoButton = findViewById(R.id.whereIsMyFriend_person_btn);
        Button locationInfoButton = findViewById(R.id.whereIsMyFriend_location_btn);
        //    private final String url = "http://140.134.48.76/USR/API/API/Default/APPGetData?name=point&token=2EV7tVz0Pv6bLgB/aXRURg==";
        Button switchLayerBtn = findViewById(R.id.layer_btn);
        Button checkInRecordBtn = findViewById(R.id.checkIn_record_btn);
        Button checkInProcessButton = findViewById(R.id.checkInBotton);


        //如果是單人地圖的話，需要處理按鈕佈局
        if (user.teamType != null && user.teamType == TeamType.SINGLE) {
            personInfoButton.setBackgroundResource(R.mipmap.ic_exit);
        }
//      打卡任務列表
        checkInRecordBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), CheckInTasksView.class);
            intent.putExtra(UserSchema.USER_DATA, user);
            startActivity(intent);
        });
//      打卡任務進度確認
        if(currentTaskProcess == null){
            checkInProcessButton.setVisibility(View.INVISIBLE);
        }
        checkInProcessButton.setOnClickListener(v -> popWindow(PopWindowType.CHECK_IN_COMPLETED));
        createNotificationChannel();

        locationInfoButton.setOnClickListener(v -> popWindow(PopWindowType.LOCATION_INFO));

        personInfoButton.setOnClickListener(v -> {
            switch (user.teamType) {
                case SINGLE:
                    exitTeam();
                    break;
                case MULTI:
                    popWindow(PopWindowType.PERSON_INFO);
                    break;
            }

        });

        //預設popupwin裡的checkbox history元件是已經勾選的
        checkedLayerSet.add("history");
        //存起來，別的layout會用到
        SharedPreferencesManager.setCheckedLayer(this, checkedLayerSet);

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference teamRef = mDatabase.getReference("team").child(user.teamId);
        usersRef = teamRef.child("userData");
        markersRef = teamRef.child("marker");

        switchLayerBtn.setOnClickListener(v -> popWindow(PopWindowType.SWITCH_LAYER));
        SharedPreferencesManager.setUser(this, user);
        SharedPreferencesManager.setCurrentTaskProcess(this, currentTaskProcess);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (user == null) {
            Log.d("NULL", "USER");
        }
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        isStopped = false;
        isCheckPopUp = false;
        //固定每5秒檢查用戶坐標是否有移動
        timer.schedule(new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                checkLocationChange();
            }
        }, 1000, 5000);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isStopped = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = getString(R.string.ChannelID);
            String name = getString(R.string.ChannelName);
            String desc = getString(R.string.ChannelDesc);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(desc);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setOnMarkerClickListener(marker -> false);
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(TeamTracker.this));
        mMap.setOnInfoWindowLongClickListener(marker -> {
            //這裡可以去firebase看現在自己的房間ID是否存在，存在的話就去TeamTracker，反之去createNewUser
            Intent intent;
            if (user.inTeam) {
                intent = new Intent(getApplicationContext(), TeamTracker.class);
            } else {
                intent = new Intent(getApplicationContext(), CreateNewUser.class);
            }
            startActivity(intent);
            addLocation(marker);
        });

        messageHandler = new Handler() {
            @Override
            public void handleMessage(@NotNull Message msg) {
                if (msg.what == 1) {
                    Bundle bundle = msg.getData();
                    String json = bundle.getString(ServiceSchema.LOCATION_MARK);
                    Log.d("RawData",json);
                    JsonArray jsonArray = JsonParser.parseString(json).getAsJsonArray();
                    Gson gson = new Gson();
                    for (JsonElement jsonElement : jsonArray) {
                        Log.d("LOCATION", jsonElement.getAsString());
                        LocationDTO locationDTO = gson.fromJson(jsonElement, LocationDTO.class);
                        Marker marker = null;
                        switch (locationDTO.PO_TYPES){
                            case FOOD:
                                marker = mMap.addMarker(new MarkerOptions().position(new LatLng(locationDTO.PO_Y, locationDTO.PO_X)).title(locationDTO.PO_TITLE).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).snippet(locationDTO.PO_CONTENT));
                                assert marker != null;
                                marker.setVisible(false);
                                marker.setTag("food");
                                foodMarkerHashMap.put(locationDTO.PO_ID, marker);
                                break;
                            case SHOPPING:
                                marker = mMap.addMarker(new MarkerOptions().position(new LatLng(locationDTO.PO_Y, locationDTO.PO_X)).title(locationDTO.PO_TITLE).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).snippet(locationDTO.PO_CONTENT));
                                assert marker != null;
                                marker.setVisible(false);
                                marker.setTag("Shopping");
                                foodMarkerHashMap.put(locationDTO.PO_ID, marker);
                                break;
                            case ROOM:
                                marker = mMap.addMarker(new MarkerOptions().position(new LatLng(locationDTO.PO_Y, locationDTO.PO_X)).title(locationDTO.PO_TITLE).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)).snippet(locationDTO.PO_CONTENT));
                                assert marker != null;
                                marker.setVisible(false);
                                marker.setTag("Room");
                                foodMarkerHashMap.put(locationDTO.PO_ID, marker);
                                break;
                            case HISTORY:
                                marker = mMap.addMarker(new MarkerOptions().position(new LatLng(locationDTO.PO_Y, locationDTO.PO_X)).title(locationDTO.PO_TITLE).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).snippet(locationDTO.PO_CONTENT));
                                assert marker != null;
                                marker.setVisible(false);
                                marker.setTag("History");
                                foodMarkerHashMap.put(locationDTO.PO_ID, marker);
                                break;
                            case PLAY:
                                marker = mMap.addMarker(new MarkerOptions().position(new LatLng(locationDTO.PO_Y, locationDTO.PO_X)).title(locationDTO.PO_TITLE).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)).snippet(locationDTO.PO_CONTENT));
                                assert marker != null;
                                marker.setVisible(false);
                                marker.setTag("Play");
                                foodMarkerHashMap.put(locationDTO.PO_ID, marker);
                                break;
                            case TRAFFIC:
                                marker = mMap.addMarker(new MarkerOptions().position(new LatLng(locationDTO.PO_Y, locationDTO.PO_X)).title(locationDTO.PO_TITLE).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)).snippet(locationDTO.PO_CONTENT));
                                assert marker != null;
                                marker.setVisible(false);
                                marker.setTag("Traffic");
                                foodMarkerHashMap.put(locationDTO.PO_ID, marker);
                                break;
                            case SERVICE:
                                marker = mMap.addMarker(new MarkerOptions().position(new LatLng(locationDTO.PO_Y, locationDTO.PO_X)).title(locationDTO.PO_TITLE).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).snippet(locationDTO.PO_CONTENT));
                                assert marker != null;
                                marker.setVisible(false);
                                marker.setTag("Service");
                                foodMarkerHashMap.put(locationDTO.PO_ID, marker);
                                break;
                            case RELIGION:
                                marker = mMap.addMarker(new MarkerOptions().position(new LatLng(locationDTO.PO_Y, locationDTO.PO_X)).title(locationDTO.PO_TITLE).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)).snippet(locationDTO.PO_CONTENT));
                                assert marker != null;
                                marker.setVisible(false);
                                marker.setTag("Religion");
                                foodMarkerHashMap.put(locationDTO.PO_ID, marker);
                                break;
                        }
                    }
                }
            }
        };
        //獲得自己裝置的位置
        getDeviceLocation();
        //使用坐標資料api
        getPointJson();

        currentTaskProcess = SharedPreferencesManager.getCurrentTaskProcess(this);

        if (currentTaskProcess != null && currentTaskProcess.contents != null && !currentTaskProcess.contents.isEmpty() && !currentTaskProcess.doneFlag) {
            setTaskMark(currentTaskProcess.contents.get(currentTaskProcess.currentTask));
        }

        //每次fireBase裡朋友資料更新時，更新本地朋友資料
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Marker marker;
                for (DataSnapshot data : snapshot.getChildren()) {
                    String userName = data.child("userName").getValue(String.class);
                    Integer userIconPath = data.child("userIconPath").getValue(Integer.class);
                    String userID = data.getKey();

                    if (userName != null && userIconPath != null && userID != null) {
                        Bitmap userBitmap = new BitmapFactory().decodeResource(getResources(), userIconPath);
                        Double userLatitude = data.child("latitude").getValue(Double.class);
                        Double userLongitude = data.child("longitude").getValue(Double.class);

                        if (userLatitude != null && userLongitude != null) {
                            marker = mMap.addMarker(new MarkerOptions().position(new LatLng(userLatitude, userLongitude)).title(userName).icon(BitmapDescriptorFactory.fromBitmap(userBitmap)));
                            marker.setTag(MarkType.USER);
                            if (userMarkerMap.containsKey(userID)) {
                                Marker delMarker = userMarkerMap.get(userID);
                                assert delMarker != null;
                                delMarker.remove();
                                userMarkerMap.remove(userID);
                            }
                            userMarkerMap.put(userID, marker);
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
                        UserMarker userMarker = data.getValue(UserMarker.class);
                        assert userMarker != null;
                        Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(userMarker.latitude, userMarker.longitude)).title(userMarker.title));
                        marker.setTag(userMarker.markType);
                        if(customMarkerMap.containsKey(userMarker.title)){
                            Marker delMarker = customMarkerMap.get(userMarker.title);
                            assert delMarker != null;
                            delMarker.remove();
                            customMarkerMap.remove(userMarker.title);
                        }
                        customMarkerMap.put(userMarker.title, marker);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private void setTaskMark(CheckInMarkerObject checkTask) {
        if (checkTask.markLatitude == null || checkTask.markLongitude == null) {
            return;
        }
        if(currentTaskMarker == null){
            currentTaskMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(checkTask.markLatitude, checkTask.markLongitude)).title(checkTask.markTitle));
        }else{
            currentTaskMarker.setPosition(new LatLng(checkTask.markLatitude, checkTask.markLongitude));
        }
        currentTaskMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentTaskMarker.setTitle(checkTask.markTitle);
        currentTaskMarker.setTag(MarkType.TASK);
    }

    //等待使用者在createNewMarker頁面把增加marker的資訊返回
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == ADD_LOCATION_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                UserMarker userMarker = (UserMarker) intent.getSerializableExtra(UserSchema.USER_MARKER);
                assert userMarker != null;
                Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(userMarker.latitude, userMarker.longitude)).title(userMarker.title).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                marker.setTag(MarkType.CUSTOMIZE);
            }
        }
    }

    //獲取使用者裝置現在的位置
    private void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        final Task<Location> location = mFusedLocationProviderClient.getLastLocation();
        if (!isExiting) {
            location.addOnSuccessListener(currentLocation -> {
                Map<String, Object> userLocations = new HashMap<>();
                DatabaseReference myRef = usersRef.child(user.userId);
                mCurrentLocation = currentLocation;

                if (mCurrentLocation != null) {

                    //檢查user有沒有移動
                    userLocations.put("latitude", mCurrentLocation.getLatitude());
                    userLocations.put("longitude", mCurrentLocation.getLongitude());
                    myRef.updateChildren(userLocations);

                    //地圖addMarker時可以使用到
                    user.latitude = currentLocation.getLatitude();
                    user.longitude = currentLocation.getLongitude();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15f));

                } else {
                    //如果用戶進入app後才開啟GPS定位的話，會需要重啟location的資料才會正常
                    finish();
                    Intent intent = new Intent(getApplicationContext(), Loading.class);
                    startActivity(intent);
                }
            });
        }
    }

    //    打卡系統進入點 目前彈出視窗問題還沒解決
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void checkTaskDone(Location mCurrentLocation) {
        if (mCurrentLocation == null || currentTaskProcess == null || currentTaskProcess.contents == null || currentTaskProcess.contents.isEmpty()) {
            return;
        }
        LatLng currentPosition = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        LatLng taskPosition = new LatLng(currentTaskProcess.contents.get(currentTaskProcess.currentTask).markLatitude, currentTaskProcess.contents.get(currentTaskProcess.currentTask).markLongitude);
        double distance = LocationUtils.getDistance(currentPosition, taskPosition);
        if (distance < 50.0f && !currentTaskProcess.doneFlag) {
            if (isStopped) {
                Intent intent = getIntent();
                intent.setClass(getBaseContext(), TeamTracker.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 100, intent, PendingIntent.FLAG_ONE_SHOT);
                Notification.Builder builder = new Notification.Builder(this, getString(R.string.ChannelID))
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle("到達打卡地點")
                        .setContentText(String.format("你已經到達 %s 任務地點", currentTaskProcess.contents.get(currentTaskProcess.currentTask).markTitle))
                        .setContentIntent(pendingIntent)
                        .setOnlyAlertOnce(true)
                        .setAutoCancel(true);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.notify(R.string.ChannelID, builder.build());
//              偵測到就立刻將activity摧毀掉
                timer.cancel();
            } else {
                if (!isCheckPopUp) {
                    new AlertDialog.Builder(TeamTracker.this)
                            .setTitle("打卡提醒")
                            .setMessage(String.format("到達任務地點 %s ", currentTaskProcess.contents.get(currentTaskProcess.currentTask).markTitle))
                            .setPositiveButton("打卡", (dialogInterface, i) -> popWindow(PopWindowType.CHECK_IN_ON_COMPLETE))
                            .setOnCancelListener(dialogInterface -> {
                            })
                            .create()
                            .show();
                }
                isCheckPopUp = true;
            }
        } else {
            isCheckPopUp = false;
        }
    }

    //    打卡任務列表的東西
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void checkLocationChange() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        final Task<Location> location = mFusedLocationProviderClient.getLastLocation();

//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();

        if (!isExiting) {
            location.addOnSuccessListener(currentLocation -> {
                mCurrentLocation = currentLocation;
                if (mCurrentLocation != null) {
                    //檢查位置
                    if (user.latitude != mCurrentLocation.getLatitude() || user.longitude != mCurrentLocation.getLongitude()) {
                        user.latitude = mCurrentLocation.getLatitude();
                        user.longitude = mCurrentLocation.getLongitude();
                        Map<String, Object> updateUser = new HashMap<>();
                        updateUser.put("latitude", user.latitude);
                        updateUser.put("longitude", user.longitude);
                        //地圖addMarker時可以使用到
                        usersRef.child(user.userId).updateChildren(updateUser);
                    }
//                      檢查下個任務點距離
                    checkTaskDone(mCurrentLocation);
                }
            });
        }
    }


    //用來移動你的攝像機
    private void animateCameraAndShowInfo(LatLng latLng){
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng), new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                currentTaskMarker.showInfoWindow();
            }

            @Override
            public void onCancel() {

            }
        });

    }

    /**
     * 挑出視窗
     *
     * @param popWindowType 跳出視窗的型態
     */
//
    public void popWindow(PopWindowType popWindowType) {
        if (popWindowType == PopWindowType.LOCATION_INFO) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(UserSchema.USER_DATA, user);
            bundle.putSerializable(UserSchema.USER_MARKER, customMarkerMap);
            LocationInfoPopUpWin locationInfoPopWin = new LocationInfoPopUpWin(this, R.layout.location_info_pop_win, mMap, this, bundle);
            locationInfoPopWin.showAtLocation(findViewById(R.id.map), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            params = getWindow().getAttributes();
            params.alpha = 0.7f;
            getWindow().setAttributes(params);
            locationInfoPopWin.setOnDismissListener(() -> {
                params = getWindow().getAttributes();
                params.alpha = 1f;
                getWindow().setAttributes(params);
            });
        } else if (popWindowType == PopWindowType.PERSON_INFO) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(UserSchema.USER_DATA, user);
            PersonInfoPopUpWin personInfoPopWin = new PersonInfoPopUpWin(this, R.layout.person_info_pop_win, mMap, bundle);
            personInfoPopWin.showAtLocation(findViewById(R.id.map), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            params = getWindow().getAttributes();
            params.alpha = 0.7f;
            getWindow().setAttributes(params);
            personInfoPopWin.setOnDismissListener(() -> {
                params = getWindow().getAttributes();
                params.alpha = 1f;
                getWindow().setAttributes(params);
                if(bundle.containsKey("leavingFLag")){
                    exitTeam();
                }

            });
        } else if (popWindowType == PopWindowType.SWITCH_LAYER) {
            SwitchLayerPopUpWin switchLayerPopUpWin = new SwitchLayerPopUpWin(this, R.layout.switch_layer_pop_up_win);
            switchLayerPopUpWin.showAtLocation(findViewById(R.id.map), Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
            params = getWindow().getAttributes();
            params.alpha = 0.7f;
            getWindow().setAttributes(params);
            switchLayerPopUpWin.setOnDismissListener(() -> {
                params = getWindow().getAttributes();
                params.alpha = 1f;
                getWindow().setAttributes(params);
            });
        } else if (popWindowType == PopWindowType.CHECK_IN_COMPLETED) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(TaskSchema.CURRENT_TASK, currentTaskProcess);
            CheckInPopUpWin checkInPopUpWin = new CheckInPopUpWin(this, R.layout.check_in_completed_pop_up_win, bundle);
            checkInPopUpWin.showAtLocation(findViewById(R.id.map), Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
            params = getWindow().getAttributes();
            params.alpha = 0.7f;
            getWindow().setAttributes(params);
            checkInPopUpWin.setOnDismissListener(() -> {
                params = getWindow().getAttributes();
                params.alpha = 1f;
                getWindow().setAttributes(params);
                if(bundle.getBoolean("guideButton")){
                    CheckInMarkerObject checkInMarkerObject = currentTaskProcess.contents.get(currentTaskProcess.currentTask);
                    animateCameraAndShowInfo(new LatLng(checkInMarkerObject.markLatitude,checkInMarkerObject.markLongitude));
                }
            });
        } else if (popWindowType == PopWindowType.CHECK_IN_ON_COMPLETE) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(TaskSchema.CURRENT_TASK, currentTaskProcess);
            CheckInOnCompletePopUpWin checkInOnCompletePopUpWin = new CheckInOnCompletePopUpWin(this, R.layout.check_in_oncomplete_win, false, bundle);
            checkInOnCompletePopUpWin.showAtLocation(findViewById(R.id.map), Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
            params = getWindow().getAttributes();
            params.alpha = 0.7f;
            getWindow().setAttributes(params);
            checkInOnCompletePopUpWin.setOnDismissListener(() -> {
                params = getWindow().getAttributes();
                params.alpha = 1f;
                getWindow().setAttributes(params);
//              初始化所有狀態
//              刪除資料庫的marker資料
                markersRef.get().addOnCompleteListener((task) -> {
                    if(task.isComplete()){
                        String title = currentTaskMarker.getTitle();
                        for(DataSnapshot dataSnapshot : task.getResult().getChildren()){
                            if(StringUtils.equals(title,dataSnapshot.child("title").getValue(String.class))){
                                markersRef.child(dataSnapshot.getKey()).removeValue();
                            }
                        }
                        isCheckPopUp = false;
                        if (!currentTaskProcess.doneFlag) {
                            SharedPreferencesManager.setCurrentTaskProcess(TeamTracker.this, currentTaskProcess);
                            setTaskMark(currentTaskProcess.contents.get(currentTaskProcess.currentTask));
//                          引導
                            popWindow(PopWindowType.CHECK_IN_COMPLETED);
                        } else {
                            SharedPreferencesManager.remove(TeamTracker.this, TaskSchema.TASK_PREF, TaskSchema.CURRENT_TASK);
                            currentTaskProcess = null;
                            currentTaskMarker = null;
                            Button checkInProcessButton = findViewById(R.id.checkInBotton);
                            checkInProcessButton.setVisibility(View.INVISIBLE);
                        }
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                        notificationManager.cancel(R.string.ChannelID);
                    }
                });
            });
        }
    }

    /**
     * Exit team.
     */
    public void exitTeam() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("是否要退出團隊？");
        alert.setPositiveButton("是", (dialog, which) -> {
            isExiting = true;
            user.inTeam = false;
            usersRef.child(user.userId).removeValue();
            SharedPreferencesManager.setUser(TeamTracker.this, user);
            alert.setView(null);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra(UserSchema.USER_DATA, user);
            startActivity(intent);
            finish();
        });
        alert.setNegativeButton("否", (dialog, which) -> {
        });
        alert.create().show();
    }

    public void addLocation(Marker marker) {
        params = getWindow().getAttributes();
        params.alpha = 1f;
        getWindow().setAttributes(params);
        UserMarker userMarker = new UserMarker();
        userMarker.title = marker.getTitle();
        userMarker.latitude = marker.getPosition().latitude;
        userMarker.longitude = marker.getPosition().longitude;
        userMarker.markType = (MarkType) marker.getTag();
        Intent intent = new Intent(this, CreateNewMarker.class);
        intent.putExtra(UserSchema.USER_DATA, user);
        intent.putExtra(UserSchema.USER_MARKER, userMarker);
        startActivityForResult(intent, ADD_LOCATION_ACTIVITY_REQUEST_CODE);
    }

    void getPointJson() {
        String url = getString(R.string.USR_API);
        Log.d("USR_API", url);
//        TODO: API端仍有Bug
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder().url(url).build();
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Request request, IOException e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    Message msg = new Message();
//                    Bundle bundle = new Bundle();
//                    bundle.putString(ServiceSchema.LOCATION_MARK, response.body().string().toString());
//                    msg.setData(bundle);
//                    msg.what = 1;
//                    messageHandler.sendMessage(msg);
//                }
//            }
//        });

    }

    /**
     * Switch layer.
     *
     * @param view the view
     */
    public void switchLayer(View view) {

        boolean checked = ((CheckBox) view).isChecked();
        String boxName = "";
        switch (view.getId()) {
            case R.id.foodCheckBox:
                boxName = "food";
                for (Map.Entry<String, Marker> entry : foodMarkerHashMap.entrySet()) {
                    entry.getValue().setVisible(checked);
                }
                break;
            case R.id.shoppingCheckBox:
                boxName = "shopping";
                for (Map.Entry<String, Marker> entry : shoppingMarkerHashMap.entrySet()) {
                    entry.getValue().setVisible(checked);
                }
                break;
            case R.id.roomCheckBox:
                boxName = "room";
                for (Map.Entry<String, Marker> entry : roomMarkerHashMap.entrySet()) {
                    entry.getValue().setVisible(checked);
                }
                break;
            case R.id.historyCheckBox:
                boxName = "history";
                for (Map.Entry<String, Marker> entry : historyMarkerHashMap.entrySet()) {
                    entry.getValue().setVisible(checked);
                }
                break;
            case R.id.playCheckBox:
                boxName = "play";
                for (Map.Entry<String, Marker> entry : playMarkerHashMap.entrySet()) {
                    entry.getValue().setVisible(checked);
                }
                break;
            case R.id.trafficCheckBox:
                boxName = "traffic";
                for (Map.Entry<String, Marker> entry : trafficMarkerHashMap.entrySet()) {
                    entry.getValue().setVisible(checked);
                }
                break;
            case R.id.serviceCheckBox:
                boxName = "service";
                for (Map.Entry<String, Marker> entry : serviceMarkerHashMap.entrySet()) {
                    entry.getValue().setVisible(checked);
                }
                break;
            case R.id.religionCheckBox:
                boxName = "religion";
                for (Map.Entry<String, Marker> entry : religionMarkerHashMap.entrySet()) {
                    entry.getValue().setVisible(checked);
                }
                break;
        }
        if (checked) {
            checkedLayerSet.add(boxName);
        } else {
            checkedLayerSet.remove(boxName);
        }
        SharedPreferencesManager.setCheckedLayer(this, checkedLayerSet);
    }
}
