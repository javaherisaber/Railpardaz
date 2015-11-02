package ir.androjavan.railpardazadmin.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import ir.androjavan.railpardazadmin.R;
import ir.androjavan.railpardazadmin.adapter.AdapterErrorRecycler;
import ir.androjavan.railpardazadmin.bundle.BundleReport;
import ir.androjavan.railpardazadmin.database.ReportDB;

public class ActivityReport extends AppCompatActivity {


    private List<BundleReport> reportList;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView tabRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initReportRecycler();
    }

    private void initReportRecycler(){
        reportList = new ArrayList<>();

        ReportDB db = new ReportDB(getApplicationContext());

        db.open();
        Cursor cursor = db.GetAllRecords(ReportDB.TABLE_REPORT,ReportDB.COLUMNS_REPORT);
        cursor.moveToFirst();
        if(cursor.getCount() != 0)
        {
            do
            {
                BundleReport bundleReport = new BundleReport();
                bundleReport.fullName = cursor.getString(1);
                bundleReport.tripInfo = cursor.getString(2);
                bundleReport.errorName = cursor.getString(3);
                bundleReport.wagonNum = cursor.getString(4);
                bundleReport.comment = cursor.getString(5);
                reportList.add(bundleReport);

            } while (cursor.moveToNext());
        }

        tabRecycler = (RecyclerView) findViewById(R.id.reportRecycler);


        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        tabRecycler.setHasFixedSize(true);

        tabRecycler.setLayoutManager(linearLayoutManager);
        AdapterErrorRecycler productListAdapter = new AdapterErrorRecycler(reportList, getApplicationContext());
        tabRecycler.setAdapter(productListAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_report, menu);
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
}
