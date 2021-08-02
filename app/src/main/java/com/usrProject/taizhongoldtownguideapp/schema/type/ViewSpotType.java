package com.usrProject.taizhongoldtownguideapp.schema.type;

import android.view.View;


import com.usrProject.taizhongoldtownguideapp.R;

public enum ViewSpotType {
    SIWEI_ST_JPANESE_COURTYARD(R.string.fivetwotwo_title,R.string.fivetwotwo_content,R.drawable.fivetwotwo),
    YSBANK_DORMITORY(R.string.fivezeroone_title,R.string.fivezeroone_content,R.drawable.fivezero_1),
    COOPERATIVE_BANK(R.string.fivezerosix_title,R.string.fivezerosix_content,R.drawable.fivezerosix),
    YSBANK_OLDBANK(R.string.fiveninenine_title,R.string.fiveninenine_content,R.drawable.fiveninenine),
    XIN_CHENG_BRIDGE(R.string.fivetwofour_title,R.string.fivetwofour_content,R.drawable.fivetwofour),
    TAICHUNG_OLD_STATION(R.string.fivethreesix_title,R.string.fivethreesix_content,R.drawable.fivethreesix);
    public int titleId;
    public int descId;
    public int drawableId;

    ViewSpotType(int titleId, int descId, int drawableId){
        this.titleId = titleId;
        this.descId = descId;
        this.drawableId = drawableId;
    }

}
