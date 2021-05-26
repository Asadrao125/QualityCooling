package sjcomputers.com.qualitycooling;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import sjcomputers.com.qualitycooling.Adapters.DeliveredAdapter;
import sjcomputers.com.qualitycooling.Adapters.KnockedTogetherAdapter;
import sjcomputers.com.qualitycooling.Admin.OrderItemActivity;
import sjcomputers.com.qualitycooling.Global.APIManager;
import sjcomputers.com.qualitycooling.Global.APIManagerCallback;
import sjcomputers.com.qualitycooling.Global.Util;
import sjcomputers.com.qualitycooling.Util.SharedPref;
import sjcomputers.com.qualitycooling.models.KnockedTogetherModel;

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static sjcomputers.com.qualitycooling.Global.Util.MSG_SERIAL_SCANNED;

public class DeliveredActivity extends AppCompatActivity {
    Button btnScan, btnManual;
    Dialog inputDialog;
    private SharedPreferences sharedPreferences;
    String apiUrl;
    public static Handler handler;
    ListView lvKnockedTogether;
    public static String inputVal;
    ArrayList<KnockedTogetherModel> knockedTogetherModelArrayList = new ArrayList<>();
    String completed, customer, inNumber, itemName, jobSite, jobSiteAddress, orderItemId;
    String pieceNo, delivered, ShowNotificationPopup, ShowPopup, Button1Text, Button2Text;
    String OrderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivered);

        btnScan = findViewById(R.id.scan_bt);
        btnManual = findViewById(R.id.manual_bt);
        lvKnockedTogether = findViewById(R.id.lvKnockedTogether);
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.colorAccent)));
        getSupportActionBar().setTitle("Delivered");

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        apiUrl = sharedPreferences.getString("URL", "");

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DeliveredActivity.this, QRScannerActivity.class);
                QRScannerActivity.screenType = 4;
                startActivity(intent);
            }
        });

        btnManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputDialog = new Dialog(DeliveredActivity.this);
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
        Util.showProgressDialog("Loading..", DeliveredActivity.this);
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

                            DeliveredAdapter adapter = new DeliveredAdapter(knockedTogetherModelArrayList,
                                    DeliveredActivity.this);
                            lvKnockedTogether.setAdapter(adapter);

                            if (ShowNotificationPopup.equals("1")) {
                                if (knockedTogetherModelArrayList.size() > 1 || knockedTogetherModelArrayList.size() > 0) {
                                    knockedTogetherModelArrayList.remove(knockedTogetherModelArrayList.size() - 1);
                                }
                                showDialog2(OrderId, objAPIResult.getString("Message"), customer, jobSite, inNumber);
                            }

                            if (ShowPopup.equals("1")) {
                                showDialog(Button1Text, Button2Text, inNumber, "Notification");
                            }
                        }

                    } catch (Exception e) {
                        Util.showToast("Failed and try again", DeliveredActivity.this);
                    }
                } else {
                    Util.showToast("Failed and try again", DeliveredActivity.this);
                }
            }
        });
        apiManager.getDeliveryList(value);
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(DeliveredActivity.this);
        builder.setMessage(confirmation)
                .setCancelable(false)
                .setPositiveButton("View Job", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Intent intent = new Intent(DeliveredActivity.this, OrderItemActivity.class);
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(DeliveredActivity.this);
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
        Util.showProgressDialog("Loading..", DeliveredActivity.this);
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
                            Toast.makeText(DeliveredActivity.this, "" + msg, Toast.LENGTH_SHORT).show();

                        } else {
                            Util.showToast(objAPIResult.getString("Message"), DeliveredActivity.this);
                        }
                    } catch (Exception e) {
                        Util.showToast("Failed and try again", DeliveredActivity.this);
                    }
                } else {
                    Util.showToast("Failed and try again", DeliveredActivity.this);
                }
            }
        });
        apiManager.showPopup(inNumber, buttonText);
    }

}