package com.example.taizhongoldtownguideapp;

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
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.DisplayMetrics;
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


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {


    private static final int REQUEST_CODE = 101;
    private Button goTeamTrackerBtn;
    private Button goNewsBtn;
    private Button goSurroundingViewBtn;
    private Button navBtn;
    private GestureDetector GD;
    private ImageView imView;
    private ImageView imView2;
    private ImageView imView3;
    private ImageView imView4;
    private ImageView imView5;
    private ImageView imView6;
    private ImageView imView7;
    private SeekBar seekBar;
    private TextView seekBarTextView;
    private TextView meibianzhiyuan;
    private ArrayList<String> imgList = null;

    private WindowManager.LayoutParams params;
    private float phoneWidthPixels;
    private float phoneHeightPixels;
    private float phoneDensity;
    private int curPointX;
    private int curPointY;
    private String weather;//1：晴天，2：陰天，3：小雨天，4： 雷雨天
    private SharedPreferences pref;
    private Handler handler;
    public boolean clickFlag = true;

    //設置地圖上有效點擊範圍
    private int [][] objList={
            {303, 1045, 387, 1960},//四維街日式招待所
            {856, 1015, 916, 1062},//彰化銀行繼光街宿舍
            {906, 912, 976, 964},//合作金庫銀行
            {1172, 734, 1234, 778},//彰化銀行舊總行
            {1294, 918, 1357, 972},//中山綠橋
            {1560, 1086, 1631, 1137},//台中車站後站
    };

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = getSharedPreferences("userData",MODE_PRIVATE);

        seekBar = (SeekBar)findViewById(R.id.seekBar);
        seekBarTextView = (TextView)findViewById(R.id.yearTextView);

        Date d =  new Date();
        int currentYear = d.getYear() + 1900;
        seekBarTextView.setText(currentYear+"年");

        getWeather();
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){
                    imView2 = (ImageView)findViewById(R.id.cloundView1);
                    imView3 = (ImageView)findViewById(R.id.cloundView2);
                    imView4 = (ImageView)findViewById(R.id.cloundView3);
                    imView5 = (ImageView)findViewById(R.id.cloundView4);
                    imView6 = (ImageView)findViewById(R.id.cloundView5);
                    imView7 = (ImageView)findViewById(R.id.bgView);
                    //weather = "雨";
                    if(weather.equals("陰")){
                        String uri = "@drawable/black_clound";
                        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                        imView2.setImageResource(imageResource);
                        imView3.setImageResource(imageResource);
                        imView4.setImageResource(imageResource);
                        imView5.setImageResource(imageResource);
                        imView6.setImageResource(imageResource);
                        cloundController(imView2);
                        cloundController(imView3);
                        cloundController(imView4);
                        cloundController(imView5);
                        cloundController(imView6);
                        imView7.setVisibility(View.VISIBLE);
                    } else if(weather.equals(weather.equals("陰带雨")) || weather.equals("雨")){
                        String uri = "@drawable/rain_effect";
                        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                        imView2.setVisibility(View.INVISIBLE);
                        imView3.setVisibility(View.INVISIBLE);
                        imView4.setVisibility(View.INVISIBLE);
                        imView5.setVisibility(View.INVISIBLE);
                        imView6.setVisibility(View.INVISIBLE);
                        imView7.setVisibility(View.VISIBLE);
                        imView7.setImageResource(imageResource);
                    } else {
                        cloundController(imView2);
                        cloundController(imView3);
                        cloundController(imView4);
                        cloundController(imView5);
                        cloundController(imView6);
                        imView7.setVisibility(View.INVISIBLE);
                    }
                }
            }
        };

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);

        //獲取手機高寬密度
        phoneDensity = metric.density;
        phoneHeightPixels = metric.heightPixels ;
        phoneWidthPixels = metric.widthPixels ;

        //用以記錄現在的點
        curPointX = 0;
        curPointY = 0;

        //宣告並初始化地圖底圖
        BitmapFactory.Options options = new BitmapFactory.Options();
        BitmapFactory.decodeResource(getResources(),R.drawable.map_now,options);

        //宣告手勢
        AndroidGestureDectector androidGestureDectector = new AndroidGestureDectector();
        GD = new GestureDetector(MainActivity.this,androidGestureDectector);

        //設置滑軌監聽
        seekBarController();

        //用以存放各年份地圖名字
        imgList = new ArrayList<String>();

        //加入各年份地圖照片
        imgList.add("map_51");
        imgList.add("map_1911");
        imgList.add("map_1937");
        imgList.add("map_now");

        imView = (ImageView)this.findViewById(R.id.mapView);

        meibianzhiyuan = (TextView)this.findViewById(R.id.meibianzhiyuan_textView);

        goTeamTrackerBtn = findViewById(R.id.team_tracker_btn);
        goNewsBtn = findViewById(R.id.news_btn);
        goSurroundingViewBtn = findViewById(R.id.surrounding_view_btn);
        navBtn = findViewById(R.id.nav_btn);


        goTeamTrackerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //請求獲取位置permission
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION
                    }, REQUEST_CODE);
                }
                else{

                    final LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
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
                                Toast.makeText(MainActivity.this,"請先開啟GPS定位",Toast.LENGTH_LONG).show();

                            }
                        });
                        alert.create().show();
                    } else{
                        Boolean newUser = pref.getBoolean("inTeam",false);
                        //這裡可以去firebase看現在自己的房間ID是否存在，存在的話就去TeamTracker，反之去createNewUser
                        if(!newUser){
                            Intent intent = new Intent(getApplicationContext(), CreateNewUser.class);
                            startActivity(intent);
                        }
                        else{
                            Intent intent = new Intent(getApplicationContext(), TeamTracker.class);
                            startActivity(intent);
                        }
                    }
                }
            }
        });

        goNewsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NewsList.class);
                startActivity(intent);
            }
        });

        goSurroundingViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SurroundingView.class);
                startActivity(intent);
            }
        });

        navBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(navBtn.isSelected()){
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
        });

    }
    //彈出介紹視窗
    public void popWindow(int i) {
        IntroductionCustomPopUpWin popUpWin = new IntroductionCustomPopUpWin(this, R.layout.introdution_custom_pop_up_win, i);
        //设置Popupwindow显示位置（从底部弹出）
        popUpWin.showAtLocation(findViewById(R.id.mapView), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
        params = getWindow().getAttributes();
        //当弹出Popupwindow时，背景变半透明
        params.alpha=0.7f;
        getWindow().setAttributes(params);

        //设置Popupwindow关闭监听，当Popupwindow关闭，背景恢复1f
        popUpWin.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                params = getWindow().getAttributes();
                params.alpha=1f;
                getWindow().setAttributes(params);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE){
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
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
                        Toast.makeText(MainActivity.this,"請先開啟GPS定位",Toast.LENGTH_LONG).show();
                    }
                });
                alert.create().show();
            } else {
                //查看使用者是否已經加入團隊
                Boolean newUser = pref.getBoolean("inTeam",false);
                if(!newUser){
                    Intent intent = new Intent(this, CreateNewUser.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(this, TeamTracker.class);
                    startActivity(intent);
                }
            }
        } else {
            Toast.makeText(MainActivity.this,"獲取裝置GPS權限失敗", Toast.LENGTH_LONG).show();
        }
    }


    //手勢控制，目前只做拖移，後續還有縮放要做
    class AndroidGestureDectector implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener{

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if(clickFlag) {
                checkPointIf(e.getX()/phoneDensity,((e.getY()/phoneDensity)-80));
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
            Animation inAnim = new AlphaAnimation(0f,1.0f);
            Animation outAnim = new AlphaAnimation(1.0f,0f);
            inAnim.setDuration(500);
            outAnim.setDuration(500);
            inAnim.setFillAfter(true);
            outAnim.setFillAfter(true);

            if(e.getY() <= phoneHeightPixels * 0.7){
                if (seekBar.getVisibility() != View.INVISIBLE){
                    seekBar.startAnimation(outAnim);
                    seekBarTextView.startAnimation(outAnim);
                    seekBar.setVisibility(View.INVISIBLE);
                    seekBarTextView.setVisibility(View.INVISIBLE);
                    seekBar.setEnabled(false);
                }
            }
            if(e.getY() > phoneHeightPixels * 0.7){
                if(seekBar.getVisibility() != View.VISIBLE){
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
            int goX = (int)distanceX;
            int goY = (int)distanceY;
            imView.scrollBy(goX, goY);
            curPointX += goX;
            curPointY += goY;

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
    public boolean onTouchEvent(MotionEvent event){
        GD.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    //對雲朵進行操控
    private void cloundController(ImageView imageView){
        String idString = imageView.getResources().getResourceEntryName(imageView.getId());
        switch (idString){
            case "cloundView1":
                Animation am1 = new TranslateAnimation(1000f,-800f,0f,0f);
                am1.setDuration(55000);
                am1.setRepeatCount(-1);
                imageView.startAnimation(am1);
                break;
            case "cloundView2":
                Animation am2 = new TranslateAnimation(1800f,-800f,900f,900f);
                am2.setDuration(55000);
                am2.setRepeatCount(-1);
                am2.setStartTime(100000);
                imageView.startAnimation(am2);
                break;
            case "cloundView3":
                Animation am3 = new TranslateAnimation(1400f,-800f,200f,200f);
                am3.setDuration(50000);
                am3.setRepeatCount(-1);
                imageView.startAnimation(am3);
                break;
            case "cloundView4":
                Animation am4 = new TranslateAnimation(1500f,-800f,800f,800f);
                am4.setDuration(50000);
                am4.setRepeatCount(-1);
                imageView.startAnimation(am4);
                break;
            case "cloundView5":
                Animation am5 = new TranslateAnimation(1200f,-800f,700f,700f);
                am5.setDuration(50000);
                am5.setRepeatCount(-1);
                am5.setStartTime(100000);
                imageView.startAnimation(am5);
                break;
        }
    }

    //監控地圖上制定地點有效區用
    private void checkPointIf(float xPoint, float yPoint){
        double finalPointX = xPoint + curPointX/phoneDensity;
        double finalPointY = yPoint + curPointY/phoneDensity;
        for(int i=0; i<objList.length; i++){
            if(finalPointX > objList[i][0] && finalPointY > objList[i][1]){
                if(finalPointX < objList[i][2] && finalPointY < objList[i][3]){
                    popWindow(i);
                    break;
                }
            }
        }
    }

    //拖移bar控制
    private void seekBarController(){
        Date d = new Date();
        final int currentYear = d.getYear() + 1900;
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress<=25){
                    seekBar.setProgress(0);
                    seekBarTextView.setText("乾隆40~51年");
                    changeImage(0);
                    meibianzhiyuan.setText(R.string.meibianzhiyuan);
                    clickFlag = false;

                }else if(progress > 25 && progress <= 50){
                    seekBar.setProgress(38);
                    seekBarTextView.setText("1911年");
                    changeImage(1);
                    meibianzhiyuan.setText(R.string.meibianzhiyuan);
                    clickFlag = false;

                }else if(progress > 50 && progress <= 75 ){
                    seekBar.setProgress(63);
                    seekBarTextView.setText("1937年");
                    changeImage(2);
                    meibianzhiyuan.setText(R.string.meibianzhiyuan);
                    clickFlag = false;

                }else if(progress > 75 ){
                    seekBar.setProgress(100);
                    seekBarTextView.setText( currentYear + "年");
                    changeImage(3);
                    meibianzhiyuan.setText(R.string.laoshi_meibianzhiyuan);
                    clickFlag = true;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    //更換地圖用
    private void changeImage(int i){
        imView = (ImageView)findViewById(R.id.mapView);
        String uri = "@drawable/" + imgList.get(i);
        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
        imView.setImageResource(imageResource);
    }

    //到氣象資料開放平台拿取資料
    private void getWeather(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    String data = getJSON("https://opendata.cwb.gov.tw/api/v1/rest/datastore/O-A0003-001?Authorization=CWB-55466E79-2D5C-4102-B476-5B001C263F2A&elementName=Weather&parameterName=CITY",9000);
                    JSONObject jsonObject = new JSONObject(data);
                    JSONArray jsonArr = jsonObject.getJSONObject("records").getJSONArray("location");

                    //過濾只獲取台中的資料
                    JSONObject taizhongData = new JSONObject();
                    for(int i=0;i< jsonArr.length(); i++){
                        JSONObject oneObject = null;
                        try {
                            oneObject = jsonArr.getJSONObject(i);
                            if(oneObject.getString("locationName").equals("臺中"))
                            {
                                taizhongData = oneObject;
                                break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    weather = taizhongData.getJSONArray("weatherElement").getJSONObject(0).getString("elementValue");

                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static String getJSON(String url, int timeout) throws IOException {
        URL u = new URL(url);
        HttpURLConnection c = (HttpURLConnection) u.openConnection();
        c.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        c.setRequestProperty("Accept", "application/json");
        c.setRequestMethod("GET");
        c.setConnectTimeout(timeout);   //设置连接主机超时（单位：毫秒）
        c.setReadTimeout(timeout);      //设置从主机读取数据超时（单位：毫秒）
        //c.setRequestProperty("User-Agent","Mozilla/5.0");
        c.connect();
        int status = c.getResponseCode();

        switch (status) {
            case 200:
            case 201:
                BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream(),"utf-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                return sb.toString();
        }
        return null;
    }

}

