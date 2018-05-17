package com.changda123.www.mycfo;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements
        AccountMainFragment.OnFragmentInteractionListener,
        AddRecordFragment.OnFragmentInteractionListener,
        ListRecordFragment.OnListFragmentInteractionListener,
        StatisticalFragment.OnFragmentInteractionListener {

    private static final String TAG = "MyCFO_MainActivity";

    private static final String DEFAULT_FRAGMENT = "mainFragment";

    MyCFOService.MyCFOServiceBinder mCFOServiceBinder;
    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_account:

                    return true;
                case R.id.navigation_note:

                    return true;
                case R.id.navigation_notifications:

                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 底部导航菜单View
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu();

        loadDefaultFragment();

        // 连接后台Service组件
        Intent startServiceIntent = new Intent(this, MyCFOService.class);
        if(null == startService(startServiceIntent)){
            new Throwable("无法启动服务");
        }
        bindService(startServiceIntent, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy(){

        super.onDestroy();
    }

    private void loadDefaultFragment(){
        // AccountMainFragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        AccountMainFragment newFragment = (AccountMainFragment) fragmentManager.findFragmentByTag(DEFAULT_FRAGMENT);
        if(null == newFragment) {
            newFragment = new AccountMainFragment();
            transaction.add(R.id.mainWindow, newFragment, DEFAULT_FRAGMENT);
        }

        transaction.show(newFragment);
        transaction.commit();
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

    public List<ContentValues> queryRecordList(int days){
        MyLog.d(TAG, "TabMenuActivity  queryRecordList ok" );
        if(null != mCFOServiceBinder) {
            return mCFOServiceBinder.listRecords(days);
        }else{
            return null;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        MyLog.d(TAG, "onFragmentInteraction url:" + uri.toString() );
    }

    @Override
    public void onListFragmentInteraction(ContentValues item) {
        MyLog.d(TAG, "onListFragmentInteraction item"  );
    }
}
