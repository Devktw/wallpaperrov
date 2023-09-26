package com.mstudio.android.mstory.manuel.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mstudio.android.mstory.manuel.R;
import com.mstudio.android.mstory.manuel.SpacesItemDecoration;
import com.mstudio.android.mstory.manuel.adapter.adapter_wall_all;
import com.mstudio.android.mstory.manuel.model.Wall;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class manuel_frag extends Fragment {

    private static manuel_frag instance;
    int pastVisiblesItems;
    int visibleItemCount;
    int totalItemCount;
    boolean loading = true;
    List<Wall> listwall;
    RecyclerView recycle_all;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    adapter_wall_all adapter;
    EditText edttext;
    ImageView search;
    ImageView delete;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        instance = this;
        getActivity().setTheme(R.style.AppTheme);
        final View view = inflater.inflate(R.layout.manuel_frag, container, false);
        edttext = view.findViewById(R.id.edittext);
        search = view.findViewById(R.id.search);
        delete = view.findViewById(R.id.delete);
        recycle_all = view.findViewById(R.id.recy_all);
        mSwipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        recycle_all.setHasFixedSize(true);
        recycle_all.setItemViewCacheSize(20);
        listwall = new ArrayList<Wall>();
        adapter = new adapter_wall_all(getActivity(),listwall);
        recycle_all.setAdapter(adapter);
        SpacesItemDecoration decoration = new SpacesItemDecoration(0);
        recycle_all.addItemDecoration(decoration);
        StaggeredGridLayoutManager sm = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        sm.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recycle_all.setLayoutManager(sm);

        mSwipeRefreshLayout.setRefreshing(true);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edttext.getText().clear();
            }
        });
        edttext.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                searchWall(s.toString().toLowerCase());
                if(s.length() != 0){
                    delete.setVisibility(View.VISIBLE);
                    search.setVisibility(View.GONE);
                }else {
                    search.setVisibility(View.VISIBLE);
                    delete.setVisibility(View.GONE);
                }

            }
        });
        getData();


        recycle_all.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                visibleItemCount = sm.getChildCount();
                totalItemCount = sm.getItemCount();
                int[] firstVisibleItems = null;
                firstVisibleItems = sm.findFirstVisibleItemPositions(firstVisibleItems);
                if(firstVisibleItems != null && firstVisibleItems.length > 0) {
                    pastVisiblesItems = firstVisibleItems[0];
                }

                if (loading) {
                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        loading = false;
                        Log.d("tag", "LOAD NEXT ITEM");
                    }
                }
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
        return view;
    }
    private void searchWall(String s){
        Query query = FirebaseDatabase.getInstance().getReference().child("Wallpaper").orderByChild("tag_wallpaper").startAt(s).endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                listwall.clear();
                for(DataSnapshot datasnapshot : snapshot.getChildren()){
                    Wall wall = datasnapshot.getValue(Wall.class);
                    listwall.add(wall);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

    }
    private void getData() {
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
                    listwall.add(wall);
                    Collections.shuffle(listwall);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });
    }

    public static manuel_frag getInstance() {
        return instance;
    }
    public void proMethod() {

    }
}