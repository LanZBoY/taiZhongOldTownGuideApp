package com.usrProject.taizhongoldtownguideapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.usrProject.taizhongoldtownguideapp.R;
import com.usrProject.taizhongoldtownguideapp.model.User.User;
import com.usrProject.taizhongoldtownguideapp.schema.UserSchema;
import com.usrProject.taizhongoldtownguideapp.schema.type.TeamType;
import com.usrProject.taizhongoldtownguideapp.utils.SharedPreferencesManager;

import org.apache.commons.lang3.StringUtils;

import java.util.Random;
import java.util.UUID;

public class TeamEntry extends AppCompatActivity {
    private TextView welcomeTitleTextView;
    private int userIconPath;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_entry);
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra(UserSchema.USER_DATA);
        if(user == null){
            user = SharedPreferencesManager.getUser(this);
        }
        welcomeTitleTextView = findViewById(R.id.notInTeam_textView);
        String wellcomeText = "歡迎你，" + user.userName;
        welcomeTitleTextView.setText(wellcomeText);
    }

    //如果使用者選擇創建團隊
    public void goCreateTeam(View view) {
        DatabaseReference teamRef = FirebaseDatabase.getInstance().getReference("team");

        //這裡要check teamID有沒有相撞
        user.isLeader = true;
        user.teamId = teamIDGenerator();
        //這裡在檢查有沒有重複的teamID
        teamRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                while(snapshot.child(user.teamId).getValue() != null) {
                    if (snapshot.child(user.teamId).getValue() == null){
                        Log.d("seeIsTeamIDBang","IDnobang!");
                    }
                    else {
                        Log.d("seeIsTeamIDBang","IDbang!");
                        user.teamId = teamIDGenerator();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//      跟database取的唯一直
        user.userId = teamRef.child(user.teamId).child("userData").push().getKey();
        user.teamType = TeamType.MULTI;
        user.inTeam = true;
        teamRef.child(user.teamId).child("userData").child(user.userId).setValue(user);
        Intent intent = new Intent(this, TeamTracker.class);
        intent.putExtra(UserSchema.USER_DATA, user);
        startActivity(intent);
        finish();
    }

    //如果使用者選擇參加團隊
    public void goJoinTeam(View view) {
        Intent intent = new Intent(this, JoinTeam.class);
        intent.putExtra(UserSchema.USER_DATA, user);
        startActivity(intent);
    }



    public void goSelf(View view) {
        DatabaseReference teamRef = FirebaseDatabase.getInstance().getReference("team");
        //這裡要check teamID有沒有相撞
        user.isLeader = true;
        user.teamId = teamIDGenerator();
        //這裡在檢查有沒有重複的teamID
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                while (snapshot.child(user.teamId).getValue() != null) {
                    if (snapshot.child(user.teamId).getValue() == null) {
                    } else {
                        user.teamId = teamIDGenerator();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        teamRef.addListenerForSingleValueEvent(listener);

        user.userId = teamRef.child(user.teamId).child("userData").push().getKey();
        user.teamType = TeamType.SINGLE;
        user.inTeam = true;
        teamRef.child(user.teamId).child("userData").child(user.userId).setValue(user);

        teamRef.removeEventListener(listener);

        Intent intent = new Intent(this, TeamTracker.class);
        intent.putExtra(UserSchema.USER_DATA, user);
        startActivity(intent);
        finish();
    }

    public String teamIDGenerator(){
        String uuid = StringUtils.substring(UUID.randomUUID().toString(),0,8);
        return uuid;
    }
}
