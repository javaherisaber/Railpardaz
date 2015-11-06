package ir.androjavan.railpardaz.net.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ir.androjavan.railpardaz.net.CheckInternet;
import ir.androjavan.railpardaz.net.FontChangeCrawler;
import ir.androjavan.railpardaz.net.json.JsonCheckNop;
import ir.androjavan.railpardaz.net.R;
import ir.androjavan.railpardaz.net.SendReport;
import ir.androjavan.railpardaz.net.bundle.BundleError;
import ir.androjavan.railpardaz.net.bundle.BundleReport;
import ir.androjavan.railpardaz.net.database.RailPardazDB;

public class ActivityFinalSubmitError extends AppCompatActivity implements OnClickListener {

	TextView txtTripName,txtProfile,txtHierarchy;
	BundleError parentError;
	
	ProgressDialog pDialog;
	String HierarchyText;
	String vagonNum;
	RailPardazDB db;
	ImageView btnSync,btnLogOut, btnReport;
	
	String CheckNop = "default";
	
	Button btnErrorSubmit;
	EditText edtErrorComment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_final_submit_error);
		
		initializeObject();
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String ProfileName =  preferences.getString("NameProfile", "");
		String TripName = preferences.getString("TripName", "");
		txtProfile.setText(ProfileName);
		txtTripName.setText(" اطلاعات سفر : "+TripName);
		
		parentError = new BundleError();
		parentError.id = getIntent().getIntExtra("errorId", 0);
		parentError.parentId = getIntent().getIntExtra("parentId", 0);
		parentError.name = getIntent().getStringExtra("errorName");
		vagonNum = getIntent().getStringExtra("vagonNum");
		
		this.HierarchyText = getIntent().getStringExtra("hierarchy") + " >> " + parentError.name;
		if(!HierarchyText.startsWith(vagonNum)){
			HierarchyText = vagonNum + HierarchyText;
		}
		txtHierarchy.setText(HierarchyText);
	
		
	}
	
	private void initializeObject()
	{
		FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "yekan.ttf");
	    fontChanger.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));
	    db = new RailPardazDB(getApplicationContext());
		btnSync = (ImageView) findViewById(R.id.btnSync);
		btnSync.setOnClickListener(this);
		btnLogOut = (ImageView) findViewById(R.id.btnSignOut);
		btnLogOut.setOnClickListener(this);
		btnReport = (ImageView) findViewById(R.id.btnReport);
		btnReport.setOnClickListener(this);
		txtProfile = (TextView) findViewById(R.id.txtProfileName);
		txtTripName = (TextView) findViewById(R.id.txtTripName);
		edtErrorComment = (EditText) findViewById(R.id.edtErrorComment);
	    btnErrorSubmit = (Button) findViewById(R.id.btnErrorSubmit);
	    btnErrorSubmit.setOnClickListener(this);
		txtHierarchy = (TextView) findViewById(R.id.txtHierarchy);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_final_submit_error, menu);
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
		
		CheckInternet checkInternet = new CheckInternet(getApplicationContext());
		
		switch (v.getId()) {
		case R.id.btnSync:
			
			if(checkInternet.isOnline()){
				try {
					new SendReportAsync().execute();
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
		case R.id.btnErrorSubmit :
			db.open();
			BundleReport reportInfo = new BundleReport();
			reportInfo.errorId = parentError.id;
			reportInfo.vagonID = String.valueOf(db.GetWagonID(vagonNum));
			reportInfo.comment = edtErrorComment.getText().toString();
			
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	    	String token = preferences.getString("token", "");
	    	reportInfo.token = token;
			reportInfo.status = 0;
			
	    	long f = db.InsertReport(reportInfo);
	    	Toast.makeText(getApplicationContext(), "اطلاعات با موفقیت در بانک ذخیره گردید", Toast.LENGTH_LONG).show();
	    	
			if(checkInternet.isOnline()) {

					Thread thread1 = new Thread(new Runnable() {
			            @Override
			            public void run() {
							try 
							{
				            	SendReport sReport = new SendReport(ActivityFinalSubmitError.this);
				        		sReport.SyncAllReports();
							}
							catch(Exception e)
							{
								// do nothing
							}
			            }
			        });

			        thread1.start();
				
			}
	    	
	    	Intent intent = new Intent(ActivityFinalSubmitError.this,ActivityVagon.class);
	    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
	    	startActivity(intent);
	    	
	    	Log.i("Insert Reprot status",String.valueOf(f));
	    	
	    	break;
		case R.id.btnSignOut :

			try {
				
				db.open();
				Cursor cursor = db.GetAllUnsyncedReports();
				if(cursor.getCount() != 0)
				{
					
					if(checkInternet.isOnline()){
						try {
							new SendReportAsync().execute();
						}
						catch(Exception e)
						{
							Toast.makeText(getApplicationContext(), "تمامی گزارشات همگام سازی نشده اند ، بعد از اطمینان از اتصال به اینترنت دوباره تلاش کنید", Toast.LENGTH_LONG).show();
						}
						ShowSignOutDialog();
					}
					else
					{
						Toast.makeText(getApplicationContext(), "تمامی گزارشات همگام سازی نشده اند ، بعد از اطمینان از اتصال به اینترنت دوباره تلاش کنید", Toast.LENGTH_LONG).show();
					}
					
				}
				else
				{
					ShowSignOutDialog();
				}
				
			}
			catch(Exception e)
			{
				Toast.makeText(getApplicationContext(), "خطایی رخ داد ، دوباره امتحان کنید", Toast.LENGTH_LONG).show();
			}
			
			break;
		case R.id.btnReport:
			Intent intent2 = new Intent(ActivityFinalSubmitError.this, ActivityReport.class);
			startActivity(intent2);
			break;	    	
		}
	}
	
	private void ShowSignOutDialog()
	{

		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        switch (which){
		        case DialogInterface.BUTTON_POSITIVE:
		        	
		        	db.ClearDatabaseAndPreferences();
		        	
			    	Intent intent = new Intent(ActivityFinalSubmitError.this,ActivityLogin.class);
			    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			    	startActivity(intent);
		        	
		            break;

		        case DialogInterface.BUTTON_NEGATIVE:
		            
		            break;
		        }
		    }
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(ActivityFinalSubmitError.this);
		builder.setMessage("مطمئنید که میخواهید خارج شوید ؟").setPositiveButton("بله", dialogClickListener)
		    .setNegativeButton("خیر", dialogClickListener).show();
	}
	
	private class SendReportAsync extends AsyncTask<Void, Void, Integer> {
    	boolean Status = false;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
    		
    		pDialog = ProgressDialog.show(ActivityFinalSubmitError.this, "در حال همگام سازی گزارشات", "لطفا صبر کنید");
        }

        @Override
        protected  Integer doInBackground(Void... params) {

        	try
        	{
        		JsonCheckNop jCheckNop =new JsonCheckNop(getApplicationContext());
				CheckNop = jCheckNop.CheckForUpdate(); 
	        	if((CheckNop!=null &&CheckNop.equals("no"))||CheckNop.equals("default"))
	        	{
	        		SendReport sReport = new SendReport(getApplicationContext());
	        		Status = sReport.SyncAllReports();
	        	}
        		
        	}
        	catch(Exception e){
        		Status = false;
        	}
	        	
        	return 0;
        }

        @Override
        protected void onPostExecute(Integer status) {
        	pDialog.dismiss();
        	if((CheckNop!=null &&CheckNop.equals("no"))||CheckNop.equals("default"))
        	{
            	if(Status)
            	{
            		Toast.makeText(getApplicationContext(), "همگام سازی موفقیت آمیز بود", Toast.LENGTH_LONG).show();
            	}
            	else
            	{
            		Toast.makeText(getApplicationContext(), "خطایی رخ داد ، دوباره امتحان کنید", Toast.LENGTH_LONG).show();
            	}
        	}
        	else if(CheckNop.equals("lock"))
        	{
    	    	Intent intent = new Intent(ActivityFinalSubmitError.this,ActivityProb.class);
    	    	startActivity(intent);
        	}


        }
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
