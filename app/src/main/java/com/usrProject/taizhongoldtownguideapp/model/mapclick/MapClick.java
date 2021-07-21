package com.usrProject.taizhongoldtownguideapp.model.mapclick;

public class MapClick {
//  地點描述
    public String desc;
//  起始點位置
    public int startX;
    public int startY;
//    結束位置
    public int endX;
    public int endY;

    public MapClick(String desc, int startX, int startY, int endX, int endY){
        this.desc = desc;
        this.startX = startX;
        this.endX = endX;
        this.startY = startY;
        this.endY = endY;
    }

    public int[] getRange(){
        return new int[]{startX, startY, endX, endY};
    }

}
