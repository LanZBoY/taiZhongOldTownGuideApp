package com.usrProject.taizhongoldtownguideapp.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
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
import com.usrProject.taizhongoldtownguideapp.component.popupwin.IntroductionCustomPopUpWin;
import com.usrProject.taizhongoldtownguideapp.model.User.User;
import com.usrProject.taizhongoldtownguideapp.schema.UserSchema;
import com.usrProject.taizhongoldtownguideapp.schema.type.MapClick;
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
    private ScaleGestureDetector SGD;
    private ImageView mapImageView;
    private ImageView backgroundImageView;
    private ArrayList<ImageView> cloudImageViews;
    private SeekBar seekBar;
    private TextView seekBarTextView;
    private TextView currentScaleTextView;
    private WindowManager.LayoutParams params;
    private float phoneWidthPixels;
    private float phoneHeightPixels;
    private float phoneDensity;
    private Handler handler;
    public boolean clickFlag = true;
    // 照片的參數設定
    private MapType currentMapType;
    //  個人資料
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = SharedPreferencesManager.getUser(this);

        initSeekBar();
        initWeather();

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        currentScaleTextView = findViewById(R.id.showCurrentScale);
        currentScaleTextView.setVisibility(View.INVISIBLE);
        //獲取手機高寬密度
        phoneDensity = metric.density;
        phoneHeightPixels = metric.heightPixels;
        phoneWidthPixels = metric.widthPixels;

        //宣告手勢
        AndroidGestureDetector androidGestureDetector = new AndroidGestureDetector();
        GD = new GestureDetector(MainActivity.this, androidGestureDetector);
        //設置滑軌監聽
        seekBarController();

        goTeamTrackerBtn = findViewById(R.id.team_tracker_btn);
        goNewsBtn = findViewById(R.id.news_btn);
        goSurroundingViewBtn = findViewById(R.id.surrounding_view_btn);
        navBtn = findViewById(R.id.nav_btn);
        mapImageView = findViewById(R.id.mapView);
        //預設是 MapType.NEW_MAP_NOW
        changeImage(MapType.NEW_MAP_NOW);
        currentScaleTextView.setText(String.format("%f%s",currentMapType.baseScaleFactor,getResources().getString(R.string.factor)));
