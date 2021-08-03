package com.usrProject.taizhongoldtownguideapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.view.GestureDetector;
import android.widget.Toast;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.usrProject.taizhongoldtownguideapp.component.popupwin.IntroductionCustomPopUpWin;
import com.usrProject.taizhongoldtownguideapp.R;
import com.usrProject.taizhongoldtownguideapp.SurroundingView;
import com.usrProject.taizhongoldtownguideapp.component.NewsList;
import com.usrProject.taizhongoldtownguideapp.schema.UserSchema;
import com.usrProject.taizhongoldtownguideapp.schema.type.MapType;
import com.usrProject.taizhongoldtownguideapp.schema.type.MapClick;
import com.usrProject.taizhongoldtownguideapp.utils.URLBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 101;
    private Button goTeamTrackerBtn;
    private Button goNewsBtn;
    private Button goSurroundingViewBtn;
    private Button navBtn;
    private GestureDetector GD;
    private ImageView mapImageView;
    private ArrayList<ImageView> cloudImageViews;
    private ImageView backgroundImageView;
    private SeekBar seekBar;
    private TextView seekBarTextView;
    private TextView meibianzhiyuan;

    private WindowManager.LayoutParams params;
    private float phoneWidthPixels;
    private float phoneHeightPixels;
    private float phoneDensity;
    private String weather;//1：晴天，2：陰天，3：小雨天，4： 雷雨天
    private SharedPreferences pref;
    private Handler handler;
    public boolean clickFlag = true;
    //記錄照片中心
    private MapType currentMapType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pref = getSharedPreferences(UserSchema.SharedPreferences.USER_DATA, MODE_PRIVATE);
        initSeekBar();
        initWeather();

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);

        //獲取手機高寬密度
        phoneDensity = metric.density;
        phoneHeightPixels = metric.heightPixels;
        phoneWidthPixels = metric.widthPixels;

        //宣告手勢
        AndroidGestureDectector androidGestureDectector = new AndroidGestureDectector();
        GD = new GestureDetector(MainActivity.this, androidGestureDectector);

        //設置滑軌監聽
        seekBarController();

        meibianzhiyuan = findViewById(R.id.meibianzhiyuan_textView);
        meibianzhiyuan.setVisibility(View.INVISIBLE);
        goTeamTrackerBtn = findViewById(R.id.team_tracker_btn);
        goNewsBtn = findViewById(R.id.news_btn);
        goSurroundingViewBtn = findViewById(R.id.surrounding_view_btn);
        navBtn = findViewById(R.id.nav_btn);

        mapImageView = findViewById(R.id.mapView);
        //預設是第四張照片
//        Bitmap initImage = ImageLoader.decodeSampledBitmapFromResource(getResources(),R.drawable.new_map_now,(int)phoneWidthPixels,(int)phoneHeightPixels);
//        mapImageView.setImageBitmap(initImage);
//        mapImageView.setImageResource(R.drawable.new_map_now);
        changeImage(MapType.NEW_MAP_NOW);
    }

