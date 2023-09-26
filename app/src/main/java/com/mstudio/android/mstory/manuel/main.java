package com.mstudio.android.mstory.manuel;

import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.mstudio.android.mstory.manuel.fragment.manuel_frag;
import com.mstudio.android.mstory.manuel.fragment.wall_frag;

public class main extends AppCompatActivity {
    private ImageView im_manuel;
    private ImageView im_wall;
    private TextView tv_manuel;
    private TextView tv_wall;
    FrameLayout content;
    private LinearLayout setfrag_manuel;
    private LinearLayout setfrag_wall;
    final Fragment fragment1 = new manuel_frag();
    final Fragment fragment2 = new wall_frag();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment1;

    public static int MY_REQUEST_CODE = 1;
    AppUpdateManager appUpdateManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.main);

        setfrag_manuel = findViewById(R.id.setfrag_manuel);
        setfrag_wall = findViewById(R.id.setfrag_wall);
        content = findViewById(R.id.content);
        im_manuel = findViewById(R.id.im_manuel);
        im_wall = findViewById(R.id.im_wall);
        tv_manuel = findViewById(R.id.tv_manuel);
        tv_wall = findViewById(R.id.tv_wall);
        fm.beginTransaction().add(R.id.content, fragment2, "2").hide(fragment2).commitAllowingStateLoss();
        fm.beginTransaction().add(R.id.content,fragment1, "1").hide(fragment1).commitAllowingStateLoss();
        fm.beginTransaction().hide(active).show(fragment1).commitAllowingStateLoss();
        active = fragment1;
        setwall();
        setfrag_manuel.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View p1) {
                setmanuel();
            }
        });
        setfrag_wall.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View p1) {
                setwall();
            }
        });

    }
    public void setmanuel() {
        fm.beginTransaction().hide(active).show(fragment1).commitAllowingStateLoss();
        active = fragment1;
        im_manuel.setColorFilter(getResources().getColor(R.color.colorAccent));
        tv_manuel.setTextColor(getResources().getColor(R.color.colorAccent));

        im_wall.setImageResource(R.drawable.ic_image_false);
        im_wall.setColorFilter(getResources().getColor(R.color.colornotab));
        tv_wall.setTextColor(getResources().getColor(R.color.colornotab));

    }
    public void setwall() {
        fm.beginTransaction().hide(active).show(fragment2).commitAllowingStateLoss();
        active = fragment2;
        im_manuel.setColorFilter(getResources().getColor(R.color.colornotab));
        tv_manuel.setTextColor(getResources().getColor(R.color.colornotab));

        im_wall.setImageResource(R.drawable.ic_image_true);
        im_wall.setColorFilter(getResources().getColor(R.color.colorAccent));
        tv_wall.setTextColor(getResources().getColor(R.color.colorAccent));

    }
}