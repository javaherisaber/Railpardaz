package ir.androjavan.railpardaz.net.adapter;

import ir.androjavan.railpardaz.net.activity.ActivityErrorDetail;
import ir.androjavan.railpardaz.net.utile.FontChangeCrawler;
import ir.androjavan.railpardaz.net.R;
import ir.androjavan.railpardaz.net.bundle.BundleReviewReport;
import ir.androjavan.railpardaz.net.database.RailPardazDB;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class AdapterReportList extends RecyclerView.Adapter<AdapterReportList.ContactViewHolder>{
	private List<BundleReviewReport> reportList;
	private Context contextMain;
	RailPardazDB db;
    String TripName;
    String TripDate;

    public AdapterReportList(List<BundleReviewReport> reportList,Context context) {
        this.reportList = reportList;
        this.contextMain = context;
        db = new RailPardazDB(context);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        TripName = preferences.getString("TripName", "");
        TripDate = preferences.getString("TripDate", null);

    }

    public AdapterReportList(Context context) {
        reportList = new ArrayList<BundleReviewReport>();
        this.contextMain = context;
        
    }
    
    @Override
    public int getItemCount() {
        return reportList.size();
    }
    
    public void removeItem(int index) {
        reportList.remove(index);
        notifyDataSetChanged();
    }
    
    public void removeAll() {
    	reportList.clear();
    	notifyDataSetChanged();
	}
    
    public void insertItem(int count ,BundleReviewReport object) {

    	reportList.add(object);  
    	notifyDataSetChanged();

    }

    
    @Override
    public void onBindViewHolder(ContactViewHolder appViewHolder, final int position) {
    	
        BundleReviewReport reportInfo = reportList.get(position);
        appViewHolder.txtErrorName.setText(reportInfo.errorName);
        appViewHolder.txtComment.setText(reportInfo.comment);
        appViewHolder.txtVagonNum.setText("(" + reportInfo.vagonNum + ")");
        if(reportInfo.status == 1){
        	appViewHolder.imgStatus.setImageResource(R.drawable.ic_report_tick);
        }
        else{
        	appViewHolder.imgStatus.setImageResource(R.drawable.ic_report_error);
        }
        appViewHolder.txtTripName.setText(TripName);
        appViewHolder.txtTripDate.setText(TripDate);
        
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
	    	FontChangeCrawler fontChanger = new FontChangeCrawler(contextMain.getAssets(), "yekan.ttf");
	        fontChanger.replaceFonts((ViewGroup)viewGroup);
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_report_item, viewGroup, false);
            ContactViewHolder error = new ContactViewHolder(itemView);
            return error;
       
    }

	public class ContactViewHolder extends RecyclerView.ViewHolder {

        protected TextView txtErrorName, txtComment, txtVagonNum, txtTripName, txtTripDate;
        ImageView imgStatus;
        

        public ContactViewHolder(View v) {
            super(v);
            
            contextMain = v.getContext();
            txtErrorName =  (TextView) v.findViewById(R.id.txtErrorName);
            txtComment = (TextView) v.findViewById(R.id.txtComment);
            txtVagonNum = (TextView) v.findViewById(R.id.txtVagonNum);
            imgStatus = (ImageView) v.findViewById(R.id.imgStatus);
            txtTripName = (TextView) v.findViewById(R.id.txtTripName);
            txtTripDate = (TextView) v.findViewById(R.id.txtTripDate);
            
            v.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {

                    Intent intent = new Intent(contextMain, ActivityErrorDetail.class);
                    intent.putExtra("errorName", txtErrorName.getText().toString());
                    intent.putExtra("errorComment", txtComment.getText().toString());
                    intent.putExtra("vagonNum", txtVagonNum.getText().toString());
                    intent.putExtra("tripName", txtTripName.getText().toString());
                    intent.putExtra("tripDate", txtTripDate.getText().toString());
                    contextMain.startActivity(intent);
				}
			});       
        }
		
    } 
}
