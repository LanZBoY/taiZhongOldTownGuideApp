package com.usrProject.taizhongoldtownguideapp.component.popupwin;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.usrProject.taizhongoldtownguideapp.R;
import com.usrProject.taizhongoldtownguideapp.model.CheckIn.CheckInMarkerObject;
import com.usrProject.taizhongoldtownguideapp.model.CheckIn.CurrentTaskProcess;
import com.usrProject.taizhongoldtownguideapp.schema.TaskSchema;
import com.usrProject.taizhongoldtownguideapp.schema.UserSchema;
import com.usrProject.taizhongoldtownguideapp.utils.SharedPreferencesManager;

import static android.content.Context.MODE_PRIVATE;

import androidx.constraintlayout.widget.ConstraintLayout;


public class CheckInPopUpWin extends CustomPopUpWin {
    private CurrentTaskProcess currentTaskProcess;
    private CheckInMarkerObject currentMarker;

    private Button closeWinButton;
    private Button cancelButton;
    private Button guideButton;
    private TextView completedCountTextView;
    private TextView titleTextView;
    private ProgressBar progressBar;

    public CheckInPopUpWin(Context mContext, int xmlLayout, Bundle bundle) {
        super(mContext, xmlLayout, false);
        closeWinButton = this.getView().findViewById(R.id.check_in_record_pop_up_win_completed_close_btn);
        cancelButton = this.getView().findViewById(R.id.check_in_record_pop_up_win_cancel_button);
        guideButton = this.getView().findViewById(R.id.check_in_record_pop_up_win_guide_button);
        completedCountTextView = this.getView().findViewById(R.id.check_in_record_pop_up_win_completed_textView);
        titleTextView = this.getView().findViewById(R.id.check_in_record_pop_up_win_completed_title_textView);
        progressBar = this.getView().findViewById(R.id.check_in_record_pop_up_win_progressBar);

        currentTaskProcess = (CurrentTaskProcess) bundle.getSerializable(TaskSchema.CURRENT_TASK);
        View.OnClickListener listener;

//      透過if else決定listener行為
        if(currentTaskProcess.contents == null || currentTaskProcess.contents.isEmpty()){
            titleTextView.setText("此任務無打卡進度");
            completedCountTextView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            guideButton.setEnabled(false);
            cancelButton.setText(R.string.DoneDirectly);
            listener = view -> {
                SharedPreferencesManager.remove(mContext, TaskSchema.TASK_PREF, TaskSchema.CURRENT_TASK);
                Toast.makeText(getView().getContext(),"完成進度",Toast.LENGTH_SHORT).show();
                dismiss();
            };
        }else{
            currentMarker = currentTaskProcess.contents.get(currentTaskProcess.currentTask);
            String title = String.format(titleTextView.getText().toString(), currentMarker.markTitle);
            titleTextView.setText(title);
            completedCountTextView.setText(String.format("%d/%d",currentTaskProcess.currentTask,currentTaskProcess.contents.size()));
            Double doneProcess = Double.valueOf(currentTaskProcess.currentTask) / Double.valueOf(currentTaskProcess.contents.size());
            progressBar.setProgress((int) (doneProcess * 100.0));
            listener = view -> dismiss();
        }
        closeWinButton.setOnClickListener(v -> dismiss());
        cancelButton.setOnClickListener(listener);
        guideButton.setOnClickListener(v -> {
            bundle.putBoolean("guideButton", true);
            dismiss();
        });
    }
}
