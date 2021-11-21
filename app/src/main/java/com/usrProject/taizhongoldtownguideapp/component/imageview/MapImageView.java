package com.usrProject.taizhongoldtownguideapp.component.imageview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.usrProject.taizhongoldtownguideapp.R;
import com.usrProject.taizhongoldtownguideapp.component.popupwin.IntroductionCustomPopUpWin;
import com.usrProject.taizhongoldtownguideapp.model.MapClickDTO;
import com.usrProject.taizhongoldtownguideapp.schema.type.MapClick;
import com.usrProject.taizhongoldtownguideapp.schema.type.MapType;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MapImageView extends androidx.appcompat.widget.AppCompatImageView{
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    private MapType currentMapType;
    private SeekBar seekBar;
    private TextView seekBarTextView;
    private ProgressBar loadProgressBar;
    private Window window;
    private WindowManager.LayoutParams params;
    private int maxHeight;
    private int maxWidth;
//    private int currentScrollX;
//    private int currentScrollY;

    public MapImageView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public MapImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MapImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    public void initSeekBar(SeekBar seekBar, TextView seekBarTextView) {
        this.seekBar = seekBar;
        this.seekBarTextView = seekBarTextView;
        this.seekBarTextView.setText(new SimpleDateFormat("yyyy").format(new Date()) + "年");
        this.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress <= 25) {
                    seekBarTextView.setText("乾隆40~51年");
                    changeImage(MapType.MAP_51);
                } else if (progress <= 50) {
                    seekBarTextView.setText("1911年");
                    changeImage(MapType.MAP_1911);
                } else if (progress <= 75) {
                    seekBarTextView.setText("1937年");
                    changeImage(MapType.MAP_1937);
                } else {
                    seekBarTextView.setText(new SimpleDateFormat("yyyy").format(new Date()) + "年");
                    changeImage(MapType.NEW_MAP_NOW);
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

    public void initProgressBar(ProgressBar loadProgressBar){
        this.loadProgressBar = loadProgressBar;
    }

    public void initWindow(Window window){
        this.window = window;
    }

    public void changeImage(MapType mapType) {
        if(mapType == null || mapType == currentMapType){
            return;
        }
        currentMapType = mapType;
        MapImageView.this.setImageResource(currentMapType.resId);
        Matrix matrix = MapImageView.this.getImageMatrix();
        matrix.setScale(currentMapType.currentScaleFactor,currentMapType.currentScaleFactor);
        MapImageView.this.setImageMatrix(matrix);
        if(currentMapType.currentScrollX == null || currentMapType.currentScrollY == null){
            currentMapType.currentScrollX = (int)(MapImageView.this.getDrawable().getIntrinsicWidth() * currentMapType.currentScaleFactor - getContext().getResources().getDisplayMetrics().widthPixels) / 2;
            currentMapType.currentScrollY = (int)(MapImageView.this.getDrawable().getIntrinsicHeight() * currentMapType.currentScaleFactor - getContext().getResources().getDisplayMetrics().heightPixels) / 2;
        }
        maxWidth = (int) (MapImageView.this.getDrawable().getIntrinsicWidth() * currentMapType.currentScaleFactor - getContext().getResources().getDisplayMetrics().widthPixels);
        maxHeight = (int) (MapImageView.this.getDrawable().getIntrinsicHeight() * currentMapType.currentScaleFactor - getContext().getResources().getDisplayMetrics().heightPixels);
        MapImageView.this.scrollTo(currentMapType.currentScrollX, currentMapType.currentScrollY);
    }

//  初始化監聽器
    private void init(Context context) {
//      手勢的監聽器
        gestureDetector = new GestureDetector(context, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                if(seekBar == null || seekBarTextView == null){
                    return false;
                }
                //設置scrollBar的animation
                Animation inAnim = new AlphaAnimation(0f, 1.0f);
                Animation outAnim = new AlphaAnimation(1.0f, 0f);
                inAnim.setDuration(500);
                outAnim.setDuration(500);
                inAnim.setFillAfter(true);
                outAnim.setFillAfter(true);

                if (e.getY() <= context.getResources().getDisplayMetrics().heightPixels * 0.7) {
                    if (seekBar.getVisibility() != View.INVISIBLE) {
                        seekBar.startAnimation(outAnim);
                        seekBarTextView.startAnimation(outAnim);
                        seekBar.setVisibility(View.INVISIBLE);
                        seekBarTextView.setVisibility(View.INVISIBLE);
                        seekBar.setEnabled(false);
                    }
                }

                if (e.getY() > context.getResources().getDisplayMetrics().heightPixels * 0.7) {
                    if (seekBar.getVisibility() != View.VISIBLE) {
                        seekBar.startAnimation(inAnim);
                        seekBarTextView.startAnimation(inAnim);
                        seekBar.setVisibility(View.VISIBLE);
                        seekBarTextView.setVisibility(View.VISIBLE);
                        seekBar.setEnabled(true);
                    }
                }
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                switch (currentMapType){
                    case MAP_51:

                        return false;
                    case MAP_1911:

                        return false;
                    case MAP_1937:

                        return false;
                    case MAP_NOW:

                        return false;
                    case NEW_MAP_NOW:
                        checkInRange(e.getX(), e.getY());
                        return true;
                }
                return false;
            }

            private void checkInRange(float xPoint, float yPoint) {
                double finalPointX = (xPoint + MapImageView.this.getScrollX()) / context.getResources().getDisplayMetrics().density;
                double finalPointY = (yPoint + MapImageView.this.getScrollY()) / context.getResources().getDisplayMetrics().density - 80;
                Log.d("onSingleTapConfirmed",String.format("(%f,%f)", finalPointX,finalPointY));
                for(MapClick mapClick : MapClick.values()){
                    if(inRange(mapClick,finalPointX,finalPointY)){
                        popWindow(mapClick);
                        break;
                    }
                }
            }

            private boolean inRange(MapClick mapClick, double x, double y){
                return (mapClick.startX * currentMapType.currentScaleFactor < x && x < mapClick.endX * currentMapType.currentScaleFactor)
                        && (mapClick.startY * currentMapType.currentScaleFactor < y && y < mapClick.endY * currentMapType.currentScaleFactor);
            }

            public void popWindow(MapClick mapClick) {
                loadProgressBar.setVisibility(View.VISIBLE);
                String documentId = context.getString(mapClick.documentId);
                CollectionReference mapClickReference = FirebaseFirestore.getInstance().collection("MapClick");
                mapClickReference.document(documentId).get().addOnCompleteListener(task -> {
                    if(task.isComplete()){
                        loadProgressBar.setVisibility(View.GONE);
                        MapClickDTO result = task.getResult().toObject(MapClickDTO.class);
                        assert result != null;
                        IntroductionCustomPopUpWin popUpWin = new IntroductionCustomPopUpWin(context, R.layout.introdution_custom_pop_up_win, result);
                        //设置Popupwindow显示位置（从底部弹出）
                        popUpWin.showAtLocation(findViewById(R.id.mapView), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                        params = window.getAttributes();
                        //当弹出Popupwindow时，背景变半透明
                        params.alpha = 0.7f;
                        window.setAttributes(params);

                        //设置Popupwindow关闭监听，当Popupwindow关闭，背景恢复1f
                        popUpWin.setOnDismissListener(() -> {
                            params = window.getAttributes();
                            params.alpha = 1f;
                            window.setAttributes(params);
                        });
                    }
                });
            }
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if(e1.getAction() == MotionEvent.ACTION_POINTER_DOWN || e1.getAction() == MotionEvent.ACTION_POINTER_UP){
                    return false;
                }
                int goX = (int) distanceX;
                int goY = (int) distanceY;
                currentMapType.currentScrollX = Math.max(0, Math.min(currentMapType.currentScrollX + goX, maxWidth));
                currentMapType.currentScrollY = Math.max(0, Math.min(currentMapType.currentScrollY + goY, maxHeight));
                MapImageView.this.scrollTo(currentMapType.currentScrollX, currentMapType.currentScrollY);
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });
//      縮放用的監聽器
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.OnScaleGestureListener() {
            Matrix matrix;
            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                if(currentMapType != MapType.NEW_MAP_NOW){
                    return false;
                }
                matrix = MapImageView.this.getImageMatrix();
                return true;
            }

            @Override
            public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
                float initScaleFactor = currentMapType.currentScaleFactor;
                currentMapType.currentScaleFactor *= scaleGestureDetector.getScaleFactor();
                currentMapType.currentScaleFactor = Math.max(0.5f, Math.min(currentMapType.currentScaleFactor, 1.5f));
                matrix.setScale(currentMapType.currentScaleFactor, currentMapType.currentScaleFactor);
                maxWidth = (int) (MapImageView.this.getDrawable().getBounds().width() * currentMapType.currentScaleFactor
                        - context.getResources().getDisplayMetrics().widthPixels);
                maxHeight = (int) (MapImageView.this.getDrawable().getBounds().height() * currentMapType.currentScaleFactor
                        - context.getResources().getDisplayMetrics().heightPixels);
                currentMapType.currentScrollX = Math.max(0, Math.min((int) (((currentMapType.currentScrollX + scaleGestureDetector.getFocusX()) * currentMapType.currentScaleFactor / initScaleFactor) - scaleGestureDetector.getFocusX()), maxWidth));
                currentMapType.currentScrollY = Math.max(0, Math.min((int) (((currentMapType.currentScrollY + scaleGestureDetector.getFocusY() - 80) * currentMapType.currentScaleFactor / initScaleFactor) - (scaleGestureDetector.getFocusY() - 80)), maxHeight));
                MapImageView.this.scrollTo(currentMapType.currentScrollX, currentMapType.currentScrollY);
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {

            }
        });
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Log.d(MapImageView.class.getSimpleName(), String.valueOf(event.getPointerCount()));
        gestureDetector.onTouchEvent(event);
        if (event.getPointerCount() == 2){
            scaleGestureDetector.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }
}
