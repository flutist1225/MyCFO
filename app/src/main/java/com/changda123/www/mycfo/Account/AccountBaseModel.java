package com.changda123.www.mycfo.Account;

import android.content.ContentValues;
import android.content.Context;

import com.changda123.www.mycfo.BaseClass.ICallbackOnModelFinished;
import com.changda123.www.mycfo.CMyDBHelper;
import com.changda123.www.mycfo.Util.MyLog;

import java.util.ArrayList;
import java.util.List;


public class AccountBaseModel  {
    private static final String TAG = "AccountBaseModel";

    public static final int STATISTIC_PERIOD_BY_YEAR    = 1;
    public static final int STATISTIC_PERIOD_BY_MONTH   = 2;
    public static final int STATISTIC_PERIOD_BY_WEEK    = 3;

    private CMyDBHelper mMyDBHelper;

    public AccountBaseModel(Context context){
        mMyDBHelper   = CMyDBHelper.getInstance(context);
    }

    public void addRecord(String strCategory,
                          String strEvent,
                          String strPrice,
                          long lngTime,
                          String strWho,
                          ICallbackOnModelFinished<Long> callback) {

        ContentValues data = mMyDBHelper.fillNewRecord(strCategory,strEvent,strPrice,lngTime,strWho);

        long retId = mMyDBHelper.insertRecord(CMyDBHelper.TABLE_NAME_RECORD, data);
        if(retId > 0){
            callback.onSuccess(retId);
        }else{
            callback.onFailure("insert new record to table "+CMyDBHelper.TABLE_NAME_RECORD + " is failed!");
        }
        callback.onComplete();
    }

    public void getListOfRecords(int days, ICallbackOnModelFinished<List> callback) {
        String[] columns = {CMyDBHelper.FIELD_CATEGORY, CMyDBHelper.FIELD_ID, CMyDBHelper.FIELD_TIME,CMyDBHelper.FIELD_EVENT, CMyDBHelper.FIELD_PRICE, CMyDBHelper.FIELD_YEAR, CMyDBHelper.FIELD_MONTH, CMyDBHelper.FIELD_WEEK};
        java.util.Date date = new java.util.Date();
        long currentTime = date.getTime();
        long timeRange = currentTime - (long)days * 24*3600*1000;
        String selection = CMyDBHelper.FIELD_TIME + ">" + timeRange;
        MyLog.d(TAG, "MyCFO WorkThread selection:"+selection);
        String orderBy = CMyDBHelper.FIELD_TIME +" DESC";

        ArrayList<ContentValues> arrayContentList = mMyDBHelper.getListOfRecord(CMyDBHelper.TABLE_NAME_RECORD, columns, selection,null,null,null,orderBy);

        // Debug 代码
        for(int i=0; i<arrayContentList.size(); i++) {
            MyLog.d(TAG, "MyCFO WorkThread DB query, category: " + arrayContentList.get(i).getAsString(CMyDBHelper.FIELD_CATEGORY) +
                    " id: " + arrayContentList.get(i).getAsString(CMyDBHelper.FIELD_ID) +
                    " event: " + arrayContentList.get(i).getAsString(CMyDBHelper.FIELD_EVENT)+

                    " year: " + arrayContentList.get(i).getAsInteger(CMyDBHelper.FIELD_YEAR) +
                    " mon: " + arrayContentList.get(i).getAsInteger(CMyDBHelper.FIELD_MONTH) +
                    " week: " + arrayContentList.get(i).getAsInteger(CMyDBHelper.FIELD_WEEK));

        }

        if(arrayContentList.size() > 0){
            callback.onSuccess(arrayContentList);
        }else{
            callback.onFailure("getListOfRecords in table "+CMyDBHelper.TABLE_NAME_RECORD + " return failed!");
        }
        callback.onComplete();
    }

    public void deleteRecord(long id, ICallbackOnModelFinished<String> callback){
        long retId  = mMyDBHelper.deleteRecord(CMyDBHelper.TABLE_NAME_RECORD, CMyDBHelper.FIELD_ID + " = " + "'" + id + "'", null);
        if(retId > 0){
            callback.onSuccess(null);
        }else{
            callback.onFailure("delRecord in table "+CMyDBHelper.TABLE_NAME_RECORD + " is failed!");
        }
        callback.onComplete();
    }

