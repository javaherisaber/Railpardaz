package ir.androjavan.railpardaz.net.sync;

import ir.androjavan.railpardaz.net.SendReport;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class InternetBroadcast extends BroadcastReceiver {

	
	private Context ctx;
	
	@Override
	public void onReceive(Context context, Intent intent) {	
		this.ctx = context;
		if(isOnline(context)) {
			try 
			{
				SendAllReprots();
			}
			catch(Exception e)
			{
				Log.e("Internet Error in Service",e.toString());
			}
		}
		
	}
	
	
	public boolean isOnline(Context context) {

	    ConnectivityManager connectionManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = connectionManager.getActiveNetworkInfo();
	    return (netInfo != null && netInfo.isConnected());

	}
	
	
	
	private void SendAllReprots() {
		
		Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {

            	SendReport sReport = new SendReport(ctx.getApplicationContext());
        		sReport.SyncAllReports();
            }
        });

        thread1.start();
	}
	
	

}
