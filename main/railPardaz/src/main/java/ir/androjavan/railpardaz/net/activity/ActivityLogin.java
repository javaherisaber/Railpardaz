package ir.androjavan.railpardaz.net.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ir.androjavan.railpardaz.net.JSON.JsonCheckNop;
import ir.androjavan.railpardaz.net.JSON.JsonError;
import ir.androjavan.railpardaz.net.JSON.JsonLogin;
import ir.androjavan.railpardaz.net.JSON.JsonTrip;
import ir.androjavan.railpardaz.net.JSON.JsonVagon;
import ir.androjavan.railpardaz.net.R;
import ir.androjavan.railpardaz.net.bundle.BundleUser;
import ir.androjavan.railpardaz.net.database.RailPardazDB;
import ir.androjavan.railpardaz.net.utile.CheckInternet;
import ir.androjavan.railpardaz.net.utile.FontChangeCrawler;
import ir.androjavan.railpardaz.net.utile.MacAddress;

public class ActivityLogin extends AppCompatActivity implements OnClickListener {

	Button btnLogin;
	EditText edtUsername;
	EditText edtPassword;
	MacAddress macAddress;
	ProgressDialog progress;
	String CheckNop = "default";
	
	RailPardazDB db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		initializeObject();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		BundleUser bundleUser = new BundleUser();
		bundleUser.Token = preferences.getString("token", "");

//        try {
//            throw new Exception("this is my new exception");
//        }catch (Exception e)
//        {
//            LogUtility logUtility = new LogUtility(getApplicationContext());
//            logUtility.InsertLog(new BundleLog(this.getClass().getSimpleName(),
//                    "My New Exception occur's"
//                    , e.toString()));
//        }
		
		if(!bundleUser.Token.equals(""))
		{
	        goToMainActivity();
		}

	}

	
	private void initializeObject() {
		
		FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "yekan.ttf");
	    fontChanger.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));
	    
	    db = new RailPardazDB(getApplicationContext());

		btnLogin = (Button) findViewById(R.id.btnLogin);
		edtUsername = (EditText) findViewById(R.id.edtUser);
		edtPassword = (EditText) findViewById(R.id.edtPass);
		macAddress = new MacAddress(getApplicationContext());
		btnLogin.setOnClickListener(this);

        edtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    hideKeyboardPassword();
                }
            }
        });


        edtUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    hideKeyboardUsername();
                }
            }
        });
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.drawer, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btnLogin:
			CheckInternet checkInternet = new CheckInternet(getApplicationContext());
			if(checkInternet.isOnline()){
				try {
					new LoginRequest().execute();
				}
				catch(Exception e)
				{
					Toast.makeText(getApplicationContext(), "اینترنت شما قطع میباشد", Toast.LENGTH_LONG).show();
				}
			}
			else
			{
				Toast.makeText(getApplicationContext(), "اینترنت شما قطع میباشد", Toast.LENGTH_LONG).show();
			}
			break;

		default:
			break;
		}
	}

	
	private class LoginRequest extends AsyncTask<Void, Void, BundleUser> {

		String username;
		String password;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
    		progress = ProgressDialog.show(ActivityLogin.this, "ورود","در حال بررسی اطلاعات", true);
			username = edtUsername.getText().toString();
			password = edtPassword.getText().toString();
        }

        @Override
        protected  BundleUser doInBackground(Void... params) {

        	BundleUser bundleUser = new BundleUser();
        	
        	try{

					
					JsonLogin jsonLogin = new JsonLogin(getApplicationContext(), username, password);
					bundleUser = jsonLogin.GetTokenLogin();
					if(bundleUser.Token.equals("-1") || bundleUser.Token.equals("-2"))
					{
						return bundleUser;
					}
					else
					{
						boolean status = false;
						JsonTrip jsonTrip = new JsonTrip(getApplicationContext(), bundleUser.Token);
						status = jsonTrip.GetTripInfo();
						if(status == false)
						{
							bundleUser.Token = "-4";
							return bundleUser;
						}
						JsonVagon jsonvagon = new JsonVagon(getApplicationContext(), bundleUser.Token);
						status = jsonvagon.GetVagonList();
						if(status == false)
						{
							bundleUser.Token = "-3";
							return bundleUser;
						}
						JsonError jsonerror = new JsonError(getApplicationContext(), bundleUser.Token);
						status = jsonerror.GetErrorList();
						if(status == false)
						{
							bundleUser.Token = "-3";
							return bundleUser;
						}
						
						JsonCheckNop jCheckNop =new JsonCheckNop(getApplicationContext());
						CheckNop = jCheckNop.CheckForUpdate();

					}					
        	}
        	catch(Exception e){
        		
        	}
	        	
        	return bundleUser;
        }

        @Override
        protected void onPostExecute(BundleUser bundleUser) {
        	progress.dismiss();
        	if((CheckNop!=null &&CheckNop.equals("no"))||CheckNop.equals("default"))
        	{
        		if(bundleUser!=null && bundleUser.Token.equals("-4"))
        		{
        			Toast.makeText(getApplicationContext() 
        					, "سفری برای شما تعریف نشده است"
        					, Toast.LENGTH_LONG).show();
        			 
        			db.open();
        			db.ClearDatabaseAndPreferences();
        		}
        		else if(bundleUser!=null && bundleUser.Token.equals("-3"))
        		{
        			Toast.makeText(getApplicationContext() 
        					, "خطایی در برقراری ارتباط رخ داد ، دوباره امتحان کنید"
        					, Toast.LENGTH_LONG).show();
        			 
        			db.open();
        			db.ClearDatabaseAndPreferences();
        		}
        		else if(bundleUser!=null && bundleUser.Token.equals("-2"))
        		{
        			Toast.makeText(getApplicationContext() , "از اتصال به اینترنت اطمینان حاصل نموده و دوباره امتحان کنید", Toast.LENGTH_LONG).show();
        		}
            	else if(bundleUser!=null && bundleUser.Token.equals("-1"))
            	{
            		Toast.makeText(getApplicationContext() , "نام کاربری یا رمز عبور اشتباه است", Toast.LENGTH_LONG).show();
            	}
        		else if(bundleUser!=null && !bundleUser.Token.equals("-1"))
            	{
            		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            		SharedPreferences.Editor editor = preferences.edit();
            		editor.putString("token",bundleUser.Token);
            		editor.putString("NameProfile", bundleUser.Name);
            		editor.commit();
            		goToMainActivity();
            	}
        	}
        	else if(CheckNop.equals("lock"))
        	{
    	    	Intent intent = new Intent(ActivityLogin.this,ActivityProb.class);
    	    	startActivity(intent);
        	}

        }
    }


	private void hideKeyboardPassword() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(edtPassword.getWindowToken(), 0);
	}

	private void hideKeyboardUsername() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(edtUsername.getWindowToken(), 0);
	}
	
	private void goToMainActivity(){
		
		Intent GotoVagon = new Intent(getApplicationContext(),ActivityVagon.class);
		startActivity(GotoVagon);
		this.finish();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		db.close();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		db.open();
	}
}
