package com.usrProject.taizhongoldtownguideapp.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;
import com.usrProject.taizhongoldtownguideapp.GlideApp;
import com.usrProject.taizhongoldtownguideapp.R;
import com.usrProject.taizhongoldtownguideapp.component.CheckInTasksView;
import com.usrProject.taizhongoldtownguideapp.model.CheckIn.CheckTasks;
import com.usrProject.taizhongoldtownguideapp.model.CheckIn.ContentDTO;
import com.usrProject.taizhongoldtownguideapp.model.CheckIn.CurrentTaskProcess;
import com.usrProject.taizhongoldtownguideapp.model.User.User;
import com.usrProject.taizhongoldtownguideapp.schema.TaskSchema;
import com.usrProject.taizhongoldtownguideapp.schema.UserSchema;
import com.usrProject.taizhongoldtownguideapp.schema.type.MarkType;
import com.usrProject.taizhongoldtownguideapp.utils.SharedPreferencesManager;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public class TaskInfoActivity extends AppCompatActivity {
    private CheckTasks tasksInfo;
    private CurrentTaskProcess currentTaskProcess;
    private TextView taskTitleView;
    private TextView taskDescView;
    private ImageView taskImageView;
    private User user;
//    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_info);
//        pref = getSharedPreferences(UserSchema.SharedPreferences.USER_DATA, MODE_PRIVATE);
//        init
        taskTitleView = findViewById(R.id.taskTitleView);
        taskDescView = findViewById(R.id.taskDescView);
        taskImageView = findViewById(R.id.taskImgView);

        Intent intent = this.getIntent();
        user = (User) intent.getSerializableExtra(UserSchema.USER_DATA);
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
        intent.putExtra(UserSchema.USER_DATA, user);
        startActivity(intent);
    }

    public void onAccept(View view) {
        Gson gson = new Gson();
        currentTaskProcess = gson.fromJson(gson.toJson(tasksInfo), CurrentTaskProcess.class);
        currentTaskProcess.contents = new ArrayList<>();
        if(SharedPreferencesManager.contains(this, TaskSchema.TASK_PREF, TaskSchema.CURRENT_TASK)){
            CurrentTaskProcess existTask = SharedPreferencesManager.getCurrentTaskProcess(this);
            new AlertDialog.Builder(TaskInfoActivity.this)
                    .setTitle("有正在執行中的任務")
                    .setMessage(String.format("你目前正在接取 %s 任務 是否重新接取任務？", existTask.taskTitle))
                    .setPositiveButton("是", (dialogInterface, i) -> initMarkDatasById(tasksInfo.Id))
                    .setNegativeButton("否", (dialogInterface, i) -> startActivity(new Intent(getApplicationContext(), TeamTracker.class)))
                    .create()
                    .show();
        }else{
            initMarkDatasById(tasksInfo.Id);
        }

    }

    private void initMarkDatasById(String Id){
        FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();
        FirebaseDatabase realtimeDB = FirebaseDatabase.getInstance();
        DatabaseReference markersRef = realtimeDB.getReference("team").child(user.teamId).child("marker");
        markersRef.get().addOnCompleteListener((task)-> {
            if(task.isComplete()){
                for(DataSnapshot dataSnapshot : task.getResult().getChildren()){
                    if(dataSnapshot.child("markType").getValue(MarkType.class) == MarkType.TASK){
                        markersRef.child(dataSnapshot.getKey()).removeValue();
                    }
                }
            }
        });
        firestoreDB.collection(TaskSchema.Database.COLLECTION_NAME)
                .document(Id)
                .get()
                .addOnCompleteListener(task -> {
            DocumentSnapshot taskDoc = task.getResult();
            DocumentReference contentsReference = taskDoc.getDocumentReference("contents");
            if(contentsReference != null){
                contentsReference.get().addOnCompleteListener(task11 -> {
                    DocumentSnapshot contentDoc = task11.getResult();
                    ContentDTO contentDTO = contentDoc.toObject(ContentDTO.class);
                    currentTaskProcess.contents = contentDTO.contents;
                    Toast.makeText(getApplicationContext(),String.format("成功接取 %s 任務", currentTaskProcess.taskTitle),Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),TeamTracker.class);
                    intent.putExtra(TaskSchema.CURRENT_TASK, currentTaskProcess);
                    startActivity(intent);
                });
            }else{
                Toast.makeText(getApplicationContext(),String.format("成功接取 %s 任務", currentTaskProcess.taskTitle),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),TeamTracker.class);
                intent.putExtra(TaskSchema.CURRENT_TASK, currentTaskProcess);
                startActivity(intent);
            }
        });;
    }

}