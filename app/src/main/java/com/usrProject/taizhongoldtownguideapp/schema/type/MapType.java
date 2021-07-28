package com.usrProject.taizhongoldtownguideapp.schema.type;

import android.content.Context;

import com.usrProject.taizhongoldtownguideapp.R;

public enum MapType {
    MAP_51(R.drawable.map_51),
    MAP_1911(R.drawable.map_1911),
    MAP_1937(R.drawable.map_1937),
    MAP_NOW(R.drawable.map_now),
    NEW_MAP_NOW(R.drawable.new_map_now);
//            {540, 507},//map_51
//            {540, 415},//map_1911
//            {540, 433},//map_1937
//            {960, 768}//map_now
    public int resId;
    public int x,y;
    MapType(int resId){
        switch (resId){
            case R.drawable.map_51:
                x = 540;
                y = 507;
                break;
            case R.drawable.map_1911:
                x = 540;
                y = 415;
                break;
            case R.drawable.map_1937:
                x = 540;
                y = 433;
                break;
            case R.drawable.map_now:
                x = 960;
                y = 768;
                break;
            case R.drawable.new_map_now:
                x = 7972;
                y = 4429;
                break;
        }
        this.resId = resId;
    }

}
