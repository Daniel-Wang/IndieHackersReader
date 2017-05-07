package ca.danielw.indiehackersreader;

import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.stetho.Stetho;

public class MainActivity extends AppCompatActivity  implements AdapterView.OnItemSelectedListener{

    private final String BASE_URL = "https://www.indiehackers.com/businesses";
    private Spinner revenueFilter;
    private Spinner catFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Stetho.initializeWithDefaults(this);

        revenueFilter = (Spinner) findViewById(R.id.revenue_spinner);
        catFilter = (Spinner) findViewById(R.id.categories_spinner);


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_layout);


        ArrayAdapter<CharSequence> adpRev = ArrayAdapter.createFromResource(this, R.array.revenue_array,
                R.layout.spinner_item);

        ArrayAdapter<CharSequence> adpCat = ArrayAdapter.createFromResource(this, R.array.categories_array,
                R.layout.spinner_item);

        adpRev.setDropDownViewResource(R.layout.spinner_dropdown_item);
        adpCat.setDropDownViewResource(R.layout.spinner_dropdown_item);

        revenueFilter.setAdapter(adpRev);
        catFilter.setAdapter(adpCat);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
