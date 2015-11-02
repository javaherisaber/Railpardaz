package ir.androjavan.railpardazadmin.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ir.androjavan.railpardazadmin.R;
import ir.androjavan.railpardazadmin.database.ReportDB;
import ir.androjavan.railpardazadmin.json.JsonLogin;
import ir.androjavan.railpardazadmin.utile.CheckInternet;

public class ActivityLogin extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private TextView btnRegister;
    private Button btnSignIn;
    private EditText edtUser,edtPass;
    private ReportDB reportDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if(Build.VERSION.SDK_INT >= 11) {
            setFinishOnTouchOutside(false);
        }
        reportDB = new ReportDB(getApplicationContext());
        reportDB.open();
        reportDB.ClearDatabase();

        initViewsAndObject();
    }

    private  void initViewsAndObject(){
        btnRegister = (TextView) findViewById(R.id.btnRegister);

        edtUser = (EditText) findViewById(R.id.edtUser);
        edtPass = (EditText) findViewById(R.id.edtPass);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().build();
//                CustomTabActivityHelper.openCustomTab(ActivityLogin.this, customTabsIntent, Uri.parse("http://www.zheel.i"),
//                        new CustomTabActivityHelper.CustomTabFallback() {
//                            @Override
//                            public void openUri(Activity activity, Uri uri) {
//                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                                startActivity(intent);
//                                overridePendingTransition(0,0);
//                            }
//                        });
            }
        });

        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CheckInternet checkInternet = new CheckInternet(getApplicationContext());
                if(checkInternet.isOnline()){
                    try {
                        new LoginRequest().execute();
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

            }
        });
    }

    private class LoginRequest extends AsyncTask<Void, Void, Integer> {

        String user,pass;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ActivityLogin.this, "ورود","در حال بررسی اطلاعات", true);
            user = edtUser.getText().toString();
            pass = edtPass.getText().toString();
        }

        @Override
        protected  Integer doInBackground(Void... params) {

            JsonLogin jsonLogin = new JsonLogin(getApplicationContext());
            int status = jsonLogin.GetLoginStatus(user,pass);
            return status;
        }

        @Override
        protected void onPostExecute(Integer result) {
            progressDialog.dismiss();

            if(result == -1)
                Toast.makeText(getApplicationContext(),"نام کاربری یا رمز عبور اشتباه است",Toast.LENGTH_SHORT).show();
            else
            {
                Intent intent = new Intent(getApplicationContext(), ActivityReport.class);
                startActivity(intent);
                ActivityLogin.this.finish();
            }

        }
    }

}
