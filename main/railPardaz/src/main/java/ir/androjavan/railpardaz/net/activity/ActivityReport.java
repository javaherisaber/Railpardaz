package ir.androjavan.railpardaz.net.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ir.androjavan.railpardaz.net.JSON.JsonCheckNop;
import ir.androjavan.railpardaz.net.R;
import ir.androjavan.railpardaz.net.adapter.AdapterReportList;
import ir.androjavan.railpardaz.net.bundle.BundleLog;
import ir.androjavan.railpardaz.net.bundle.BundleReviewReport;
import ir.androjavan.railpardaz.net.utile.LogUtility;
import ir.androjavan.railpardaz.net.database.RailPardazDB;
import ir.androjavan.railpardaz.net.utile.CheckInternet;
import ir.androjavan.railpardaz.net.utile.FontChangeCrawler;
import ir.androjavan.railpardaz.net.utile.SendReport;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ActivityReport extends Activity implements OnClickListener{

	TextView txtProfileName, txtTripName, txtTripDate;
	ImageView btnSignOut, btnSync, btnBack;
	RecyclerView recyclerReport;
	LinearLayoutManager linearLayoutManager;
	AdapterReportList reportListAdapter;
	
	RailPardazDB db;
	
	String CheckNop = "default";
	
	List<BundleReviewReport> reportList = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report);
		initilizeObjects();
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String ProfileName =  preferences.getString("NameProfile", "");
		String TripName = preferences.getString("TripName", "");
		txtProfileName.setText(ProfileName);
		txtTripName.setText(" اطلاعات سفر : "+TripName);
		String TripDate = preferences.getString("TripDate", null);

		txtTripDate.setText(TripDate);
		
		
		db.open();
		Cursor mcursor = db.GetAllRecords(RailPardazDB.TABLE_REPORT, RailPardazDB.COLUMNS_REPORT);
    	mcursor.moveToFirst();
    	
		if(mcursor.getCount() != 0)
		{
			do 
    		{
				BundleReviewReport bundleReviewReport = new BundleReviewReport();
				int ErrorId = mcursor.getInt(2);
				String ErrorName = db.GetErrorNameWithID(ErrorId);
				bundleReviewReport.errorName = ErrorName;
				bundleReviewReport.vagonNum = " واگن : " + mcursor.getString(4);
				bundleReviewReport.comment = "کامنت : " + mcursor.getString(5);
				bundleReviewReport.status = mcursor.getInt(6);
				
				reportList.add(bundleReviewReport);
				
			} while (mcursor.moveToNext());
		}
		
		reportListAdapter = new AdapterReportList(reportList, getApplicationContext());
    	recyclerReport.setAdapter(reportListAdapter);
	}
	
	private void initilizeObjects(){
		
		FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "yekan.ttf");
	    fontChanger.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));
		
	    db = new RailPardazDB(getApplicationContext());
		txtProfileName = (TextView) findViewById(R.id.txtProfileName);
		txtTripName = (TextView) findViewById(R.id.txtTripName);
		btnSignOut = (ImageView) findViewById(R.id.btnSignOut);
		btnSync = (ImageView) findViewById(R.id.btnSync);
		recyclerReport = (RecyclerView) findViewById(R.id.recyclerReportList);
		linearLayoutManager = new LinearLayoutManager(getApplicationContext());
		recyclerReport.setHasFixedSize(true);
		recyclerReport.setLayoutManager(linearLayoutManager);
		btnBack = (ImageView) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(this);
		btnSignOut.setOnClickListener(this);
		btnSync.setOnClickListener(this);

		txtTripDate = (TextView) findViewById(R.id.txtTripDate);

		reportList = new ArrayList<BundleReviewReport>();
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

		case R.id.btnSignOut:
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
						db.open();
						Cursor mcursor = db.GetAllRecords(RailPardazDB.TABLE_REPORT, RailPardazDB.COLUMNS_REPORT);
				    	mcursor.moveToFirst();
				    	reportList.clear();
						if(mcursor.getCount() != 0)
						{
							do 
				    		{
								BundleReviewReport bundleReviewReport = new BundleReviewReport();
								int ErrorId = mcursor.getInt(2);
								String ErrorName = db.GetErrorNameWithID(ErrorId);
								bundleReviewReport.errorName = ErrorName;
								bundleReviewReport.vagonNum = " واگن : " + mcursor.getString(4);
								bundleReviewReport.comment = "کامنت : " + mcursor.getString(5);
								bundleReviewReport.status = mcursor.getInt(6);
								
								reportList.add(bundleReviewReport);
								
							} while (mcursor.moveToNext());
							
							reportListAdapter.notifyDataSetChanged();
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
		        	
			    	Intent intent = new Intent(ActivityReport.this,ActivityLogin.class);
			    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			    	startActivity(intent);
		        	
		            break;

		        case DialogInterface.BUTTON_NEGATIVE:
		            
		            break;
		        }
		    }
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(ActivityReport.this);
		builder.setMessage("مطمئنید که میخواهید خارج شوید ؟").setPositiveButton("بله", dialogClickListener)
		    .setNegativeButton("خیر", dialogClickListener).show();
	}
	
	private class SendReportAsync extends AsyncTask<Void, Void, Integer> {
    	boolean Status = false;
    	ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            
            pDialog = ProgressDialog.show(ActivityReport.this, "در حال همگام سازی گزارشات", "لطفا صبر کنید", true);
    		

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
        	if((CheckNop!=null &&CheckNop.equals("no"))||CheckNop.equals("default")) {
                if (Status) {
                    Toast.makeText(getApplicationContext(), "همگام سازی موفقیت آمیز بود", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "خطایی رخ داد ، دوباره امتحان کنید", Toast.LENGTH_LONG).show();
                }

                db.open();
                Cursor mcursor = db.GetAllRecords(RailPardazDB.TABLE_REPORT, RailPardazDB.COLUMNS_REPORT);
                mcursor.moveToFirst();
                reportList.clear();
                if (mcursor.getCount() != 0) {
                    do {
                        BundleReviewReport bundleReviewReport = new BundleReviewReport();
                        int ErrorId = mcursor.getInt(2);
                        String ErrorName = db.GetErrorNameWithID(ErrorId);
                        bundleReviewReport.errorName = ErrorName;
                        bundleReviewReport.vagonNum = " واگن : " + mcursor.getString(4);
                        bundleReviewReport.comment = "کامنت : " + mcursor.getString(5);
                        bundleReviewReport.status = mcursor.getInt(6);

                        reportList.add(bundleReviewReport);

                    } while (mcursor.moveToNext());

                    reportListAdapter = new AdapterReportList(reportList, getApplicationContext());
                    recyclerReport.setAdapter(reportListAdapter);
                }
            }
        	else if(CheckNop.equals("lock"))
        	{
    	    	Intent intent = new Intent(ActivityReport.this,ActivityProb.class);
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


