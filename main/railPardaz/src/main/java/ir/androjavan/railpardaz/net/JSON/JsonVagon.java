package ir.androjavan.railpardaz.net.JSON;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ir.androjavan.railpardaz.net.bundle.BundleLog;
import ir.androjavan.railpardaz.net.bundle.BundleVagon;
import ir.androjavan.railpardaz.net.utile.LogUtility;
import ir.androjavan.railpardaz.net.database.RailPardazDB;

public class JsonVagon {
	
	Context context;
	private String mUrl = "http://rps-net.com/api/wagon/";

	RailPardazDB db;
	
	public JsonVagon(Context context ,String token) 
	{
		this.context = context;
		mUrl+= token;
	}
	public boolean GetVagonList()
	{
		 try
         {
			db = new RailPardazDB(context);
			db.open();
			
         	JsonFunctions jsf = new JsonFunctions(context);
         	JsonArray jsonArray = jsf.getJSONfromURL(mUrl);
            for (int i = 0; i < jsonArray.size(); i++) 
            {
 					JsonObject jsonobject = jsonArray.get(i).getAsJsonObject();
 					BundleVagon vagonInfo = new BundleVagon();
 					vagonInfo.vagonNumber = jsonobject.get("Code").getAsString();
 	                vagonInfo.id = Integer.parseInt(vagonInfo.vagonNumber);
 	                
 	                db.InsertVagon(vagonInfo);

         	}
         }
		 catch (Exception e) {

			 LogUtility logUtility = new LogUtility(context);
			 logUtility.InsertLog(new BundleLog(this.getClass().getSimpleName(),
					 "while trying to get JsonWagon"
					 , e.toString()));
				return false;
			}
		 
		 return true;
	}
}
