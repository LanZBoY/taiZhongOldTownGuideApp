package com.usrProject.taizhongoldtownguideapp.model.mapclick;


import com.usrProject.taizhongoldtownguideapp.schema.type.ViewSpotType;

/**
 * 宣告地圖中可點擊的物件
 */
public class MapClick {

    public ViewSpotType clickType;
    /**
     * 地點描述
     */
    public String desc;
    /**
     * 開始 x軸
     */
//  起始點位置
    public double startX;
    /**
     * 開始 y軸
     */
    public double startY;
    /**
     * 結束 x軸
     */
//    結束位置
    public double endX;
    /**
     * 結束 y軸
     */
    public double endY;

    /**
     * 建立物件
     *
     * @param desc   the desc
     * @param startX the start x
     * @param startY the start y
     * @param endX   the end x
     * @param endY   the end y
     */
    public MapClick(ViewSpotType clickType,String desc, double startX, double startY, double endX, double endY){
        this.clickType = clickType;
        this.desc = desc;
        this.startX = startX;
        this.endX = endX;
        this.startY = startY;
        this.endY = endY;
    }

}
