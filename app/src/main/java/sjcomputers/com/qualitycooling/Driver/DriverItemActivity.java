package sjcomputers.com.qualitycooling.Driver;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
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
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import sjcomputers.com.qualitycooling.Adapters.KnockedTogetherAdapter;
import sjcomputers.com.qualitycooling.Admin.OrderItemAdapter;
import sjcomputers.com.qualitycooling.Global.APIManager;
import sjcomputers.com.qualitycooling.Global.APIManagerCallback;
import sjcomputers.com.qualitycooling.Global.Util;
import sjcomputers.com.qualitycooling.KnockedTogetherActivity;
import sjcomputers.com.qualitycooling.QRScannerActivity;
import sjcomputers.com.qualitycooling.R;
import sjcomputers.com.qualitycooling.models.KnockedTogetherModel;

import static sjcomputers.com.qualitycooling.Global.Util.MSG_DRIVER_ORDER_ITEM_ADDED;
import static sjcomputers.com.qualitycooling.Global.Util.MSG_ORDER_ITEMS_MARK_CHANGED;
import static sjcomputers.com.qualitycooling.Global.Util.MSG_ORDER_ITEMS_MARK_COMPLETED;
import static sjcomputers.com.qualitycooling.Global.Util.MSG_SERIAL_SCANNED;

public class DriverItemActivity extends AppCompatActivity {
    DriverItemAdapter driverItemAdapter;
    public static int orderID;
    Spinner filterSpinner;
    String[] filters;
    public static Handler handler;
    Button btnScan;
    EditText edtManualInput;
    Handler handler2 = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_item);
        configureDesign();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MSG_DRIVER_ORDER_ITEM_ADDED) {
                    driverItemAdapter.getOrderItems(filterSpinner.getSelectedItemPosition());
                }

                if (msg.what == MSG_SERIAL_SCANNED) {
                    String scanResult = (String) msg.obj;
                    manualInputApiCall(scanResult);
                }
            }
        };

        edtManualInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 5 && charSequence.length() <= 9) {
                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String serial = charSequence.toString();
                            manualInputApiCall(serial);
                            edtManualInput.setText("");
                        }
                    }, 2000);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void manualInputApiCall(String scannedValue) {
        Util.showProgressDialog("Loading..", DriverItemActivity.this);
        handler2.removeMessages(0);
        edtManualInput.requestFocus();
        APIManager apiManager = new APIManager();
        apiManager.setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                Util.hideProgressDialog();
                if (objAPIResult != null) {
                    try {
                        Log.d("manual_api_call", "APICallback: " + objAPIResult);
                        JSONObject jsonObject = new JSONObject(objAPIResult.toString());
                        JSONArray jsonArray = jsonObject.getJSONArray("Items");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            Log.d("mnbvcxxz", "APICallback: " + obj);
                        }
                        Toast.makeText(DriverItemActivity.this, "" + objAPIResult.getString("Message"), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(DriverItemActivity.this, DriverItemActivity.class));
                        finish();
                        //saveItems();

                    } catch (Exception e) {
                        Util.showToast("Failed and try again", DriverItemActivity.this);
                    }
                } else {
                    Util.showToast("Failed and try again", DriverItemActivity.this);
                }
            }
        });
        Log.d("value_check", "manualInputApiCall: \n" + orderID + "\n" + scannedValue);
        apiManager.manualInputCall(orderID, scannedValue);
    }

    private void configureDesign() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.colorAccent)));
        getSupportActionBar().setTitle("Items");

        btnScan = findViewById(R.id.btnScan);
        edtManualInput = findViewById(R.id.edtManualInput);

        Button finishBt = findViewById(R.id.button9);
        finishBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveItems();
            }
        });

        filterSpinner = findViewById(R.id.spinner4);
        configureFilterSpinner();

        ListView itemLv = findViewById(R.id.item_lv);
        driverItemAdapter = new DriverItemAdapter(this);
        itemLv.setAdapter(driverItemAdapter);

        Button deliveredBt = findViewById(R.id.button11);
        Button loadedBt = findViewById(R.id.button12);

        deliveredBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markItemDelivered();
            }
        });

        loadedBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markItemLoadedInTruck();
            }
        });

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DriverItemActivity.this, QRScannerActivity.class);
                QRScannerActivity.screenType = 6;
                startActivity(intent);
            }
        });

    }

    private void saveItems() {
        APIManager apiManager = new APIManager();
        apiManager.setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                Util.hideProgressDialog();
                finish();
            }
        });
        JSONArray completedItemJSONArr = new JSONArray();
        try {
            for (int i = 0; i < DriverItemAdapter.itemOrderJSONArr.length(); i++) {
                JSONObject itemJSONObj = DriverItemAdapter.itemOrderJSONArr.getJSONObject(i);
                completedItemJSONArr.put(itemJSONObj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject object = new JSONObject();
        try {
            object.accumulate("Items", completedItemJSONArr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        apiManager.updateDriverOrderItemsDriver(orderID, object);
        Util.showProgressDialog("Updating..", this);
    }

    private void markItemDelivered() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Are you sure you want to delivered all items?");
        builder1.setCancelable(true);

        builder1.setNegativeButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        APIManager.getInstance().setCallback(new APIManagerCallback() {
                            @Override
                            public void APICallback(JSONObject objAPIResult) {
                                Util.hideProgressDialog();
                                if (objAPIResult != null) {
                                    try {
                                        //finishAndUpdateMainOrders();
                                        driverItemAdapter.getOrderItems(filterSpinner.getSelectedItemPosition());
                                    } catch (Exception e) {
                                        Util.showToast("Failed and try again", DriverItemActivity.this);
                                    }
                                } else {
                                    Util.showToast("Failed and try again", DriverItemActivity.this);
                                }

                            }
                        });

                        Util.showProgressDialog("Updating..", DriverItemActivity.this);
                        APIManager.getInstance().markItemDelivered(orderID);
                    }
                });

        builder1.setPositiveButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });


        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void markItemLoadedInTruck() {
        APIManager.getInstance().setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                Util.hideProgressDialog();
                if (objAPIResult != null) {
                    try {
                        //finishAndUpdateMainOrders();
                        driverItemAdapter.getOrderItems(filterSpinner.getSelectedItemPosition());
                    } catch (Exception e) {
                        Util.showToast("Failed and try again", DriverItemActivity.this);
                    }
                } else {
                    Util.showToast("Failed and try again", DriverItemActivity.this);
                }

            }
        });

        Util.showProgressDialog("Updating..", DriverItemActivity.this);
        APIManager.getInstance().markItemLoadedInTruck(orderID);
    }

    private void configureFilterSpinner() {
        filters = new String[3];
        filters[0] = "All";
        filters[1] = "Not Completed";
        filters[2] = "Complete";

        ArrayAdapter<String> filterAdapter = new ArrayAdapter<String>(DriverItemActivity.this, R.layout.item_spinner, filters);
        filterSpinner.setAdapter(filterAdapter);
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                driverItemAdapter.showItemsWithStatus(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_more, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.add_more:
                showAddItemActivity();
                /*HashMap<String, Object> newObj = new HashMap<>();
                newObj.put("ItemName", "");
                newObj.put("ItemId", 0);
                newObj.put("Quantity", 0);
                SignatureActivity.itemArr.add(newObj);*/
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAddItemActivity() {
        Intent intent = new Intent(DriverItemActivity.this, NewItemActivity.class);
        startActivity(intent);
    }
}
