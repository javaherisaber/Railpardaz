package ir.androjavan.railpardaz.net.Sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import ir.androjavan.railpardaz.net.bundle.BundleLog;
import ir.androjavan.railpardaz.net.utile.LogUtility;
import ir.androjavan.railpardaz.net.utile.SendReport;

public class InternetBroadcast extends BroadcastReceiver {

	
	private Context ctx;
	
	@Override
	public void onReceive(Context context, Intent intent) {	
		this.ctx = context;
		if(isOnline(context)) {
			try 
			{
				SendAllReports();
			}
			catch(Exception e)
			{
				LogUtility logUtility = new LogUtility(context);
				logUtility.InsertLog(new BundleLog(this.getClass().getSimpleName(),
						"Exception occur's in Send report or log"
						, e.toString()));
			}
		}
		
	}
	
	
	public boolean isOnline(Context context) {

	    ConnectivityManager connectionManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = connectionManager.getActiveNetworkInfo();
	    return (netInfo != null && netInfo.isConnected());

	}
	
	
	
	private void SendAllReports() {
		
		Thread reportThread = new Thread(new Runnable() {
            @Override
            public void run() {

            	SendReport sReport = new SendReport(ctx.getApplicationContext());
        		sReport.SyncAllReports();
            }
        });

        reportThread.start();
//
//		Thread logThread = new Thread(new Runnable() {
//			@Override
//			public void run() {
//
//				SendLog sendLog = new SendLog(ctx.getApplicationContext());
//				sendLog.SyncAllLogs();
//			}
//		});
//
//		logThread.start();
	}
	
	

}
