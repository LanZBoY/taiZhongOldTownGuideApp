package com.usrProject.taizhongoldtownguideapp.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.usrProject.taizhongoldtownguideapp.R;
import com.usrProject.taizhongoldtownguideapp.SurroundingView;
import com.usrProject.taizhongoldtownguideapp.component.NewsList;
import com.usrProject.taizhongoldtownguideapp.component.imageview.MapImageView;
import com.usrProject.taizhongoldtownguideapp.component.imageview.ObjectView;
import com.usrProject.taizhongoldtownguideapp.model.User.User;
import com.usrProject.taizhongoldtownguideapp.schema.UserSchema;
import com.usrProject.taizhongoldtownguideapp.schema.type.MapAnimation;
import com.usrProject.taizhongoldtownguideapp.schema.type.MapType;
import com.usrProject.taizhongoldtownguideapp.utils.SharedPreferencesManager;
import com.usrProject.taizhongoldtownguideapp.utils.URLBuilder;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 101;
    private Button goTeamTrackerBtn;
    private Button goNewsBtn;
    private Button goSurroundingViewBtn;
    private Button navBtn;
    private MapImageView mapImageView;
    private ImageView backgroundImageView;
    private ArrayList<ImageView> cloudImageViews;
    private TextView currentScaleTextView;
    private Handler handler;
    // 照片的參數設定
    //  個人資料
    private User user;

    @SuppressLint({"DefaultLocale", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = SharedPreferencesManager.getUser(this);
        initWeather();
        currentScaleTextView = findViewById(R.id.showCurrentScale);
        currentScaleTextView.setVisibility(View.INVISIBLE);
        goTeamTrackerBtn = findViewById(R.id.team_tracker_btn);
        goNewsBtn = findViewById(R.id.news_btn);
        goSurroundingViewBtn = findViewById(R.id.surrounding_view_btn);
        navBtn = findViewById(R.id.nav_btn);
        mapImageView = findViewById(R.id.mapView);
        //預設是 MapType.NEW_MAP_NOW
        mapImageView.changeImage(MapType.MAP_NOW);
        mapImageView.initSeekBar(findViewById(R.id.seekBar), findViewById(R.id.yearTextView));
        mapImageView.initProgressBar(findViewById(R.id.mainActProgressBar));
        mapImageView.initWindow(getWindow());
//        trainImageView.setScaleX(4.0f);
//        trainImageView.setScaleY(4.0f);
//        trainImageView.scrollTo((int)MapAnimation.Train.startX, (int) MapAnimation.Train.startY);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferencesManager.setUser(this, user);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferencesManager.setUser(this, user);
    }

    //
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void goTeamTracker(View view) {
        //請求獲取位置permission

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        } else {

            final LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                alert.setTitle("使用此功能需要開啟GPS定位");
                alert.setMessage("是否前往開啟GPS定位？");
                alert.setPositiveButton("是", (dialog, which) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)));
                alert.setNegativeButton("否", (dialog, which) -> Toast.makeText(MainActivity.this, "請先開啟GPS定位", Toast.LENGTH_LONG).show());
                alert.create().show();
            } else {
                //這裡可以去firebase看現在自己的房間ID是否存在，存在的話就去TeamTracker，反之去createNewUser
                Intent intent;
                if (user.inTeam) {
                    intent = new Intent(getApplicationContext(), TeamTracker.class);
                } else {
                    intent = new Intent(getApplicationContext(), CreateNewUser.class);
                }
                intent.putExtra(UserSchema.USER_DATA, user);
                startActivity(intent);
            }
        }
    }

    public void goNews(View view){
        Intent intent = new Intent(getApplicationContext(), NewsList.class);
        startActivity(intent);
    }

    public void setGoSurroundingView(View view){
        Intent intent = new Intent(getApplicationContext(), SurroundingView.class);
        startActivity(intent);
    }

    public void navButtonOnClick(View view){
        if (navBtn.isSelected()) {
            Animation goTeamTrackerBtnAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);
            goTeamTrackerBtnAnim.setDuration(100);
            Animation goNewsBtnAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);
            goNewsBtnAnim.setDuration(200);
            Animation goSurroundingViewBtnAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);
            goSurroundingViewBtnAnim.setDuration(300);
            navBtn.setSelected(false);
            goTeamTrackerBtn.startAnimation(goTeamTrackerBtnAnim);
            goTeamTrackerBtn.setEnabled(false);
            goTeamTrackerBtn.setVisibility(View.INVISIBLE);
            goNewsBtn.startAnimation(goNewsBtnAnim);
            goNewsBtn.setEnabled(false);
            goNewsBtn.setVisibility(View.INVISIBLE);
            goSurroundingViewBtn.startAnimation(goSurroundingViewBtnAnim);
            goSurroundingViewBtn.setEnabled(false);
            goSurroundingViewBtn.setVisibility(View.INVISIBLE);
        } else {
            Animation goTeamTrackerBtnAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
            goTeamTrackerBtnAnim.setDuration(300);
            Animation goNewsBtnAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
            goNewsBtnAnim.setDuration(200);
            Animation goSurroundingViewBtnAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
            goSurroundingViewBtnAnim.setDuration(100);
            navBtn.setSelected(true);
            goTeamTrackerBtn.setEnabled(true);
            goTeamTrackerBtn.setVisibility(View.VISIBLE);
            goTeamTrackerBtn.startAnimation(goTeamTrackerBtnAnim);
            goNewsBtn.setEnabled(true);
            goNewsBtn.setVisibility(View.VISIBLE);
            goNewsBtn.startAnimation(goNewsBtnAnim);
            goSurroundingViewBtn.setEnabled(true);
            goSurroundingViewBtn.setVisibility(View.VISIBLE);
            goSurroundingViewBtn.startAnimation(goSurroundingViewBtnAnim);
        }
    }


    @SuppressLint("HandlerLeak")
    private void initWeather() {
        getWeather();
        //      載入圖檔
        handler = new Handler() {
            @Override
            public void handleMessage(@NotNull Message msg) {
                if (msg.what == 1) {
                    String weather = msg.getData().getString("WEATHER");
                    if(StringUtils.isBlank(weather)){
                        Log.d("WeatherAccess","no weather info.");
                        weather = "晴";
                    }
                    cloudImageViews = new ArrayList<>();
                    cloudImageViews.add(findViewById(R.id.cloudView_1));
                    cloudImageViews.add(findViewById(R.id.cloudView_2));
                    cloudImageViews.add(findViewById(R.id.cloudView_3));
                    cloudImageViews.add(findViewById(R.id.cloudView_4));
                    cloudImageViews.add(findViewById(R.id.cloudView_5));
                    backgroundImageView = findViewById(R.id.backGroundImageView);
                    if (weather.equals("陰")) {
                        String uri = "@drawable/black_clound";
                        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                        for (ImageView cloudImageView : cloudImageViews) {
                            cloudImageView.setImageResource(imageResource);
                            cloudController(cloudImageView);
                        }
                        backgroundImageView.setVisibility(View.VISIBLE);
                    } else if (weather.equals("陰带雨")|| weather.equals("雨")) {
                        for (ImageView cloudImageView : cloudImageViews) {
                            cloudImageView.setVisibility(View.INVISIBLE);
                        }
                        backgroundImageView.setVisibility(View.VISIBLE);
                        backgroundImageView.setImageResource(R.drawable.rain_effect);
                    } else {
                        for (ImageView cloudImageView : cloudImageViews) {
                            cloudController(cloudImageView);
                        }
                        backgroundImageView.setVisibility(View.INVISIBLE);
                    }
                }
            }
        };
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                alert.setTitle("使用此功能需要開啟GPS定位");
                alert.setMessage("是否前往開啟GPS定位？");
                alert.setPositiveButton("是", (dialog, which) -> {
                    //前往開啟GPS定位
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                });
                alert.setNegativeButton("否", (dialog, which) -> Toast.makeText(MainActivity.this, "請先開啟GPS定位", Toast.LENGTH_LONG).show());
                alert.create().show();
            } else {
                //查看使用者是否已經加入團隊
                Intent intent;
                if(user.inTeam) {
                    intent = new Intent(this, TeamTracker.class);
                } else {
                    intent = new Intent(this, CreateNewUser.class);
                }
                intent.putExtra(UserSchema.USER_DATA, user);
                startActivity(intent);
            }
        } else {
            Toast.makeText(MainActivity.this, "獲取裝置GPS權限失敗", Toast.LENGTH_LONG).show();
        }
    }

    //對雲朵進行操控
    @SuppressLint("NonConstantResourceId")
    private void cloudController(ImageView imageView) {
        switch (imageView.getId()) {
            case R.id.cloudView_1:
                Animation am1 = new TranslateAnimation(1000f, -800f, 0f, 0f);
                am1.setDuration(55000);
                am1.setRepeatCount(-1);
                imageView.startAnimation(am1);
                break;
            case R.id.cloudView_2:
                Animation am2 = new TranslateAnimation(1800f, -800f, 900f, 900f);
                am2.setDuration(55000);
                am2.setRepeatCount(-1);
                am2.setStartTime(100000);
                imageView.startAnimation(am2);
                break;
            case R.id.cloudView_3:
                Animation am3 = new TranslateAnimation(1400f, -800f, 200f, 200f);
                am3.setDuration(50000);
                am3.setRepeatCount(-1);
                imageView.startAnimation(am3);
                break;
            case R.id.cloudView_4:
                Animation am4 = new TranslateAnimation(1500f, -800f, 800f, 800f);
                am4.setDuration(50000);
                am4.setRepeatCount(-1);
                imageView.startAnimation(am4);
                break;
            case R.id.cloudView_5:
                Animation am5 = new TranslateAnimation(1200f, -800f, 700f, 700f);
                am5.setDuration(50000);
                am5.setRepeatCount(-1);
                am5.setStartTime(100000);
                imageView.startAnimation(am5);
                break;
        }
    }
    //到氣象資料開放平台拿取資料
    private void getWeather() {
        new Thread(() -> {
            try {
                URLBuilder builder = new URLBuilder();
                String URL = builder.getOpenDataUrl(getApplicationContext(),"CWB-55466E79-2D5C-4102-B476-5B001C263F2A","Weather","CITY","臺中");
                JsonReader jsonReader = getJsonReaderByUrl(URL);
                JsonObject jsonObject = JsonParser.parseReader(jsonReader).getAsJsonObject();
                Log.d("Json", jsonObject.toString());
                JsonArray jsonArray = jsonObject.getAsJsonObject("records").getAsJsonArray("location");

                Message msg = new Message();
                Bundle bundle = new Bundle();
                for (JsonElement member : jsonArray) {
                    JsonObject jsonLocation = member.getAsJsonObject();
                    JsonObject jsonWeather = jsonLocation.get("weatherElement").getAsJsonArray().get(0).getAsJsonObject();
                    //1：晴天，2：陰天，3：小雨天，4： 雷雨天
                    bundle.putString("WEATHER", jsonWeather.get("elementValue").getAsString());
                }
                msg.what = 1;
                msg.setData(bundle);
                handler.sendMessage(msg);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private JsonReader getJsonReaderByUrl(String urlPath) throws IOException {
        JsonReader result = null;
        URL url = new URL(urlPath);
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(9000);
            connection.setReadTimeout(9000);

            connection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (connection != null) {
            int stateCode = connection.getResponseCode();
            switch (stateCode) {
                case HttpURLConnection.HTTP_OK:
                case HttpURLConnection.HTTP_CREATED:
                    result = new JsonReader(new InputStreamReader((InputStream) connection.getContent()));
                    break;
            }
        }
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mapImageView.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}

