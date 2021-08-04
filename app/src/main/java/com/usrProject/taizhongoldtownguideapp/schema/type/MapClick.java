package com.usrProject.taizhongoldtownguideapp.schema.type;


import com.usrProject.taizhongoldtownguideapp.R;

public enum MapClick {
    SIWEI_ST_JPANESE_COURTYARD(R.string.fivetwotwo_title,R.string.fivetwotwo_content,R.drawable.fivetwotwo,1435.0,1749.0,1560.0,1815.0),
    YSBANK_DORMITORY(R.string.fivezeroone_title,R.string.fivezeroone_content,R.drawable.fivezero_1,2120.0,1695.0,2257.0,1748.0),
    COOPERATIVE_BANK(R.string.fivezerosix_title,R.string.fivezerosix_content,R.drawable.fivezerosix,2230.0,1560.0,2369.0,1621.0),
    YSBANK_OLDBANK(R.string.fiveninenine_title,R.string.fiveninenine_content,R.drawable.fiveninenine,2560.0,1360.0,2683.0,1443.0),
    XIN_CHENG_BRIDGE(R.string.fivetwofour_title,R.string.fivetwofour_content,R.drawable.fivetwofour,2728.0,1582.0,2847.0,1636.0),
    TAICHUNG_OLD_STATION(R.string.fivethreesix_title,R.string.fivethreesix_content,R.drawable.fivethreesix,3295.0,1804.0,3418.0,1840.0);
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
