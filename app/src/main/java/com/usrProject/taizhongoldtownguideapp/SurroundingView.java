package com.usrProject.taizhongoldtownguideapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class SurroundingView extends AppCompatActivity {
    private CardView cardView1;
    private CardView cardView2;
    private CardView cardView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surrounding_view);

        cardView1 = findViewById(R.id.cardView1);
        cardView2 = findViewById(R.id.cardView2);
        cardView3 = findViewById(R.id.cardView3);

        cardView1.setOnClickListener(v -> {
            Uri uri = Uri.parse("http://140.134.48.76/USR360/WanChuen/");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
        cardView2.setOnClickListener(v -> {
            Uri uri = Uri.parse("http://140.134.48.76/USR360/Lechun/");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
        /*
        cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://my.matterport.com/show/?m=iuVduPBfv67");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        */
    }
}
