package ir.androjavan.railpardaz.net.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ir.androjavan.railpardaz.net.JSON.JsonCheckNop;
import ir.androjavan.railpardaz.net.R;
import ir.androjavan.railpardaz.net.adapter.AdapterErrorList;
import ir.androjavan.railpardaz.net.bundle.BundleError;
import ir.androjavan.railpardaz.net.bundle.BundleLog;
import ir.androjavan.railpardaz.net.bundle.BundleReport;
import ir.androjavan.railpardaz.net.utile.LogUtility;
import ir.androjavan.railpardaz.net.database.RailPardazDB;
import ir.androjavan.railpardaz.net.utile.CheckInternet;
import ir.androjavan.railpardaz.net.utile.FontChangeCrawler;
import ir.androjavan.railpardaz.net.utile.SendReport;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ActivitySubmitError extends AppCompatActivity implements OnClickListener {
	
	RecyclerView recyclerErrorList;
	LinearLayoutManager linearLayoutManager;
	AdapterErrorList errorListAdapter;
	
	ProgressDialog pDialog;
	TextView txtTripName,txtProfile,txtHierarchy,txtTripDate;
	RailPardazDB db;
	List<BundleError> errorList;
	BundleError parentError;
	Button btnErrorSubmit;
	EditText edtErrorComment;
	ImageView btnSync,btnLogOut, btnReport, btnBack;
	
	String CheckNop = "default";
	
	String HierarchyText;
	String vagonNum;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_submit_error);
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
		
		this.vagonNum = getIntent().getStringExtra("vagonNum");
		this.HierarchyText = getIntent().getStringExtra("hierarchy") + " >> " + parentError.name;
		if(!HierarchyText.startsWith(vagonNum)){
			HierarchyText = vagonNum + HierarchyText;
		}
		txtHierarchy.setText(HierarchyText);
		db.open();
		
		Cursor mcursor = db.GetErrorWithParentID(parentError.id);
    	mcursor.moveToFirst();
    	if(mcursor.getCount() != 0)
    	{
    		errorList = new ArrayList<BundleError>();
    		do 
    		{
				BundleError errorBundle = new  BundleError();
				errorBundle.id = mcursor.getInt(1);
				errorBundle.parentId = mcursor.getInt(2);
				errorBundle.name = mcursor.getString(3);
				errorList.add(errorBundle);
				
			} while (mcursor.moveToNext());
    	}
    	
    	errorListAdapter = new AdapterErrorList(errorList, getApplicationContext(),HierarchyText);
    	recyclerErrorList.setAdapter(errorListAdapter);

		String TripDate = preferences.getString("TripDate", null);

		txtTripDate.setText(TripDate);
    	
	}

	
	private void initializeObject(){
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
		txtTripDate = (TextView) findViewById(R.id.txtTripDate);
		recyclerErrorList = (RecyclerView) findViewById(R.id.recyclerErrorList);
		linearLayoutManager = new LinearLayoutManager(getApplicationContext());
		recyclerErrorList.setHasFixedSize(true);
		recyclerErrorList.setLayoutManager(linearLayoutManager);
		btnBack = (ImageView) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(this);


		edtErrorComment.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus){
					hideKeyboard();
				}
			}
		});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_submit_error, menu);
		return true;
	}


	private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(edtErrorComment.getWindowToken(), 0);
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
			reportInfo.vagonNum = vagonNum;
			reportInfo.comment = edtErrorComment.getText().toString();
			
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	    	String token = preferences.getString("token", "");
	    	String TripId = preferences.getString("TripId", "");
	    	reportInfo.token = token;
			reportInfo.tripId = TripId;
			reportInfo.comment = edtErrorComment.getText().toString();
			reportInfo.status = 0;
	    	
	    	long f = db.InsertReport(reportInfo);
	    	
	    	Toast.makeText(getApplicationContext(), "اطلاعات با موفقیت در بانک ذخیره گردید", Toast.LENGTH_LONG).show();
			if(checkInternet.isOnline()) {

					Thread thread1 = new Thread(new Runnable() {
			            @Override
			            public void run() {
							try 
							{
				            	SendReport sReport = new SendReport(ActivitySubmitError.this);
				        		sReport.SyncAllReports();
							}
							catch(Exception e)
							{
								LogUtility logUtility = new LogUtility(getApplicationContext());
								logUtility.InsertLog(new BundleLog(this.getClass().getSimpleName(),
										"while trying to sync report"
										, e.toString()));
							}
			            }
			        });

			        thread1.start();
				
			}

	    	
	    	Intent intent = new Intent(ActivitySubmitError.this,ActivityVagon.class);
	    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
	    	startActivity(intent);
	    	
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
				LogUtility logUtility = new LogUtility(getApplicationContext());
				logUtility.InsertLog(new BundleLog(this.getClass().getSimpleName(),
						"When trying to sync report in sign out dialog"
						, e.toString()));
			}

            break;

		case R.id.btnReport:
			Intent intent2 = new Intent(ActivitySubmitError.this, ActivityReport.class);
			startActivity(intent2);
			break;


			case R.id.btnBack:
				this.finish();
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
		        	
			    	Intent intent = new Intent(ActivitySubmitError.this,ActivityLogin.class);
			    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			    	startActivity(intent);
		        	
		            break;

		        case DialogInterface.BUTTON_NEGATIVE:
		            
		            break;
		        }
		    }
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(ActivitySubmitError.this);
		builder.setMessage("مطمئنید که میخواهید خارج شوید ؟").setPositiveButton("بله", dialogClickListener)
		    .setNegativeButton("خیر", dialogClickListener).show();
	}
	
	private class SendReportAsync extends AsyncTask<Void, Void, Integer> {
    	boolean Status = false;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
    		
    		pDialog = ProgressDialog.show(ActivitySubmitError.this, "در حال همگام سازی گزارشات", "لطفا صبر کنید");
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
    	    	Intent intent = new Intent(ActivitySubmitError.this,ActivityProb.class);
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
