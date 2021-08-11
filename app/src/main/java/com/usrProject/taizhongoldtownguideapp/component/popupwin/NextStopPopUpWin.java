package com.usrProject.taizhongoldtownguideapp.component.popupwin;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.usrProject.taizhongoldtownguideapp.R;
import com.usrProject.taizhongoldtownguideapp.model.CheckIn.CheckInMarkerObject;
import com.usrProject.taizhongoldtownguideapp.schema.TaskSchema;

public class NextStopPopUpWin extends CustomPopUpWin{
    private TextView nextStopName;
    private Button nextStopButton;
    public NextStopPopUpWin(Context mContext, int xmlLayout, boolean fullWidth, Bundle bundle) {
        super(mContext, xmlLayout, fullWidth);
        CheckInMarkerObject checkInMarkerObject = (CheckInMarkerObject) bundle.getSerializable(TaskSchema.CURRENT_TASK);


        nextStopName = this.getView().findViewById(R.id.stopName);
        nextStopButton = this.getView().findViewById(R.id.nextStopButton);

        nextStopName.setText(checkInMarkerObject.markTitle);

        nextStopButton.setOnClickListener(V -> dismiss());
    }
}
