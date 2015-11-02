package ir.androjavan.railpardazadmin.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ir.androjavan.railpardazadmin.R;
import ir.androjavan.railpardazadmin.activity.ActivityReportDetail;
import ir.androjavan.railpardazadmin.bundle.BundleReport;

/**
 * Created by mohammad on 5/5/2016.
 */
public class AdapterErrorRecycler extends RecyclerView.Adapter<AdapterErrorRecycler.ErrorViewHolder> {

    private List<BundleReport> errorList;
    private Context contextMain;
    Typeface tfSans;

    public AdapterErrorRecycler(List<BundleReport> errorList, Context context) {
        this.errorList = errorList;
        this.contextMain = context;
        tfSans = Typeface.createFromAsset(contextMain.getAssets(), "IRAN Sans.ttf");
    }
    public AdapterErrorRecycler(Context context) {
        errorList = new ArrayList<>();
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

    public void insertItem(int count ,BundleReport object) {

        errorList.add(object);
        notifyDataSetChanged();

    }


    @Override
    public void onBindViewHolder(ErrorViewHolder errorViewHolder, final int position) {

        BundleReport errorInfo = errorList.get(position);
        errorViewHolder.bundleReport = errorInfo;
        errorViewHolder.errorVagon.setText("شماره واگن : " + errorInfo.wagonNum);
        errorViewHolder.errorUsername.setText("نام کاربر : " + errorInfo.fullName);
        errorViewHolder.tripInfo.setText("اطلاعات سفر :"+ errorInfo.tripInfo);
        errorViewHolder.errorVagon.setTypeface(tfSans);
        errorViewHolder.errorUsername.setTypeface(tfSans);
        errorViewHolder.tripInfo.setTypeface(tfSans);

    }

    @Override
    public ErrorViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_report_item, viewGroup, false);
        ErrorViewHolder error = new ErrorViewHolder(itemView);
        return error;
    }

    public class ErrorViewHolder extends RecyclerView.ViewHolder {

        protected TextView errorVagon;
        protected TextView errorUsername;
        protected TextView tripInfo;
        protected BundleReport bundleReport;

        public ErrorViewHolder(View v) {
            super(v);

            contextMain = v.getContext();
            errorVagon =  (TextView) v.findViewById(R.id.txtVagon);
            errorUsername = (TextView)  v.findViewById(R.id.txtUsername);
            tripInfo = (TextView) v.findViewById(R.id.txtTrip);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(contextMain,ActivityReportDetail.class);
                    intent.putExtra("user",bundleReport.fullName);
                    intent.putExtra("wagon",bundleReport.wagonNum);
                    intent.putExtra("error",bundleReport.errorName);
                    intent.putExtra("comment",bundleReport.comment);
                    contextMain.startActivity(intent);
                }
            });

        }

    }

}
