package com.changda123.www.mycfo;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.changda123.www.mycfo.dummy.DummyContent;

import java.util.ArrayList;
import java.util.List;

public class TabMenuActivity extends AppCompatActivity
        implements ListRecordFragment.OnListFragmentInteractionListener,
                   AddRecordFragment.OnFragmentInteractionListener,
                   StatisticalFragment.OnFragmentInteractionListener{

    private static final String TAG = "MyCFO_TabMenuActivity";
    MyCFOService.MyCFOServiceBinder mCFOServiceBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_menu);
        MyLog.d(TAG, "TabMenuActivity  onCreate" );
        Intent startServiceIntent = new Intent(this, MyCFOService.class);
        if(null == startService(startServiceIntent)){
            new Throwable("无法启动服务");
        }
        bindService(startServiceIntent, mConnection, BIND_AUTO_CREATE);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab) ;
        ViewPager viewpager = (ViewPager)findViewById(R.id.viewpager);
        List<Fragment> fragments = new ArrayList<>();

        fragments.add(new AddRecordFragment());
        fragments.add(new ListRecordFragment());
        fragments.add(new StatisticalFragment());

        TitleFragmentPagerAdapter adapter = new TitleFragmentPagerAdapter(getSupportFragmentManager(), fragments, new String[]{"新增交易", "交易记录", "统计报表"});
        viewpager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewpager);


    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Toast.makeText(this,"This is onFragmentInteraction", Toast.LENGTH_LONG).show();
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mCFOServiceBinder = (MyCFOService.MyCFOServiceBinder) service;
            MyLog.d(TAG, "TabMenuActivity  onServiceConnected ok" );
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            MyLog.d(TAG, "TabMenuActivity onServiceDisconnected ok");
        }
    };

    public void addNewRecord(String strCategory,
                   String strEvent,
                   String strPrice,
                   String strLocation,
                   long   lngTime,
                   String strWho,
                   String strPayType){
        mCFOServiceBinder.commitTransaction(strCategory, strEvent, strPrice, strLocation, lngTime,strWho, strPayType);
    }

    public List<ContentValues> queryRecord(int days){
        MyLog.d(TAG, "TabMenuActivity  queryRecord ok" );
        if(null != mCFOServiceBinder) {
            return mCFOServiceBinder.listRecords(days);
        }else{
            return null;
        }
    }
    @Override
    public void onListFragmentInteraction(ContentValues item) {

    }
}
