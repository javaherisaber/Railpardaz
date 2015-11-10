package ir.androjavan.railpardaz.net.JSON;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ir.androjavan.railpardaz.net.bundle.BundleLog;
import ir.androjavan.railpardaz.net.bundle.BundleUser;
import ir.androjavan.railpardaz.net.utile.LogUtility;

public class JsonLogin {
	
	Context context;
	private String mUrl = "http://rps-net.com/api/users/";
	String username;
	String password;
	
	public JsonLogin(Context context ,String username ,String password) 
	{
		this.context = context;
		this.username = username;
		this.password = password;
		mUrl = mUrl + username + "|" + password;
	}
	
	public BundleUser GetTokenLogin()
	{
		BundleUser bundleUser = new BundleUser();

		try
        {
         	JsonFunctions jsf = new JsonFunctions(context);
         	JsonArray jsonArray = jsf.getJSONfromURL(mUrl);
			JsonObject jsonobject = jsonArray.get(0).getAsJsonObject();
			         		
			bundleUser.Token = jsonobject.get("Id").getAsString();
			bundleUser.Name = jsonobject.get("Name").getAsString();
		

     	} catch (Exception e) {

     		bundleUser.Token = "-2";
			LogUtility logUtility = new LogUtility(context);
			logUtility.InsertLog(new BundleLog(this.getClass().getSimpleName(),
					"while trying to get login info"
					, e.toString()));
			return bundleUser;
		}
	 return bundleUser;
	}
}
