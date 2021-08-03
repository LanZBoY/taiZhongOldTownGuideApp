package com.usrProject.taizhongoldtownguideapp.schema.type;


import com.usrProject.taizhongoldtownguideapp.R;

public enum MapClick {
    SIWEI_ST_JPANESE_COURTYARD(R.string.fivetwotwo_title,R.string.fivetwotwo_content,R.drawable.fivetwotwo,1025.0,1886.0,1193.0,2030.0),
    YSBANK_DORMITORY(R.string.fivezeroone_title,R.string.fivezeroone_content,R.drawable.fivezero_1,2168.0,1717.0,2207.0,1470.0),
    COOPERATIVE_BANK(R.string.fivezerosix_title,R.string.fivezerosix_content,R.drawable.fivezerosix,2272.0,1582.0,2333.0,1615.0),
    YSBANK_OLDBANK(R.string.fiveninenine_title,R.string.fiveninenine_content,R.drawable.fiveninenine,2567.0,1361.0,2677.0,1438.0),
    XIN_CHENG_BRIDGE(R.string.fivetwofour_title,R.string.fivetwofour_content,R.drawable.fivetwofour,2728.0,1582.0,2847.0,1636.0),
    TAICHUNG_OLD_STATION(R.string.fivethreesix_title,R.string.fivethreesix_content,R.drawable.fivethreesix,3150.0,1683.0,3271.0,1737.0);
    public int titleId;
    public int descId;
    public int drawableId;
    public Double startX,startY;
    public Double endX,endY;

    MapClick(int titleId, int descId, int drawableId, Double startX, Double startY, Double endX, Double endY){
        this.titleId = titleId;
        this.descId = descId;
        this.drawableId = drawableId;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

}
