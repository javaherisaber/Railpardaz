package ir.androjavan.railpardazadmin.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import ir.androjavan.railpardazadmin.bundle.BundleReport;

public class ReportDB {
	
	private static final String DATABASE_NAME = "Report.db";
	private static final int    DATABASE_VERSION = 1;
	private static final String TAG = "Report_Database";
    
    public static final String TABLE_REPORT ="report";
    
    private static final String FIELD_ID = "_id";
    private static final String FIELD_FULL_NAME = "full_name";
    private static final String FIELD_TRIP_INFO = "trip_info";
    private static final String FIELD_ERROR_NAME = "error_name";
    private static final String FIELD_WAGON_NUM = "wagon_num";
    private static final String FIELD_COMMENT = "comment";

    public static final String[] COLUMNS_REPORT = {FIELD_ID,FIELD_FULL_NAME,FIELD_TRIP_INFO,FIELD_ERROR_NAME,
                                                    FIELD_WAGON_NUM,FIELD_COMMENT};

	private static final String CREATE_REPORT_TABLE =
		  "create table " + TABLE_REPORT +"("+FIELD_ID+ " integer primary key autoincrement, "
				  + FIELD_FULL_NAME+ " text not null,"
				  + FIELD_TRIP_INFO+ " text not null,"+FIELD_ERROR_NAME+ " text not null,"
				  + FIELD_WAGON_NUM+ " text not null,"
				  + FIELD_COMMENT + " text not null);";
    

    private final Context context;

    DatabaseHelper DBHelper;
    SQLiteDatabase db;
	
	public ReportDB(Context ctx)
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
        	db.execSQL(CREATE_REPORT_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
        	 Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
        	 
//            db.execSQL("DROP TABLE IF EXISTS "+TABLE_TAB);  
//            db.execSQL("DROP TABLE IF EXISTS "+TABLE_PRODUCT);
        	 
        }
    	
    }
	
    //---opens the database---
    public ReportDB open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---
    public synchronized void close() 
    {
        DBHelper.close();
    }

    public long InsertReport(BundleReport Report)
    {
        ContentValues initialValues = new ContentValues();//a storage for set of data that contentResolver can handle it
        initialValues.put(FIELD_FULL_NAME, Report.fullName);
        initialValues.put(FIELD_TRIP_INFO, Report.tripInfo);
        initialValues.put(FIELD_ERROR_NAME, Report.errorName);
        initialValues.put(FIELD_WAGON_NUM, Report.wagonNum);
        initialValues.put(FIELD_COMMENT, Report.comment);
        
        return db.insert(TABLE_REPORT, null, initialValues);
    }

    public  Cursor GetAllRecords(String table,String[] columns)
    {
    	Cursor cursor = db.query(table,columns, null, null, null, null, null);	
        return cursor;
    }
    
    public void ClearDatabase()
    {
    	db.execSQL("delete from "+ TABLE_REPORT);
    }
    
}
	

