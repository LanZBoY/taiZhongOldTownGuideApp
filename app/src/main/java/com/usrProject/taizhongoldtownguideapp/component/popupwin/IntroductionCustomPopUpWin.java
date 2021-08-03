package com.usrProject.taizhongoldtownguideapp.component.popupwin;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.usrProject.taizhongoldtownguideapp.R;
import com.usrProject.taizhongoldtownguideapp.schema.type.MapClick;


public class IntroductionCustomPopUpWin extends CustomPopUpWin {

    private TextView titleTV;
    private TextView contentTextView;
    private ImageView contentImageView;

    public IntroductionCustomPopUpWin(Context mContext, int xmlLayout, MapClick mapClick) {
        super(mContext, xmlLayout,true);
        titleTV = getView().findViewById(R.id.title_TextView);
        contentTextView =  getView().findViewById(R.id.contentTextView);
        contentImageView = getView().findViewById(R.id.contentImageView);

        //這裡可能要寫成看資料有多少再去找
        titleTV.setText(mapClick.titleId);
        contentTextView.setText(mapClick.descId);
        contentImageView.setImageResource(mapClick.drawableId);
    }
}
