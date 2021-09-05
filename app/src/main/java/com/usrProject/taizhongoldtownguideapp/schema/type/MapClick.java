package com.usrProject.taizhongoldtownguideapp.schema.type;


import com.usrProject.taizhongoldtownguideapp.R;

public enum MapClick {
    SIWEI_ST_JPANESE_COURTYARD(R.string.四維街日式招待所,1435.0,1749.0,1560.0,1815.0),
    YSBANK_DORMITORY(R.string.彰化銀行繼光街宿舍,2120.0,1695.0,2257.0,1748.0),
    COOPERATIVE_BANK(R.string.合作金庫銀行臺中分行,2230.0,1560.0,2369.0,1621.0),
    YSBANK_OLDBANK(R.string.彰化銀行舊總行_株式會社彰化銀行本店,2560.0,1360.0,2683.0,1443.0),
    XIN_CHENG_BRIDGE(R.string.中山綠橋,2728.0,1582.0,2847.0,1636.0),
    TAICHUNG_OLD_STATION(R.string.臺中市後火車站,3295.0,1804.0,3418.0,1840.0);
    public int documentId;
    public Double startX,startY;
    public Double endX,endY;

    MapClick(int documentId, Double startX, Double startY, Double endX, Double endY){
        this.documentId = documentId;

        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

}
