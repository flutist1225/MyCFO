package com.changda123.www.mycfo;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

public class CMyDBHelper extends SQLiteOpenHelper{
    private static final String TAG = "MyCFO_CMyDBHelper";
    //数据库的版本号
    private static final int DATABASE_VERSION = 1000;

    //数据库名称
    private static final String DATABASE_NAME = "MyCFO.db";
    //表名+字段
    public static final String TABLE_NAME_RECORD = "records";// records表
    public static final String FIELD_ID = "id";
    public static final String FIELD_CATEGORY = "category";
    public static final String FIELD_EVENT = "event";
    public static final String FIELD_PRICE = "price";
    public static final String FIELD_LOCATION = "location";
    public static final String FIELD_TIME ="time";
    public static final String FIELD_DISPLAY_TIME ="display_time";
    public static final String FIELD_WHO = "who";
    public static final String FIELD_PAY_TYPE = "pay_type";

    public static final String TABLE_NAME_CATEGORY = "category";// category表
    public static final String FIELD_NAME = "category";
    public static final String FIELD_DISPLAY_FLAG = "isDisplay";

    public static final String TABLE_NAME_PERSON = "person";// person表
    public static final String FIELD_PEOPLE_NAME = "who";

    public static final String TABLE_NAME_PAY = "pay";// pay表
    public static final String FIELD_NAME_PAY_TYPE = "name";
    //自定义类别和Person的最大字符数
    private static final int MAX_CHAR_NUMBER = 10;

    public CMyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        MyLog.i(TAG, "CMyDBHelper onCreate.");
        createRecordsTable(db);
        createCategoryTable(db);
        createPeopleTable(db);
        createPayTypeTable(db);

        // 若不是第一个版本安装，直接执行数据库升级
        // !!请不要修改FIRST_DATABASE_VERSION的值，其为第一个数据库版本大小
        final int FIRST_DATABASE_VERSION = 1000;
        onUpgrade(db, FIRST_DATABASE_VERSION, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 使用for实现跨版本升级数据库
        for (int i = oldVersion; i < newVersion; i++) {
            switch (i) {
                /*
                case 1000:
                    String sql1 = "ALTER TABLE "+SQL.T_FAVORITE+" ADD COLUMN deleted VARCHAR";
                    db.execSQL(sql1);
                    break;
                case 1001:
                    String sql1 = "ALTER TABLE "+SQL.T_FAVORITE+" ADD COLUMN message VARCHAR";
                    db.execSQL(sql1);
                    break;
                */
                default:
                    break;
            }
        }
    }
    /**
     * 创建账目流水表
     * @param db 数据库
     * @return
     */
    private synchronized boolean createRecordsTable(SQLiteDatabase db){
        // 创建交易记录表
        String  sql = "CREATE TABLE IF NOT EXISTS [" + TABLE_NAME_RECORD + "] " +
                "(" +
                FIELD_ID + " integer primary key autoincrement , " +
                FIELD_CATEGORY + " nvarchar(" + MAX_CHAR_NUMBER + ") not null, " +
                FIELD_EVENT + " nvarchar(50) not null, " +
                FIELD_PRICE + " int, " +
                FIELD_TIME + " int, " +
                FIELD_DISPLAY_TIME + " nvarchar(50) not null, " +
                FIELD_WHO + " nvarchar(" + MAX_CHAR_NUMBER + ")," +
                FIELD_LOCATION + " nvarchar(50), " +
                FIELD_PAY_TYPE + " nvarchar(" + MAX_CHAR_NUMBER + ") " +
                ");";

        MyLog.d(TAG, "createRecordsTable execSQL: " + sql);
        try {
            db.execSQL(sql);
            return true;
        } catch (SQLException e) {
            MyLog.e(TAG, "onCreate " + TABLE_NAME_RECORD + "Error" + e.toString());
        }
        return false;
    }

    /**
     * 创建账目类别表
     * @param db 数据库
     * @return
     */
    private synchronized boolean createCategoryTable(SQLiteDatabase db){
        // 创建账目类别记录表
        String  sql = "CREATE TABLE " + TABLE_NAME_CATEGORY +
                "(" +
                FIELD_NAME + " nvarchar(" + MAX_CHAR_NUMBER + ") , " +
                FIELD_DISPLAY_FLAG + " int " +
                ");";
        MyLog.d(TAG, "createCategoryTable execSQL: " + sql);
        try {
            db.execSQL(sql);
            return true;
        } catch (SQLException e) {
            MyLog.e(TAG, "onCreate " + TABLE_NAME_CATEGORY + "Error" + e.toString());
        }

        return false;
    }

    /**
     * 创建相关人物名称表
     * @param db 数据库
     * @return
     */
    private synchronized boolean createPeopleTable(SQLiteDatabase db){
        // 创建人物表
        String  sql = "CREATE TABLE " + TABLE_NAME_PERSON +
                "(" +
                FIELD_PEOPLE_NAME + " nvarchar(" + MAX_CHAR_NUMBER + ")  " +
                ");";
        MyLog.d(TAG, "createPeopleTable execSQL: " + sql);
        try {
            db.execSQL(sql);
            return true;
        } catch (SQLException e) {
            MyLog.e(TAG, "onCreate " + TABLE_NAME_PERSON + "Error" + e.toString());
        }

        return false;
    }

    /**
     * 用户付款方式表
     * @param db 数据库
     * @return
     */
    private synchronized boolean createPayTypeTable(SQLiteDatabase db){
        // 创建人物表
        String  sql = "CREATE TABLE " + TABLE_NAME_PAY +
                "(" +
                FIELD_NAME_PAY_TYPE + " nvarchar(" + MAX_CHAR_NUMBER + ")  " +
                ");";
        MyLog.d(TAG, "createPayTypeTable execSQL: " + sql);
        try {
            db.execSQL(sql);
            return true;
        } catch (SQLException e) {
            MyLog.e(TAG, "onCreate " + TABLE_NAME_PAY + "Error" + e.toString());
        }

        return false;
    }

    /**
     * 删除数据库
     * @param db 数据库文件名
     */
    private synchronized void deleteDB(String db){
        SQLiteDatabase.deleteDatabase(new File(db));
    }

}
