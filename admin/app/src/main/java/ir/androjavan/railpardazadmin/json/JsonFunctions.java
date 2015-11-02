package ir.androjavan.railpardazadmin.json;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class JsonFunctions
{

	private final String USER_AGENT = "Mozilla/5.0";

	public JsonArray getJsonArrayWithPOST(String URL,String UrlParameters)
	{
		JsonArray jArray = null;

		URL url = makeUrl(URL);

		HttpURLConnection con = null ;
		try {
			con = (HttpURLConnection) url.openConnection();
			//add reuqest header
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", USER_AGENT);
//			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			con.setUseCaches(true); // enable using Response Cache


			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(UrlParameters);
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();

			if(responseCode != HttpURLConnection.HTTP_OK)
			{
				Log.e(this.getClass().getSimpleName(),"Http url is not OK :"
						+ "response code is :"+responseCode);
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
		} catch (IOException e) {
			Log.e(this.getClass().getSimpleName(),e.toString());
		} finally {
			if(con != null)
				con.disconnect(); //this method is for releasing pool
		}

		return jArray;
	}

	public JsonArray getJsonArrayWithGET(String URL,String UrlParameters)
	{
		JsonArray jArray = null;

		URL url = makeUrl(URL + "?" + UrlParameters);

		HttpURLConnection con = null ;
		try {
			con = (HttpURLConnection) url.openConnection();

			// optional default is GET
			con.setRequestMethod("GET");

			//add request header
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setUseCaches(true); // enable using Response Cache


			int responseCode = con.getResponseCode();

			if(responseCode != HttpURLConnection.HTTP_OK)
			{
				Log.e(this.getClass().getSimpleName(),"Http url is not OK :"
						+ "response code is :"+responseCode);
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
		} catch (IOException e) {
			Log.e(this.getClass().getSimpleName(),e.toString());
		} finally {
			if(con != null)
				con.disconnect(); //this method is for releasing network pool
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
			Log.e(this.getClass().getSimpleName(),e.toString());
		}
		return url;
	}
}