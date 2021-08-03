package com.usrProject.taizhongoldtownguideapp.model.CheckIn;

import java.io.Serializable;
import java.util.List;

public class CurrentTaskProcess implements Serializable {
    public String taskTitle;

    public String taskDesc;

    public String taskImg;

    public int currentTask;

    public boolean doneFlag;

    public List<CheckInMarkerObject> contents;
}
