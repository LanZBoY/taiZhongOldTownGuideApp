package com.usrProject.taizhongoldtownguideapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.usrProject.taizhongoldtownguideapp.model.CheckIn.CurrentTaskProcess;
import com.usrProject.taizhongoldtownguideapp.model.User.User;
import com.usrProject.taizhongoldtownguideapp.schema.TaskSchema;
import com.usrProject.taizhongoldtownguideapp.schema.UserSchema;

import java.util.Set;

public class SharedPreferencesManager {

    public static boolean contains(Context mContext,String sharedPreferencesKey, String key){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE);
        return sharedPreferences.contains(key);
    }

    public  static void remove(Context mContext,String sharedPreferencesKey, String key){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(key).apply();
    }

    public static void setUser(Context mContext, User user){
        if(user == null){
            return;
        }
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(UserSchema.USER_PREF, Context.MODE_PRIVATE);
        String json = new Gson().toJson(user);
        sharedPreferences.edit().putString(UserSchema.USER_DATA, json).apply();
    }

    public static User getUser(Context mContext){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(UserSchema.USER_PREF, Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(UserSchema.USER_DATA,"{}");
        User user = new Gson().fromJson(json, User.class);
        return user;
    }


    public static void setCurrentTaskProcess(Context mContext, CurrentTaskProcess currentTaskProcess){
        if(currentTaskProcess == null){
            return;
        }
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(TaskSchema.TASK_PREF, Context.MODE_PRIVATE);
        String json = new Gson().toJson(currentTaskProcess);
        sharedPreferences.edit().putString(TaskSchema.CURRENT_TASK, json).apply();
    }

    public static CurrentTaskProcess getCurrentTaskProcess(Context mContext){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(TaskSchema.TASK_PREF, Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(TaskSchema.CURRENT_TASK, null);
        CurrentTaskProcess currentTaskProcess = new Gson().fromJson(json, CurrentTaskProcess.class);
        return currentTaskProcess;
    }

    public static void setCheckedLayer(Context mContext, Set<String>checkedLayer){
        if(checkedLayer == null){
            return;
        }
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(UserSchema.USER_PREF, Context.MODE_PRIVATE);
        sharedPreferences.edit().putStringSet(UserSchema.CHECKED_LAYER, checkedLayer).apply();
    }

    public static Set<String> getCheckedLayer(Context mContext){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(UserSchema.USER_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getStringSet(UserSchema.CHECKED_LAYER,null);
    }
}
