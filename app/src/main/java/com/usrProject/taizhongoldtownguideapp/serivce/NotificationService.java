package com.usrProject.taizhongoldtownguideapp.serivce;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.usrProject.taizhongoldtownguideapp.R;
import com.usrProject.taizhongoldtownguideapp.activity.TeamTracker;
import com.usrProject.taizhongoldtownguideapp.model.CheckIn.CheckInMarkerObject;
import com.usrProject.taizhongoldtownguideapp.model.CheckIn.CurrentTaskProcess;
import com.usrProject.taizhongoldtownguideapp.schema.TaskSchema;
import com.usrProject.taizhongoldtownguideapp.schema.UserSchema;
import com.usrProject.taizhongoldtownguideapp.utils.LocationUtils;


import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends Service {
    private CurrentTaskProcess currentTaskProcess;
    private CheckInMarkerObject checkInMarkerObject;
    private Timer timer;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private NotificationBinder notificationBinder = new NotificationBinder();

    private Notification.Builder builder = null;
    public class NotificationBinder extends Binder {
        public NotificationService getService() {
            return NotificationService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (ActivityCompat.checkSelfPermission(NotificationService.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(NotificationService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(NotificationService.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Log.d(NotificationService.class.getSimpleName(), "Create NotificationService");
        SharedPreferences sharedPreferences = getSharedPreferences(UserSchema.SharedPreferences.USER_DATA, MODE_PRIVATE);
        Gson gson = new Gson();
        currentTaskProcess = gson.fromJson(sharedPreferences.getString(TaskSchema.CURRENT_TASK, null), CurrentTaskProcess.class);
        if (currentTaskProcess != null && currentTaskProcess.contents != null && !currentTaskProcess.contents.isEmpty()) {
            checkInMarkerObject = currentTaskProcess.contents.get(currentTaskProcess.currentTask);
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Intent intent = new Intent(NotificationService.this, TeamTracker.class);
            PendingIntent activity = PendingIntent.getActivity(NotificationService.this,100, intent,PendingIntent.FLAG_CANCEL_CURRENT);
            builder = new Notification.Builder(NotificationService.this, getString(R.string.ChannelID))
                    .setSmallIcon(R.drawable.location_icon)
                    .setContentTitle("到達打卡地點")
                    .setContentText(String.format("你已經到達 %s 任務地點",currentTaskProcess.contents.get(currentTaskProcess.currentTask).markTitle))
                    .setContentIntent(activity)
                    .setOnlyAlertOnce(true)
                    .setAutoCancel(true);

        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(NotificationService.class.getSimpleName(), "command");
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(NotificationService.this);

                if (ActivityCompat.checkSelfPermission(NotificationService.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(NotificationService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(NotificationService.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Task<Location> locationTask = mFusedLocationProviderClient.getLastLocation();
                locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location != null){
                            Double distance = LocationUtils.getDistance(new LatLng(location.getLatitude(),location.getLongitude()),new LatLng(checkInMarkerObject.markLatitude,checkInMarkerObject.markLongitude));
                            if(distance < 30.0f){
                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(NotificationService.this);
                                notificationManager.notify(R.string.ChannelID,builder.build());
                            }
                        }
                    }
                });
            }
        },1000,5000);
        flags = START_STICKY;
        return super.onStartCommand(intent,flags,startId);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(NotificationService.class.getSimpleName(), "OnBind");
        return notificationBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(NotificationService.class.getSimpleName(), "UnBind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(NotificationService.class.getSimpleName(), "Destroy NotificationService");
        timer.cancel();
    }
}
