package ca.winnipegtrails.winnipegtrails;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

public class WebViewActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Intent intent = getIntent();
        String web = intent.getStringExtra("web");
        String url = intent.getStringExtra("url");

        if(getActionBar() != null) {

            getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getActionBar().setCustomView(R.layout.actionbar);
            getActionBar().setDisplayHomeAsUpEnabled(true);

            TextView title = (TextView) findViewById(R.id.title);
            title.setText(web);
        }

        WebView webView = (WebView) findViewById(R.id.web);
        webView.loadUrl(url);
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}