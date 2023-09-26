package com.mstudio.android.mstory.manuel.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.legacy.content.WakefulBroadcastReceiver;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mstudio.android.mstory.manuel.R;
import com.mstudio.android.mstory.manuel.SpacesItemDecoration;
import com.mstudio.android.mstory.manuel.adapter.adapter_wall_all;
import com.mstudio.android.mstory.manuel.model.Tab;
import com.mstudio.android.mstory.manuel.model.Wall;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class all extends Fragment {

    List<Wall> listwall = new ArrayList<>();
    RecyclerView recycle_all;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    adapter_wall_all adapter;
    boolean isloadsuc=false;
    String key;
    private static all instance;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        instance = this;
        getActivity().setTheme(R.style.AppTheme);
        final View view = inflater.inflate(R.layout.all, container, false);

        recycle_all = view.findViewById(R.id.recy_all);
        mSwipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        recycle_all.setHasFixedSize(true);
        recycle_all.setItemViewCacheSize(20);

        adapter = new adapter_wall_all(getActivity(),listwall);
        recycle_all.setAdapter(adapter);
        SpacesItemDecoration decoration = new SpacesItemDecoration(0);
        recycle_all.addItemDecoration(decoration);
        StaggeredGridLayoutManager sm = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        sm.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recycle_all.setLayoutManager(sm);
        mSwipeRefreshLayout.setRefreshing(true);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(getArguments()!=null){
                    key = getArguments().getString("key");
                    getData(key);
                }
             isloadsuc = false;

            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    public void getdata(){
        if(!isloadsuc){
            if (isValidContext(getActivity())) {

            }
        }

        new Handler().postDelayed(new Runnable() {
            public void run() {
               isloadsuc = true;

            }
        }, 1000);
    }

    public static all neeInstanve(String key){
        Bundle args = new Bundle();
        args.putString("key",key);
        all fr = new all();
        fr.setArguments(args);
        return fr;
    }

    @Override
    public void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                adapter_wall_all.getInstance().playvideo(adapter.exoPlayer);
                if(!isloadsuc){
                    if(getArguments()!=null){
                        key = getArguments().getString("key");
                        getData(key);
                    }
                }
            }
        }, 500);



    }

    @Override
    public void onPause() {
        super.onPause();
        adapter_wall_all.getInstance().pausevideo(adapter.exoPlayer);

    }

    public static boolean isValidContext(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return false;
            }
        }
        return true;
    }
    public void getData(String keydata) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Wallpaper");
        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {

                listwall.clear();
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);

                    }
                }, 1000);
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Wall wall = dataSnapshot.getValue(Wall.class);


                        if(wall.getType_wallpaper().equals(keydata)){
                            listwall.add(wall);
                        }else {
                            if(keydata.equals("all")){
                                listwall.add(wall);
                            }
                        }


                        Collections.shuffle(listwall);
                        isloadsuc =true;

                }

                adapter.notifyDataSetChanged();

            }


            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });

    }




}