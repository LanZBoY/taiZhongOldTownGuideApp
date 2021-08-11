package com.usrProject.taizhongoldtownguideapp.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.usrProject.taizhongoldtownguideapp.R;
import com.usrProject.taizhongoldtownguideapp.model.User.UserDTO;

import java.util.List;


public class PersonalInfoPopUpWinRecycleViewAdapter extends RecyclerView.Adapter<PersonalInfoPopUpWinRecycleViewAdapter.FriendListRecycleViewHolder> {

    private List<UserDTO> friendList;
    private DatabaseReference teamMemberRef;
    private final LayoutInflater mInflater;
    private GoogleMap mMap;
    public Context context;

    class FriendListRecycleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final TextView wordItemView;
        public ImageView userIcon;
        public ImageView isLeaderIcon;
        final PersonalInfoPopUpWinRecycleViewAdapter mAdapter;

        public FriendListRecycleViewHolder(View itemView, PersonalInfoPopUpWinRecycleViewAdapter adapter) {
            super(itemView);
            wordItemView = itemView.findViewById(R.id.user_context);
            userIcon = itemView.findViewById(R.id.user_icon);
            isLeaderIcon = itemView.findViewById(R.id.isLeaderIcon);
            this.mAdapter = adapter;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            final int mPosition = getLayoutPosition();
            teamMemberRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String currUserID = friendList.get(mPosition).userId;
                    Double mCurrentUserLatitude = snapshot.child(currUserID).child("latitude").getValue(Double.class);
                    Double mCurrentUserLongitude = snapshot.child(currUserID).child("longitude").getValue(Double.class);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentUserLatitude, mCurrentUserLongitude),15f));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public PersonalInfoPopUpWinRecycleViewAdapter(Context context, List<UserDTO> friendList, DatabaseReference teamMemberRef, GoogleMap map) {
        mInflater = LayoutInflater.from(context);
        this.mMap = map;
        this.friendList = friendList;
        this.teamMemberRef = teamMemberRef;
        this.context = context;
    }

    @NonNull
    @Override
    public FriendListRecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.person_info_recycle_view_item, parent, false);
        return new FriendListRecycleViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull final FriendListRecycleViewHolder holder, final int position) {
        holder.wordItemView.setText(friendList.get(position).userName);
        holder.userIcon.setImageResource(friendList.get(position).userIconPath);
        UserDTO user = friendList.get(position);
        if(!user.isLeader){
            holder.isLeaderIcon.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    public int getItemCount() {
        return friendList.size();
    }
}
