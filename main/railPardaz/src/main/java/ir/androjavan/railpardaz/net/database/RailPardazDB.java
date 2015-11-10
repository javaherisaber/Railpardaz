package ir.androjavan.railpardaz.net.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

import ir.androjavan.railpardaz.net.bundle.BundleReport;
import ir.androjavan.railpardaz.net.bundle.BundleVagon;

public class RailPardazDB {
	
	private static final String DATABASE_NAME = "RailPardaz.db";
	private static final int    DATABASE_VERSION = 5;
	private static final String TAG = "RailPardaz_Database";
    
    public static final String TABLE_VAGON="vagon";
    public static final String TABLE_ERROR="error";
    public static final String TABLE_REPORT="report";
    
    private static final String FIELD_ID = "_id";
    private static final String FIELD_VAGON_ID = "vagon_id";
    private static final String FIELD_VAGON_NUM = "vagon_num";
    private static final String FIELD_ERROR_NAME = "error_name";
    private static final String FIELD_ERROR_ID = "error_id";
    private static final String FIELD_ERROR_PARENT_ID = "parent_id";
    private static final String FIELD_TRIP_ID = "trip_id";
    private static final String FIELD_COMMENT = "comment";
    private static final String FIELD_TOKEN = "token";
    private static final String FIELD_STATUS = "status";
    
    public static final String[] COLUMNS_VAGON = {FIELD_ID,FIELD_VAGON_ID,FIELD_VAGON_NUM};
    public static final String[] COLUMNS_ERROR = {FIELD_ID,FIELD_ERROR_ID,FIELD_ERROR_PARENT_ID,FIELD_ERROR_NAME};
    public static final String[] COLUMNS_REPORT = {FIELD_ID,FIELD_TOKEN,FIELD_ERROR_ID,FIELD_TRIP_ID,FIELD_VAGON_NUM,FIELD_COMMENT,FIELD_STATUS};
    
    private static final String CREATE_TABLE_VAGON =
            "create table " + TABLE_VAGON  +"("+FIELD_ID+ " integer primary key autoincrement, "
            + FIELD_VAGON_ID + " integer not null,"
            + FIELD_VAGON_NUM+ " text not null);";
    
    private static final String CREATE_TABLE_ERROR =
            "create table " + TABLE_ERROR +"("+FIELD_ID+ " integer primary key autoincrement, "
            + FIELD_ERROR_ID + " integer not null,"
            + FIELD_ERROR_PARENT_ID+ " integer not null,"
            + FIELD_ERROR_NAME+ " text not null);";
    private static final String CREATE_TABLE_REPORT = 
    		"create table " + TABLE_REPORT +"("+FIELD_ID+ " integer primary key autoincrement, "
    		+ FIELD_TOKEN + " text not null,"
    		+ FIELD_ERROR_ID + " integer not null,"
    	    + FIELD_TRIP_ID + " text not null,"
    	    + FIELD_VAGON_NUM + " text not null,"
    	    + FIELD_COMMENT + " text not null,"
    	    + FIELD_STATUS + " integer not null);";
    
    private final Context context;

    DatabaseHelper DBHelper;
    SQLiteDatabase db;
	
	public RailPardazDB(Context ctx)
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
        	db.execSQL(CREATE_TABLE_ERROR);
            db.execSQL(CREATE_TABLE_VAGON);
            db.execSQL(CREATE_TABLE_REPORT);
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
    public RailPardazDB open() throws SQLException 
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---
    public synchronized void close() 
    {
        DBHelper.close();
    }
    
    public long InsertVagon(BundleVagon vagonInfo)
    {
    	ContentValues initialValues = new ContentValues();
    	initialValues.put(FIELD_VAGON_ID, vagonInfo.id);
    	initialValues.put(FIELD_VAGON_NUM, vagonInfo.vagonNumber);
        
        return db.insert(TABLE_VAGON, null, initialValues);
    }
    
