package com.usrProject.taizhongoldtownguideapp.component.popupwin;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.usrProject.taizhongoldtownguideapp.R;
import com.usrProject.taizhongoldtownguideapp.component.PersonalInfoPopUpWinRecycleViewAdapter;
import com.usrProject.taizhongoldtownguideapp.model.User.UserDTO;
import com.usrProject.taizhongoldtownguideapp.model.User.User;
import com.usrProject.taizhongoldtownguideapp.schema.UserSchema;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class PersonInfoPopUpWin extends CustomPopUpWin {

    private List<UserDTO> friendList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private PersonalInfoPopUpWinRecycleViewAdapter mAdapter;//    private SharedPreferences pref;

    private TextView inviteCodeTextView;
    private String inviteCode;
    private ImageView exitImageView ;

    private FirebaseDatabase mDatabase;
    private DatabaseReference teamMemberRef;

    //    private String teamID;
    //    private String teamName;//之後會擴充
    private User user;

    public PersonInfoPopUpWin(final Context mContext, int xmlLayout, final GoogleMap map, Bundle bundle) {
        super(mContext, xmlLayout,true);

        mDatabase = FirebaseDatabase.getInstance();
        user = (User) bundle.getSerializable(UserSchema.USER_DATA);

        teamMemberRef = mDatabase.getReference().child("team").child(user.teamId).child("userData");

        inviteCodeTextView = getView().findViewById(R.id.personInfo_inviteCode_TextView);
        inviteCode = "團隊號碼："+ user.teamId;
        inviteCodeTextView.setText(inviteCode);
        mAdapter = new PersonalInfoPopUpWinRecycleViewAdapter(mContext, friendList, teamMemberRef, map);
        mRecyclerView = getView().findViewById(R.id.showFriend_recycleView);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        teamMemberRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                UserDTO user = snapshot.getValue(UserDTO.class);
                friendList.add(user);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                UserDTO user = snapshot.getValue(UserDTO.class);
                removeFriendList(friendList, user);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        exitImageView = getView().findViewById(R.id.exitTeam_imageView);
        exitImageView.setOnClickListener(view -> {
            bundle.putBoolean("leavingFLag", true);
            dismiss();
        });
    }
    private void removeFriendList(List<UserDTO> friendList, UserDTO targetUser){
        for (int i = 0; i < friendList.size(); i++){
            UserDTO user = friendList.get(i);
            if(StringUtils.equals(user.userId, targetUser.userId)){
                friendList.remove(i);
                break;
            }
        }
    }
}