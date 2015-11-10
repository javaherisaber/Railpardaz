package ir.androjavan.railpardaz.net.JSON;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ir.androjavan.railpardaz.net.bundle.BundleError;
import ir.androjavan.railpardaz.net.bundle.BundleLog;
import ir.androjavan.railpardaz.net.utile.LogUtility;
import ir.androjavan.railpardaz.net.database.RailPardazDB;

public class JsonError {
	
	private String mUrl = "http://rps-net.com/api/errors/";
	private RailPardazDB db;
	private Context context;
	
	public JsonError(Context context,String token) 
	{
		mUrl += token;
		this.context = context;
	}
	public boolean GetErrorList()
	{
		 try
         {
			db = new RailPardazDB(context);
			db.open();
			
         	JsonFunctions jsf = new JsonFunctions(context);
         	JsonArray jsonArray = jsf.getJSONfromURL(mUrl);

            for(int i = 0; i < jsonArray.size(); i++)
            {
            	
 					JsonObject jsonobject = jsonArray.get(i).getAsJsonObject();
 	                String errorName = jsonobject.get("Title").getAsString();
 	                int errorId = jsonobject.get("Id").getAsInt();
 	                int errorParentId = jsonobject.get("ParentId").getAsInt();
 	                BundleError errorBundle = new BundleError();
 	                errorBundle.id = errorId;
 	                errorBundle.name = errorName;
 	                errorBundle.parentId = errorParentId;
 	                db.InsertError(errorId, errorParentId, errorName);
 	                               
            }

         	} catch (Exception e) {

			 LogUtility logUtility = new LogUtility(context);
			 logUtility.InsertLog(new BundleLog(this.getClass().getSimpleName(),
					 "When trying to get JsonError"
					 , e.toString()));
				return false;
			}
		 
		 return true;
	}
}