//
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
                alert.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //前往開啟GPS定位
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });
                alert.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "請先開啟GPS定位", Toast.LENGTH_LONG).show();
                    }
                });
                alert.create().show();
            } else {
                boolean newUser = pref.getBoolean("inTeam", false);
                //這裡可以去firebase看現在自己的房間ID是否存在，存在的話就去TeamTracker，反之去createNewUser
                if (newUser) {
                    Intent intent = new Intent(getApplicationContext(), TeamTracker.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), CreateNewUser.class);
                    startActivity(intent);
                }
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
            navBtn.setSelected(false);
            goTeamTrackerBtn.setEnabled(false);
            goTeamTrackerBtn.setVisibility(View.INVISIBLE);
            goNewsBtn.setEnabled(false);
            goNewsBtn.setVisibility(View.INVISIBLE);
            goSurroundingViewBtn.setEnabled(false);
            goSurroundingViewBtn.setVisibility(View.INVISIBLE);
        } else {
            navBtn.setSelected(true);
            goTeamTrackerBtn.setEnabled(true);
            goTeamTrackerBtn.setVisibility(View.VISIBLE);
            goNewsBtn.setEnabled(true);
            goNewsBtn.setVisibility(View.VISIBLE);
            goSurroundingViewBtn.setEnabled(true);
            goSurroundingViewBtn.setVisibility(View.VISIBLE);
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
                    cloudImageViews = new ArrayList<>();
                    cloudImageViews.add((ImageView) findViewById(R.id.cloudView_1));
                    cloudImageViews.add((ImageView) findViewById(R.id.cloudView_2));
                    cloudImageViews.add((ImageView) findViewById(R.id.cloudView_3));
                    cloudImageViews.add((ImageView) findViewById(R.id.cloudView_4));
                    cloudImageViews.add((ImageView) findViewById(R.id.cloudView_5));
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
                        String uri = "@drawable/rain_effect";
                        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                        for (ImageView cloudImageView : cloudImageViews) {
                            cloudImageView.setVisibility(View.INVISIBLE);
                        }
                        backgroundImageView.setVisibility(View.VISIBLE);
                        backgroundImageView.setImageResource(imageResource);
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

    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    private void initSeekBar() {
        seekBar = findViewById(R.id.seekBar);
        seekBarTextView = findViewById(R.id.yearTextView);
        seekBarTextView.setText(new SimpleDateFormat("yyyy").format(new Date()) + "年");
    }


    //彈出介紹視窗
    public void popWindow(MapClick mapClick) {
        IntroductionCustomPopUpWin popUpWin = new IntroductionCustomPopUpWin(this, R.layout.introdution_custom_pop_up_win, mapClick);
        //设置Popupwindow显示位置（从底部弹出）
        popUpWin.showAtLocation(findViewById(R.id.mapView), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        params = getWindow().getAttributes();
        //当弹出Popupwindow时，背景变半透明
        params.alpha = 0.7f;
        getWindow().setAttributes(params);

        //设置Popupwindow关闭监听，当Popupwindow关闭，背景恢复1f
        popUpWin.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                params = getWindow().getAttributes();
                params.alpha = 1f;
                getWindow().setAttributes(params);
            }
        });
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
                alert.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //前往開啟GPS定位
                        //startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });
                alert.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "請先開啟GPS定位", Toast.LENGTH_LONG).show();
                    }
                });
                alert.create().show();
            } else {
                //查看使用者是否已經加入團隊
                boolean newUser = pref.getBoolean("inTeam", false);
                if(newUser) {
                    Intent intent = new Intent(this, TeamTracker.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(this, CreateNewUser.class);
                    startActivity(intent);
                }
            }
        } else {
            Toast.makeText(MainActivity.this, "獲取裝置GPS權限失敗", Toast.LENGTH_LONG).show();
        }
    }


    //TODO:手勢控制，目前只做拖移，後續還有縮放要做
    class AndroidGestureDectector implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            if (clickFlag) {
                checkInRange(e.getX() ,e.getY());
            }
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            //設置scrollBar的animation
            Animation inAnim = new AlphaAnimation(0f, 1.0f);
            Animation outAnim = new AlphaAnimation(1.0f, 0f);
            inAnim.setDuration(500);
            outAnim.setDuration(500);
            inAnim.setFillAfter(true);
            outAnim.setFillAfter(true);

            if (e.getY() <= phoneHeightPixels * 0.7) {
                if (seekBar.getVisibility() != View.INVISIBLE) {
                    seekBar.startAnimation(outAnim);
                    seekBarTextView.startAnimation(outAnim);
                    seekBar.setVisibility(View.INVISIBLE);
                    seekBarTextView.setVisibility(View.INVISIBLE);
                    seekBar.setEnabled(false);
                }
            }
            if (e.getY() > phoneHeightPixels * 0.7) {
                if (seekBar.getVisibility() != View.VISIBLE) {
                    seekBar.startAnimation(inAnim);
                    seekBarTextView.startAnimation(inAnim);
                    seekBar.setVisibility(View.VISIBLE);
                    seekBarTextView.setVisibility(View.VISIBLE);
                    seekBar.setEnabled(true);
                }
            }
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {

            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            int goX = (int) distanceX;
            int goY = (int) distanceY;

            mapImageView.scrollBy(goX, 0);
            mapImageView.scrollBy(0, goY);
            int maxWidth = (int) (mapImageView.getDrawable().getBounds().width() - phoneWidthPixels);
            int maxHeight = (int) (mapImageView.getDrawable().getBounds().height() - phoneHeightPixels);
            if(mapImageView.getScrollX() > maxWidth){
                mapImageView.scrollTo(maxWidth, mapImageView.getScrollY());
            }
            if(mapImageView.getScrollX() < 0){
                mapImageView.scrollTo(0, mapImageView.getScrollY());
            }
            if(mapImageView.getScrollY() > maxHeight){
                mapImageView.scrollTo(mapImageView.getScrollX(),maxHeight);
            }
            if(mapImageView.getScrollY() < 0){
                mapImageView.scrollTo(mapImageView.getScrollX(),0);
            }
//            Log.d(ImageView.class.getSimpleName(),String.format("Current(%d,%d)",mapImageView.getScrollX(),mapImageView.getScrollY()));
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        GD.onTouchEvent(event);
        return super.onTouchEvent(event);
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

    //監控地圖上制定地點有效區用
    private void checkInRange(float xPoint, float yPoint) {
        double finalPointX = (xPoint + mapImageView.getScrollX()) / phoneDensity;
        double finalPointY = (yPoint + mapImageView.getScrollY()) / phoneDensity - 80;
//        Log.d("onSingleTapConfirmed",String.format("(%f,%f)", finalPointX,finalPointY));
        for(MapClick mapClick : MapClick.values()){
            if(inRange(mapClick,finalPointX,finalPointY)){
                popWindow(mapClick);
                break;
            }
        }
    }
    private boolean inRange(MapClick mapClick, double x, double y){
         return (mapClick.startX < x && x < mapClick.endX ) && (mapClick.startY < y && y < mapClick.endY);
    }


    //拖移bar控制
    private void seekBarController() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress <= 25) {
                    seekBarTextView.setText("乾隆40~51年");
                    changeImage(MapType.MAP_51);
                    meibianzhiyuan.setText(R.string.meibianzhiyuan);
                    clickFlag = false;

                } else if (progress <= 50) {
                    seekBarTextView.setText("1911年");
                    changeImage(MapType.MAP_1911);
                    meibianzhiyuan.setText(R.string.meibianzhiyuan);
                    clickFlag = false;

                } else if (progress <= 75) {
                    seekBarTextView.setText("1937年");
                    changeImage(MapType.MAP_1937);
                    meibianzhiyuan.setText(R.string.meibianzhiyuan);
                    clickFlag = false;

                } else {
                    seekBarTextView.setText(new SimpleDateFormat("yyyy").format(new Date()) + "年");
                    changeImage(MapType.NEW_MAP_NOW);
                    meibianzhiyuan.setText(R.string.laoshi_meibianzhiyuan);
                    clickFlag = true;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if (progress <= 25) {
                    seekBar.setProgress(0);
                } else if (progress <= 50) {
                    seekBar.setProgress(38);
                } else if (progress <= 75) {
                    seekBar.setProgress(63);
                } else {
                    seekBar.setProgress(100);
                }
            }
        });

    }

    //更換地圖用
    private void changeImage(MapType changeType) {
        if(mapImageView == null || currentMapType == changeType){
            return;
        }
        if(changeType == MapType.NEW_MAP_NOW){
//            mapImageView.setImageBitmap(ImageLoader.decodeSampledBitmapFromResource(getResources(),changeType.resId,(int)phoneWidthPixels,(int)phoneHeightPixels));
            mapImageView.setImageResource(changeType.resId);
        }else{
            mapImageView.setImageResource(changeType.resId);
        }
        Log.d(MapType.class.getSimpleName(),String.format("%s,真實寬高(%d,%d)",changeType.name(),mapImageView.getDrawable().getIntrinsicWidth(),mapImageView.getDrawable().getIntrinsicHeight()));
        currentMapType = changeType;
        mapImageView.scrollTo( (int)(mapImageView.getDrawable().getIntrinsicWidth() - phoneWidthPixels) / 2,(int) (mapImageView.getDrawable().getIntrinsicHeight() - phoneHeightPixels) / 2);
    }

    //到氣象資料開放平台拿取資料
    private void getWeather() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    URLBuilder builder = new URLBuilder();
                    String URL = builder.getOpenDataUrl(getApplicationContext(),"CWB-55466E79-2D5C-4102-B476-5B001C263F2A","Weather","CITY","臺中");
                    JsonReader jsonReader = getJsonReaderByUrl(URL, 9000);
                    JsonObject jsonObject = JsonParser.parseReader(jsonReader).getAsJsonObject();
                    Log.d("Json", jsonObject.toString());
                    JsonArray jsonArray = jsonObject.getAsJsonObject("records").getAsJsonArray("location");
                    for (JsonElement member : jsonArray) {
                        JsonObject jsonLocation = member.getAsJsonObject();
                        JsonObject jsonWeather = jsonLocation.get("weatherElement").getAsJsonArray().get(0).getAsJsonObject();
                        weather = jsonWeather.get("elementValue").getAsString();
                    }
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private JsonReader getJsonReaderByUrl(String urlPath, int timeout) throws IOException {
        JsonReader result = null;
        URL url = new URL(urlPath);
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);

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

}

