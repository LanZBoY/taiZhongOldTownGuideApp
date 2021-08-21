package com.usrProject.taizhongoldtownguideapp.model.User;

import com.usrProject.taizhongoldtownguideapp.schema.type.MarkType;

import java.io.Serializable;

public class UserMarker implements Serializable {
    public String id;

    public String title;

    public String context;

    public double latitude;

    public double longitude;

    public boolean setRemind;

    public int iconId;

    public MarkType markType;
}
