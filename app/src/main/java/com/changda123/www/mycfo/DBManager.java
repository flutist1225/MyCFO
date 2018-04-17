package com.changda123.www.mycfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DBManager {
    private static final String TAG = "MyCFO_DBManager";
    public static final String RETCODE_KEY_RESULT = "result";
    public static final int RETCODE_VALUE_OK         = 0;
    public static final int RETCODE_VALUE_NO_RECORD  = 1;
    public static final int RETCODE_VALUE_QUERY_FAIL = 2;

    // 静态引用
    private volatile static DBManager mInstance;
    // DatabaseHelper
    private CMyDBHelper mDbHelper;
    private SQLiteDatabase mDbWriteConnection;

    private DBManager(Context context) {
        mDbHelper = new CMyDBHelper(context);
        mDbWriteConnection = mDbHelper.getWritableDatabase();
    }

    /**
     * 获取单例引用
     *
     * @param context
     * @return
     */
    static DBManager getInstance(Context context) {
        DBManager inst = mInstance;
        if (inst == null) {
            synchronized (DBManager.class) {
                inst = mInstance;
                if (inst == null) {
                    inst = new DBManager(context);
                    mInstance = inst;
                }
            }
        }
        return inst;
    }

    /**
     * 构建要新增记录到record表的ContentValues对象
     * @param strCategory
     * @param strEvent
     * @param strPrice
     * @param strLocation
     * @param lngTime
     * @param strWho
     * @param strPayType
     * @return 构建好的ContentValues对象
     */
    ContentValues newRecord(String strCategory,
                                   String strEvent,
                                   String strPrice,
                                   String strLocation,
                                   long lngTime,
                                   String strWho,
                                   String strPayType){

        ContentValues values = new ContentValues();
        // ContenValues Key只能是String类型，Value只能存储基本类型数据，不能存储对象
        values.put(CMyDBHelper.FIELD_CATEGORY, strCategory);
        values.put(CMyDBHelper.FIELD_EVENT, strEvent);
        values.put(CMyDBHelper.FIELD_PRICE, convertPriceToInt(strPrice));
        values.put(CMyDBHelper.FIELD_LOCATION, strLocation);
        values.put(CMyDBHelper.FIELD_TIME, lngTime);
        values.put(CMyDBHelper.FIELD_DISPLAY_TIME, dateToString(new Date(lngTime)));
        values.put(CMyDBHelper.FIELD_WHO, strWho);
        values.put(CMyDBHelper.FIELD_PAY_TYPE, strPayType);
        MyLog.d(TAG, "MyCFO newRecord time:"+lngTime);

        return values;
    }

    long insertRecord(String table, ContentValues values){
        // 调用insert()方法将数据插入到数据库当中
        long id = getWriteDatabase().insert(table, null, values);
        MyLog.d(TAG, "saveTransactionToDB ret id:"+id);
        return id;
    }

    /**
     * 查询交易记录表，并将记录存入ContentValues数组链表中返回
     * @param columns
     * @param selection
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @return
     */
    ArrayList<ContentValues> queryRecordTable(String[] columns,
                                                      String selection,
                                                      String[] selectionArgs,
                                                      String groupBy,
                                                      String having,
                                                      String orderBy) {

        Cursor cursor = getWriteDatabase().query(CMyDBHelper.TABLE_NAME_RECORD, columns, selection, selectionArgs, groupBy, having, orderBy);
        // Error
        if (cursor == null) {
            MyLog.w(TAG, "MyCFO WorkThread query fail. cursor == null.");
            return  setReturnCode(RETCODE_VALUE_QUERY_FAIL);
        }

        // No record
        if  (cursor.getCount()<1){
            MyLog.d(TAG, "MyCFO WorkThread query return "+cursor.getCount()+" records");
            cursor.close();
            return  setReturnCode(RETCODE_VALUE_NO_RECORD);
        }

        // return records
        ArrayList<ContentValues> list = new ArrayList<>();
        int columnsCnt = columns.length;
        ContentValues value = new ContentValues();
        value.put(RETCODE_KEY_RESULT, RETCODE_VALUE_OK);
        list.add(value);

        //遍历Cursor
        while(cursor.moveToNext()){
            value = new ContentValues();

            for(int i=0; i<columnsCnt;i++) {
                if(columns[i].equals(mDbHelper.FIELD_PRICE)) {
                    value.put(columns[i], showPrice(cursor.getInt(i)));
                }else{
                    value.put(columns[i], cursor.getString(i));
                }
            }
            list.add(value);
        }
        cursor.close();
        return list;

    }

    /**
     * 删除指定表的所有数据
     * @param db 数据库
     * @param table 表名
     */
    synchronized void deleteDatas(SQLiteDatabase db, String table)
    {
        String sql="delete from "+ table;
        db.execSQL(sql);
    }
    /**
     * 打开数据库写连接
     * @return
     */
    SQLiteDatabase getWriteDatabase(){
        if(null == mDbWriteConnection){
            mDbWriteConnection = mDbHelper.getWritableDatabase();
        }
        return mDbWriteConnection;
    }

    /**
     * 关闭数据库连接
     */
    public void closeDatabase(){
        if(null != mDbWriteConnection){
            mDbWriteConnection.close();
        }
    }

    private static int convertPriceToInt(String price){
        return (int)(Float.parseFloat(price)*100);
    }
    private String showPrice(int iPrice){
        float num= (float)iPrice/100;
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(num);
    }

    private static String dateToString(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;
        return  format.format(date);
    }

    /**
     * 执行sql语句
     */
    private void execSQL(String sql){
        //直接执行sql语句
        getWriteDatabase().execSQL(sql);//或者
    }

    private ArrayList<ContentValues> setReturnCode(int returnCode){
        ArrayList<ContentValues> list = new ArrayList<>();
        ContentValues value = new ContentValues();
        value.put(RETCODE_KEY_RESULT, returnCode);
        list.add(value);
        return list;
    }
}
