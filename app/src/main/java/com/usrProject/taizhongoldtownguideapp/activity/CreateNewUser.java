package com.usrProject.taizhongoldtownguideapp.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.usrProject.taizhongoldtownguideapp.R;
import com.usrProject.taizhongoldtownguideapp.schema.UserSchema;

public class CreateNewUser extends AppCompatActivity {
    private EditText editText;
    private int userIconPath;
    private ImageView userIcon;
    private SharedPreferences pref;
    final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_user);

        pref = getSharedPreferences(UserSchema.SharedPreferences.USER_DATA ,MODE_PRIVATE);

        editText = findViewById(R.id.newUser_editText);
        userIcon = findViewById(R.id.userIcon);
        userIconPath = R.drawable.user_icon1;
        userIcon.setImageResource(userIconPath);
    }

    public void goSelect(View view) {
        String newUserName = editText.getText().toString();
        pref.edit().putString("userName", newUserName).apply();
        pref.edit().putInt("userIconPath", userIconPath).apply();

        Intent intent = new Intent(this, TeamEntry.class);
        startActivity(intent);
        finish();
    }

    public void changeUserIcon(View view) {
        Intent intent = new Intent(this, ChangeUserIcon.class);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK){
            userIconPath = data.getIntExtra("userPickedIcon", 0);
            userIcon.setImageResource(userIconPath);
        }
    }
}
