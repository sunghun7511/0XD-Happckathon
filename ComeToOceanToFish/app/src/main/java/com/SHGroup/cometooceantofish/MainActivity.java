package com.SHGroup.cometooceantofish;

import android.os.StrictMode;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.SHGroup.cometooceantofish.fragments.CommunityFragment;
import com.SHGroup.cometooceantofish.fragments.FragmentBase;
import com.SHGroup.cometooceantofish.fragments.LendFragment;
import com.SHGroup.cometooceantofish.fragments.PartyFragment;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private String access_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        access_token = getIntent().getStringExtra("access_token");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private final HashMap<Integer, FragmentBase> maps = new HashMap<>();

        @Override
        public Fragment getItem(int position) {
            FragmentBase fb = null;
            if(maps.containsKey(position)){
                fb = maps.get(position);
            }else{
                if(position == 0){
                    fb = new LendFragment();
                }else if(position == 1){
                    fb = new CommunityFragment();
                }else if(position == 2){
                    fb = new PartyFragment();
                }
                maps.put(position, fb);
            }
            if(fb != null)
                fb.setAccessToken(access_token);
            return fb;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}