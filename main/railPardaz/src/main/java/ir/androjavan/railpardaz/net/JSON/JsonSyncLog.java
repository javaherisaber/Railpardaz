package ir.androjavan.railpardaz.net.JSON;

import android.content.Context;
import android.net.Uri;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ir.androjavan.railpardaz.net.bundle.BundleLog;
import ir.androjavan.railpardaz.net.utile.LogUtility;

/**
 * Created by Mahdi on 18/05/2016.
 */
public class JsonSyncLog {

    Context context;
    private String mUrl = "http://rps-net.com/api/OURapi";

    public JsonSyncLog(Context context,BundleLog bundleLog)
    {
        this.context = context;
        String Continue = bundleLog.tagName +"|"+ bundleLog.logBody
                +"|"+bundleLog.logInfo+"|"+bundleLog.lineNumber+"|"+bundleLog.dateTime;
        Continue = Uri.encode(Continue);
        mUrl = mUrl + Continue;
    }

    public boolean getSyncLogStatus()
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
                    "while trying to sync log information's"
                    , e.toString()));
        }

        return false;
    }
}