    public void updateRecord(long id,
                             String strCategory,
                             String strEvent,
                             String strPrice,
                             long lngTime,
                             String strWho,
                             ICallbackOnModelFinished<String> callback){
        ContentValues data = mMyDBHelper.fillNewRecord(strCategory,strEvent,strPrice,lngTime,strWho);
        long retId  = mMyDBHelper.updateRecord(CMyDBHelper.TABLE_NAME_RECORD, data, CMyDBHelper.FIELD_ID + " = " + "'" + id + "'", null);
        if(retId > 0){
            callback.onSuccess(null);
        }else{
            callback.onFailure("updateRecord in table "+CMyDBHelper.TABLE_NAME_RECORD + " is failed!");
        }
        callback.onComplete();
    }
    /**
     * 全部分类在指定时间条件（年/月/周）的分别费用小计，对比各类所占比例
     * @param periodType
     * @param periodValue
     */
    public void querySubtotalForAllByCategory(int periodType, int periodValue, ICallbackOnModelFinished<List> callback) {

        String[] columns = {CMyDBHelper.FIELD_CATEGORY, CMyDBHelper.FIELD_PRICE_SUM};
        String groupBy = CMyDBHelper.FIELD_CATEGORY;

        String selection = null;

        if (-1 < periodValue) {
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

        ArrayList<ContentValues> arrayContentList = mMyDBHelper.getListOfRecord(CMyDBHelper.TABLE_NAME_RECORD, columns, selection, null,groupBy,null,null);

        if(arrayContentList.size() > 0){
            callback.onSuccess(arrayContentList);
        }else{
            callback.onFailure("querySubtotalForAllByCategory in table "+CMyDBHelper.TABLE_NAME_RECORD + " return failed!");
        }
        callback.onComplete();
    }


    /**
     * 某一类一定时间来的subtotal对比。 单类费用的变化
     * @param category  事件类别（衣食住行等）
     * @param periodType 查询周期的类型，取值：年、月、周
     * @param beginTime 查询起始时间，单位：毫秒
     * @return 无返回值，回调通知Presenter
     */
    public void querySubtotalByPeriod(String category, int periodType, long beginTime, ICallbackOnModelFinished<List> callback) {

        String[] columns = null;
        String selection = null;
        String groupBy = null;

        switch (periodType) {
            case STATISTIC_PERIOD_BY_YEAR:
                columns = new String[]{CMyDBHelper.FIELD_YEAR, CMyDBHelper.FIELD_PRICE_SUM};
                selection = CMyDBHelper.FIELD_TIME + " > " + beginTime ;
                if(category != null) {
                    selection += " AND " + CMyDBHelper.FIELD_CATEGORY + " = '" + category + "'";
                }
                groupBy = CMyDBHelper.FIELD_YEAR;
                break;
            case STATISTIC_PERIOD_BY_WEEK:
                columns = new String[]{CMyDBHelper.FIELD_WEEK, CMyDBHelper.FIELD_PRICE_SUM};
                selection = CMyDBHelper.FIELD_TIME + " > " + beginTime ;
                if(category != null) {
                    selection += " AND " + CMyDBHelper.FIELD_CATEGORY + " = '" + category + "'";
                }

                groupBy = CMyDBHelper.FIELD_WEEK;
                break;
            case STATISTIC_PERIOD_BY_MONTH:
            default:
                columns = new String[]{CMyDBHelper.FIELD_MONTH, CMyDBHelper.FIELD_PRICE_SUM};
                selection = CMyDBHelper.FIELD_TIME + " > " + beginTime;
                if(category != null) {
                    selection += " AND " + CMyDBHelper.FIELD_CATEGORY + " = '" + category + "'";
                }

                groupBy = CMyDBHelper.FIELD_MONTH;
                break;
        }
        ArrayList<ContentValues> arrayContentList = mMyDBHelper.getListOfRecord(CMyDBHelper.TABLE_NAME_RECORD, columns, selection, null,groupBy,null,null);

        if(arrayContentList.size() > 0){
            callback.onSuccess(arrayContentList);
        }else{
            callback.onFailure("querySubtotalByPeriod in table "+CMyDBHelper.TABLE_NAME_RECORD + " return failed!");
        }
        callback.onComplete();

    }

    public String getFieldNameCategory(){
        return CMyDBHelper.FIELD_CATEGORY;
    }

    public String getFieldNameSumPrice(){
        return CMyDBHelper.FIELD_PRICE_SUM;
    }

    public String getFieldNamePeriod(int periodType){
        switch (periodType){
            case STATISTIC_PERIOD_BY_YEAR:
                return CMyDBHelper.FIELD_YEAR;

            case STATISTIC_PERIOD_BY_WEEK:
                return CMyDBHelper.FIELD_WEEK;
            case STATISTIC_PERIOD_BY_MONTH:
            default:
                return CMyDBHelper.FIELD_MONTH;
        }
    }

}
