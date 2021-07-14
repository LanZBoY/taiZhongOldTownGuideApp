package com.usrProject.taizhongoldtownguideapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.usrProject.taizhongoldtownguideapp.R;
import com.usrProject.taizhongoldtownguideapp.activity.TaskInfoActivity;
import com.usrProject.taizhongoldtownguideapp.model.CheckIn.CheckTasks;
import com.usrProject.taizhongoldtownguideapp.schema.TaskSchema;
import com.usrProject.taizhongoldtownguideapp.schema.type.MarkTask;

import java.util.ArrayList;
import java.util.List;

public class CheckInTasksView extends AppCompatActivity {
    private FirebaseFirestore db;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_in_item_view);
        RecyclerView tasksItemsList = findViewById(R.id.tasksList);
        tasksItemsList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        tasksItemsList.setLayoutManager(layoutManager);

        final ArrayList<CheckTasks> testDataSet = new ArrayList<>();
//      初始化dataset
        adapter = new TaskAdapter(testDataSet);
        tasksItemsList.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
//      當成功撈上資料時將資料做更新
        db.collection("tasks")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                CheckTasks checkTasks = document.toObject(CheckTasks.class);
                                checkTasks.Id = document.getId();
                                testDataSet.add(checkTasks);
                                adapter.notifyDataSetChanged();
                            }

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
            holder.taskTitle.setText(dataset.get(position).taskTitle);
//            holder.taskDesc.setText(dataset.get(position).taskDesc);
            holder.taskTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Log.d("onClick", dataset.get(position).taskTitle);
                    Intent intent = new Intent(getApplicationContext(), TaskInfoActivity.class);
                    intent.putExtra(MarkTask.TASK_INFO.key, dataset.get(position));
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return dataset.size();
        }
    }
}
