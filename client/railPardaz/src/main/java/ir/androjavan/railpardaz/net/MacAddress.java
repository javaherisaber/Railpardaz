package ir.androjavan.railpardaz.net;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class MacAddress {

	private Context ctx;

	public MacAddress(Context c)
	{
		this.ctx = c;
	}
	
	public String GetMacAddress()
	{
		WifiManager manager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = manager.getConnectionInfo();
		String address = info.getMacAddress();
		return address;
	}
}
