package com.changda123.www.mycfo;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyCFOService extends Service implements OnResult{
    private static final String TAG = "MyCFO_Service";
    private static final int MSG_TYPE_ADD_TRANSACTION      = 1;
    private static final int MSG_TYPE_MODIFY_TRANSACTION   = 2;
    private static final int MSG_TYPE_DELETE_TRANSACTION   = 3;
    private static final int MSG_TYPE_QUERY_TRANSACTION    = 4;

    private MyCFOServiceBinder mServiceBinder;
    private HandlerThread mWorkThread;
    private Handler  mWorkThreadHandler;

    private final byte[] mListRecordLock = new byte[0];
    private List<ContentValues> mRecordList;
    DBManager   mMyDBManager;
    SQLiteDatabase mDatabase;

    @Override
    public void onCreate() {
        MyLog.d(TAG, "=====     1. MyCFO Service Create ok     =====");
        super.onCreate();

        // 创建数据库和表
        //Todo 这里会因为数据库升级而导致开启APP卡顿吗？？，给APP一个状态提示最好
        mMyDBManager   = DBManager.getInstance(MyCFOService.this);
        mDatabase      = mMyDBManager.getWriteDatabase();
        // 启动工作线程
        initWorkThread();

        // 开门迎客
        mServiceBinder = new MyCFOServiceBinder();
    }

    @Override
    public IBinder onBind(Intent intent) {
        MyLog.d(TAG, "onBind" );
        return mServiceBinder;
    }

    @Override
    public void onDestroy() {
        MyLog.e(TAG, "################## MyCFO Service Destroy!##############");
        mWorkThread.quit();
        if(mDatabase != null){
            mDatabase.close();
        }
        super.onDestroy();
    }

    @Override
    public void onSuccess(ArrayList<ContentValues> list) {

    }

    @Override
    public void onError(String errMsg) {

    }

    public class MyCFOServiceBinder extends Binder {

        /**
         * 添加新交易纪录
         * @param strCategory
         * @param strEvent
         * @param strPrice
         * @param strLocation
         * @param lngTime
         * @param strWho
         * @param strPayType
         * @return
         */
        public int commitTransaction(String strCategory,
                                     String strEvent,
                                     String strPrice,
                                     String strLocation,
                                     long   lngTime,
                                     String strWho,
                                     String strPayType){

            ContentValues values = mMyDBManager.newRecord(strCategory,strEvent,strPrice,strLocation,lngTime,strWho,strPayType);

            if(sendMsgToWorkThread(MSG_TYPE_ADD_TRANSACTION,values)){
                return 0;
            }

            return -1;
        }

        /**
         * 查询某条记录
         */
        public List<ContentValues> listRecords(int days){
            if(days <1){
                return null;
            }

            synchronized (mListRecordLock) {

                if (sendMsgToWorkThread(MSG_TYPE_QUERY_TRANSACTION, days)) {
                    try {
                        mListRecordLock.wait();
                        return mRecordList;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        /**
         * 删除某条记录
         * @param id 待删除记录的id；
         */
        public void deleteRecord(int id){
            if(sendMsgToWorkThread(MSG_TYPE_DELETE_TRANSACTION, id)){

            }
        }
    }

    /**
     * 初始化循环取重工作线程
     */
    private void initWorkThread()
    {
        mWorkThread = new HandlerThread("MyCFO WorkThread");
        mWorkThread.start();

        mWorkThreadHandler = new Handler(mWorkThread.getLooper())
        {
            long retId = 0;
            @Override
            public void handleMessage(Message msg) {

                switch (msg.what) {
                    case MSG_TYPE_ADD_TRANSACTION:
                        ContentValues newRecord = (ContentValues)msg.obj;
                        MyLog.d(TAG, "MyCFO WorkThread rcv msg: MSG_TYPE_ADD_TRANSACTION time:" + newRecord.get(CMyDBHelper.FIELD_DISPLAY_TIME));
                        retId = mMyDBManager.insertRecord(CMyDBHelper.TABLE_NAME_RECORD,newRecord);
                        if(retId > 0){
                            onSuccess(null);
                        }else{
                            onError(null);
                        }
                        break;
                    case MSG_TYPE_MODIFY_TRANSACTION:
                        MyLog.d(TAG, "MyCFO WorkThread rcv msg: MSG_TYPE_MODIFY_TRANSACTION");

                        break;
                    case MSG_TYPE_DELETE_TRANSACTION:
                        MyLog.d(TAG, "MyCFO WorkThread rcv msg: MSG_TYPE_DELETE_TRANSACTION");

                        //生成条件语句
                        int id = (int)msg.obj;
                        StringBuffer whereBuffer = new StringBuffer();
                        whereBuffer.append(CMyDBHelper.FIELD_ID).append(" = ").append("'").append(id).append("'");
                        retId  = mDatabase.delete(CMyDBHelper.TABLE_NAME_RECORD, whereBuffer.toString(), null);
                        if(retId > 0){
                            onSuccess(null);
                        }else{
                            onError(null);
                        }
                        break;
                    case MSG_TYPE_QUERY_TRANSACTION:
                        MyLog.d(TAG, "MyCFO WorkThread rcv msg: MSG_TYPE_QUERY_TRANSACTION");
                        int days = (int)msg.obj;

                        java.util.Date date = new java.util.Date();
                        long currentTime = date.getTime();
                        long timeRange = currentTime - (long)days * 24*3600*1000;
                        String selection = CMyDBHelper.FIELD_TIME + ">" + timeRange;
                        MyLog.d(TAG, "MyCFO WorkThread selection:"+selection);

                        String[] columns = {CMyDBHelper.FIELD_CATEGORY, CMyDBHelper.FIELD_ID, CMyDBHelper.FIELD_DISPLAY_TIME,CMyDBHelper.FIELD_EVENT, CMyDBHelper.FIELD_PRICE};
                        /*
                        StringBuffer whereBuffer = new StringBuffer();
                        whereBuffer.append(CMyDBHelper.FIELD_NAME).append(" = ").append("'").append(name).append("'");
                        */
                        ArrayList<ContentValues> arrayContentList = mMyDBManager.queryRecordTable(columns,selection,null,null,null,null);
                        if(DBManager.RETCODE_VALUE_OK == arrayContentList.get(0).getAsInteger(DBManager.RETCODE_KEY_RESULT)){
                            arrayContentList.remove(0);
                            // Debug 代码
                            for(int i=0; i<arrayContentList.size(); i++) {
                                MyLog.d(TAG, "MyCFO WorkThread DB query, category: " + arrayContentList.get(i).getAsString(CMyDBHelper.FIELD_CATEGORY) +
                                        " id: " + arrayContentList.get(i).getAsString(CMyDBHelper.FIELD_ID) +
                                        " event: " + arrayContentList.get(i).getAsString(CMyDBHelper.FIELD_EVENT));

                            }

                            synchronized (mListRecordLock) {
                                mRecordList = arrayContentList;
                                mListRecordLock.notifyAll();
                            }
                            onSuccess(arrayContentList);

                        }else{
                            synchronized (mListRecordLock) {
                                mRecordList = null;
                                mListRecordLock.notifyAll();
                            }
                            onError(null);
                        }

                        break;
                    default:
                        MyLog.w(TAG, "LCWorkThread rcv an Invalid msg: " + msg.what);
                        break;
                }
            }
        };  // End of mWorkThreadHandler = new Handler(mWorkThread.getLooper())
    } // End of initWorkThread()


    private Boolean sendMsgToWorkThread(int msgId) {
        return null != mWorkThreadHandler && mWorkThreadHandler.sendEmptyMessage(msgId);
    }
    private Boolean sendMsgToWorkThread(int what, Object obj) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj  = obj;
        return null != mWorkThreadHandler &&  mWorkThreadHandler.sendMessage(msg);
    }
    private Boolean sendMsgToWorkThreadAtFront(int msgId) {
        if(null == mWorkThreadHandler){
            return false;
        }
        Message msg = mWorkThreadHandler.obtainMessage(msgId);
        return mWorkThreadHandler.sendMessageAtFrontOfQueue(msg);
    }
    private Boolean sendMsgToWorkThreadAtFront(Message msg) {
        return null != mWorkThreadHandler &&  mWorkThreadHandler.sendMessageAtFrontOfQueue(msg);
    }


}
