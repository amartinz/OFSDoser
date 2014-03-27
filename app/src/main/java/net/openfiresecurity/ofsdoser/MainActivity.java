/*
 * Copyright (c) 2013. Alexander Martinz
 */

package net.openfiresecurity.ofsdoser;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import net.openfiresecurity.ofsdoser.events.ScheduleEvent;
import net.openfiresecurity.ofsdoser.events.ToastEvent;
import net.openfiresecurity.ofsdoser.events.VisibilityEvent;
import net.openfiresecurity.ofsdoser.fragments.DosFragment;
import net.openfiresecurity.ofsdoser.fragments.PrefFragment;
import net.openfiresecurity.ofsdoser.services.DosService;
import net.openfiresecurity.ofsdoser.util.BusProvider;
import net.openfiresecurity.ofsdoser.widgets.adapters.ScreenSlidePagerAdapter;
import net.openfiresecurity.ofsdoser.widgets.transformers.ZoomOutPageTransformer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MainActivity extends ActionBarActivity {

    //====================
    // Fragments
    //====================
    public         DosFragment  mDosFragment;
    //public static InformationFragment mInformationFragment;
    public         PrefFragment mPrefFragment;
    //====================
    // Fields
    //====================
    private static long         back_pressed;
    private static Toast        mToast;
    //====================
    // Elements
    //====================
    private        ViewPager    mPager;
    private        List<String> mTitleList;
    //====================
    // Others
    //====================
    private        ProgressBar  pbActionStart;
    private        ImageView    ivActionStart;
    private        TextView     mCounter;
    //====================
    // Scheduler
    //====================
    private final ScheduledExecutorService mScheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture mSchedulerPacketsUpdate;


    /**
     * Override backbutton presses to exit
     */
    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            if (back_pressed + 2000 > System.currentTimeMillis()) {
                if (mToast != null) mToast.cancel();
                finish();
            } else {
                BusProvider.getBus().post(new ToastEvent(R.string.action_press_again));
            }
            back_pressed = System.currentTimeMillis();
        } else {
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItemCompat.getActionView(menu.findItem(R.id.action_progress))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mDosFragment != null) { mDosFragment.toggleDos(); }
                    }
                });
        pbActionStart =
                (ProgressBar) MenuItemCompat.getActionView(menu.findItem(R.id.action_progress))
                        .findViewById(R.id.pbActionProgress);
        pbActionStart.setVisibility(View.INVISIBLE);
        ivActionStart =
                (ImageView) MenuItemCompat.getActionView(menu.findItem(R.id.action_progress))
                        .findViewById(R.id.ivActionStart);
        ivActionStart.setVisibility(View.VISIBLE);
        mCounter = (TextView) MenuItemCompat.getActionView(menu.findItem(R.id.action_progress))
                .findViewById(R.id.tvCounter);
        mCounter.setText(getString(R.string.info_counter, "0"));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action buttons
        switch (item.getItemId()) {
            case R.id.action_settings:
                mPager.setCurrentItem(2, true);
                /*startActivity(new Intent(MainActivity.this, PrefFragment.class));*/
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        enableActionBar();

        mPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(),
                getFragmentList(), getTitleList());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(0);
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) { }

            @Override
            public void onPageSelected(int i) {
                getSupportActionBar().setTitle(mTitleList.get(i));
            }

            @Override
            public void onPageScrollStateChanged(int i) { }
        });
        mPager.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.viewpager_margin));
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getBus().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.getBus().unregister(this);
        if (isFinishing()) {
            Runtime.getRuntime().exit(0);
        }
    }

    private List<Fragment> getFragmentList() {
        List<Fragment> mList = new ArrayList<>();

        // Get instance and add DosFragment
        mDosFragment = new DosFragment();
        mList.add(mDosFragment);

        // Get instance and add InformationFragment
        //mInformationFragment = new InformationFragment();
        //mList.add(mInformationFragment);

        // Get instance and add PrefFragment
        mPrefFragment = new PrefFragment();
        mList.add(mPrefFragment);

        return mList;
    }

    private List<String> getTitleList() {
        mTitleList = new ArrayList<>();

        mTitleList.add(getString(R.string.activity_doser));
        //mTitleList.add(getString(R.string.activity_information));
        mTitleList.add(getString(R.string.activity_preferences));

        return mTitleList;
    }

    private void enableActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
    }

    @Subscribe
    public void onToggleStartVisibility(final VisibilityEvent event) {
        final boolean mShow = event.getVisibility();
        if (mShow) {
            pbActionStart.setVisibility(View.INVISIBLE);
            ivActionStart.setVisibility(View.VISIBLE);
        } else {
            pbActionStart.setVisibility(View.VISIBLE);
            ivActionStart.setVisibility(View.INVISIBLE);
        }
    }

    @Subscribe
    public void onToast(final ToastEvent event) {
        if (mToast != null) mToast.cancel();
        if (event.getIsString()) {
            mToast = Toast.makeText(this, event.getMsg(), Toast.LENGTH_SHORT);
        } else {
            mToast = Toast.makeText(this, event.getMsgId(), Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    //================
    // Runnable
    //================
    final Runnable mSchedulerPacketsUpdateRunnable = new Runnable() {
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (mCounter != null) {
                            mCounter.setText(getString(R.string.info_counter,
                                    DosService.mCounterDone));
                            mCounter.invalidate();
                        }
                    } catch (Exception ignored) {
                    }
                }
            });
        }
    };

    @Subscribe
    public void onSchedulePacketsUpdate(final ScheduleEvent event) {
        if (event.isEnabled()) {
            mSchedulerPacketsUpdate = mScheduler.scheduleAtFixedRate(
                    mSchedulerPacketsUpdateRunnable, 1, 2, TimeUnit.SECONDS);
        } else {
            if (mSchedulerPacketsUpdate != null) { mSchedulerPacketsUpdate.cancel(true); }
        }
    }

}
