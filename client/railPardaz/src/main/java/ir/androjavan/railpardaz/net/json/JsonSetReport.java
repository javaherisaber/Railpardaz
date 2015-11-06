package ir.androjavan.railpardaz.net.json;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ir.androjavan.railpardaz.net.bundle.BundleReport;

public class JsonSetReport {
	
	Context context;
	private String mUrl = "http://logicbase.ir/net/setreport.php";
	private String urlParameters;

	public JsonSetReport(Context context,BundleReport bundleReport) 
	{
		this.context = context;
        urlParameters = "token="+bundleReport.token+"&error="
                +bundleReport.errorId+"&wagon="+bundleReport.vagonID+"&comment=" + bundleReport.comment;
	}
	
	public boolean GetReportStatus()
	{
		try
        {
         	JsonFunctions jsf = new JsonFunctions();
         	JsonArray jsonArray = jsf.getJsonArrayWithPOST(mUrl,urlParameters);

         	JsonObject jsonobject = jsonArray.get(0).getAsJsonObject();
         		
			String Status = jsonobject.get("Status").getAsString();

			if(Status.equals("true"))
				return true;
			else
				return false;

     	} catch (Exception e) {

				Log.e("Internet", "internet connection is not available");
		}
		
		return false;
	}
}
