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
import com.usrProject.taizhongoldtownguideapp.component.popupwin.LocationInfoPopUpWin;
import com.usrProject.taizhongoldtownguideapp.model.User.UserMarker;

import java.util.List;

public class LocationInfoPopUpWinRecycleViewAdapter extends RecyclerView.Adapter<LocationInfoPopUpWinRecycleViewAdapter.locationListRecycleViewHolder> {

    private List<UserMarker> locationList;
    private DatabaseReference teamMarkerRef;
    private final LayoutInflater mInflater;
    private GoogleMap mMap;
    private LocationInfoPopUpWin locationInfoPopUpWin;
    public Context context;

    class locationListRecycleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final TextView wordItemView;
        public ImageView markerIcon;
        final LocationInfoPopUpWinRecycleViewAdapter mAdapter;

        public locationListRecycleViewHolder(View itemView, LocationInfoPopUpWinRecycleViewAdapter adapter) {
            super(itemView);
            wordItemView = itemView.findViewById(R.id.location_context);
            markerIcon = itemView.findViewById(R.id.location_icon);
            this.mAdapter = adapter;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final int mPosition = getLayoutPosition();
            teamMarkerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String currMarkerID = locationList.get(mPosition).id;
                    Double mCurrentMarkerLatitude = snapshot.child(currMarkerID).child("latitude").getValue(Double.class);
                    Double mCurrentMarkerLongitude = snapshot.child(currMarkerID).child("longitude").getValue(Double.class);
                    locationInfoPopUpWin.dismiss();
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(mCurrentMarkerLatitude, mCurrentMarkerLongitude)));

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }

    public LocationInfoPopUpWinRecycleViewAdapter(Context context, List<UserMarker> locationList, DatabaseReference markRef, GoogleMap map, LocationInfoPopUpWin locationInfoPopUpWin) {
        mInflater = LayoutInflater.from(context);
        this.mMap = map;
        this.teamMarkerRef = markRef;
        this.locationList = locationList;
        this.context = context;
        this.locationInfoPopUpWin = locationInfoPopUpWin;
    }

    @NonNull
    @Override
    public locationListRecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.location_info_recycle_view_item, parent, false);
        return new locationListRecycleViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull final locationListRecycleViewHolder holder, final int position) {
        holder.wordItemView.setText(locationList.get(position).title);
        holder.markerIcon.setImageResource(locationList.get(position).iconId);
//        teamMarkerRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String currMarkerID = locationList.get(position);
//                String mCurrentComtext = snapshot.child(currMarkerID).child("markContext").getValue(String.class);
//                String mCurrentMarkerIconPath = snapshot.child(currMarkerID).child("markPath").getValue(String.class);
//                holder.wordItemView.setText(mCurrentComtext);
//                int imageResource = context.getResources().getIdentifier("@drawable/" + mCurrentMarkerIconPath, null, context.getPackageName());
//                holder.markerIcon.setImageResource(imageResource);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }
}
