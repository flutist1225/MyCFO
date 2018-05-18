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
    private static final int MSG_ADD_RECORD           = 1;
    private static final int MSG_MODIFY_RECORD   = 2;
    private static final int MSG_DELETE_RECORD   = 3;
    private static final int MSG_QUERY_RECORD    = 4;
    private static final int MSG_QUERY_SUBTOTAL_BY_PERIOD  = 5;
    private static final int MSG_QUERY_SUBTOTAL_BY_TYPE    = 6;

    private static final int STATISTIC_PERIOD_BY_YEAR    = 1;
    private static final int STATISTIC_PERIOD_BY_MONTH   = 2;
    private static final int STATISTIC_PERIOD_BY_WEEK    = 3;

    private MyCFOServiceBinder mServiceBinder;
    private HandlerThread mWorkThread;
    private Handler  mWorkThreadHandler;

    private final byte[] mListRecordLock = new byte[0];
    private List<ContentValues> mRecordList;
    DBManager      mMyDBManager;
    SQLiteDatabase mDBHandler;

    @Override
    public void onCreate() {
        MyLog.d(TAG, "=====     1. MyCFO Service Create ok     =====");
        super.onCreate();

        // 创建数据库和表
        //Todo 这里会因为数据库升级而导致开启APP卡顿吗？？，给APP一个状态提示最好
        mMyDBManager   = DBManager.getInstance(MyCFOService.this);
        mDBHandler     = mMyDBManager.getWriteDBHandler();
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
        if(mDBHandler != null){
            mDBHandler.close();
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

            ContentValues values = mMyDBManager.fillNewRecord(strCategory,strEvent,strPrice,strLocation,lngTime,strWho,strPayType);

            if(sendMsgToWorkThread(MSG_ADD_RECORD,values)){
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

                if (sendMsgToWorkThread(MSG_QUERY_RECORD, days)) {
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
        public List<ContentValues> getAllRecords(int periodType, int periodValue){

            Message msg = Message.obtain();
            msg.what = MSG_QUERY_SUBTOTAL_BY_PERIOD;
            msg.arg1 = periodType;
            msg.arg2 = periodValue;
            mWorkThreadHandler.sendMessage(msg);

            return null;
        }
        /**
         * 删除某条记录
         * @param id 待删除记录的id；
         */
        public void deleteRecord(int id){
            if(sendMsgToWorkThread(MSG_DELETE_RECORD, id)){

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
                    case MSG_ADD_RECORD:
                        ContentValues newRecord = (ContentValues)msg.obj;
                        MyLog.d(TAG, "MyCFO WorkThread rcv msg: MSG_ADD_RECORD time:" + newRecord.get(CMyDBHelper.FIELD_DISPLAY_TIME));
                        retId = mMyDBManager.insertRecord(CMyDBHelper.TABLE_NAME_RECORD,newRecord);
                        if(retId > 0){
                            onSuccess(null);
                        }else{
                            onError(null);
                        }
                        break;
                    case MSG_MODIFY_RECORD:
                        MyLog.d(TAG, "MyCFO WorkThread rcv msg: MSG_MODIFY_RECORD");

                        break;
                    case MSG_DELETE_RECORD:
                        MyLog.d(TAG, "MyCFO WorkThread rcv msg: MSG_DELETE_RECORD");

                        //生成条件语句
                        int id = (int)msg.obj;
                        StringBuffer whereBuffer = new StringBuffer();
                        whereBuffer.append(CMyDBHelper.FIELD_ID).append(" = ").append("'").append(id).append("'");
                        retId  = mDBHandler.delete(CMyDBHelper.TABLE_NAME_RECORD, whereBuffer.toString(), null);
                        if(retId > 0){
                            onSuccess(null);
                        }else{
                            onError(null);
                        }
                        break;
                    case MSG_QUERY_RECORD:
                        MyLog.d(TAG, "MyCFO WorkThread rcv msg: MSG_QUERY_RECORD");
                        int days = (int)msg.obj;

                        java.util.Date date = new java.util.Date();
                        long currentTime = date.getTime();
                        long timeRange = currentTime - (long)days * 24*3600*1000;
                        String selection = CMyDBHelper.FIELD_TIME + ">" + timeRange;
                        MyLog.d(TAG, "MyCFO WorkThread selection:"+selection);

                        String[] columns = {CMyDBHelper.FIELD_CATEGORY, CMyDBHelper.FIELD_ID, CMyDBHelper.FIELD_TIME,CMyDBHelper.FIELD_EVENT, CMyDBHelper.FIELD_PRICE, CMyDBHelper.FIELD_YEAR, CMyDBHelper.FIELD_MONTH, CMyDBHelper.FIELD_WEEK};
                        //String[] columns = {CMyDBHelper.FIELD_CATEGORY, CMyDBHelper.FIELD_ID, CMyDBHelper.FIELD_TIME,CMyDBHelper.FIELD_EVENT, CMyDBHelper.FIELD_PRICE};
                        /*
                        StringBuffer whereBuffer = new StringBuffer();
                        whereBuffer.append(CMyDBHelper.FIELD_NAME).append(" = ").append("'").append(name).append("'");
                        */
                        ArrayList<ContentValues> arrayContentList = mMyDBManager.getRecordList(columns,selection,null,null,null,null);
                        if(DBManager.RETCODE_VALUE_OK == arrayContentList.get(0).getAsInteger(DBManager.RETCODE_KEY_RESULT)){
                            arrayContentList.remove(0);
                            // Debug 代码
                            for(int i=0; i<arrayContentList.size(); i++) {
                                MyLog.d(TAG, "MyCFO WorkThread DB query, category: " + arrayContentList.get(i).getAsString(CMyDBHelper.FIELD_CATEGORY) +
                                        " id: " + arrayContentList.get(i).getAsString(CMyDBHelper.FIELD_ID) +
                                        " event: " + arrayContentList.get(i).getAsString(CMyDBHelper.FIELD_EVENT)+

                                        " year: " + arrayContentList.get(i).getAsInteger(CMyDBHelper.FIELD_YEAR) +
                                        " mon: " + arrayContentList.get(i).getAsInteger(CMyDBHelper.FIELD_MONTH) +
                                        " week: " + arrayContentList.get(i).getAsInteger(CMyDBHelper.FIELD_WEEK));

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
                    case MSG_QUERY_SUBTOTAL_BY_PERIOD:
                        int periodType = msg.arg1;
                        int param = msg.arg2;

                        MyLog.d(TAG, "MSG_QUERY_SUBTOTAL_BY_PERIOD Begin" );
                        doQuerySubTotalByPeriod(periodType,param);
                        break;
                    case MSG_QUERY_SUBTOTAL_BY_TYPE:

                        break;
                    default:
                        MyLog.w(TAG, "LCWorkThread rcv an Invalid msg: " + msg.what);
                        break;
                }
            }
        };  // End of mWorkThreadHandler = new Handler(mWorkThread.getLooper())
    } // End of initWorkThread()

    /**
     * 全部种类的某年/月/周的对比（种类间对比）
     * @param periodType
     * @param param
     */
    public void doQuerySubTotalByPeriod(int periodType, int param){

        java.util.Date date = new java.util.Date();
        long currentTime = date.getTime();
        long timeRange = currentTime - (long)10 * 24*3600*1000;
        doQueryTotalGroupByPeriod(periodType, timeRange);
    }

    public void doQueryTotalByPeroid(int periodType, int param){
        doQuerySubtotalGroupByCategory(periodType,param);
    }

    /**
     * 全部分类在指定时间条件（年/月/周）的分别费用小计，对比各类所占比例
     * @param periodType
     * @param periodValue
     */
    private ArrayList<ContentValues> doQuerySubtotalGroupByCategory(int periodType, int periodValue){

        String[] columns = {CMyDBHelper.FIELD_CATEGORY, "SUM("+ CMyDBHelper.FIELD_PRICE +")"};
        String   groupBy =  CMyDBHelper.FIELD_CATEGORY;

        String selection = null;

        if( -1 < periodValue ) {
            switch (periodType) {
                case STATISTIC_PERIOD_BY_YEAR:
                    selection = CMyDBHelper.FIELD_YEAR + "=" + periodValue;
                    break;
                case STATISTIC_PERIOD_BY_WEEK:
                    selection = CMyDBHelper.FIELD_WEEK + "=" + periodValue;
                    break;
                case STATISTIC_PERIOD_BY_MONTH:
                default:
                    selection = CMyDBHelper.FIELD_MONTH + "=" + periodValue;
                    break;
            }
        }

        MyLog.d(TAG, "columns:"+ columns[1]+ " select:"+ selection + " groupby:"+ groupBy);
        ArrayList<ContentValues> arrayContentList = mMyDBManager.getRecordList(columns,selection, null, groupBy,null,null);
        if(DBManager.RETCODE_VALUE_OK == arrayContentList.get(0).getAsInteger(DBManager.RETCODE_KEY_RESULT)) {
            arrayContentList.remove(0);
            // Debug:
            for(int i=0; i<arrayContentList.size(); i++) {
                MyLog.d(TAG, "doQuerySubTotalByPeriod, category: " + arrayContentList.get(i).getAsString(CMyDBHelper.FIELD_CATEGORY) +
                        " sum: " + arrayContentList.get(i).getAsInteger("SUM("+ CMyDBHelper.FIELD_PRICE +")"));
            }
        }
        return arrayContentList;
    }

    /**
     * 查询在从beginTime开始的，每个年/月/周周期的总费用。 对比总费用的变化
     * @param periodType
     * @param beginTime
     * @return
     */
    private ArrayList<ContentValues> doQueryTotalGroupByPeriod(int periodType, long beginTime){

        String[] columns = null;
        String selection = null;
        String   groupBy = null;

        switch (periodType) {
            case STATISTIC_PERIOD_BY_YEAR:
                columns   = new String[]{CMyDBHelper.FIELD_YEAR, "SUM(" + CMyDBHelper.FIELD_PRICE + ")"};
                selection = CMyDBHelper.FIELD_TIME + ">" + beginTime;
                groupBy   = CMyDBHelper.FIELD_YEAR;
                break;
            case STATISTIC_PERIOD_BY_WEEK:
                columns   = new String[]{CMyDBHelper.FIELD_WEEK, "SUM(" + CMyDBHelper.FIELD_PRICE + ")"};
                selection = CMyDBHelper.FIELD_TIME + ">" + beginTime;
                groupBy   = CMyDBHelper.FIELD_WEEK;
                break;
            case STATISTIC_PERIOD_BY_MONTH:
            default:
                columns   = new String[]{CMyDBHelper.FIELD_MONTH, "SUM(" + CMyDBHelper.FIELD_PRICE + ")"};
                selection = CMyDBHelper.FIELD_TIME + ">" + beginTime;
                groupBy   = CMyDBHelper.FIELD_MONTH;
                break;
        }


        MyLog.d(TAG, "columns:"+ columns[1]+ " select:"+ selection + " groupby:"+ groupBy);
        ArrayList<ContentValues> arrayContentList = mMyDBManager.getRecordList(columns,selection, null, groupBy,null,null);
        if(DBManager.RETCODE_VALUE_OK == arrayContentList.get(0).getAsInteger(DBManager.RETCODE_KEY_RESULT)) {
            arrayContentList.remove(0);
            for(int i=0; i<arrayContentList.size(); i++) {
                MyLog.d(TAG, "doQueryTotalGroupByPeriod,  sum: " + arrayContentList.get(i).getAsInteger("SUM("+ CMyDBHelper.FIELD_PRICE +")"));
            }
        }
        return arrayContentList;
    }

    /**
     * 某一类一定时间来的subtotal对比。 单类费用的变化
     * @param catogery
     * @param periodType
     * @param beginTime
     * @return
     */

    private ArrayList<ContentValues> doQuerySubtotalByCategory(String catogery, int periodType, long beginTime){

        String[] columns = null;
        String selection = null;
        String   groupBy = null;

        switch (periodType) {
            case STATISTIC_PERIOD_BY_YEAR:
                columns   = new String[]{CMyDBHelper.FIELD_YEAR, "SUM(" + CMyDBHelper.FIELD_PRICE + ")"};
                selection = CMyDBHelper.FIELD_TIME + " > " + beginTime + " AND " +
                            CMyDBHelper.FIELD_CATEGORY + " = " + catogery;

                groupBy   = CMyDBHelper.FIELD_YEAR;
                break;
            case STATISTIC_PERIOD_BY_WEEK:
                columns   = new String[]{CMyDBHelper.FIELD_WEEK, "SUM(" + CMyDBHelper.FIELD_PRICE + ")"};
                selection = CMyDBHelper.FIELD_TIME + " > " + beginTime + " AND " +
                            CMyDBHelper.FIELD_CATEGORY + " = " + catogery;

                groupBy   = CMyDBHelper.FIELD_WEEK;
                break;
            case STATISTIC_PERIOD_BY_MONTH:
            default:
                columns   = new String[]{CMyDBHelper.FIELD_MONTH, "SUM(" + CMyDBHelper.FIELD_PRICE + ")"};
                selection = CMyDBHelper.FIELD_TIME + " > " + beginTime + " AND " +
                            CMyDBHelper.FIELD_CATEGORY + " = " + catogery;

                groupBy   = CMyDBHelper.FIELD_MONTH;
                break;
        }


        MyLog.d(TAG, "columns:"+ columns[1]+ " select:"+ selection + " groupby:"+ groupBy);
        ArrayList<ContentValues> arrayContentList = mMyDBManager.getRecordList(columns,selection, null, groupBy,null,null);
        if(DBManager.RETCODE_VALUE_OK == arrayContentList.get(0).getAsInteger(DBManager.RETCODE_KEY_RESULT)) {
            arrayContentList.remove(0);
            for(int i=0; i<arrayContentList.size(); i++) {
                MyLog.d(TAG, "doQuerySubTotalByPeriod, category: " + arrayContentList.get(i).getAsString(CMyDBHelper.FIELD_CATEGORY) +
                        " sum: " + arrayContentList.get(i).getAsInteger("SUM("+ CMyDBHelper.FIELD_PRICE +")"));
            }
        }
        return arrayContentList;
    }

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
