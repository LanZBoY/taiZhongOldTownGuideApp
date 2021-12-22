package com.usrProject.taizhongoldtownguideapp.schema.type;

public enum MapAnimation {
    Train(4055f, 1500f, 115f, 2600f);

    public float startX;
    public float startY;
    public float endX;
    public float endY;
    MapAnimation(float startX, float startY, float endX, float endY){
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }
}
