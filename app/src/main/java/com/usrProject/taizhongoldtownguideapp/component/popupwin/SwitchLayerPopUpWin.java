package com.usrProject.taizhongoldtownguideapp.component.popupwin;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.CheckBox;

import com.usrProject.taizhongoldtownguideapp.R;
import com.usrProject.taizhongoldtownguideapp.component.popupwin.CustomPopUpWin;
import com.usrProject.taizhongoldtownguideapp.schema.UserSchema;
import com.usrProject.taizhongoldtownguideapp.utils.SharedPreferencesManager;

import java.util.Set;


public class SwitchLayerPopUpWin extends CustomPopUpWin {

//    private SharedPreferences pref;
    Set<String> checkedLayerSet;
    CheckBox foodCheckBox;
    CheckBox shoppingCheckBox;
    CheckBox roomCheckBox;
    CheckBox historyCheckBox;
    CheckBox playCheckBox;
    CheckBox trafficCheckBox;
    CheckBox serviceCheckBox;
    CheckBox religionCheckBox;

    public SwitchLayerPopUpWin(Context mContext, int xmlLayout) {
        super(mContext, xmlLayout,true);

//        pref = mContext.getSharedPreferences(UserSchema.SharedPreferences.USER_DATA, mContext.MODE_PRIVATE);
//        checkedLayerSet = pref.getStringSet("checkedLayer",null);
        checkedLayerSet = SharedPreferencesManager.getCheckedLayer(mContext);
        foodCheckBox = getView().findViewById(R.id.foodCheckBox);
        shoppingCheckBox = getView().findViewById(R.id.shoppingCheckBox);
        roomCheckBox = getView().findViewById(R.id.roomCheckBox);
        historyCheckBox = getView().findViewById(R.id.historyCheckBox);
        playCheckBox = getView().findViewById(R.id.playCheckBox);
        trafficCheckBox = getView().findViewById(R.id.trafficCheckBox);
        serviceCheckBox = getView().findViewById(R.id.serviceCheckBox);
        religionCheckBox = getView().findViewById(R.id.religionCheckBox);

        if(checkedLayerSet.contains("food")){ foodCheckBox.setChecked(true); }
        if(checkedLayerSet.contains("shopping")){ shoppingCheckBox.setChecked(true); }
        if(checkedLayerSet.contains("room")){ roomCheckBox.setChecked(true); }
        if(checkedLayerSet.contains("history")){ historyCheckBox.setChecked(true); }
        if(checkedLayerSet.contains("play")){ playCheckBox.setChecked(true); }
        if(checkedLayerSet.contains("traffic")){ trafficCheckBox.setChecked(true); }
        if(checkedLayerSet.contains("service")){ serviceCheckBox.setChecked(true); }
        if(checkedLayerSet.contains("religion")){ religionCheckBox.setChecked(true); }

    }
}