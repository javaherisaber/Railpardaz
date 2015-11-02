package ir.androjavan.railpardazadmin.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import ir.androjavan.railpardazadmin.R;

public class ActivityReportDetail extends AppCompatActivity {

    TextView txtError, txtComment, txtUsername, txtVagon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initViewsAndObject();

    }

    private void initViewsAndObject(){
        txtComment = (TextView) findViewById(R.id.txtComment);
        txtError = (TextView) findViewById(R.id.txtError);
        txtUsername = (TextView) findViewById(R.id.txtUsername);
        txtVagon = (TextView) findViewById(R.id.txtVagon);
        String user = getIntent().getExtras().getString("user");
        String wagon = getIntent().getExtras().getString("wagon");
        String error = getIntent().getExtras().getString("error");
        String comment = getIntent().getExtras().getString("comment");
        txtUsername.setText("کاربر : "+user);
        txtVagon.setText("واگن : "+wagon);
        txtError.setText("نام خطا : "+error);
        txtComment.setText("کامنت : "+comment);
    }

}
