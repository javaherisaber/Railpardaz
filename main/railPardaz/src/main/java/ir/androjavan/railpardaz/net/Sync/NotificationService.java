package ir.androjavan.railpardaz.net.Sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class NotificationService extends Service {
	
	
	private static String TAG = "NOTIFICATION SERVICE";

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		
		
		return super.onStartCommand(intent, flags, startId);
		
	}





	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
	}


	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Intent intent = new Intent();
		intent.setAction("YouWillNeverKillMe");
		sendBroadcast(intent);
		Log.i(TAG, "onDestroy()...");
	}
	
	
}
