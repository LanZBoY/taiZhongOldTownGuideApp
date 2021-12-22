package com.usrProject.taizhongoldtownguideapp.component.imageview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ObjectView extends androidx.appcompat.widget.AppCompatImageView{
    public int currentScrollX;
    public int currentScrollY;

    public ObjectView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ObjectView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ObjectView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        currentScrollX = 0;
        currentScrollY = 0;
        scrollTo(currentScrollX, currentScrollY);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
