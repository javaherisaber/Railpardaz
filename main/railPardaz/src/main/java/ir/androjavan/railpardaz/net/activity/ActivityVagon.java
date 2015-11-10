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
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ir.androjavan.railpardaz.net.JSON.JsonCheckNop;
import ir.androjavan.railpardaz.net.R;
import ir.androjavan.railpardaz.net.adapter.AdapterErrorList;
import ir.androjavan.railpardaz.net.bundle.BundleError;
import ir.androjavan.railpardaz.net.bundle.BundleLog;
import ir.androjavan.railpardaz.net.utile.LogUtility;
import ir.androjavan.railpardaz.net.database.RailPardazDB;
import ir.androjavan.railpardaz.net.utile.CheckInternet;
import ir.androjavan.railpardaz.net.utile.FontChangeCrawler;
import ir.androjavan.railpardaz.net.utile.SendReport;

public class ActivityVagon extends AppCompatActivity implements OnClickListener {
	
	private Spinner spinnerVagonItems;
	RecyclerView recyclerErrorList;
	LinearLayoutManager linearLayoutManager;
	AdapterErrorList errorListAdapter;
	DrawerLayout mDrawerLayout;
	TextView txtProfileName, txtTripName, txtTripDate;
	ImageView btnSync,btnLogOut, btnReport, btnBack;
	
	String CheckNop = "default";
	ArrayAdapter<String> spinnerVagonListAdapter = null;
	
	RailPardazDB db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_vagon);
		
		initializeObjects();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String TripName = preferences.getString("TripName", "");

        String ProfileName =  preferences.getString("NameProfile", "");
		txtProfileName.setText(ProfileName);
		txtTripName.setText("اطلاعات سفر : " + TripName);
		new getVagonItems().execute();

		String TripDate = preferences.getString("TripDate", null);

		txtTripDate.setText(TripDate);

	}
	
	private void initializeObjects(){
		FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "yekan.ttf");
	    fontChanger.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));
	    
		db = new RailPardazDB(getApplicationContext());
		txtTripName = (TextView) findViewById(R.id.txtTripName);
		btnSync = (ImageView) findViewById(R.id.btnSync);
		btnSync.setOnClickListener(this);
		btnLogOut = (ImageView) findViewById(R.id.btnSignOut);
		btnLogOut.setOnClickListener(this);
		btnReport = (ImageView) findViewById(R.id.btnReport);
		btnReport.setOnClickListener(this);
		txtProfileName = (TextView) findViewById(R.id.txtProfileName);
		recyclerErrorList = (RecyclerView) findViewById(R.id.recyclerErrorList);
		linearLayoutManager = new LinearLayoutManager(getApplicationContext());
		recyclerErrorList.setHasFixedSize(true);
		recyclerErrorList.setLayoutManager(linearLayoutManager);
		txtTripDate = (TextView) findViewById(R.id.txtTripDate);

		btnBack = (ImageView) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(this);
		
		spinnerVagonItems = (Spinner) findViewById(R.id.spinnerVagonNumber);
		spinnerVagonItems.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String vagon = parent.getItemAtPosition(position).toString();
				
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        		SharedPreferences.Editor editor = preferences.edit();
        		editor.putString("vagonNum",vagon);
        		editor.commit();
        		
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.menu.vagon, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
        case android.R.id.home:
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;     
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private class getVagonItems extends AsyncTask<Void, Void, String[]> {
		
		List<BundleError> errorList;
		
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
    		
        }

        @Override
        protected  String[] doInBackground(Void... params) {
        	
        	db.open();
        	Cursor cursor = db.GetAllRecords(RailPardazDB.TABLE_VAGON, RailPardazDB.COLUMNS_VAGON);
        	cursor.moveToFirst();
        	String[] vagonList = null;
        	if(cursor.getCount() != 0)
        	{
        		int index = 0;
        		vagonList = new String[cursor.getCount()];
        		do 
        		{
    				vagonList[index++] = cursor.getString(2);
    			} while (cursor.moveToNext());
        	}
//        	selectedVagon = vagonList[0];
        	Cursor mcursor = db.GetErrorWithParentID(0);
        	
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

        	return vagonList;
        }

        @Override
        protected void onPostExecute(String[] vagonList) {
        	try 
        	{
				spinnerVagonListAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, vagonList);
            	spinnerVagonListAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            	
            	errorListAdapter = new AdapterErrorList(errorList, getApplicationContext(),"");
            	
            	
                spinnerVagonItems.setAdapter(spinnerVagonListAdapter);
                recyclerErrorList.setAdapter(errorListAdapter);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ActivityVagon.this);
                String vagonNum = preferences.getString("vagonNum", "");
                if(!vagonNum.equals("")){
                    spinnerVagonItems.setSelection(spinnerVagonListAdapter.getPosition(vagonNum));
                }
        	}
        	catch(Exception e)
        	{
				LogUtility logUtility = new LogUtility(getApplicationContext());
				logUtility.InsertLog(new BundleLog(this.getClass().getSimpleName(),
						"an error occur's while binding wagon list"
						, e.toString()));
        	}
        }
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
			Intent intent2 = new Intent(ActivityVagon.this, ActivityReport.class);
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
		        	
			    	Intent intent = new Intent(ActivityVagon.this,ActivityLogin.class);
			    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			    	startActivity(intent);
		        	
		            break;

		        case DialogInterface.BUTTON_NEGATIVE:
		            
		            break;
		        }
		    }
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(ActivityVagon.this);
		builder.setMessage("مطمئنید که میخواهید خارج شوید ؟").setPositiveButton("بله", dialogClickListener)
		    .setNegativeButton("خیر", dialogClickListener).show();
	}
	
	private class SendReportAsync extends AsyncTask<Void, Void, Integer> {
    	boolean Status = false;
    	ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            
            pDialog = ProgressDialog.show(ActivityVagon.this, "در حال همگام سازی گزارشات", "لطفا صبر کنید", true);
    		

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
    	    	Intent intent = new Intent(ActivityVagon.this,ActivityProb.class);
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
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ActivityVagon.this);
		String vagonNum = preferences.getString("vagonNum", "");
        if(!vagonNum.equals("") && spinnerVagonListAdapter != null){
            spinnerVagonItems.setSelection(spinnerVagonListAdapter.getPosition(vagonNum));
        }
	}
}
