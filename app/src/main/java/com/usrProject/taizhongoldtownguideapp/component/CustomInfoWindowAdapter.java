package com.usrProject.taizhongoldtownguideapp.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.usrProject.taizhongoldtownguideapp.R;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private View mWindow = null;
    private Context mContext;

    public CustomInfoWindowAdapter(Context context) {
        mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
    }

    private void setWindowText(Marker marker, View view){

        String title = marker.getTitle();
        TextView titleTextView = view.findViewById(R.id.info_window_title);
        if(!titleTextView.equals("")){
            titleTextView.setText(title);
        }

        String snippet = marker.getSnippet();
        TextView snippetTextView = view.findViewById(R.id.info_window_content);

        if(!snippetTextView.equals("")){
            snippetTextView.setText(snippet);
        }

        if(marker.getTag().toString().equals("customize") || marker.getTag().toString().equals("user") || marker.getTag().toString().equals("checkIn")){
            view.findViewById(R.id.info_window_tips).setVisibility(View.GONE);
        }
        else{
            view.findViewById(R.id.info_window_tips).setVisibility(View.VISIBLE);
        }


    }

    @Override
    public View getInfoWindow(Marker marker) {
        setWindowText(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        setWindowText(marker, mWindow);
        return mWindow;
    }
}
