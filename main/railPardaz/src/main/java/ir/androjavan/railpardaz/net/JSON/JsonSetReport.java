package ir.androjavan.railpardaz.net.JSON;

import android.content.Context;
import android.net.Uri;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ir.androjavan.railpardaz.net.bundle.BundleLog;
import ir.androjavan.railpardaz.net.bundle.BundleReport;
import ir.androjavan.railpardaz.net.utile.LogUtility;

public class JsonSetReport {
	
	Context context;
	private String mUrl = "http://rps-net.com/api/setreport/";
	
	public JsonSetReport(Context context,BundleReport bundleReport) 
	{
		this.context = context;
		String Continiue = bundleReport.token +"|"+ bundleReport.errorId
				+"|"+bundleReport.tripId+"|"+bundleReport.vagonNum+"|"+bundleReport.comment;
		Continiue = Uri.encode(Continiue);
		mUrl = mUrl + Continiue;
	}
	
	public boolean GetReportStatus()
	{
		try
        {
         	JsonFunctions jsf = new JsonFunctions(context);
         	JsonArray jsonArray = jsf.getJSONfromURL(mUrl);

         	JsonObject jsonobject = jsonArray.get(0).getAsJsonObject();
         		
			String Status = jsonobject.get("Status").getAsString();

			if(Status.equals("true"))
				return true;
			else
				return false;

     	} catch (Exception e) {

			LogUtility logUtility = new LogUtility(context);
			logUtility.InsertLog(new BundleLog(this.getClass().getSimpleName(),
					"while trying to set Report"
					, e.toString()));
		}
		
		return false;
	}
}
