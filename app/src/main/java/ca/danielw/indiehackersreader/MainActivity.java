package ca.danielw.indiehackersreader;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.stetho.Stetho;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity  implements AdapterView.OnItemSelectedListener{
    public static final String DETAIL_URL = "DETAIL_URL";

    private WebView webView;
    private final String BASE_URL = "https://www.indiehackers.com/businesses";
    private final String MARKDOWN_SUFFIX = ".md";
    private Spinner revenueFilter;
    private Spinner catFilter;

    private String rev;
    private String cat;

    private ArrayAdapter<CharSequence> adpRev;
    ArrayAdapter<CharSequence> adpCat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Stetho.initializeWithDefaults(this);

        revenueFilter = (Spinner) findViewById(R.id.revenue_spinner);
        catFilter = (Spinner) findViewById(R.id.categories_spinner);


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_layout);


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

        revenueFilter.setSelection(0);
        catFilter.setSelection(0);

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        webView.setWebViewClient(new MyWebViewClient());

        webView.loadUrl(BASE_URL);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        Log.e("Error", text);
        if(parent.equals(adpRev)){
            rev = text;
        } else {
            cat = text;
        }
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class MyWebViewClient extends WebViewClient{

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.e("LOL", "HIIIIII");
            if(url.equals(BASE_URL)) {
                view.loadUrl(url);
                return false;
            }
            return true;
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Log.e("LOL", "HIIIIII");
            String url = request.getUrl().toString();
            if(url.equals(BASE_URL)){
                view.loadUrl(url);
                return false;
            }
            return true;
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            Log.e("Hello", url);

            if(url.endsWith(MARKDOWN_SUFFIX)){
                Log.e("Hello", url);
                //Render clicks in another webview in the Detail Acitivity
                URL mUrl = null;
                try {
                    mUrl = new URL(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                if(mUrl != null) {
                    //Parse the .md url to the actual url
                    String[] parts =  mUrl.getPath().split("/");
                    String realUrl = BASE_URL + "/" + parts[3].substring(0, parts[3].length() - 3);
                    Log.e("Test", realUrl);
                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                    intent.putExtra(DETAIL_URL, realUrl);
                    startActivity(intent);
                }
            }
        }
    }
}
