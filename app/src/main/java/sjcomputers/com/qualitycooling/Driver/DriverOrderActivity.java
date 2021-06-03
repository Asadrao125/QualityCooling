package sjcomputers.com.qualitycooling.Driver;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import sjcomputers.com.qualitycooling.Adapters.KnockedTogetherAdapter;
import sjcomputers.com.qualitycooling.Global.APIManager;
import sjcomputers.com.qualitycooling.Global.APIManagerCallback;
import sjcomputers.com.qualitycooling.Global.UserData;
import sjcomputers.com.qualitycooling.Global.Util;
import sjcomputers.com.qualitycooling.KnockedTogetherActivity;
import sjcomputers.com.qualitycooling.R;
import sjcomputers.com.qualitycooling.models.KnockedTogetherModel;

import static sjcomputers.com.qualitycooling.Global.Util.LOCATION_UPDATE_INTERVAL;
import static sjcomputers.com.qualitycooling.Global.Util.MSG_REFRESH_ORDER;

public class DriverOrderActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    public static EditText driverSearchInEt;
    public static EditText driverSearchCusEt;
    public static int orderId;
    public static int status;

    public static Button prevBt;
    public static Button nextBt;
    public static Spinner pageSpinner;
    private Timer timer;
    public static Spinner spinner5;
    ArrayList<String> vehicleNameList = new ArrayList<>();
    public static ArrayList<String> vehicleIdList = new ArrayList<>();
    public String DriverVehicleId;

    String[] statuses = {"Assigned Deliveries", "Open Deliveries", "Open Orders", "Completed"};
    private DriverOrderAdapter driverOrderAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        initValue();
        configureDesign();
        startTimer();
        getVehicles();

    }

    @Override
    protected void onResume() {
        super.onResume();
        orderId = 0;
    }

    private void initValue() {
        status = 2;
        orderId = 0;
    }

    private void configureDesign() {
        prevBt = findViewById(R.id.prev_bt);
        nextBt = findViewById(R.id.next_bt);
        pageSpinner = findViewById(R.id.spinner3);
        spinner5 = findViewById(R.id.spinner5);

        ListView driverOrderLv = (ListView) findViewById(R.id.driver_order_lv);
        driverSearchInEt = (EditText) findViewById(R.id.search_driver_IN_txt);
        driverSearchCusEt = (EditText) findViewById(R.id.search_driver_cus_txt);
        driverOrderAdapter = new DriverOrderAdapter(this);
        driverOrderLv.setAdapter(driverOrderAdapter);

        Button searchBt = findViewById(R.id.button6);
        searchBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DriverOrderAdapter.handler.sendEmptyMessage(MSG_REFRESH_ORDER);
            }
        });

        Spinner statusSpinner = findViewById(R.id.spinner2);
        final ArrayAdapter<String> statusAdapter = new ArrayAdapter<String>(DriverOrderActivity.this, R.layout.item_spinner, statuses);
        statusSpinner.setAdapter(statusAdapter);
        statusSpinner.setSelection(1);
        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (status != (position + 1)) {
                    status = position + 1;
                    DriverOrderAdapter.handler.sendEmptyMessage(MSG_REFRESH_ORDER);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateDriverLocation();
            }
        }, 0, LOCATION_UPDATE_INTERVAL);
    }

    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        cancelTimer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return true;
    }

    /*private void setTruckSpinner() {
        final ArrayAdapter<String> statusAdapter = new ArrayAdapter<String>(DriverOrderActivity.this, R.layout.item_spinner, statuses);
        DriverOrderActivity.spinner5.setAdapter(statusAdapter);
        DriverOrderActivity.spinner5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(status != (position + 1)) {
                    status = position + 1;
                    DriverOrderAdapter.handler.sendEmptyMessage(MSG_REFRESH_ORDER);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                driverOrderAdapter.refreshOrders();
                return true;
            case R.id.logout:
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                sharedPreferences.edit().putString("UserName", "").apply();
                sharedPreferences.edit().putString("Password", "").apply();
                finish();
                cancelTimer();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateDriverLocation() {
        APIManager apiManager = new APIManager();
        apiManager.setCallback(null);
        apiManager.sendDriverLocation(orderId, UserData.getInstance().userId, UserData.getInstance().lat, UserData.getInstance().lng);
    }

    /*class GetLastLocation extends TimerTask {
        APIManager apiManager = new APIManager();
        @Override
        public void run() {
            if(currentOrderDelivered) {
                return;
            }
            Log.d("lat value: ",  String.valueOf(lat));
            Log.d("lng value: ", String.valueOf(lng));
            int selectedItemIndex = DriverOrderAdapter.selectedItemIndex;
            if(selectedItemIndex != -1) {
                HashMap<String, Object> selectedOrderObj = DriverOrderAdapter.driverOrderArry.get(selectedItemIndex);
                apiManager.sendDriverLocation(0, (Integer) selectedOrderObj.get("OrderID"), UserData.getInstance().userId, lat, lng);
            }
        }
    }*/

    public void getVehicles() {
        APIManager apiManager = new APIManager();
        apiManager.setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                if (objAPIResult != null) {
                    try {
                        if (objAPIResult.getString("Status").equals("Success")) {
                            DriverVehicleId = objAPIResult.getString("DriverVehicleId");
                            Log.d("ldmldkbjbd", "APICallback: " + objAPIResult);
                            JSONObject jsonObject = new JSONObject(objAPIResult.toString());
                            JSONArray jsonArray = jsonObject.getJSONArray("VehicleList");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                String vehicleName = jsonArray.getJSONObject(i).getString("VehicleName");
                                String vehicleId = jsonArray.getJSONObject(i).getString("VehicleId");
                                vehicleNameList.add(vehicleName);
                                vehicleIdList.add(vehicleId);
                            }

                            final ArrayAdapter<String> statusAdapter2 = new ArrayAdapter<String>(DriverOrderActivity.this, R.layout.item_spinner, vehicleNameList);
                            spinner5.setAdapter(statusAdapter2);

                            for (int i = 0; i < vehicleIdList.size(); i++) {
                                if (DriverVehicleId.equals(vehicleIdList.get(i))) {
                                    String default_vehicle = vehicleNameList.get(i);
                                    //Toast.makeText(DriverOrderActivity.this, "" + default_vehicle, Toast.LENGTH_SHORT).show();
                                    spinner5.setSelection(i);
                                }
                            }
                        }

                    } catch (Exception e) {
                        Util.showToast("Failed and try again", DriverOrderActivity.this);
                    }
                } else {
                    Util.showToast("Failed and try again", DriverOrderActivity.this);
                }
            }
        });
        apiManager.getVehicleList();
    }
}
