package com.usrProject.taizhongoldtownguideapp.component.popupwin;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.usrProject.taizhongoldtownguideapp.R;
import com.usrProject.taizhongoldtownguideapp.model.MapClickDTO;
import com.usrProject.taizhongoldtownguideapp.schema.type.MapClick;


public class IntroductionCustomPopUpWin extends CustomPopUpWin {

    private TextView titleTextView;
    private TextView descTextView;
    private ImageView imgImageView;

    public IntroductionCustomPopUpWin(Context mContext, int xmlLayout, MapClick mapClick) {
        super(mContext, xmlLayout,true);
        titleTextView = getView().findViewById(R.id.title_TextView);
        descTextView =  getView().findViewById(R.id.contentTextView);
        imgImageView = getView().findViewById(R.id.contentImageView);

        //這裡可能要寫成看資料有多少再去找
        String documentId = mContext.getString(mapClick.documentId);
        CollectionReference mapClickReference = FirebaseFirestore.getInstance().collection("MapClick");
        mapClickReference.document(documentId).get().addOnCompleteListener(task -> {
           if(task.isComplete()){
               MapClickDTO result = task.getResult().toObject(MapClickDTO.class);
               titleTextView.setText(result.title);
               descTextView.setText(result.desc);
           }
        });
    }
}
