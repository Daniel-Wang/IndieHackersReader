package ca.danielw.readerindiehackers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ScrollView;

import com.eyalbira.loadingdots.LoadingDots;

public class DetailActivity extends AppCompatActivity {
    private String URL;
    private WebView webView;
    private LoadingDots loadingDots;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadingDots = (LoadingDots) findViewById(R.id.loading_bar);
        scrollView = (ScrollView) findViewById(R.id.wrapper);

        scrollView.setVisibility(View.GONE);

        Intent intent = getIntent();
        URL = intent.getStringExtra(MainActivity.DETAIL_URL);

        webView = (WebView) findViewById(R.id.detailWebView);
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageFinished(WebView view, String url) {
//                Log.e("hmmm", "hi");
                webView.getSettings().setJavaScriptEnabled(true);
                webView.loadUrl("javascript:(function() { " +
                        "document.getElementsByClassName('title-bar__content')[0].style.display='none'; " +
                        "document.getElementsByClassName('ember-view interview-footer')[0].style.display='none'; " +
                        "})()");

            }
        });
        webView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });
        webView.loadUrl(URL);

        loadingDots.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
