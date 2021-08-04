package com.usrProject.taizhongoldtownguideapp.component.popupwin;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.usrProject.taizhongoldtownguideapp.GlideApp;
import com.usrProject.taizhongoldtownguideapp.R;
import com.usrProject.taizhongoldtownguideapp.model.CheckIn.CheckInMarkerObject;
import com.usrProject.taizhongoldtownguideapp.model.CheckIn.CurrentTaskProcess;
import com.usrProject.taizhongoldtownguideapp.schema.TaskSchema;
import com.usrProject.taizhongoldtownguideapp.schema.UserSchema;

import org.apache.commons.lang3.StringUtils;

public class CheckInOnCompletePopUpWin extends CustomPopUpWin{
    private CurrentTaskProcess currentTaskProcess;
    private CheckInMarkerObject checkInMarkerObject;
    private TextView checkInDesc;
    private ImageView checkInImageView;
    private Button checkInDoneButton;

    public CheckInOnCompletePopUpWin(final Context mContext, int xmlLayout, boolean fullWidth, Bundle bundle) {
        super(mContext, xmlLayout, fullWidth);
        final SharedPreferences pref = mContext.getSharedPreferences(UserSchema.SharedPreferences.USER_DATA, Context.MODE_PRIVATE);
        this.currentTaskProcess = (CurrentTaskProcess) bundle.getSerializable(TaskSchema.CURRENT_TASK);
        checkInDesc = getView().findViewById(R.id.check_in_oncomplete_desc);
        checkInDoneButton = getView().findViewById(R.id.check_in_oncomplete_done_button);
        checkInImageView = getView().findViewById(R.id.check_In_ImageView);
        if(currentTaskProcess.contents != null || !currentTaskProcess.contents.isEmpty()){
            checkInMarkerObject = currentTaskProcess.contents.get(currentTaskProcess.currentTask);
        }
        checkInDesc.setText(checkInMarkerObject.markTitle);


        loadImageFromDatabase(mContext);
        checkInDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInMarkerObject.setChecked(true);
                currentTaskProcess.currentTask += 1;
                if(currentTaskProcess.currentTask >= currentTaskProcess.contents.size()){
                    currentTaskProcess.doneFlag = true;
                    Toast.makeText(mContext, String.format("恭喜你完成 %s 任務",currentTaskProcess.taskTitle),Toast.LENGTH_LONG).show();
                }
                pref.edit().putString(TaskSchema.CURRENT_TASK, new Gson().toJson(currentTaskProcess));
                dismiss();
            }
        });

    }

    private void loadImageFromDatabase(Context mContext){
        if(checkInMarkerObject == null || StringUtils.isBlank(checkInMarkerObject.markImg)){
            return;
        }
        FirebaseStorage storage = FirebaseStorage.getInstance(mContext.getString(R.string.firestore));
        StorageReference storageReference = storage.getReference(checkInMarkerObject.markImg);
        Log.d(StorageReference.class.getSimpleName(), storageReference.getPath());
        GlideApp.with(mContext)
                .load(storageReference)
                .into(checkInImageView);
    }
}
