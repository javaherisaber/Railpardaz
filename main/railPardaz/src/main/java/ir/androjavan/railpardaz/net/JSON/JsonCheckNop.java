package ir.androjavan.railpardaz.net.JSON;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ir.androjavan.railpardaz.net.bundle.BundleLog;
import ir.androjavan.railpardaz.net.utile.LogUtility;

public class JsonCheckNop {

	private String URL = "http://androjavan.ir/shop/api/check_for_update.php";
	private String UrlParameters = "pn=";
    private Context context;
    
    public JsonCheckNop(Context c) {
    	
    	this.context = c;
	}
    
    
    public String CheckForUpdate(){
    	String title;
    	try {
        	JsonFunctions jsf = new JsonFunctions(context);
        	JsonArray jsonarray = jsf.getNewVerWithPOST(URL,PrepareUpdateUrl());

    		JsonObject jsonobject = jsonarray.get(0).getAsJsonObject();
                                   
    		title = jsonobject.get("title").getAsString();

    	}catch (Exception e) {

    		return "default";
		}

    	return title;
    }
    
    private String PrepareUpdateUrl(){
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            LogUtility logUtility = new LogUtility(context);
            logUtility.InsertLog(new BundleLog(this.getClass().getSimpleName(),
                    "while trying to Get package name"
                    , e.toString()));
        }
        int version = pInfo.versionCode;
        return UrlParameters + context.getPackageName() + "&ver=" + String.valueOf(version);
    }
}
