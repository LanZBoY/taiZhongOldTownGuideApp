package com.usrProject.taizhongoldtownguideapp.model.mapclick;


/**
 * 宣告地圖中可點擊的物件
 */
public class MapClick {
    /**
     * 地點描述
     */
    public String desc;
    /**
     * 開始 x軸
     */
//  起始點位置
    public int startX;
    /**
     * 開始 y軸
     */
    public int startY;
    /**
     * 結束 x軸
     */
//    結束位置
    public int endX;
    /**
     * 結束 y軸
     */
    public int endY;

    /**
     * 建立物件
     *
     * @param desc   the desc
     * @param startX the start x
     * @param startY the start y
     * @param endX   the end x
     * @param endY   the end y
     */
    public MapClick(String desc, int startX, int startY, int endX, int endY){
        this.desc = desc;
        this.startX = startX;
        this.endX = endX;
        this.startY = startY;
        this.endY = endY;
    }

    /**
     * Get range int [ ].
     *
     * @return the int [ ]
     */
    public int[] getRange(){
        return new int[]{startX, startY, endX, endY};
    }

}
