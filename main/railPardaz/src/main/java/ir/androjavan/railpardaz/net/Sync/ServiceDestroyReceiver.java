package ir.androjavan.railpardaz.net.Sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ServiceDestroyReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		ServiceManager.startService(context);
		
	}

}
