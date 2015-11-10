package ir.androjavan.railpardaz.net.utile;

import android.content.Context;
import android.database.Cursor;

import ir.androjavan.railpardaz.net.JSON.JsonSyncLog;
import ir.androjavan.railpardaz.net.bundle.BundleLog;
import ir.androjavan.railpardaz.net.database.StatisticsDB;

public class SendLog {

    private Context context;
    private StatisticsDB db;

    public SendLog(Context c) {
        this.context = c;
        db = new StatisticsDB(context);
    }

    public boolean SyncAllLogs()
    {
        db.open();
        Cursor cursor = db.getAllRecords(StatisticsDB.TABLE_TBLLOG,StatisticsDB.COLUMNS_TBLLOG);
        cursor.moveToFirst();
        if(cursor.getCount() != 0)
        {
            do
            {
                BundleLog bundleLog = new BundleLog(cursor.getString(1),cursor.getString(2),cursor.getString(3));
                bundleLog.dateTime = cursor.getString(4);
                bundleLog.lineNumber = String.valueOf(cursor.getInt(5));
                int writeStatus = cursor.getInt(6);

                JsonSyncLog jsonSyncLog = new JsonSyncLog(context,bundleLog);
                boolean flag = jsonSyncLog.getSyncLogStatus();
                if(!flag)
                    return false;
                else
                {
                    if(writeStatus == 0)
                    {
                        try
                        {
                            LogUtility logUtility = new LogUtility(context);
                            logUtility.writeToFile(bundleLog);
                            db.deleteLog(bundleLog);
                        }catch (Exception e)
                        {
                          e.printStackTrace();
                        }
                    }
                    else
                    {
                        db.deleteLog(bundleLog);
                    }
                }
            } while (cursor.moveToNext());
        }

        db.close();
        return true;
    }

}
