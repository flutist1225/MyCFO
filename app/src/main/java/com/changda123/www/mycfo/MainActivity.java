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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.widget.TextView;

import com.changda123.www.mycfo.Account.AccountMainFragment;
import com.changda123.www.mycfo.Account.AddRecord.AddRecordFragment;
import com.changda123.www.mycfo.Account.RecordList.ListRecordFragment;
import com.changda123.www.mycfo.Account.Statistics.StatisticalFragment;
import com.changda123.www.mycfo.BaseClass.BaseActivity;
import com.changda123.www.mycfo.Util.MyLog;

import java.util.List;

public class MainActivity extends BaseActivity implements
        AccountMainFragment.OnFragmentInteractionListener,
        AddRecordFragment.OnFragmentInteractionListener,
        ListRecordFragment.OnListFragmentInteractionListener,
        StatisticalFragment.OnFragmentInteractionListener {

    private static final String TAG = "MyCFO_MainActivity";

    private static final String DEFAULT_FRAGMENT = "mainFragment";

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
                    Intent intent = new Intent(MainActivity.this, ConfManagerActivity.class);
                    startActivity(intent);
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
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu();

        loadDefaultFragment();

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


    @Override
    public void onFragmentInteraction(Uri uri) {
        MyLog.d(TAG, "onFragmentInteraction url:" + uri.toString() );
    }

    @Override
    public void onListFragmentInteraction(ContentValues item) {
        MyLog.d(TAG, "onListFragmentInteraction item"  );
    }
}
