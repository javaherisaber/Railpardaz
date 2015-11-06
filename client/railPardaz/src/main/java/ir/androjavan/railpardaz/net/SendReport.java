package ir.androjavan.railpardaz.net;

import android.content.Context;
import android.database.Cursor;

import ir.androjavan.railpardaz.net.bundle.BundleReport;
import ir.androjavan.railpardaz.net.database.RailPardazDB;
import ir.androjavan.railpardaz.net.json.JsonSetReport;

public class SendReport {

	private Context context;
	private RailPardazDB db;
	
	public SendReport(Context c) {
		this.context = c;
		db = new RailPardazDB(context);
	}
	
	public boolean SyncAllReports()
	{
		db.open();
    	Cursor cursor = db.GetAllUnsyncedReports();
    	cursor.moveToFirst();
    	if(cursor.getCount() != 0)
    	{
    		do
    		{
    			BundleReport bundleReport = new BundleReport();
    			bundleReport.token = cursor.getString(1);
				bundleReport.errorId = cursor.getInt(2);
				bundleReport.vagonID = cursor.getString(3);
				bundleReport.comment = cursor.getString(4);
				bundleReport.status = cursor.getInt(5);
				
				JsonSetReport jReport = new JsonSetReport(context, bundleReport);
				boolean flag = jReport.GetReportStatus();
				if(!flag)
					return false;
				else
				{
					db.SetSyncedReport(bundleReport);
				}
			} while (cursor.moveToNext());
    	}
    	
    	return true;
	}
	

}
