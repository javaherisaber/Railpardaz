package ir.androjavan.railpardaz.net.activity;

import ir.androjavan.railpardaz.net.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class ActivityProb extends Activity {

	ImageView btnBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_prob);
		
		btnBack = (ImageView) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ActivityProb.this.finish();
			}
		});
	}

}