//      縮放用的監聽器
        SGD = new ScaleGestureDetector(MainActivity.this, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
//                Log.d(currentMapType.name(), String.format("ScaleFactor = %f",currentMapType.baseScaleFactor));
                currentMapType.baseScaleFactor *= scaleGestureDetector.getScaleFactor();
                currentMapType.baseScaleFactor = Math.max(0.5f, Math.min(currentMapType.baseScaleFactor, 1.5f));
//                Log.d("baseScaleFactor",String.valueOf(currentMapType.baseScaleFactor));
                Matrix matrix = mapImageView.getImageMatrix();
                matrix.setScale(currentMapType.baseScaleFactor, currentMapType.baseScaleFactor);
//                matrix.setScale(currentMapType.baseScaleFactor, currentMapType.baseScaleFactor, mapImageView.getScrollX() + scaleGestureDetector.getFocusX(),mapImageView.getScrollY() + scaleGestureDetector.getFocusY());
//                Log.d("Focus",String.format("(X,Y)=(%f,%f)",scaleGestureDetector.getFocusX(),scaleGestureDetector.getFocusY()));
//              TODO:尚未解決縮放中心點的問題
                mapImageView.setImageMatrix(matrix);
                currentScaleTextView.setText(String.format("%f%s",currentMapType.baseScaleFactor,getResources().getString(R.string.factor)));
                int maxWidth = (int) (mapImageView.getDrawable().getBounds().width() * currentMapType.baseScaleFactor - phoneWidthPixels);
                int maxHeight = (int) (mapImageView.getDrawable().getBounds().height() * currentMapType.baseScaleFactor - phoneHeightPixels);
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
                return true;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
                if(currentMapType != MapType.NEW_MAP_NOW){
                    return false;
                }
                Animation showAnimation =  new AlphaAnimation(0f, 1.0f);
                showAnimation.setDuration(500);
                currentScaleTextView.startAnimation(showAnimation);
                currentScaleTextView.setVisibility(View.VISIBLE);
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
                Animation fadeAnimation =  new AlphaAnimation(1.0f, 0f);
                fadeAnimation.setDuration(500);
                currentScaleTextView.startAnimation(fadeAnimation);
                currentScaleTextView.setVisibility(View.INVISIBLE);
            }
        });
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
//                boolean newUser = pref.getBoolean("inTeam", false);
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
                    String weather = msg.getData().getString("WEATHER");
                    if(StringUtils.isBlank(weather)){
                        weather = new String("晴");
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
        popUpWin.setOnDismissListener(() -> {
            params = getWindow().getAttributes();
            params.alpha = 1f;
            getWindow().setAttributes(params);
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
                alert.setPositiveButton("是", (dialog, which) -> {
                    //前往開啟GPS定位
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                });
                alert.setNegativeButton("否", (dialog, which) -> Toast.makeText(MainActivity.this, "請先開啟GPS定位", Toast.LENGTH_LONG).show());
                alert.create().show();
            } else {
                //查看使用者是否已經加入團隊
//                boolean newUser = pref.getBoolean("inTeam", false);
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

    //監控地圖上制定地點有效區用
    private void checkInRange(float xPoint, float yPoint) {
        double finalPointX = (xPoint + mapImageView.getScrollX()) / phoneDensity;
        double finalPointY = (yPoint + mapImageView.getScrollY()) / phoneDensity - 80;
        Log.d("onSingleTapConfirmed",String.format("(%f,%f)", finalPointX,finalPointY));

        for(MapClick mapClick : MapClick.values()){

            if(inRange(mapClick,finalPointX,finalPointY)){
                popWindow(mapClick);
                break;
            }
        }
    }
    private boolean inRange(MapClick mapClick, double x, double y){
         return (mapClick.startX * currentMapType.baseScaleFactor < x && x < mapClick.endX * currentMapType.baseScaleFactor )
                 && (mapClick.startY * currentMapType.baseScaleFactor < y && y < mapClick.endY * currentMapType.baseScaleFactor);
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
                    clickFlag = false;
                } else if (progress <= 50) {
                    seekBarTextView.setText("1911年");
                    changeImage(MapType.MAP_1911);
                    clickFlag = false;
                } else if (progress <= 75) {
                    seekBarTextView.setText("1937年");
                    changeImage(MapType.MAP_1937);
                    clickFlag = false;
                } else {
                    seekBarTextView.setText(new SimpleDateFormat("yyyy").format(new Date()) + "年");
                    changeImage(MapType.NEW_MAP_NOW);
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
        mapImageView.setImageResource(changeType.resId);
        Matrix matrix = mapImageView.getImageMatrix();
        matrix.setScale(changeType.baseScaleFactor,changeType.baseScaleFactor);
        mapImageView.setImageMatrix(matrix);
        currentMapType = changeType;
        mapImageView.scrollTo( (int)(mapImageView.getDrawable().getIntrinsicWidth() * currentMapType.baseScaleFactor - phoneWidthPixels) / 2,(int) (mapImageView.getDrawable().getIntrinsicHeight() * currentMapType.baseScaleFactor - phoneHeightPixels) / 2);
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
//        Log.d(MotionEvent.class.getSimpleName(),event.toString());
        GD.onTouchEvent(event);
        SGD.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
    class AndroidGestureDetector implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

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
            if(e1.getAction() == MotionEvent.ACTION_POINTER_DOWN || e1.getAction() == MotionEvent.ACTION_POINTER_UP){
                return false;
            }
            int goX = (int) distanceX;
            int goY = (int) distanceY;

            mapImageView.scrollBy(goX, 0);
            mapImageView.scrollBy(0, goY);
            int maxWidth = (int) (mapImageView.getDrawable().getBounds().width() * currentMapType.baseScaleFactor - phoneWidthPixels);
            int maxHeight = (int) (mapImageView.getDrawable().getBounds().height() * currentMapType.baseScaleFactor - phoneHeightPixels);
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
//            Log.d("onScroll",String.format("Current(%d,%d)",mapImageView.getScrollX(),mapImageView.getScrollY()));
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }

}

