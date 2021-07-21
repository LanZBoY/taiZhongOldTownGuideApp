package com.usrProject.taizhongoldtownguideapp.component.popupwin;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;
import com.usrProject.taizhongoldtownguideapp.R;
import com.usrProject.taizhongoldtownguideapp.model.CheckIn.CheckInMarkerObject;
import com.usrProject.taizhongoldtownguideapp.model.CheckIn.CurrentTask;
import com.usrProject.taizhongoldtownguideapp.model.CheckIn.CurrentTaskProcess;
import com.usrProject.taizhongoldtownguideapp.schema.UserSchema;
import com.usrProject.taizhongoldtownguideapp.schema.type.MarkTask;

import org.jetbrains.annotations.NotNull;

public class CheckInOnCompletePopUpWin extends CustomPopUpWin{
    private CurrentTaskProcess currentTaskProcess;
    private CheckInMarkerObject checkInMarkerObject;
    private TextView checkInDesc;
    private ImageView checkInImageView;
    private Button checkInDoneButton;

    public CheckInOnCompletePopUpWin(Context mContext, int xmlLayout, boolean fullWidth, @NotNull final CurrentTaskProcess currentTaskProcess) {
        super(mContext, xmlLayout, fullWidth);
        final SharedPreferences pref = mContext.getSharedPreferences(UserSchema.SharedPreferences.USER_DATA, Context.MODE_PRIVATE);
        this.currentTaskProcess = currentTaskProcess;
        checkInDesc = getView().findViewById(R.id.check_in_oncomplete_desc);
        checkInDoneButton = getView().findViewById(R.id.check_in_oncomplete_done_button);
        checkInImageView = getView().findViewById(R.id.check_In_ImageView);
        if(currentTaskProcess.contents != null || !currentTaskProcess.contents.isEmpty()){
            checkInMarkerObject = currentTaskProcess.contents.get(currentTaskProcess.currentTask);
        }
        checkInImageView.setVisibility(View.INVISIBLE);
        checkInDesc.setText(checkInMarkerObject.markTitle);
        checkInDoneButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                checkInMarkerObject.setChecked(true);
                currentTaskProcess.currentTask += 1;
                if(currentTaskProcess.currentTask >= currentTaskProcess.contents.size()){
                    currentTaskProcess.doneFlag = true;
                }
                pref.edit().putString(MarkTask.CURRENT_TASK.key, new Gson().toJson(currentTaskProcess));
                dismiss();
            }
        });

    }
}
