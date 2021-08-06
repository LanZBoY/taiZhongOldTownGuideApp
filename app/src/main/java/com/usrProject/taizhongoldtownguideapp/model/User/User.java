package com.usrProject.taizhongoldtownguideapp.model.User;

import com.usrProject.taizhongoldtownguideapp.model.CheckIn.CurrentTaskProcess;
import com.usrProject.taizhongoldtownguideapp.schema.type.TeamType;

import java.io.Serializable;

public class User implements Serializable {

    public String userId;

    public String userName;

    public int userIconPath;

    public boolean inTeam;

    public String teamId;

    public boolean isLeader;

    public TeamType teamType;

    public double latitude;

    public double longitude;
}
