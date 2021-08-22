package com.usrProject.taizhongoldtownguideapp.component;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
    private DatabaseReference markersRef;
    private final LayoutInflater mInflater;
    private GoogleMap mMap;
    private LocationInfoPopUpWin locationInfoPopUpWin;
    public Context context;

    class locationListRecycleViewHolder extends RecyclerView.ViewHolder{
        public final TextView wordItemView;
        public ImageView markerIcon;
        final LocationInfoPopUpWinRecycleViewAdapter mAdapter;

        public locationListRecycleViewHolder(View itemView, LocationInfoPopUpWinRecycleViewAdapter adapter) {
            super(itemView);
            wordItemView = itemView.findViewById(R.id.location_context);
            markerIcon = itemView.findViewById(R.id.location_icon);
            this.mAdapter = adapter;
        }

    }

    public LocationInfoPopUpWinRecycleViewAdapter(Context context, List<UserMarker> locationList, DatabaseReference markersRef, GoogleMap map, LocationInfoPopUpWin locationInfoPopUpWin) {
        mInflater = LayoutInflater.from(context);
        this.mMap = map;
        this.markersRef = markersRef;
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

        holder.wordItemView.setOnClickListener(view -> markersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String currMarkerID = locationList.get(holder.getAdapterPosition()).id;
                Double mCurrentMarkerLatitude = snapshot.child(currMarkerID).child("latitude").getValue(Double.class);
                Double mCurrentMarkerLongitude = snapshot.child(currMarkerID).child("longitude").getValue(Double.class);
                locationInfoPopUpWin.dismiss();
                mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(mCurrentMarkerLatitude, mCurrentMarkerLongitude)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }));
        holder.wordItemView.setOnLongClickListener(view -> {
            new AlertDialog.Builder(this.context)
                    .setTitle("是否刪除此標記？")
                    .setNegativeButton("否", (dialogInterface, i) -> {})
                    .setPositiveButton("是",(dialogInterface, i)-> markersRef.child(locationList.get(holder.getAdapterPosition()).id).removeValue())
                    .create()
                    .show();
            return true;
        });

    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }
}
