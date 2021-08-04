package com.usrProject.taizhongoldtownguideapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;
import com.usrProject.taizhongoldtownguideapp.GlideApp;
import com.usrProject.taizhongoldtownguideapp.R;
import com.usrProject.taizhongoldtownguideapp.model.CheckIn.CheckInMarkerObject;
import com.usrProject.taizhongoldtownguideapp.model.CheckIn.CheckTasks;
import com.usrProject.taizhongoldtownguideapp.model.CheckIn.ContentDTO;
import com.usrProject.taizhongoldtownguideapp.model.CheckIn.CurrentTask;
import com.usrProject.taizhongoldtownguideapp.schema.TaskSchema;
import com.usrProject.taizhongoldtownguideapp.schema.UserSchema;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class TaskInfoActivity extends AppCompatActivity {
    private CheckTasks tasksInfo;
    private CurrentTask currentTask;
    private TextView taskTitleView;
    private TextView taskDescView;
    private ImageView taskImageView;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_info);
        pref = getSharedPreferences(UserSchema.SharedPreferences.USER_DATA, MODE_PRIVATE);
//        init
        taskTitleView = findViewById(R.id.taskTitleView);
        taskDescView = findViewById(R.id.taskDescView);
        taskImageView = findViewById(R.id.taskImgView);

        Intent intent = this.getIntent();
        tasksInfo = (CheckTasks) intent.getSerializableExtra(TaskSchema.TASK_INFO);

        taskTitleView.setText(tasksInfo.taskTitle);
        taskDescView.setText(tasksInfo.taskDesc);

        if(StringUtils.isNotBlank(tasksInfo.taskImg)){
            FirebaseStorage storage = FirebaseStorage.getInstance(getString(R.string.firestore));
            GlideApp.with(this)
                    .load(storage.getReference(tasksInfo.taskImg))
                    .into(taskImageView);
        }
    }

    public void onCancel(View view) {
        Intent intent = new Intent(getApplicationContext(), CheckInTasksView.class);
        startActivity(intent);
    }

    public void onAccept(View view) {
        Gson gson = new Gson();
        currentTask = gson.fromJson(gson.toJson(tasksInfo), CurrentTask.class);
        currentTask.contents = new ArrayList<CheckInMarkerObject>();
        if(pref.contains(TaskSchema.CURRENT_TASK)){
            String json = pref.getString(TaskSchema.CURRENT_TASK, null);
            CurrentTask exsitTask = new Gson().fromJson(json, CurrentTask.class);

            new AlertDialog.Builder(TaskInfoActivity.this)
                    .setTitle("有正在執行中的任務")
                    .setMessage(String.format("你目前正在接取 %s 任務 是否重新接取任務？", exsitTask.taskTitle))
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            initMarkDatasById(tasksInfo.Id);
                        }
                    })
                    .setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(getApplicationContext(), TeamTracker.class));
                        }
                    })
                    .create()
                    .show();
        }else{
            initMarkDatasById(tasksInfo.Id);
        }

    }

    private void initMarkDatasById(String Id){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<DocumentSnapshot> task = db.collection(TaskSchema.Database.COLLECTION_NAME).document(Id).get();
        task.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                DocumentSnapshot taskDoc = task.getResult();
                DocumentReference contentsReference = taskDoc.getDocumentReference("contents");
                if(contentsReference != null){
                    contentsReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot contentDoc = task.getResult();
                            ContentDTO contentDTO = contentDoc.toObject(ContentDTO.class);
                            currentTask.contents = contentDTO.contents;
                            pref.edit().putString(TaskSchema.CURRENT_TASK, new Gson().toJson(currentTask)).apply();
                            Toast.makeText(getApplicationContext(),String.format("成功接取 %s 任務",currentTask.taskTitle),Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),TeamTracker.class));
                        }
                    });
                }else{
                    pref.edit().putString(TaskSchema.CURRENT_TASK, new Gson().toJson(currentTask)).apply();
                    Toast.makeText(getApplicationContext(),String.format("成功接取 %s 任務",currentTask.taskTitle),Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),TeamTracker.class));
                }

            }
        });
    }

}