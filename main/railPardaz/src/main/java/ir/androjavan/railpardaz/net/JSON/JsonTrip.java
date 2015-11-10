package ir.androjavan.railpardaz.net.JSON;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ir.androjavan.railpardaz.net.bundle.BundleLog;
import ir.androjavan.railpardaz.net.utile.LogUtility;

public class JsonTrip {
	
	Context context;
	private String mUrl = "http://rps-net.com/api/trip/";
	
	
	public JsonTrip(Context context,String Token) 
	{
		this.context = context;
		mUrl = mUrl + Token;
	}
	
	public boolean GetTripInfo()
	{
		try
        {
         	JsonFunctions jsf = new JsonFunctions(context);
         	JsonArray jsonArray = jsf.getJSONfromURL(mUrl);

         	JsonObject jsonobject = jsonArray.get(0).getAsJsonObject();
         		
			String TripId = jsonobject.get("Id").getAsString();
			String TripName = jsonobject.get("Name").getAsString();
         	String TripDate = jsonobject.get("Date").getAsString();
         	
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString("TripId",TripId);
			editor.putString("TripName",TripName);
			editor.putString("TripDate", TripDate);
			editor.commit();

     	} catch (Exception e) {

			LogUtility logUtility = new LogUtility(context);
			logUtility.InsertLog(new BundleLog(this.getClass().getSimpleName(),
					"while trying to get JsonTrip"
					, e.toString()));
				return false;
		}
		
		return true;
	}
}