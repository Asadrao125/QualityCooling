package sjcomputers.com.qualitycooling.Admin;

import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import sjcomputers.com.qualitycooling.Global.Util;
import sjcomputers.com.qualitycooling.R;

import static sjcomputers.com.qualitycooling.Global.Util.MSG_ORDER_SEARCHED;
import static sjcomputers.com.qualitycooling.Global.Util.MSG_REFRESH_ORDER;

public class JobActivity extends AppCompatActivity {

    public static OrderAdapter orderAdapter;
    public static EditText searchInEt;
    public static EditText searchCusEt;
    public static boolean isLive;

    public static Button prevBt;
    public static Button nextBt;
    public static Spinner pageSpinner;
    String[] statuses = {"All", "Open"};
    public static String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initValue();
        configureDesign();
    }

    private void initValue() {
        isLive = true;
        status = "Open";
    }

    private void configureDesign() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.colorAccent)));
        getSupportActionBar().setTitle("Jobs");

        prevBt = findViewById(R.id.prev_bt);
        nextBt = findViewById(R.id.next_bt);

        //-----Calculate order list view height
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int listHeight = (int) (height - Util.convertDpToPixel(144 + 25, this) - actionBarHeight);
        int readCount = (int) (listHeight / Util.convertDpToPixel(37, this));

        Spinner statusSpinner = findViewById(R.id.spinner2);
        final ArrayAdapter<String> statusAdapter = new ArrayAdapter<String>(JobActivity.this, R.layout.item_spinner, statuses);
        statusSpinner.setAdapter(statusAdapter);
        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String newStatus = statuses[position];
                if(!newStatus.equals(status)) {
                    status = statuses[position];
                    OrderAdapter.handler.sendEmptyMessage(MSG_REFRESH_ORDER);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        statusSpinner.setSelection(1);

        final Button searchBt = findViewById(R.id.button6);
        /*CheckBox liveCb = findViewById(R.id.checkBox);
        liveCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    isLive = true;
                    searchBt.setVisibility(View.VISIBLE);
                }
                else {
                    isLive = false;
                    searchBt.setVisibility(View.INVISIBLE);
                }
            }
        });*/

        searchBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderAdapter.handler.sendEmptyMessage(MSG_ORDER_SEARCHED);
            }
        });

        pageSpinner = findViewById(R.id.spinner3);

        final ListView orderLv = (ListView)findViewById(R.id.order_lv);
        searchInEt = (EditText)findViewById(R.id.search_IN_txt);
        searchCusEt = (EditText)findViewById(R.id.search_cus_txt);
        orderAdapter = new OrderAdapter(this, readCount);
        orderLv.setAdapter(orderAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.refresh:
                OrderAdapter.handler.sendEmptyMessage(MSG_REFRESH_ORDER);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
