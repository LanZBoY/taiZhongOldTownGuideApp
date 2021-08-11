package com.usrProject.taizhongoldtownguideapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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

public class JoinTeam extends AppCompatActivity {
    private EditText editText;
//    private String teamID;
    private FirebaseDatabase mDatabase;
    private DatabaseReference teamRef;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_team);
        editText = (EditText)findViewById(R.id.joinTeam_editText);
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra(UserSchema.USER_DATA);
        mDatabase = FirebaseDatabase.getInstance();
        teamRef = mDatabase.getReference("team");
        SharedPreferencesManager.setUser(this, user);
    }

    public void quickJoin(View view) {

        //這裡離要檢查輸入碼對不對
        user.teamId = editText.getText().toString();

        teamRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child( user.teamId).getValue() != null){
                    user.userId = teamRef.child("userData").push().getKey();
                    user.inTeam = true;
                    user.teamType = TeamType.MULTI;
                    teamRef.child(user.teamId).child("userData").child(user.userId).setValue(user);
                    SharedPreferencesManager.setUser(JoinTeam.this, user);
                    teamRef.removeEventListener(this);
                    Intent intent = new Intent(getApplicationContext(), TeamTracker.class);
                    intent.putExtra(UserSchema.USER_DATA, user);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(),"房號不正確",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
