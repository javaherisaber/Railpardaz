package ir.androjavan.railpardaz.net.json;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

public class JsonCheckNop {

    private String CHECK_UPDATE_URL = "NO NEED";
    private Context context;
    
    public JsonCheckNop(Context c) {
    	
    	this.context = c;
	}
    
    
    public String CheckForUpdate(){
//    	String title = null ;
//    	try {
//        	JsonFunctions jsf = new JsonFunctions();
//        	JsonArray jsonarray = jsf.getJsonArrayWithPOST(CHECK_UPDATE_URL,PrepareUpdateUrl());
//
//        	for (int j = 0; j < 1; j++) {
//
//    				JsonObject jsonobject = jsonarray.get(j).getAsJsonObject();
//
//    				title = jsonobject.get("title").getAsString();
//        	}
//    	}catch (Exception e) {
    		return "default";
//		}
//
//    	return title;
    }
    
    private String PrepareUpdateUrl(){
    	PackageInfo pInfo = null;
		try {
			pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
    	int version = pInfo.versionCode;
    	String Url = "pn=" + context.getPackageName() + "&ver=" + String.valueOf(version);
		return Url;
    }
}
