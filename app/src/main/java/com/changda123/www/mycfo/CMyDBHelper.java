package com.changda123.www.mycfo;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.widget.Toast;

import com.changda123.www.mycfo.Util.MyLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CMyDBHelper extends SQLiteOpenHelper{
    private static final String TAG = "MyCFO_CMyDBHelper";
    //数据库的版本号
    private static final int DATABASE_VERSION = 1001;


    //数据库名称
    private static final String DATABASE_NAME = "MyCFO.db";
    private static final String DATABASE_NAME_BACKUP = "MyCFO_hw.db";
    private static final String DATABASE_RECORD_BACKUP_FILE = "MyCFO_Records.ini";

    //表名+字段
    public static final String TABLE_NAME_RECORD = "records";// records表
    public static final String FIELD_ID = "id";
    public static final String FIELD_CATEGORY = "category";
    public static final String FIELD_EVENT = "event";
    public static final String FIELD_PRICE = "price";
    public static final String FIELD_PRICE_SUM = "SUM(price)";
    public static final String FIELD_TIME ="time";
    public static final String FIELD_DISPLAY_TIME ="display_time";
    public static final String FIELD_WHO = "who";
    public static final String FIELD_YEAR  = "year";
    public static final String FIELD_MONTH = "month";
    public static final String FIELD_WEEK  = "week";


    public static final String TABLE_NAME_CATEGORY = "category";// category表
    public static final String FIELD_NAME = "category";
    public static final String FIELD_DISPLAY_FLAG = "isDisplay";

    public static final String TABLE_NAME_PERSON = "person";// person表
    public static final String FIELD_PEOPLE_NAME = "who";

    //自定义类别和Person的最大字符数
    private static final int MAX_CHAR_NUMBER = 10;
    // 静态引用
    private volatile static CMyDBHelper mInstance;
    private SQLiteDatabase mDBWriteHandler;
    Context mContext;

    private CMyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static CMyDBHelper getInstance(Context context) {
        CMyDBHelper inst = mInstance;
        if (inst == null) {
            synchronized (CMyDBHelper.class) {
                inst = mInstance;
                if (inst == null) {
                    inst = new CMyDBHelper(context);
                    mInstance = inst;
                    mInstance.mContext = context;
                }
            }
        }
        return inst;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        MyLog.i(TAG, "CMyDBHelper onCreate.");
        createRecordsTable(db);
        createCategoryTable(db);
        createPeopleTable(db);

        // 若不是第一个版本安装，直接执行数据库升级
        // !!请不要修改FIRST_DATABASE_VERSION的值，其为第一个数据库版本大小
        final int FIRST_DATABASE_VERSION = 1000;
        onUpgrade(db, FIRST_DATABASE_VERSION, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 使用for实现跨版本升级数据库
        MyLog.d(TAG, "do onUpgrade oldVersion:"+oldVersion + " newVersion:"+newVersion);
        for (int i = oldVersion; i < newVersion; i++) {
            switch (i) {
                case 1000:
                    MyLog.d(TAG, "do onUpgrade to ver 1001....");
                    db.beginTransaction();
                    try {

                        String sqlAddYear = "ALTER TABLE " + TABLE_NAME_RECORD + " ADD COLUMN year int";
                        String sqlAddMonth = "ALTER TABLE " + TABLE_NAME_RECORD + " ADD COLUMN month int";
                        String sqlAddWeek = "ALTER TABLE " + TABLE_NAME_RECORD + " ADD COLUMN week int";
                        db.execSQL(sqlAddYear);
                        db.execSQL(sqlAddMonth);
                        db.execSQL(sqlAddWeek);

                        Cursor cursor = db.query(TABLE_NAME_RECORD, new String[]{FIELD_ID, FIELD_TIME}, null, null, null, null, null);
                        while (cursor.moveToNext()) {

                            int id = cursor.getInt(cursor.getColumnIndex(FIELD_ID));
                            long time = cursor.getLong(cursor.getColumnIndex(FIELD_TIME));
                            ContentValues values = new ContentValues();
                            Date date = new Date(time);
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(date);

                            values.put(FIELD_YEAR, cal.get(Calendar.YEAR));
                            values.put(FIELD_MONTH, cal.get(Calendar.MONTH) + 1);
                            values.put(FIELD_WEEK, cal.get(Calendar.WEEK_OF_YEAR));

                            db.update(TABLE_NAME_RECORD, values, FIELD_ID + "=?", new String[]{String.valueOf(id)});
                        }
                        db.setTransactionSuccessful();
                        cursor.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        db.endTransaction();
                    }

                    break;
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
                FIELD_WHO + " nvarchar(" + MAX_CHAR_NUMBER + ")" +
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
     * 删除数据库
     * @param db 数据库文件名
     */
    private synchronized void deleteDB(String db){
        SQLiteDatabase.deleteDatabase(new File(db));
    }
    /**
     * 删除指定表的所有数据
     * @param db 数据库
     * @param table 表名
     */
    synchronized void deleteAllTableData(SQLiteDatabase db, String table)
    {
        String sql="delete from "+ table;
        db.execSQL(sql);
    }

    /******************* 数据库记录操作开始 *************************************/
    /**
     * 打开数据库写连接
     * @return
     */
    public SQLiteDatabase getWriteDBHandler(){
        if(null == mDBWriteHandler){
            mDBWriteHandler = mInstance.getWritableDatabase();
        }
        return mDBWriteHandler;
    }
    /**
     * 关闭数据库连接
     */
    public void closeDatabase(){
        if(null != mDBWriteHandler){
            mDBWriteHandler.close();
        }
    }

    /**
     * 执行sql语句
     */
    private void execSQL(String sql){
        //直接执行sql语句
        getWriteDBHandler().execSQL(sql);
    }

    public long insertRecord(String table, ContentValues values){
        // 调用insert()方法将数据插入到数据库当中
        long id = getWriteDBHandler().insert(table, null, values);
        MyLog.d(TAG, "insertRecord ret id:"+id);
        return id;
    }

    public ArrayList<ContentValues> getListOfRecord(
                                                    String table,
                                                    String[] columns,
                                                    String selection,
                                                    String[] selectionArgs,
                                                    String groupBy,
                                                    String having,
                                                    String orderBy) {

        Cursor cursor = getWriteDBHandler().query(table, columns, selection, selectionArgs, groupBy, having, orderBy);

        // return records
        ArrayList<ContentValues> list = new ArrayList<>();
        int columnsCnt = columns.length;
        ContentValues value;
        //遍历Cursor
        while(cursor.moveToNext()){
            value = new ContentValues();

            for(int i=0; i<columnsCnt;i++) {
                if(columns[i].equals(CMyDBHelper.FIELD_PRICE)) {
                    value.put(columns[i], showPrice(cursor.getInt(i)));
                }else if(columns[i].equals(CMyDBHelper.FIELD_PRICE_SUM)) {
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
    public long deleteRecord(String table, String whereClause, String[] whereArgs){
        // 调用insert()方法将数据插入到数据库当中
        long id = getWriteDBHandler().delete(table, whereClause, whereArgs);
        MyLog.d(TAG, "delRecord ret id:"+id);
        return id;
    }

    public long updateRecord(String table, ContentValues values, String whereClause, String[] whereArgs){
        // 调用insert()方法将数据插入到数据库当中
        long id = getWriteDBHandler().update(table, values, whereClause, whereArgs);
        MyLog.d(TAG, "updateRecord ret id:"+id);
        return id;
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

    public ContentValues fillNewRecord(String strCategory,
                                       String strEvent,
                                       String strPrice,
                                       long lngTime,
                                       String strWho){

        ContentValues values = new ContentValues();
        // ContentValues Key只能是String类型，Value只能存储基本类型数据，不能存储对象
        values.put(CMyDBHelper.FIELD_CATEGORY, strCategory);
        values.put(CMyDBHelper.FIELD_EVENT, strEvent);
        values.put(CMyDBHelper.FIELD_PRICE, CMyDBHelper.convertPriceToInt(strPrice));
        values.put(CMyDBHelper.FIELD_TIME, lngTime);
        values.put(CMyDBHelper.FIELD_DISPLAY_TIME, CMyDBHelper.dateToString(new Date(lngTime)));
        values.put(CMyDBHelper.FIELD_WHO, strWho);

        Date date = new Date(lngTime);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        values.put(CMyDBHelper.FIELD_YEAR,  cal.get(Calendar.YEAR));
        values.put(CMyDBHelper.FIELD_MONTH, cal.get(Calendar.MONTH) + 1);
        values.put(CMyDBHelper.FIELD_WEEK,  cal.get(Calendar.WEEK_OF_YEAR));

        return values;
    }

    boolean IsBackupDatabaseExist(){
        File file = new File(Environment.getExternalStorageDirectory(), DATABASE_RECORD_BACKUP_FILE );
        return file.exists();
    }
    boolean BackupRecordToFile(Context context){
        File file = new File(Environment.getExternalStorageDirectory(), DATABASE_RECORD_BACKUP_FILE);
        if (!file.exists()) {
            Toast.makeText(context, "File:"+ Environment.getExternalStorageDirectory()+" Not existed!", Toast.LENGTH_LONG).show();
            try {
                if (!file.createNewFile()) {
                    MyLog.e(TAG, "===yangyu=== Create Record backup file failed!!");
                    Toast.makeText(context, "Create File failed!", Toast.LENGTH_LONG).show();
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        JSONArray allDataArray = new JSONArray();
        String[] columns = { CMyDBHelper.FIELD_EVENT, CMyDBHelper.FIELD_PRICE, CMyDBHelper.FIELD_CATEGORY, CMyDBHelper.FIELD_TIME,  CMyDBHelper.FIELD_WHO };
        ArrayList<ContentValues> arrayContentList = getListOfRecord(CMyDBHelper.TABLE_NAME_RECORD,columns, null, null,null,null,CMyDBHelper.FIELD_ID);
        Toast.makeText(context, "File:"+ arrayContentList.size(), Toast.LENGTH_LONG).show();

        // Debug 代码
        for(int i=0; i<arrayContentList.size(); i++) {
            JSONObject oneRecordObject = new JSONObject();
            try {
                oneRecordObject.put(CMyDBHelper.FIELD_EVENT, arrayContentList.get(i).getAsString(CMyDBHelper.FIELD_EVENT));
                oneRecordObject.put(CMyDBHelper.FIELD_PRICE, arrayContentList.get(i).getAsString(CMyDBHelper.FIELD_PRICE));
                oneRecordObject.put(CMyDBHelper.FIELD_CATEGORY, arrayContentList.get(i).getAsString(CMyDBHelper.FIELD_CATEGORY));
                oneRecordObject.put(CMyDBHelper.FIELD_TIME, arrayContentList.get(i).getAsLong(CMyDBHelper.FIELD_TIME));
                oneRecordObject.put(CMyDBHelper.FIELD_WHO, arrayContentList.get(i).getAsString(CMyDBHelper.FIELD_WHO));
                allDataArray.put(oneRecordObject);

            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }
        Toast.makeText(context, "Begin Backup Records to external path:"+Environment.getExternalStorageDirectory() + DATABASE_RECORD_BACKUP_FILE, Toast.LENGTH_LONG).show();

        MyLog.e(TAG, "===yangyu=== Begin Backup Records to external path:"+Environment.getExternalStorageDirectory() + DATABASE_RECORD_BACKUP_FILE);
        try {
            FileOutputStream outputFileStream = new FileOutputStream(file);
            outputFileStream.write(allDataArray.toString().getBytes());
            outputFileStream.flush();
            outputFileStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    void RestoreDBfromFile(Context context){
        try {
            // 得到 assets 目录下我们实现准备好的 SQLite 数据库作为输入流
            File file =new File(Environment.getExternalStorageDirectory(), DATABASE_RECORD_BACKUP_FILE);
            if (!file.exists()) {
                return;
            }

            InputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String laststr = "";
            String tempString = null;
            while((tempString = reader.readLine()) != null){
                laststr += tempString;
            }
            reader.close();
            fileInputStream.close();
            MyLog.d(TAG, "===yangyu=== Begin Restore Records from string:"+laststr);

            JSONArray allData = new JSONArray(laststr);
            for(int i=0; i<allData.length(); i++){
                JSONObject jsonObject = new JSONObject(allData.get(i).toString());

                ContentValues data = fillNewRecord(jsonObject.getString(CMyDBHelper.FIELD_CATEGORY),
                        jsonObject.getString(CMyDBHelper.FIELD_EVENT),
                        jsonObject.getString(CMyDBHelper.FIELD_PRICE),
                        jsonObject.getLong(CMyDBHelper.FIELD_TIME),
                        jsonObject.getString(CMyDBHelper.FIELD_WHO));
                long retId = insertRecord(CMyDBHelper.TABLE_NAME_RECORD, data);
                if(retId > 0){
                    Toast.makeText(context, "DB Restore records Num:"+allData.length(), Toast.LENGTH_LONG).show();
                    MyLog.d(TAG, "===yangyu=== Add Record ok: "+ jsonObject.getString(CMyDBHelper.FIELD_EVENT));
                }else{
                    MyLog.e(TAG, "===yangyu=== add failed! :" + jsonObject.getString(CMyDBHelper.FIELD_EVENT));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean RestoreRecordsFromAssetDB(Context context)
    {
        File jhPath = context.getDatabasePath(DATABASE_NAME);
        if(!jhPath.exists()){
            MyLog.i(TAG, "[==yangyu==] Default DB not exist! :"+ context.getDatabasePath(DATABASE_NAME));
            return false;
        }

        try {
            //得到资源
            AssetManager am = context.getAssets();
            //得到数据库的输入流
            InputStream is=am.open(DATABASE_NAME_BACKUP);

            //用输出流写到SDcard上面
            FileOutputStream fos = new FileOutputStream(jhPath);

            //创建byte数组  用于1KB写一次
            byte[] buffer = new byte[1024];
            int count = 0;
            while((count = is.read(buffer))>0){
                MyLog.i(TAG, "得到");
                fos.write(buffer,0,count);
            }
            //最后关闭就可以了
            fos.flush();
            fos.close();
            is.close();
            return true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }



}
