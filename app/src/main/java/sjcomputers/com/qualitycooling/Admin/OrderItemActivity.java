package sjcomputers.com.qualitycooling.Admin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import sjcomputers.com.qualitycooling.DocumentActivity;
import sjcomputers.com.qualitycooling.Global.APIManager;
import sjcomputers.com.qualitycooling.Global.APIManagerCallback;
import sjcomputers.com.qualitycooling.Global.UserData;
import sjcomputers.com.qualitycooling.Global.Util;
import sjcomputers.com.qualitycooling.R;
import sjcomputers.com.qualitycooling.Util.ImageViewActivity;

import static sjcomputers.com.qualitycooling.Global.Util.BitmapToString;
import static sjcomputers.com.qualitycooling.Global.Util.MSG_ORDER_ITEMS_MARK_CHANGED;
import static sjcomputers.com.qualitycooling.Global.Util.MSG_ORDER_ITEMS_MARK_COMPLETED;
import static sjcomputers.com.qualitycooling.Global.Util.STATUS_UPDATE_INTERVAL;

/**
 * Created by RabbitJang on 5/26/2018.
 */

public class OrderItemActivity extends AppCompatActivity {
    public static Handler handler;
    OrderItemAdapter orderDetailListAdapter;
    HashMap<String, Object> orderObj;
    ArrayList<HashMap<String, Object>> statusArray;
    String[] filters;
    String[] statuses;
    String customer;
    String jobSite;
    String status;
    String title;
    public static int orderID;
    int index = 0;
    int selectedStatusID;
    int readyForPickupStatusID;
    int readyForDeliveryStatusID;

    private ConstraintLayout portraitCl;

    ListView listView;
    private Spinner filterSpinner;
    private Spinner statusSpinner;
    private TextView customerTv;
    private TextView jobSiteTv;
    private Button signatureBt;

    private Spinner filterSpinnerLand;
    private Spinner statusSpinnerLand;
    private TextView customerTvLand;
    private TextView jobSiteTvLand;
    private Button signatureBtLand;

    Dialog signatureDialog;
    SignaturePad mSignaturePad;
    Button mSaveButton;
    Button mClearButton;
    ImageView signatureIv;
    public static TextView countTv;
    public static TextView visibleCountTv, tvActualCount;

    private ConstraintLayout landscapeCl;

    String signatureImgUrl;
    EditText receiverNameEt;

    private Timer timer;
    public static boolean isApiCalling;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        initValue();
        getOrderDetailValuesFromOrderActivity();
        configureDesign();

