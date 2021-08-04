package com.usrProject.taizhongoldtownguideapp.model.CheckIn;

import java.io.Serializable;

public class CheckInMarkerObject implements Serializable {
    public String markTitle;
    public String markContent;
    public Double markLatitude;
    public Double markLongitude;

    public String markImg;
    private boolean checked = false;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
