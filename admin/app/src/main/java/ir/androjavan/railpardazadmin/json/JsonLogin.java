package ir.androjavan.railpardazadmin.json;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ir.androjavan.railpardazadmin.bundle.BundleReport;
import ir.androjavan.railpardazadmin.database.ReportDB;

public class JsonLogin {
	
	private ReportDB db;
	private String URL = "http://androjavan.ir/shop/api/net_admin_review.php";

	public JsonLogin(Context context)
	{
		db=new ReportDB(context);
	}
	
	public int GetLoginStatus(String user,String pass){

        String UrlParameters = "user="+user+"&pass="+pass;
		JsonArray jsonarray;
		try {
			
		JsonFunctions jsf = new JsonFunctions();
		jsonarray = jsf.getJsonArrayWithGET(URL,UrlParameters);
		         	
		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(),e.toString());
			return -1;
		}

        db.open();
             
        for (int i = 0; i < jsonarray.size(); i++) {
        	
             try {

                 JsonObject jsonobject = jsonarray.get(i).getAsJsonObject();

                 BundleReport bundleError = new BundleReport();
                 bundleError.wagonNum = jsonobject.get("wagonName").getAsString();
                 if(bundleError.wagonNum.equals("-1"))
                     return -1;
                 bundleError.fullName = jsonobject.get("fullName").getAsString();
                 bundleError.tripInfo = jsonobject.get("tripInfo").getAsString();
                 bundleError.errorName = jsonobject.get("errorName").getAsString();
                 bundleError.comment = jsonobject.get("comment").getAsString();
 	                
                 db.InsertReport(bundleError);

 				} catch (Exception e) {
                 Log.e(this.getClass().getSimpleName(),e.toString());
 					continue;
 				}                
        }
					

		db.close();

        return 1;
		 
	}
}
