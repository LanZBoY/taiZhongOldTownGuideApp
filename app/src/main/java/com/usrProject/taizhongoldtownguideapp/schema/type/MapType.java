package com.usrProject.taizhongoldtownguideapp.schema.type;

import android.content.Context;

import com.usrProject.taizhongoldtownguideapp.R;

public enum MapType {
    MAP_51(R.drawable.map_51,1.0f),
    MAP_1911(R.drawable.map_1911,1.0f),
    MAP_1937(R.drawable.map_1937,1.0f),
    MAP_NOW(R.drawable.map_now,1.0f),
    NEW_MAP_NOW(R.drawable.new_map_now,0.5f);
    public int resId;
    public float baseScaleFactor;
    MapType(int resId, float baseScaleFactor){
        this.resId = resId;
        this.baseScaleFactor = baseScaleFactor;
    }

}
