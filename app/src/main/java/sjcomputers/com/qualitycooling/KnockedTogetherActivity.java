package sjcomputers.com.qualitycooling;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import cz.msebera.android.httpclient.Header;
import sjcomputers.com.qualitycooling.Adapters.KnockedTogetherAdapter;
import sjcomputers.com.qualitycooling.Adapters.LoadingAdapter;
import sjcomputers.com.qualitycooling.Adapters.LoadingModel;
import sjcomputers.com.qualitycooling.Global.APIManager;
import sjcomputers.com.qualitycooling.Global.APIManagerCallback;
import sjcomputers.com.qualitycooling.Global.UserData;
import sjcomputers.com.qualitycooling.Global.Util;
import sjcomputers.com.qualitycooling.Util.SharedPref;
import sjcomputers.com.qualitycooling.models.KnockedTogetherModel;

import android.app.Dialog;
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
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static sjcomputers.com.qualitycooling.Global.Util.MSG_SERIAL_SCANNED;

public class KnockedTogetherActivity extends AppCompatActivity {
    Button btnScan, btnManual;
    Dialog inputDialog;
    private SharedPreferences sharedPreferences;
    String apiUrl;
    public static Handler handler;
    ListView lvKnockedTogether;
    public static String inputVal;
    String val = "d";
    ArrayList<KnockedTogetherModel> knockedTogetherModelArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_knocked_together);

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
                    val = scanResult;
                    inputVal = scanResult;
                }
            }
        };

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.colorAccent)));
        getSupportActionBar().setTitle("Knocked Together");

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

                serialEt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (charSequence.length() > 5 && charSequence.length() <= 9) {
                            String serial = charSequence.toString();
                            checkcheckcheck(serial);
                            val = serial;
                            inputVal = serial;
                            serialEt.setText("");
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                Button okBt = (Button) inputDialog.findViewById(R.id.button8);
                okBt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String serial = serialEt.getText().toString();
                        if (!TextUtils.isEmpty(serial)) {
                            checkcheckcheck(serial);
                            val = serial;
                        }
                        inputDialog.hide();
                    }
                });
            }
        });
    }

    public void checkcheckcheck(String value) {
        Log.d("val_value", "checkcheckcheck: val= " + val + "\n" + "value: " + value);
        if (!val.equals(value)) {
            knockedTogetherModelArrayList.clear();
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
                                JSONArray jsonArray = jsonObject.getJSONArray("Items");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    String completed = obj.getString("Completed");
                                    String customer = obj.getString("Customer");
                                    String inNumber = obj.getString("INNumber");
                                    String itemName = obj.getString("ItemName");
                                    String jobSite = obj.getString("JobSite");
                                    String jobSiteAddress = obj.getString("JobSiteAddress");
                                    String orderItemId = obj.getString("OrderItemId");
                                    String pieceNo = obj.getString("PieceNo");
                                    String delivered = obj.getString("Delivered");

                                    knockedTogetherModelArrayList.add(new KnockedTogetherModel(completed, customer, inNumber,
                                            itemName, jobSite, jobSiteAddress, orderItemId, pieceNo, delivered));
                                }

                                KnockedTogetherAdapter adapter = new KnockedTogetherAdapter(knockedTogetherModelArrayList,
                                        KnockedTogetherActivity.this);
                                lvKnockedTogether.setAdapter(adapter);

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
            apiManager.knockedTogether(value);
        } else {
            Toast.makeText(this, "This item is already scanned", Toast.LENGTH_SHORT).show();
        }
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
}