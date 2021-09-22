package com.usrProject.taizhongoldtownguideapp.component;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimationUtilsCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.usrProject.taizhongoldtownguideapp.R;
import com.usrProject.taizhongoldtownguideapp.activity.TaskInfoActivity;
import com.usrProject.taizhongoldtownguideapp.model.CheckIn.CheckTasks;
import com.usrProject.taizhongoldtownguideapp.model.User.User;
import com.usrProject.taizhongoldtownguideapp.schema.TaskSchema;
import com.usrProject.taizhongoldtownguideapp.schema.UserSchema;

import java.util.ArrayList;

public class CheckInTasksView extends AppCompatActivity {
    private FirebaseFirestore db;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_in_item_view);
        RecyclerView tasksItemsList = findViewById(R.id.tasksList);
        tasksItemsList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        tasksItemsList.setLayoutManager(layoutManager);
        final ArrayList<CheckTasks> testDataSet = new ArrayList<>();
        user = (User) this.getIntent().getSerializableExtra(UserSchema.USER_DATA);
//      初始化dataset
        adapter = new TaskAdapter(testDataSet);
        tasksItemsList.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();
//      當成功撈上資料時將資料做更新
        db.collection("tasks")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            CheckTasks checkTasks = document.toObject(CheckTasks.class);
                            checkTasks.Id = document.getId();
                            testDataSet.add(checkTasks);
                            adapter.notifyDataSetChanged();
                        }

                    }
                });
    }



//  建立調配器
    private class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder>{
        private ArrayList<CheckTasks> dataset;
//      設定要綁定的元件
        public  class ViewHolder extends RecyclerView.ViewHolder {
            public TextView taskTitle;
//            public TextView taskDesc;
            public ViewHolder(View view) {
                super(view);
                // Define click listener for the ViewHolder's View
                taskTitle = view.findViewById(R.id.post_title);
                taskTitle.setBackgroundResource(R.drawable.task_background);
//                taskDesc = view.findViewById(R.id.taskDesc);
            }


        }
//      規定調配器一定要從外部資料傳入才行
        public TaskAdapter(ArrayList<CheckTasks> dataset){
            this.dataset = dataset;
        }

        @NonNull
        @Override
        public TaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.post_recycle_view_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TaskAdapter.ViewHolder holder, final int position) {
            holder.taskTitle.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_from_right));
            holder.taskTitle.setText(dataset.get(position).taskTitle);
            holder.taskTitle.setOnClickListener(view -> {
                Intent intent = new Intent(getApplicationContext(), TaskInfoActivity.class);
                intent.putExtra(TaskSchema.TASK_INFO, dataset.get(position));
                intent.putExtra(UserSchema.USER_DATA, user);
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return dataset.size();
        }
    }
}
