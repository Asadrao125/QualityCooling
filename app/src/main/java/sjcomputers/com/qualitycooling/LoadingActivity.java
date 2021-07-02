package sjcomputers.com.qualitycooling;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.entity.mime.Header;
import sjcomputers.com.qualitycooling.Adapters.LoadingAdapter;
import sjcomputers.com.qualitycooling.Adapters.LoadingModel;
import sjcomputers.com.qualitycooling.Adapters.LoadingModel2;
import sjcomputers.com.qualitycooling.Admin.OrderItemActivity;
import sjcomputers.com.qualitycooling.Global.APIManager;
import sjcomputers.com.qualitycooling.Global.APIManagerCallback;
import sjcomputers.com.qualitycooling.Global.UserData;
import sjcomputers.com.qualitycooling.Global.Util;

import static sjcomputers.com.qualitycooling.Global.Util.MSG_SERIAL_SCANNED;

public class LoadingActivity extends AppCompatActivity {
    sjcomputers.com.qualitycooling.Adapters.LoadingAdapter adapter;
    Dialog inputDialog;
    public static Handler handler;
    ListView loadLv;
    public static String inputVal;
    ArrayList<LoadingModel2> loadingModelArrayList = new ArrayList<>();
    String Loaded, inNumber, ItemName, JobSite, JobSiteAddress, OrderItemId;
    String PieceNo, ShowNotificationPopup, ShowPopup, Button1Text, Button2Text;
    String OrderId, Customer;
    EditText edtManualInput;
    Handler handler2 = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        setupView();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MSG_SERIAL_SCANNED) {
                    String scanResult = (String) msg.obj;
                    loadValue(scanResult);
                    inputVal = scanResult;
                }
            }
        };

        edtManualInput = findViewById(R.id.edtManualInput);
        showSoftKeyboard(edtManualInput);
        /*edtManualInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    String input = edtManualInput.getText().toString().trim();
                    if (!TextUtils.isEmpty(input)) {
                        loadValue(input);
                        inputVal = input;
                        edtManualInput.setText("");
                    } else {
                        Toast.makeText(LoadingActivity.this, "Please enter input value", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            }
        });*/
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
                            loadValue(serial);
                            inputVal = serial;
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

    private void loadValue(String value) {
        Util.showProgressDialog("Loading..", LoadingActivity.this);
        handler2.removeMessages(0);
        showSoftKeyboard(edtManualInput);
        edtManualInput.requestFocus();
        APIManager apiManager = new APIManager();
        apiManager.setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                Util.hideProgressDialog();
                if (objAPIResult != null) {
                    try {
                        if (objAPIResult.getString("Status").equals("Success") || objAPIResult.getString("Status").equals("Scanned")) {
                            JSONObject jsonObject = new JSONObject(objAPIResult.toString());
                            JSONArray jsonArray = jsonObject.getJSONArray("Items");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                Customer = obj.getString("Customer");
                                inNumber = obj.getString("INNumber");
                                ItemName = obj.getString("ItemName");
                                JobSite = obj.getString("JobSite");
                                JobSiteAddress = obj.getString("JobSiteAddress");
                                Loaded = obj.getString("Loaded");
                                OrderItemId = obj.getString("OrderItemId");
                                PieceNo = obj.getString("PieceNo");
                                OrderId = obj.getString("OrderId");

                                Button1Text = obj.getString("Button1Text");
                                Button2Text = obj.getString("Button2Text");
                                ShowNotificationPopup = obj.getString("ShowNotificationPopup");
                                ShowPopup = obj.getString("ShowPopup");


                                loadingModelArrayList.add(new LoadingModel2(Button1Text, Button2Text, Customer, inNumber,
                                        ItemName, JobSite, JobSiteAddress, Loaded, OrderId, OrderItemId, PieceNo, ShowNotificationPopup, ShowPopup));
                            }

                            adapter = new LoadingAdapter(loadingModelArrayList, LoadingActivity.this);
                            loadLv.setAdapter(adapter);

                            if (ShowNotificationPopup.equals("1")) {
                                if (loadingModelArrayList.size() > 1 || loadingModelArrayList.size() > 0) {
                                    loadingModelArrayList.remove(loadingModelArrayList.size() - 1);
                                }
                                Collections.reverse(loadingModelArrayList);
                                showDialog2(OrderId, objAPIResult.getString("Message"), Customer, JobSite);
                            }

                            if (ShowPopup.equals("1")) {
                                showDialog(Button1Text, Button2Text, inNumber, "Notification");
                            }

                            Collections.reverse(loadingModelArrayList);


                        } else {
                            Util.showToast(objAPIResult.getString("Message"), LoadingActivity.this);
                        }
                    } catch (Exception e) {
                        Util.showToast("Failed and try again", LoadingActivity.this);
                    }
                } else {
                    Util.showToast("Failed and try again", LoadingActivity.this);
                }
            }
        });
        apiManager.loading(value);
        showSoftKeyboard(edtManualInput);
        edtManualInput.requestFocus();
    }

    private void setupView() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.colorAccent)));
        getSupportActionBar().setTitle("Loading");

        loadLv = findViewById(R.id.load_lv);
        Button scanBt = findViewById(R.id.scan_bt);
        Button manualBt = findViewById(R.id.manual_bt);

        scanBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoadingActivity.this, QRScannerActivity.class);
                QRScannerActivity.screenType = 2;
                startActivity(intent);
            }
        });

        manualBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputDialog = new Dialog(LoadingActivity.this);
                inputDialog.requestWindowFeature(getWindow().FEATURE_NO_TITLE);
                inputDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                inputDialog.setContentView(R.layout.dialog_input_serial);
                inputDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                inputDialog.getWindow().setGravity(Gravity.CENTER);
                inputDialog.show();

                final EditText serialEt = (EditText) inputDialog.findViewById(R.id.editText5);
                Button closeBt = (Button) inputDialog.findViewById(R.id.button20);
                closeBt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        inputDialog.hide();
                    }
                });

                //Chnages By Asad
                /*serialEt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (charSequence.length() > 5 && charSequence.length() <= 9) {
                            String serial = charSequence.toString();
                            loadValue(serial);
                            inputVal = serial;
                            serialEt.setText("");
                            inputDialog.dismiss();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });*/

                Button okBt = (Button) inputDialog.findViewById(R.id.button8);
                okBt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String serial = serialEt.getText().toString();
                        if (!TextUtils.isEmpty(serial)) {
                            loadValue(serial);
                            inputVal = serial;
                        }
                        inputDialog.hide();
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // For View Job Popup
    private void showDialog2(String orderId, String confirmation, String customer, String jobSite) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(LoadingActivity.this);
        builder.setMessage(confirmation)
                .setCancelable(false)
                .setPositiveButton("View Job", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Intent intent = new Intent(LoadingActivity.this, OrderItemActivity.class);
                        Bundle b = new Bundle();
                        b.putInt("OrderID", Integer.parseInt(orderId));
                        b.putString("Title", orderId);
                        b.putString("Jobsite", jobSite);
                        b.putString("Customer", customer);

                        intent.putExtras(b);
                        startActivity(intent);
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        final AlertDialog alert = builder.create();
        alert.show();
    }

    // For Ready For Pickup / Delivery Popup
    private void showDialog(String button1_text, String button2_text, String inNumber, String confirmation) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(LoadingActivity.this);
        builder.setMessage(confirmation)
                .setCancelable(false)
                .setPositiveButton(button2_text, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String NewString = button2_text.replaceAll(" ", "_");
                        finalHit2(inNumber, NewString);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(button1_text, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String NewString = button1_text.replaceAll(" ", "_");
                        finalHit2(inNumber, NewString);
                        dialog.dismiss();
                    }
                })
                .setNeutralButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void finalHit2(String inNumber, String buttonText) {
        Util.showProgressDialog("Loading..", LoadingActivity.this);
        String newIn = inNumber.trim();
        APIManager apiManager = new APIManager();
        apiManager.setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                Util.hideProgressDialog();
                if (objAPIResult != null) {
                    try {
                        if (objAPIResult.getString("Status").equals("Success")) {
                            JSONObject jsonObject = new JSONObject(objAPIResult.toString());
                            String msg = jsonObject.getString("Message");
                            Toast.makeText(LoadingActivity.this, "" + msg, Toast.LENGTH_SHORT).show();

                        } else {
                            Util.showToast(objAPIResult.getString("Message"), LoadingActivity.this);
                        }
                    } catch (Exception e) {
                        Util.showToast("Failed and try again 1", LoadingActivity.this);
                    }
                } else {
                    Util.showToast("Failed and try again 2", LoadingActivity.this);
                }
            }
        });
        apiManager.showPopup(newIn, buttonText);
    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }
}
