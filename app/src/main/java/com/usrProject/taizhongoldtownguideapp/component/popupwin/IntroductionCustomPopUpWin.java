package com.usrProject.taizhongoldtownguideapp.component.popupwin;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.usrProject.taizhongoldtownguideapp.GlideApp;
import com.usrProject.taizhongoldtownguideapp.R;
import com.usrProject.taizhongoldtownguideapp.model.MapClickDTO;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;


public class IntroductionCustomPopUpWin extends CustomPopUpWin {
    private Context mContext;
    private MapClickDTO mapClickDTO;
    private TextView titleTextView;
    private TextView descTextView;
    private ImageView imgImageView;
    private StorageReference storageReference;
    private ArrayList<String> allImgName;
    private HashMap<String, byte[]> imgMap;
    private int currentImg = 0;
//  目前限制傳輸量最多為3MB
    private final long ONE_MEGABYTE = 3 * 1024 * 1024;
    public IntroductionCustomPopUpWin(Context mContext, int xmlLayout, MapClickDTO mapClickDTO) {
        super(mContext, xmlLayout,true);
        this.mContext = mContext;
        this.mapClickDTO = mapClickDTO;
        titleTextView = getView().findViewById(R.id.title_TextView);
        descTextView =  getView().findViewById(R.id.contentTextView);
        imgImageView = getView().findViewById(R.id.contentImageView);
        storageReference = FirebaseStorage.getInstance(mContext.getString(R.string.storage)).getReference();
        initImageView();
        //這裡可能要寫成看資料有多少再去找
        titleTextView.setText(mapClickDTO.title);
        descTextView.setText(mapClickDTO.desc);
        imgImageView.setOnClickListener(view -> {
            currentImg = (currentImg + 1) % allImgName.size();
            loadImgResource();
        });
    }
    private void initImageView(){
        if(StringUtils.isNotBlank(mapClickDTO.imgId)){
            allImgName = new ArrayList<>();
            imgMap = new HashMap<>();
            Log.d(FirebaseStorage.class.getSimpleName(),storageReference.child("MapClick").child(mapClickDTO.imgId).getPath());
            storageReference.child("MapClick").child(mapClickDTO.imgId).listAll().addOnCompleteListener(task -> {
                if(task.isComplete()){
                    for(StorageReference temp : task.getResult().getItems()){
                        Log.d(FirebaseStorage.class.getSimpleName(),temp.getName());
                        allImgName.add(temp.getName());
                        imgMap.put(temp.getName(), null);
                    }
                    loadImgResource();
                }
            });
        }
    }

    private void loadImgResource(){
        if(imgMap.get(allImgName.get(currentImg)) == null){
            imgImageView.setImageResource(R.drawable.file);
            storageReference.child("MapClick").child(mapClickDTO.imgId).child(allImgName.get(currentImg)).getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                imgMap.put(allImgName.get(currentImg), bytes);
                GlideApp.with(mContext)
                        .load(bytes)
                        .into(imgImageView);
            });
        }else{
            GlideApp.with(mContext)
                    .load(imgMap.get(allImgName.get(currentImg)))
                    .into(imgImageView);
        }
    }
}
