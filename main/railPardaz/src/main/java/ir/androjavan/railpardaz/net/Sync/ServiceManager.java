package ir.androjavan.railpardaz.net.Sync;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ServiceManager {
	
	private static String TAG = "NOTIFICATION SERVICE";
		
	public static void startService(Context context) {
		
		Log.i(TAG, "startService Is run");
		Intent goToService = new Intent(context.getApplicationContext(), NotificationService.class);
		context.startService(goToService);		
	}
	
}