    public long InsertError(int errorId,int ParentId,String Name)
    {
    	ContentValues initialValues = new ContentValues();//a storage for set of data that contentResolver can handle it
        initialValues.put(FIELD_ERROR_ID, errorId);
        initialValues.put(FIELD_ERROR_PARENT_ID, ParentId);
        initialValues.put(FIELD_ERROR_NAME, Name);
        
        return db.insert(TABLE_ERROR, null, initialValues);
    }
    
    public long InsertReport(BundleReport reportInfo)
    {
    	ContentValues initialValues = new ContentValues();//a storage for set of data that contentResolver can handle it
        initialValues.put(FIELD_ERROR_ID, reportInfo.errorId);
        initialValues.put(FIELD_TRIP_ID, reportInfo.tripId);
        initialValues.put(FIELD_TOKEN, reportInfo.token);
        initialValues.put(FIELD_VAGON_NUM, reportInfo.vagonNum);
        initialValues.put(FIELD_COMMENT, reportInfo.comment);
        initialValues.put(FIELD_STATUS,reportInfo.status);
        
        return db.insert(TABLE_REPORT, null, initialValues);
    }
    
    public  Cursor GetAllRecords(String table,String[] columns)
    {
    	return db.query(table,columns, null, null, null, null, null);
    }
    
    public Cursor GetErrorWithParentID(int ParentId)
    {
    	String sql="Select * from "+TABLE_ERROR+" where "+FIELD_ERROR_PARENT_ID+" = "+ParentId;
        return db.rawQuery(sql, null);
    }
    
    public String GetErrorNameWithID(int Id)
    {
    	String sql="Select * from "+TABLE_ERROR+" where "+FIELD_ERROR_ID+" = "+Id;
    	Cursor cursor = db.rawQuery(sql, null);
    	cursor.moveToFirst();
    	return cursor.getString(3);
    }
    
    public Cursor GetAllUnsyncedReports()
    {
    	String sql="Select * from "+TABLE_REPORT+" where "+FIELD_STATUS+" = "+ 0;
        return db.rawQuery(sql, null);
    }
    
    public boolean SetSyncedReport(BundleReport bundleReport)
    {
    	ContentValues initialValues=new ContentValues();

    	initialValues.put(FIELD_TOKEN, bundleReport.token);
    	initialValues.put(FIELD_ERROR_ID, bundleReport.errorId);
    	initialValues.put(FIELD_TRIP_ID, bundleReport.tripId);
    	initialValues.put(FIELD_VAGON_NUM, bundleReport.vagonNum);
    	initialValues.put(FIELD_COMMENT, bundleReport.comment);
    	initialValues.put(FIELD_STATUS, 1);

    	return db.update(TABLE_REPORT, initialValues, 
    		"(" + FIELD_TOKEN + " = " + bundleReport.token + ")AND"
    		+ "(" + FIELD_ERROR_ID + " = " + bundleReport.errorId + ")AND"
    		+ "(" + FIELD_TRIP_ID + " = " + bundleReport.tripId + ")AND" 
    		+ "(" + FIELD_VAGON_NUM + " = " + bundleReport.vagonNum + ")AND"
    		+ "(" + FIELD_COMMENT + " = " +"'"+ bundleReport.comment +"'"+ ")AND"
    		+ "(" + FIELD_STATUS + " = " + bundleReport.status + ")", null) > 0;
    }
    
    public void ClearDatabaseAndPreferences()
    {
    	db.execSQL("delete from "+ TABLE_VAGON);
    	db.execSQL("delete from "+ TABLE_ERROR);
    	db.execSQL("delete from "+ TABLE_REPORT);
    	
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("token","");
		editor.putString("NameProfile","");
		editor.putString("TripId", "");
		editor.putString("TripName", "");
		editor.putString("TripDate", "");
		editor.putString("vagonNum", "");
		editor.commit();
    }
    
}
	

