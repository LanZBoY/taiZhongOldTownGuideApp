package com.usrProject.taizhongoldtownguideapp.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.StringRes;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.usrProject.taizhongoldtownguideapp.R;
import com.usrProject.taizhongoldtownguideapp.schema.type.MarkType;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.internal.StringUtil;

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
        TextView snippetTextView = view.findViewById(R.id.info_window_content);
        Button longPressWindowTip = view.findViewById(R.id.info_window_tips);
        if(StringUtils.isNotBlank(title)){
            titleTextView.setText(title);
        }

        String snippet = marker.getSnippet();

        if(StringUtils.isNotBlank(snippet)){
            snippetTextView.setText(snippet);
        }
        if(marker.getTag() == MarkType.TASK){
            longPressWindowTip.setText(R.string.LongPressAddMarker);
        }
//        if(marker.getTag().toString().equals("customize") || marker.getTag().toString().equals("user") || marker.getTag().toString().equals("checkIn")){
//            view.findViewById(R.id.info_window_tips).setVisibility(View.GONE);
//        }
//        else{
//            view.findViewById(R.id.info_window_tips).setVisibility(View.VISIBLE);
//        }


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
