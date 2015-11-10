package ir.androjavan.railpardaz.net.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import ir.androjavan.railpardaz.net.utile.FontChangeCrawler;
import ir.androjavan.railpardaz.net.R;
import ir.androjavan.railpardaz.net.activity.ActivityFinalSubmitError;
import ir.androjavan.railpardaz.net.activity.ActivitySubmitError;
import ir.androjavan.railpardaz.net.bundle.BundleError;
import ir.androjavan.railpardaz.net.database.RailPardazDB;

import java.util.ArrayList;
import java.util.List;

public class AdapterErrorList extends RecyclerView.Adapter<AdapterErrorList.ContactViewHolder> {

    private List<BundleError> errorList;
	private Context contextMain;
	RailPardazDB db;
	
	private String Hierarchy;
	
    public AdapterErrorList(List<BundleError> errorList,Context context,String hierarchy) {
        this.errorList = errorList;
        this.contextMain = context;
        db = new RailPardazDB(context);
        this.Hierarchy = hierarchy;
    }

    public AdapterErrorList(Context context) {
        errorList = new ArrayList<BundleError>();
        this.contextMain = context;
        
    }
    
    @Override
    public int getItemCount() {
        return errorList.size();
    }
    
    public void removeItem(int index) {
        errorList.remove(index);
        notifyDataSetChanged();
    }
    
    public void removeAll() {
    	errorList.clear();
    	notifyDataSetChanged();
	}
    
    public void insertItem(int count ,BundleError object) {

    	errorList.add(object);  
    	notifyDataSetChanged();

    }

    
    @Override
    public void onBindViewHolder(ContactViewHolder appViewHolder, final int position) {
    	
        BundleError errorInfo = errorList.get(position);
        appViewHolder.errorName.setText("+  " + errorInfo.name);
        
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
	    	FontChangeCrawler fontChanger = new FontChangeCrawler(contextMain.getAssets(), "yekan.ttf");
	        fontChanger.replaceFonts((ViewGroup)viewGroup);
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_item_error, viewGroup, false);
            ContactViewHolder error = new ContactViewHolder(itemView);
            return error;
       
    }

	public class ContactViewHolder extends RecyclerView.ViewHolder {

        protected TextView errorName;

        public ContactViewHolder(View v) {
            super(v);
            
            contextMain = v.getContext();
            errorName =  (TextView) v.findViewById(R.id.txtErrorName);
            
            v.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					
					int errorParentId = errorList.get(getLayoutPosition()).parentId;
					String errorName = errorList.get(getLayoutPosition()).name;
					int errorId = errorList.get(getLayoutPosition()).id;
					
					db.open();
					Cursor mcursor = db.GetErrorWithParentID(errorId);
			    	mcursor.moveToFirst();
			    	db.close();
					if(mcursor.getCount() == 0){
						
						SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(contextMain);
						String vagonNum = preferences.getString("vagonNum", "");
						
						Intent goToActivityFinalSubmitError = new Intent(contextMain, ActivityFinalSubmitError.class);
						goToActivityFinalSubmitError.putExtra("parentId", errorParentId);
						goToActivityFinalSubmitError.putExtra("errorName", errorName);
						goToActivityFinalSubmitError.putExtra("errorId", errorId);
						goToActivityFinalSubmitError.putExtra("hierarchy", Hierarchy);
						goToActivityFinalSubmitError.putExtra("vagonNum", vagonNum);
						contextMain.startActivity(goToActivityFinalSubmitError);
					}
					else{
						
						SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(contextMain);
						String vagonNum = preferences.getString("vagonNum", "");
						
						Intent goToActivitySubmitError = new Intent(contextMain, ActivitySubmitError.class);
						goToActivitySubmitError.putExtra("parentId", errorParentId);
						goToActivitySubmitError.putExtra("errorName", errorName);
						goToActivitySubmitError.putExtra("errorId", errorId);
						goToActivitySubmitError.putExtra("hierarchy", Hierarchy);
						goToActivitySubmitError.putExtra("vagonNum", vagonNum);
						contextMain.startActivity(goToActivitySubmitError);
					}
				}
			});       
        }
		
    } 

}