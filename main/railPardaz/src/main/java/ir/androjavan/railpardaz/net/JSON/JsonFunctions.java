package ir.androjavan.railpardaz.net.JSON;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

import ir.androjavan.railpardaz.net.bundle.BundleLog;
import ir.androjavan.railpardaz.net.utile.LogUtility;

public class JsonFunctions
{
    private Context context;

    public JsonFunctions(Context ctx) {
        this.context = ctx;
    }

    public JsonArray getJSONfromURL(String inputURL)
    {
    	JsonArray jArray = null;

	    // Connect to the URL using java's native library
	    URL url = null;
		try {
			url = new URL(inputURL);
		} catch (MalformedURLException e) {
			LogUtility logUtility = new LogUtility(context);
			logUtility.InsertLog(new BundleLog(this.getClass().getSimpleName(),
					"Url Malformed"
					, e.toString()));
		}
	    
	    try {
	    	
	    	HttpURLConnection request = (HttpURLConnection) url.openConnection();
			request.connect();

		    // Convert to a JSON object to print data
		    JsonParser jp = new JsonParser(); //from gson
		    JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
		    jArray = root.getAsJsonArray(); //May be an array, may be an object. 
		} catch (IOException e) {
			LogUtility logUtility = new LogUtility(context);
			logUtility.InsertLog(new BundleLog(this.getClass().getSimpleName(),
					"while trying to connect to the server"
					, e.toString()));
		}
	    
	    return jArray;
    }

    public JsonArray getNewVerWithPOST(String URL,String UrlParameters)
    {
        JsonArray jArray = null;
        String USER_AGENT = "Mozilla/5.0";

        URL url = makeUrl(URL);

        HttpURLConnection con = null ;
        try {
            con = (HttpURLConnection) url.openConnection();
            //add reuqest header
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setConnectTimeout(3000);
            con.setReadTimeout(3000);

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(UrlParameters);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();

            if(responseCode != HttpURLConnection.HTTP_OK)
            {
                LogUtility logUtility = new LogUtility(context);
                logUtility.InsertLog(new BundleLog(this.getClass().getSimpleName(),
                        "Http url is not OK "
                        , "response code is :"+responseCode));
                return null;
            }

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "UTF-8"));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();


            // Convert to a JSON object to print data
            JsonParser jp = new JsonParser(); //from gson
            JsonElement root = jp.parse(response.toString()); //Convert the input stream to a json element
            jArray = root.getAsJsonArray(); //May be an array, may be an object.
        }catch (SocketTimeoutException e) {

            LogUtility logUtility = new LogUtility(context);
            logUtility.InsertLog(new BundleLog(this.getClass().getSimpleName(),
                    "More than 3 secs elapsed "
                    , e.toString()));
        } catch (IOException e) {
            e.printStackTrace();
            LogUtility logUtility = new LogUtility(context);
            logUtility.InsertLog(new BundleLog(this.getClass().getSimpleName(),
                    "IOException occur while trying to connect to server by HttpPOST"
                    , e.toString()));
        } finally {
            if(con != null)
                con.disconnect(); //this method is for releasing pool
        }

        return jArray;
    }

    private URL makeUrl(String inputUrl)
    {
        // Connect to the URL using java's native library
        URL url = null;
        try {
            url = new URL(inputUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            LogUtility logUtility = new LogUtility(context);
            logUtility.InsertLog(new BundleLog(this.getClass().getSimpleName(),
                    "MalformedUrlException occur"
                    , e.toString()));
        }
        return url;
    }
}