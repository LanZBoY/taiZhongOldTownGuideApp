package com.usrProject.taizhongoldtownguideapp.schema.type;

import com.usrProject.taizhongoldtownguideapp.R;

public enum MapType {
    MAP_51(R.drawable.map_51,1.0f),
    MAP_1911(R.drawable.map_1911,1.0f),
    MAP_1937(R.drawable.map_1937,1.0f),
    MAP_NOW(R.drawable.map_now,0.5f);

    public Integer currentScrollX;
    public Integer currentScrollY;

    public int resId;
    public float currentScaleFactor;
    MapType(int resId, float currentScaleFactor){
        this.resId = resId;
        this.currentScaleFactor = currentScaleFactor;
    }

}
