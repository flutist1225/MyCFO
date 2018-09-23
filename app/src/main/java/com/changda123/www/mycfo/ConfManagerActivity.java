package com.changda123.www.mycfo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.changda123.www.mycfo.Util.MyLog;

public class ConfManagerActivity extends AppCompatActivity {

    private static final String TAG = "ConfManagerActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conf_manager);
        initView();
    }

    private void initView(){

        Button backupButton = findViewById(R.id.idButtonBackupDB);
        backupButton.setOnClickListener(v -> {
            //if(!CMyDBHelper.getInstance(this).IsBackupDatabaseExist()){
            MyLog.d(TAG, "Begin Backup DB...");
              CMyDBHelper.getInstance(this).BackupRecordToFile();
            //}
        });

        Button restoreButton = findViewById(R.id.idButtonRestoreDB);
        restoreButton.setOnClickListener(v -> {
            if(CMyDBHelper.getInstance(this).IsBackupDatabaseExist()){
                CMyDBHelper.getInstance(this).RestoreDBfromFile(this);
            }
        });

        Button restoreButton2 = findViewById(R.id.idButtonRestoreDBFromAssetDB);
        restoreButton2.setOnClickListener(v -> {

            CMyDBHelper.getInstance(this).RestoreRecordsFromAssetDB(this);

        });
    }


}
