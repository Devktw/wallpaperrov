package com.mstudio.android.mstory.manuel.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mstudio.android.mstory.manuel.R;
import com.mstudio.android.mstory.manuel.adapter.ViewPager2Adapter;
import com.mstudio.android.mstory.manuel.model.Wall;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class slide_wallpaper extends AppCompatActivity{
    ViewPager2 viewPager2;
    ViewPager2Adapter viewPager2Adapter;
    List<Wall> listwall = new ArrayList<>();
    CardView exit;
    ImageView im_gif;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        setTheme(R.style.FullimageTheme);
        super.onCreate(savedInstanceState);



        setContentView(R.layout.slide_wallpaper);

        SharedPreferences sh = getSharedPreferences("dialog", MODE_PRIVATE);
        String s = sh.getString("state", "");
        if(!s.equals("1")){
            LayoutInflater factory = LayoutInflater.from(this);
            final View view = factory.inflate(R.layout.dialog_custom, null);
            final AlertDialog deleteDialog = new AlertDialog.Builder(this).create();
            deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            ImageView close = view.findViewById(R.id.close);
            ImageView im_gif = view.findViewById(R.id.im_gif);
            Glide.with(this).load(R.raw.swipe_up).into(new DrawableImageViewTarget(im_gif));
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteDialog.dismiss();
                    SharedPreferences sharedPreferences = getSharedPreferences("dialog",MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putString("state", "1");
                    myEdit.commit();
                }
            });
            deleteDialog.setView(view);
            deleteDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    SharedPreferences sharedPreferences = getSharedPreferences("dialog",MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putString("state", "1");
                    myEdit.commit();
                }
            });
            deleteDialog.show();
        }
        String id = getIntent().getStringExtra("id");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);




        viewPager2 = findViewById(R.id.viewpager);
        viewPager2Adapter = new ViewPager2Adapter(this,listwall);
        viewPager2.setAdapter(viewPager2Adapter);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            // This method is triggered when there is any scrolling activity for the current page
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                final Wall wall = listwall.get(position);
                if(wall.getType_wallpaper().equals("icon")||wall.getType_wallpaper().equals("cat")){
                    getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                }else {

                    getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);

            }
        });


        exit = findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Wallpaper").child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                Wall wall = snapshot.getValue(Wall.class);
                if(wall.getType_wallpaper().equals("icon")||wall.getType_wallpaper().equals("cat")){
                    getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                }else {

                    getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        getData();
    }
    public void getData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Wallpaper");
        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {

                listwall.clear();
                new Handler().postDelayed(new Runnable() {
                    public void run() {


                    }
                }, 1000);
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Wall wall = dataSnapshot.getValue(Wall.class);
                    listwall.add(wall);


                    Collections.shuffle(listwall);


                }
                viewPager2Adapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
