package ir.androjavan.railpardaz.net.json;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ir.androjavan.railpardaz.net.bundle.BundleError;
import ir.androjavan.railpardaz.net.bundle.BundleUser;
import ir.androjavan.railpardaz.net.bundle.BundleVagon;
import ir.androjavan.railpardaz.net.database.RailPardazDB;

public class JsonLogin {
	
	Context context;
	private String mUrl = "http://logicbase.ir/net/login.php";
    private String UrlParameters = "";

	RailPardazDB db;
	
	public JsonLogin(Context context ,String username ,String password) 
	{
		this.context = context;
        UrlParameters = "user=" + username + "&" + "pass=" + password;
	}
	
	public BundleUser GetTokenLogin()
	{
		BundleUser bundleUser = new BundleUser();

		try
        {
			db = new RailPardazDB(context);
			db.open();

         	JsonFunctions jsf = new JsonFunctions();
         	JsonArray jsonArray = jsf.getJsonArrayWithPOST(mUrl,UrlParameters);
			JsonObject userInfo_object = jsonArray.get(0).getAsJsonObject();
			         		
			bundleUser.Token = userInfo_object.get("id").getAsString();
			bundleUser.Name = userInfo_object.get("Name").getAsString();
			String TripName = userInfo_object.get("TripName").getAsString();

			if(bundleUser.Token.equals("-1"))
			{
				return bundleUser;
			}

			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString("TripName",TripName);
			editor.apply();

			JsonObject wagon_object = jsonArray.get(1).getAsJsonObject();
			JsonArray wagon_array = wagon_object.get("wagon").getAsJsonArray();

			for (int i = 0; i < wagon_array.size(); i++)
			{
				JsonObject wagonObject = wagon_array.get(i).getAsJsonObject();
				BundleVagon vagonInfo = new BundleVagon();
				vagonInfo.vagonNumber = wagonObject.get("wagonNum").getAsString();
				vagonInfo.id = wagonObject.get("id").getAsInt();

				db.InsertVagon(vagonInfo);
			}

			JsonObject error_object = jsonArray.get(2).getAsJsonObject();
			JsonArray errorArray = error_object.get("error").getAsJsonArray();

			for(int i = 0; i < errorArray.size(); i++)
			{

				JsonObject errorObject = errorArray.get(i).getAsJsonObject();
				String errorName = errorObject.get("title").getAsString();
				int errorId = errorObject.get("id").getAsInt();
				int errorParentId = errorObject.get("parentID").getAsInt();
				BundleError errorBundle = new BundleError();
				errorBundle.id = errorId;
				errorBundle.name = errorName;
				errorBundle.parentId = errorParentId;
				db.InsertError(errorId, errorParentId, errorName);

			}

     	} catch (Exception e) {

     		Log.e("XML error",e.toString() + "");
			Log.e("Internet", "internet connection is not available");
		}
		finally {
			db.close();
		}
	 return bundleUser;
	}
}
