package sjcomputers.com.qualitycooling;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import sjcomputers.com.qualitycooling.Adapters.KnockedTogetherAdapter;
import sjcomputers.com.qualitycooling.Admin.OrderItemActivity;
import sjcomputers.com.qualitycooling.Global.APIManager;
import sjcomputers.com.qualitycooling.Global.APIManagerCallback;
import sjcomputers.com.qualitycooling.Global.Util;
import sjcomputers.com.qualitycooling.Util.SharedPref;
import sjcomputers.com.qualitycooling.models.KnockedTogetherModel;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import static sjcomputers.com.qualitycooling.Global.Util.MSG_SERIAL_SCANNED;

public class KnockedTogetherActivity extends AppCompatActivity {
    Button btnScan, btnManual;
    Dialog inputDialog;
    EditText edtManualInput;
    private SharedPreferences sharedPreferences;
    String apiUrl;
    public static Handler handler;
    ListView lvKnockedTogether;
    public static String inputVal;
    ArrayList<KnockedTogetherModel> knockedTogetherModelArrayList;
    String completed, customer, inNumber, itemName, jobSite, jobSiteAddress, orderItemId;
    String pieceNo, delivered, ShowNotificationPopup, ShowPopup, Button1Text, Button2Text;
    String OrderId;
    Handler handler2 = new Handler();
    public static String lastLoc = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_knocked_together);

        btnScan = findViewById(R.id.scan_bt);
        btnManual = findViewById(R.id.manual_bt);
        lvKnockedTogether = findViewById(R.id.lvKnockedTogether);
        edtManualInput = findViewById(R.id.edtManualInput);
        SharedPref.init(this);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MSG_SERIAL_SCANNED) {
                    String scanResult = (String) msg.obj;
                    checkcheckcheck(scanResult);
                    inputVal = scanResult;
                }
            }
        };

        KnockedTogetherAdapter.inputLoc = "";

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.colorAccent)));
        getSupportActionBar().setTitle("Knocked Together");

        knockedTogetherModelArrayList = new ArrayList<>();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        apiUrl = sharedPreferences.getString("URL", "");

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(KnockedTogetherActivity.this, QRScannerActivity.class);
                QRScannerActivity.screenType = 3;
                startActivity(intent);
            }
        });

        showSoftKeyboard(edtManualInput);
        /* edtManualInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    String input = edtManualInput.getText().toString().trim();
                    if (!TextUtils.isEmpty(input)) {
                        checkcheckcheck(input);
                        inputVal = input;
                        edtManualInput.setText("");
                    } else {
                        Toast.makeText(KnockedTogetherActivity.this, "Please enter input value", Toast.LENGTH_SHORT).show();
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
                            checkcheckcheck(serial);
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

        btnManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputDialog = new Dialog(KnockedTogetherActivity.this);
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
                /*serialEt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (charSequence.length() > 5 && charSequence.length() <= 9) {
                            String serial = charSequence.toString();
                            checkcheckcheck(serial);
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
                            checkcheckcheck(serial);
                            inputVal = serial;
                        }
                        inputDialog.hide();
                    }
                });
            }
        });
    }

    public void checkcheckcheck(String value) {
        Util.showProgressDialog("Loading..", KnockedTogetherActivity.this);
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
                            Log.d("kfkjfkfkfk", "APICallback: " + objAPIResult);
                            JSONObject jsonObject = new JSONObject(objAPIResult.toString());
                            JSONArray jsonArray = jsonObject.getJSONArray("Items");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                completed = obj.getString("Completed");
                                customer = obj.getString("Customer");
                                inNumber = obj.getString("INNumber");
                                itemName = obj.getString("ItemName");
                                jobSite = obj.getString("JobSite");
                                jobSiteAddress = obj.getString("JobSiteAddress");
                                orderItemId = obj.getString("OrderItemId");
                                pieceNo = obj.getString("PieceNo");
                                delivered = obj.getString("Delivered");
                                ShowNotificationPopup = obj.getString("ShowNotificationPopup");
                                ShowPopup = obj.getString("ShowPopup");
                                Button1Text = obj.getString("Button1Text");
                                Button2Text = obj.getString("Button2Text");
                                OrderId = obj.getString("OrderId");

                                knockedTogetherModelArrayList.add(new KnockedTogetherModel(completed, customer, inNumber,
                                        itemName, jobSite, jobSiteAddress, orderItemId, pieceNo, delivered, ShowNotificationPopup,
                                        ShowPopup, Button1Text, Button2Text, OrderId));
                            }

                            KnockedTogetherAdapter adapter = new KnockedTogetherAdapter(knockedTogetherModelArrayList,
                                    KnockedTogetherActivity.this);
                            lvKnockedTogether.setAdapter(adapter);

                            if (ShowNotificationPopup.equals("1")) {
                                if (knockedTogetherModelArrayList.size() > 1 || knockedTogetherModelArrayList.size() > 0) {
                                    knockedTogetherModelArrayList.remove(knockedTogetherModelArrayList.size() - 1);
                                }
                                Collections.reverse(knockedTogetherModelArrayList);
                                showDialog2(OrderId, objAPIResult.getString("Message"), customer, jobSite, inNumber);
                            }

                            if (ShowPopup.equals("1")) {
                                showDialog(Button1Text, Button2Text, inNumber, "Notification");
                            }

                            Collections.reverse(knockedTogetherModelArrayList);

                        }

                    } catch (Exception e) {
                        Util.showToast("Failed and try again", KnockedTogetherActivity.this);
                    }
                } else {
                    Util.showToast("Failed and try again", KnockedTogetherActivity.this);
                }
            }
        });
        Log.d("last_loc_check", "checkcheckcheck: \n" + value + "\n" + lastLoc);
        apiManager.knockedTogether(value, lastLoc);
        showSoftKeyboard(edtManualInput);
        edtManualInput.requestFocus();
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
    private void showDialog2(String orderId, String confirmation, String customer, String jobSite, String in_number) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(KnockedTogetherActivity.this);
        builder.setMessage(confirmation)
                .setCancelable(false)
                .setPositiveButton("View Job", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(KnockedTogetherActivity.this, OrderItemActivity.class);
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
                        dialog.dismiss();
                    }
                });

        final AlertDialog alert = builder.create();
        alert.show();
    }

    // For Ready For Pickup / Delivery Popup
    private void showDialog(String button1_text, String button2_text, String inNumber, String confirmation) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(KnockedTogetherActivity.this);
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
        Util.showProgressDialog("Loading..", KnockedTogetherActivity.this);
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
                            Toast.makeText(KnockedTogetherActivity.this, "" + msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Util.showToast(objAPIResult.getString("Message"), KnockedTogetherActivity.this);
                        }
                    } catch (Exception e) {
                        Util.showToast("Failed and try again", KnockedTogetherActivity.this);
                    }
                } else {
                    Util.showToast("Failed and try again", KnockedTogetherActivity.this);
                }
            }
        });
        apiManager.showPopup(inNumber, buttonText);
    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        lastLoc = "";
    }
}