        getOrderDetail(true);
        setStatus();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MSG_ORDER_ITEMS_MARK_COMPLETED) {
                    showStatusOptionDialog();
                } else if (msg.what == MSG_ORDER_ITEMS_MARK_CHANGED) {
                    updateOnlyOrderItems();
                }

            }
        };

        startTimer();
    }

    private void startTimer() {
        //-----Start timer
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getUpdateStatus();
            }
        }, 0, STATUS_UPDATE_INTERVAL);
    }

    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void getUpdateStatus() {
        APIManager apiManager = new APIManager();
        apiManager.setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                if (objAPIResult != null) {
                    try {
                        String statusCode = objAPIResult.getString("StatusCode");
                        if (statusCode.equals("1")) {
                            getOrderDetail(true);
                        }
                    } catch (Exception e) {
                        Util.showToast("Failed and try again", OrderItemActivity.this);
                    }
                } else {
                    Util.showToast("Failed and try again", OrderItemActivity.this);
                }
            }
        });
        apiManager.checkOrderLastAction(orderID);
    }

    private void getOrderDetailValuesFromOrderActivity() {
        Bundle b = getIntent().getExtras();
        customer = b.getString("Customer");
        jobSite = b.getString("Jobsite");
        status = b.getString("Status");
        title = b.getString("Title");
        orderID = b.getInt("OrderID");
    }

    private void initValue() {
        signatureImgUrl = "";
        isApiCalling = false;
    }

    private void configureDesign() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.colorAccent)));
        //getSupportActionBar().setTitle("Order #: " + title);

        landscapeCl = findViewById(R.id.landscape_cl);
        portraitCl = findViewById(R.id.portrait_cl);
        Button completeBt = findViewById(R.id.button10);
        Button deliveredBt = findViewById(R.id.button11);
        Button loadedBt = findViewById(R.id.button12);
        signatureBt = findViewById(R.id.button5);
        signatureBtLand = findViewById(R.id.button5_land);
        signatureIv = findViewById(R.id.imageView);
        tvActualCount = findViewById(R.id.tvActualCount);

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            portraitCl.setVisibility(View.GONE);
            landscapeCl.setVisibility(View.VISIBLE);
            signatureBt.setVisibility(View.GONE);
            signatureBtLand.setVisibility(View.VISIBLE);
        } else {
            portraitCl.setVisibility(View.VISIBLE);
            landscapeCl.setVisibility(View.GONE);
            signatureBt.setVisibility(View.VISIBLE);
            signatureBtLand.setVisibility(View.GONE);
        }

        countTv = findViewById(R.id.textView11);
        visibleCountTv = findViewById(R.id.textView15);

        completeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markItemComplete();
            }
        });

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

        listView = findViewById(R.id.item_order_list_lv);

        customerTv = (TextView) findViewById(R.id.textView12);
        jobSiteTv = (TextView) findViewById(R.id.textView13);
        filterSpinner = findViewById(R.id.spinner4);
        statusSpinner = (Spinner) findViewById(R.id.spinner);

        customerTvLand = (TextView) findViewById(R.id.textView12_land);
        jobSiteTvLand = (TextView) findViewById(R.id.textView13_land);
        filterSpinnerLand = findViewById(R.id.spinner4_land);
        statusSpinnerLand = (Spinner) findViewById(R.id.spinner_land);


        if (jobSite != null && !jobSite.isEmpty()) {
            jobSiteTv.setText("Job Site: " + jobSite);
            jobSiteTvLand.setText("Job Site: " + jobSite);
        } else {
            jobSiteTv.setText("");
            jobSiteTvLand.setText("");
        }

        customerTv.setText(String.format("Customer Name: %s ", customer));
        customerTvLand.setText(String.format("Customer Name: %s ", customer));

        Button updateBt = (Button) findViewById(R.id.button);
        updateBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateOrderItems();
            }
        });

        Button seeDocumentBt = (Button) findViewById(R.id.see_doc_bt);
        seeDocumentBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderItemActivity.this, DocumentActivity.class);
                DocumentActivity.orderId = orderID;
                DocumentActivity.documentType = 0;
                startActivity(intent);
            }
        });

        signatureBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignatureDialog();
            }
        });

        signatureBtLand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSignatureDialog();
            }
        });

        signatureIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderItemActivity.this, ImageViewActivity.class);
                ImageViewActivity.imageUrl = signatureImgUrl;
                startActivity(intent);
            }
        });
    }

    private void configureFilterSpinner() {
        filters = new String[3];
        filters[0] = "All";
        filters[1] = "Complete";
        filters[2] = "Not Completed";

        ArrayAdapter<String> filterAdapter = new ArrayAdapter<String>(OrderItemActivity.this, R.layout.item_spinner, filters);
        filterSpinner.setAdapter(filterAdapter);
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                orderDetailListAdapter.showItemsWithStatus(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        filterSpinnerLand.setAdapter(filterAdapter);
        filterSpinnerLand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                orderDetailListAdapter.showItemsWithStatus(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showStatusOptionDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Please change order status.");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Ready for Pickup",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        selectedStatusID = readyForPickupStatusID;
                        updateOrderStatus();
                    }
                });

        builder1.setNegativeButton(
                "Ready for Delivery",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        selectedStatusID = readyForDeliveryStatusID;
                        updateOrderStatus();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void markItemComplete() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Confirm");
        builder1.setMessage("Did you complete all the items?");
        builder1.setCancelable(true);

        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                APIManager apiManager = new APIManager();
                apiManager.setCallback(new APIManagerCallback() {
                    @Override
                    public void APICallback(JSONObject objAPIResult) {
                        isApiCalling = false;
                        Util.hideProgressDialog();
                        if (objAPIResult != null) {
                            try {
                                orderDetailListAdapter.getOrderItems(filterSpinner.getSelectedItemPosition());

                                //finishAndUpdateMainOrders();
                            } catch (Exception e) {
                                Util.showToast("Failed and try again", OrderItemActivity.this);
                            }
                        } else {
                            Util.showToast("Failed and try again", OrderItemActivity.this);
                        }

                    }
                });

                Util.showProgressDialog("Updating..", OrderItemActivity.this);
                apiManager.markItemComplete(orderID);
                isApiCalling = true;
            }
        });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void markItemDelivered() {
        APIManager apiManager = new APIManager();
        apiManager.setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                isApiCalling = false;
                Util.hideProgressDialog();
                if (objAPIResult != null) {
                    try {
                        //finishAndUpdateMainOrders();
                        orderDetailListAdapter.getOrderItems(filterSpinner.getSelectedItemPosition());
                    } catch (Exception e) {
                        Util.showToast("Failed and try again", OrderItemActivity.this);
                    }
                } else {
                    Util.showToast("Failed and try again", OrderItemActivity.this);
                }

            }
        });

        Util.showProgressDialog("Updating..", OrderItemActivity.this);
        apiManager.markItemDelivered(orderID);
        isApiCalling = true;

    }

    private void markItemLoadedInTruck() {
        APIManager apiManager = new APIManager();
        apiManager.setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                isApiCalling = false;
                Util.hideProgressDialog();
                if (objAPIResult != null) {
                    try {
                        //finishAndUpdateMainOrders();
                        orderDetailListAdapter.getOrderItems(filterSpinner.getSelectedItemPosition());
                    } catch (Exception e) {
                        Util.showToast("Failed and try again", OrderItemActivity.this);
                    }
                } else {
                    Util.showToast("Failed and try again", OrderItemActivity.this);
                }

            }
        });

        Util.showProgressDialog("Updating..", OrderItemActivity.this);
        apiManager.markItemLoadedInTruck(orderID);
        isApiCalling = true;
    }

    private void showSignatureDialog() {
        signatureDialog = new Dialog(this);
        signatureDialog.requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        signatureDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        signatureDialog.setContentView(R.layout.dialog_signature);
        signatureDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        signatureDialog.getWindow().setGravity(Gravity.CENTER);
        signatureDialog.show();
        setSignature();

        Button closeBt = (Button) signatureDialog.findViewById(R.id.closeBtn);
        closeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signatureDialog.hide();
            }
        });
    }

    private void setSignature() {
        mSignaturePad = signatureDialog.findViewById(R.id.signature_pad);
        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {

            }

            @Override
            public void onSigned() {
                mSaveButton.setEnabled(true);
                mClearButton.setEnabled(true);
            }

            @Override
            public void onClear() {
                mSaveButton.setEnabled(false);
                mClearButton.setEnabled(false);
            }
        });

        mSaveButton = (Button) signatureDialog.findViewById(R.id.saveSignature);
        mClearButton = (Button) signatureDialog.findViewById(R.id.clearSignature);
        receiverNameEt = signatureDialog.findViewById(R.id.editText11);

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String receiverName = receiverNameEt.getText().toString();
                if (receiverName.equals("")) {
                    Util.showToast("Please input name.", OrderItemActivity.this);
                    return;
                }

                Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
                confirmSignature(signatureBitmap, receiverName);
            }
        });

        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSignaturePad.clear();
            }
        });
    }

    private void getOrderDetail(final boolean shouldReadStatus) {
        if (isApiCalling) {
            return;
        }

        APIManager apiManager = new APIManager();
        apiManager.setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                isApiCalling = false;
                Util.hideProgressDialog();
                if (objAPIResult != null) {
                    try {
                        JSONObject orderJSONObj = objAPIResult.getJSONObject("Order");
                        orderObj = Util.toMap(orderJSONObj);

                        status = orderJSONObj.getString("Status").trim();
                        setTitle("Order #: " + orderJSONObj.getString("INNumber"));
                        customerTv.setText("Customer: " + orderJSONObj.getString("CustomerName"));
                        setStatus();

                        if (orderObj.get("DriverSignature") != JSONObject.NULL) {
                            signatureImgUrl = (String) orderObj.get("DriverSignature");
                            ImageLoader.getInstance().displayImage(signatureImgUrl, signatureIv, Util.optionsImg);
                        }

                        orderDetailListAdapter = new OrderItemAdapter(OrderItemActivity.this);
                        listView.setAdapter(orderDetailListAdapter);
                        configureFilterSpinner();
                    } catch (Exception e) {
                        Util.showToast("Failed and try again", OrderItemActivity.this);
                    }
                } else {
                    Util.showToast("Failed and try again", OrderItemActivity.this);
                }

            }
        });

        Util.showProgressDialog("Getting order detail..", OrderItemActivity.this);
        apiManager.getOrderDetail(orderID);
        isApiCalling = true;
    }

    private void confirmSignature(Bitmap signatureBitmap, String receiverName) {
        String signatureString = BitmapToString(signatureBitmap);

        APIManager apiManager = new APIManager();
        apiManager.setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                isApiCalling = false;
                Util.hideProgressDialog();
                if (objAPIResult != null) {
                    try {
                        String message = objAPIResult.getString("Message");
                        if (objAPIResult.getInt("Status") == 1) {
                            signatureDialog.hide();
                            Util.showToast(message, OrderItemActivity.this);
                            getOrderDetail(false);
                        } else {
                            Util.showToast(message, OrderItemActivity.this);
                        }
                    } catch (Exception e) {
                        Util.showToast("Failed and try again", OrderItemActivity.this);
                    }
                } else {
                    Util.showToast("Failed and try again", OrderItemActivity.this);
                }

            }
        });

        Util.showProgressDialog("Updating signature..", OrderItemActivity.this);
        apiManager.saveSignature(orderID, signatureString, receiverName);
        isApiCalling = true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                cancelTimer();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        cancelTimer();
    }

    private void setStatus() {
        statusArray = new ArrayList<>();
        statusArray = UserData.getInstance().statusArray;
        statuses = new String[statusArray.size()];
        for (int i = 0; i < statusArray.size(); i++) {
            statuses[i] = statusArray.get(i).get("Status").toString();
            if (statuses[i].equals("Ready for Delivery")) {
                readyForDeliveryStatusID = Integer.parseInt(statusArray.get(i).get("ID").toString());
            }
            if (statuses[i].equals("Ready for Pickup")) {
                readyForPickupStatusID = Integer.parseInt(statusArray.get(i).get("ID").toString());
            }
        }

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<String>(OrderItemActivity.this, R.layout.item_spinner, statuses);
        statusSpinner.setAdapter(statusAdapter);
        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                index = statusSpinner.getSelectedItemPosition();
                selectedStatusID = Integer.parseInt(statusArray.get(index).get("ID").toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        statusSpinnerLand.setAdapter(statusAdapter);
        statusSpinnerLand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                index = statusSpinner.getSelectedItemPosition();
                selectedStatusID = Integer.parseInt(statusArray.get(index).get("ID").toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //will be changed in the future
        for (int i = 0; i < statuses.length; i++) {
            String temp = statuses[i];
            if (temp.equals(status)) {
                statusSpinner.setSelection(i);
                statusSpinnerLand.setSelection(i);
                break;
            }
        }
    }

    private void updateOrderStatus() {
        Util.showProgressDialog("Updating status..", OrderItemActivity.this);

        APIManager apiManager = new APIManager();
        apiManager.setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                isApiCalling = false;
                Util.hideProgressDialog();
                if (objAPIResult != null) {
                    try {
                        //if(objAPIResult.getString("StatusCode").equals("Success")) {\
                        updateMainOrders();
                        /*}
                        else{
                            Util.showToast("Failed and try again", OrderItemActivity.this);
                        }*/
                    } catch (Exception e) {
                        Util.showToast("Failed and try again", OrderItemActivity.this);
                    }
                } else {
                    Util.showToast("Failed and try again", OrderItemActivity.this);
                }
            }
        });
        apiManager.changeOrderStatus(orderID, selectedStatusID);
        isApiCalling = true;
    }

    private void updateMainOrders() {
        Util.showToast("Status is updated successfully.", OrderItemActivity.this);
        /*JobActivity.searchInEt.setText("");
        if (!JobActivity.isLive) {
            JobActivity.orderAdapter.getOrders();
        }*/
    }

    private void updateOrderItems() {
        Util.showProgressDialog("Updating order items..", OrderItemActivity.this);

        APIManager apiManager = new APIManager();
        apiManager.setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                isApiCalling = false;
                Util.hideProgressDialog();
                if (objAPIResult != null) {
                    try {
                        //if(objAPIResult.getString("StatusCode").equals("Success")) {
                        updateOrderStatus();
                        //}
                    } catch (Exception e) {
                        Util.showToast("Failed and try again", OrderItemActivity.this);
                    }
                } else {
                    Util.showToast("Failed and try again", OrderItemActivity.this);
                }
            }
        });

        JSONArray completedItemJSONArr = new JSONArray();
        try {
            for (int i = 0; i < OrderItemAdapter.itemOrderJSONArr.length(); i++) {
                JSONObject itemJSONObj = OrderItemAdapter.itemOrderJSONArr.getJSONObject(i);
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
        apiManager.updateOrderItems(orderID, object);
        isApiCalling = true;
    }

    private void updateOnlyOrderItems() {
        APIManager apiManager = new APIManager();
        apiManager.setCallback(null);
        JSONArray completedItemJSONArr = new JSONArray();
        try {
            for (int i = 0; i < OrderItemAdapter.itemOrderJSONArr.length(); i++) {
                JSONObject itemJSONObj = OrderItemAdapter.itemOrderJSONArr.getJSONObject(i);
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
        apiManager.updateOrderItems(orderID, object);
    }
}
