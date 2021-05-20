package sjcomputers.com.qualitycooling;

import android.app.Dialog;
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
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import java.util.HashMap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.entity.mime.Header;
import sjcomputers.com.qualitycooling.Adapters.LoadingAdapter;
import sjcomputers.com.qualitycooling.Adapters.LoadingModel;
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
    String val = "d";
    ArrayList<LoadingModel> loadingModelArrayList = new ArrayList<>();

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
                    val = scanResult;
                    inputVal = scanResult;
                }
            }
        };
    }

    private void loadValue(String value) {
        if (!val.equals(value)) {
            loadingModelArrayList.clear();
            Util.showProgressDialog("Loading..", LoadingActivity.this);
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
                                    String A_R_ENERGY = obj.getString("Customer");
                                    String INNumber = obj.getString("INNumber");
                                    String ItemName = obj.getString("ItemName");
                                    String JobSite = obj.getString("JobSite");
                                    String JobSiteAddress = obj.getString("JobSiteAddress");
                                    String Loaded = obj.getString("Loaded");
                                    String OrderItemId = obj.getString("OrderItemId");
                                    String PieceNo = obj.getString("PieceNo");

                                    loadingModelArrayList.add(new LoadingModel(A_R_ENERGY, INNumber, ItemName, JobSite, JobSiteAddress, Loaded,
                                            OrderItemId, PieceNo));
                                }

                            } else {
                                Util.showToast(objAPIResult.getString("Message"), LoadingActivity.this);
                            }
                        } catch (Exception e) {
                            Util.showToast("Failed and try again", LoadingActivity.this);
                        }
                    } else {
                        Util.showToast("Failed and try again", LoadingActivity.this);
                    }

                    adapter = new LoadingAdapter(loadingModelArrayList, LoadingActivity.this);
                    loadLv.setAdapter(adapter);

                }
            });
            apiManager.loading(value);
        } else {
            Toast.makeText(this, "This item is already scanned", Toast.LENGTH_SHORT).show();
        }
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
                serialEt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (charSequence.length() > 5 && charSequence.length() <= 9) {
                            String serial = charSequence.toString();
                            loadValue(serial);
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
                            loadValue(serial);
                            val = serial;
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
}
