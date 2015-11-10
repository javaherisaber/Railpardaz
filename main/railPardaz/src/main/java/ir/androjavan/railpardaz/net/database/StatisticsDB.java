package ir.androjavan.railpardaz.net.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import ir.androjavan.railpardaz.net.bundle.BundleLog;

public class StatisticsDB {
	private static final String DATABASE_NAME = "Statistics.db";
	private static final int    DATABASE_VERSION = 1;
	private static final String TAG = "Statistics_Database";
    
    public static final String TABLE_TBLLOG = "tbl_log";
    
    private static final String FIELD_ID = "_id";
    private static final String FIELD_TAG_NAME = "tag_name";
    private static final String FIELD_LOG_BODY = "log_body";
    private static final String FIELD_LOG_INFO = "log_info";
    private static final String FIELD_DATE_TIME = "date_time";
    private static final String FIELD_LINE_NUM = "line_num";
    private static final String FIELD_WRITE_STATUS = "write_status";
    
    public static final String[] COLUMNS_TBLLOG = {FIELD_ID,FIELD_TAG_NAME,FIELD_LOG_BODY,FIELD_LOG_INFO,FIELD_DATE_TIME,FIELD_LINE_NUM,FIELD_WRITE_STATUS};
    
    private static final String CREATE_TBLLOG_TABLE =
            "create table " + TABLE_TBLLOG  +"("+FIELD_ID+ " integer primary key autoincrement, "
            + FIELD_TAG_NAME+ " text not null,"
            + FIELD_LOG_BODY+ " text not null,"
            + FIELD_LOG_INFO+ " text not null,"
            + FIELD_DATE_TIME+ " text not null,"
            + FIELD_LINE_NUM + " integer not null,"
            + FIELD_WRITE_STATUS + " integer not null);";
    
    private final Context context;

    DatabaseHelper DBHelper;
    SQLiteDatabase db;
	
	public StatisticsDB(Context ctx)
	{
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper
    {
		
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
        	db.execSQL(CREATE_TBLLOG_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
        	 Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");

        }

    }
	
    //---opens the database---
    public StatisticsDB open() throws SQLException 
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---
    public synchronized void close() 
    {
        DBHelper.close();
    }
    
    public long insertLog(BundleLog bundleLog)
    {
    	ContentValues initialValues = new ContentValues();
    	initialValues.put(FIELD_TAG_NAME, bundleLog.tagName);
    	initialValues.put(FIELD_LOG_BODY, bundleLog.logBody);
    	initialValues.put(FIELD_LOG_INFO, bundleLog.logInfo);
    	initialValues.put(FIELD_DATE_TIME, bundleLog.dateTime);
        initialValues.put(FIELD_LINE_NUM, bundleLog.lineNumber);
        initialValues.put(FIELD_WRITE_STATUS,0);
        
        return db.insert(TABLE_TBLLOG, null, initialValues);
    }

    public boolean updateLog(BundleLog bundleLog)
    {
        ContentValues initialValues=new ContentValues();

        initialValues.put(FIELD_TAG_NAME, bundleLog.tagName);
        initialValues.put(FIELD_LOG_BODY, bundleLog.logBody);
        initialValues.put(FIELD_LOG_INFO, bundleLog.logInfo);
        initialValues.put(FIELD_LOG_BODY, bundleLog.logBody);
        initialValues.put(FIELD_DATE_TIME,bundleLog.dateTime);
        initialValues.put(FIELD_LINE_NUM, bundleLog.lineNumber);
        initialValues.put(FIELD_WRITE_STATUS, 1);

        return db.update(TABLE_TBLLOG, initialValues,"(" + FIELD_TAG_NAME + " = " + "\"" + bundleLog.tagName +  "\"" + ")AND("+
                FIELD_DATE_TIME + " = " + "\"" + bundleLog.dateTime +  "\"" + ")AND("+
                FIELD_LOG_BODY + " = " + "\"" + bundleLog.logBody +  "\"" + ")AND("+
                FIELD_WRITE_STATUS + " = " + 0 +")",null) > 0;
    }

    public long deleteLog(BundleLog bundleLog)
    {
        return db.delete(TABLE_TBLLOG,"(" + FIELD_TAG_NAME + " = " + "\"" + bundleLog.tagName +  "\"" + ")AND("+
                FIELD_DATE_TIME + " = " + "\"" + bundleLog.dateTime +  "\"" + ")AND("+
                FIELD_LOG_BODY + " = " + "\"" + bundleLog.logBody +  "\"" + ")",null);
    }
    
    public  Cursor getAllRecords(String table, String[] columns)
    {
    	 return db.query(table,columns, null, null, null, null, null);
    }

}