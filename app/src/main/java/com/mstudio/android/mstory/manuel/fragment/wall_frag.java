package com.mstudio.android.mstory.manuel.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mstudio.android.mstory.manuel.R;
import com.mstudio.android.mstory.manuel.model.Tab;

import java.util.ArrayList;
import java.util.List;

public class wall_frag extends Fragment {

    private static wall_frag instance;
    TabLayout tabLayout;
    ViewPager viewPager;
    private List<Fragment> fragmentList = new ArrayList<>();
    private List<Tab> titleList = new ArrayList<>();
    View linetabyout;
    Tab tablist;
    SectionPagerAdapter adapter;
    boolean isloadsuc =false;
    boolean isfrist = true;
    AppBarLayout appbar;
    TextView tv_main;
    ImageView ic_main;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        instance = this;
        getActivity().setTheme(R.style.AppTheme);
        final View view = inflater.inflate(R.layout.wall_frag, container, false);
        linetabyout = view.findViewById(R.id.linetabyout);
        tabLayout=view.findViewById(R.id.tablayout);
        viewPager=view.findViewById(R.id.viewpager);
        appbar=view.findViewById(R.id.appBarLayout);
        tv_main=view.findViewById(R.id.tv_main);
        ic_main=view.findViewById(R.id.icon_main);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        adapter = new SectionPagerAdapter(fm);
        viewPager.setAdapter(adapter);
        setUpUi();
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(final TabLayout.Tab tab) {
                int position = tab.getPosition();

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });
        setUpViewPager(viewPager);
        return view;
    }

    public  class SectionPagerAdapter extends FragmentPagerAdapter {


        Context context;
        public SectionPagerAdapter(FragmentManager fm) {
            super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.context = context;
        }
        @Override
        public Fragment getItem(int position) {

            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }


        @Override
        public CharSequence getPageTitle(int position) {

            return  titleList.get(position).getTitle();


        }


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
    private void setUpUi() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("ui");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                    String toolbar = snapshot.child("toolbar").getValue(String.class);
                    String icon = snapshot.child("icon").getValue(String.class);
                    tv_main.setText(toolbar);
                    if(isValidContext(getActivity())){
                        Glide.with(getActivity())
                                .load(icon)
                                .into(ic_main);
                    }

            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });

    }
        private void setUpViewPager(ViewPager viewPager) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("tab");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                titleList.clear();
                fragmentList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                     tablist = dataSnapshot.getValue(Tab.class);
                     titleList.add(tablist);
                     fragmentList.add(all.neeInstanve(tablist.getType()));
                     linetabyout.setVisibility(View.VISIBLE);
                     appbar.setVisibility(View.VISIBLE);

                }
                isloadsuc = true;
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        isloadsuc = false;

                    }
                }, 500);

                adapter.notifyDataSetChanged();
                viewPager.setOffscreenPageLimit(titleList.size());


            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });


    }

    public static wall_frag getInstance() {
        return instance;
    }

    public void proMethod() {
        Toast.makeText(getActivity(),"oo",Toast.LENGTH_SHORT).show();
    }
}