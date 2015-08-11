package eu.veldsoft.brainstonz;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

public class HelpActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);

		((WebView) findViewById(R.id.help_html))
				.loadUrl("file:///android_asset/help.html");
	}
}
