package ir.androjavan.railpardaz.net.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import ir.androjavan.railpardaz.net.R;

public class ActivityErrorDetail extends AppCompatActivity {

    private TextView txtErrorName, txtErrorComment, txtVagonNum, txtTripDate, txtTripName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_detail);

        initObjectsAndViews();

        Intent intent = getIntent();
        String errorName = intent.getStringExtra("errorName");
        String errorComment = intent.getStringExtra("errorComment");
        String tripName = intent.getStringExtra("tripName");
        String tripDate = intent.getStringExtra("tripDate");
        String vagonNum = intent.getStringExtra("vagonNum");


        txtErrorName.setText(errorName);
        txtErrorComment.setText(errorComment);
        txtVagonNum.setText(vagonNum);
        txtTripName.setText(tripName);
        txtTripDate.setText(tripDate);
    }

    private void initObjectsAndViews(){

        txtErrorName = (TextView) findViewById(R.id.txtErrorName);
        txtErrorComment = (TextView) findViewById(R.id.txtComment);
        txtVagonNum = (TextView) findViewById(R.id.txtVagonNum);
        txtTripName = (TextView) findViewById(R.id.txtTripName);
        txtTripDate = (TextView) findViewById(R.id.txtTripDate);

    }

}
