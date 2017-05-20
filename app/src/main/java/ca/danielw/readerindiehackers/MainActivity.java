package ca.danielw.readerindiehackers;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.eyalbira.loadingdots.LoadingDots;
import com.facebook.stetho.Stetho;

public class MainActivity extends AppCompatActivity  implements AdapterView.OnItemSelectedListener{
    public static final String DETAIL_URL = "DETAIL_URL";

    private WebView webView;
    private final String BASE_URL = "https://www.indiehackers.com/businesses";
    private final String ALL_REV = "All Revenue";
    private final String ALL_CAT = "All Categories";

    private final String cat_query = "categories";
    private final String rev_query = "revenue";

    private String CUR_URL = BASE_URL;

    private Spinner revenueFilter;
    private Spinner catFilter;

    private String rev;
    private String cat;

    private LinearLayout linearLayout;
    private LoadingDots loadingDots;

    private ArrayAdapter<CharSequence> adpRev;
    ArrayAdapter<CharSequence> adpCat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Stetho.initializeWithDefaults(this);

        linearLayout = (LinearLayout) findViewById(R.id.main_layout);
        loadingDots = (LoadingDots) findViewById(R.id.loading_bar);

        revenueFilter = (Spinner) findViewById(R.id.revenue_spinner);
        catFilter = (Spinner) findViewById(R.id.categories_spinner);


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_layout);

        linearLayout.setVisibility(View.GONE);
        loadingDots.setVisibility(View.VISIBLE);


        adpRev = ArrayAdapter.createFromResource(this, R.array.revenue_array,
                R.layout.spinner_item);

        adpCat = ArrayAdapter.createFromResource(this, R.array.categories_array,
                R.layout.spinner_item);

        adpRev.setDropDownViewResource(R.layout.spinner_dropdown_item);
        adpCat.setDropDownViewResource(R.layout.spinner_dropdown_item);

        revenueFilter.setOnItemSelectedListener(this);
        catFilter.setOnItemSelectedListener(this);

        revenueFilter.setAdapter(adpRev);
        catFilter.setAdapter(adpCat);

        rev = ALL_REV;
        cat = ALL_CAT;

        revenueFilter.setSelection(0);
        catFilter.setSelection(0);

        webView = (WebView) findViewById(R.id.webView);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new MyWebViewClient());

        if(isNetworkAvailable()){
            webView.loadUrl(BASE_URL);
        } else {
            Snackbar snackbar = Snackbar
                    .make(linearLayout, "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(isNetworkAvailable()){
                                webView.loadUrl(BASE_URL);
                            }
                        }
                    });

            // Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(ContextCompat.getColor(this, R.color.blue));
            snackbar.show();
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        int adpId = parent.getId();
//        Log.e("INFO", cat + " " + rev);
        if(adpId == R.id.revenue_spinner){
            rev = text;
        } else {
            cat = text;
        }
//        Log.e("INFO", cat + " " + rev);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("www.indiehackers.com")
                .appendPath("businesses");

        if(cat.equals(ALL_CAT) && rev.equals(ALL_REV)){

        } else if(rev.equals(ALL_REV)){
            builder.appendQueryParameter(cat_query, cat);
        } else if(cat.equals(ALL_CAT)){
            builder.appendQueryParameter(rev_query, rev);
        } else {
            builder.appendQueryParameter(cat_query, cat)
                    .appendQueryParameter(rev_query, rev);
        }

        String myUrl = builder.build().toString();


        if(!myUrl.equals(CUR_URL)){
            CUR_URL = myUrl;
            webView.loadUrl(myUrl);
//            Log.e("times", "hi many times");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class MyWebViewClient extends WebViewClient{

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            Log.e("LOL", "HIIIIII this is the url: " + url);
            if(url.equals(BASE_URL)) {
                view.loadUrl(url);
                return false;
            } else {
                startDetail(url);
            }
            return true;
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
//            Log.e("LOL", "HIIIIII this is the url: " + url);
            if(url.equals(BASE_URL)){
                view.loadUrl(url);
                return false;
            } else {
                startDetail(url);
            }
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
//            Log.e("page finished", "Counter");
            linearLayout.setVisibility(View.VISIBLE);
            loadingDots.setVisibility(View.GONE);

            webView.getSettings().setJavaScriptEnabled(true);

            webView.loadUrl("javascript:(function() { " +
                    "document.getElementsByClassName('filters__content')[0].style.display='none'; " +
                    "document.getElementsByClassName('ember-view businesses-list businesses-list--grid')[0].style.marginTop='0px'; " +
                    "})()");

            webView.loadUrl("javascript:(function() { " +
                    "document.getElementsByClassName('ember-view businesses-hero')[0].style.display='none'; " +
                    "document.getElementsByClassName('title-bar-sticky-wrapper')[0].style.height='0px';" +
                    "})()");

            webView.loadUrl("javascript:(function() { " +
                    "document.getElementsByClassName('ember-view title-bar')[0].style.display='none'; " +
                    "})()");

            webView.getSettings().setJavaScriptEnabled(false);

        }
    }

    private void startDetail(String url){
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(DETAIL_URL, url);
        startActivity(intent);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
