package sjcomputers.com.qualitycooling;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import sjcomputers.com.qualitycooling.Global.APIManager;
import sjcomputers.com.qualitycooling.Global.APIManagerCallback;
import sjcomputers.com.qualitycooling.Global.Util;
import sjcomputers.com.qualitycooling.models.FindItemModel;
import sjcomputers.com.qualitycooling.models.ItemModel;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static sjcomputers.com.qualitycooling.Global.Util.MSG_SERIAL_SCANNED;

public class FindItemActivity extends AppCompatActivity {
    Button btnScan;
    EditText edtManualInput;
    String apiUrl;
    public static Handler handler;
    public static String inputVal;
    SharedPreferences sharedPreferences;
    Handler handler2 = new Handler();
    TextView itemInfoTv;
    ListView lvFindItem;
    ArrayList<FindItemModel> findItemModelArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_item);

        btnScan = findViewById(R.id.scan_bt);
        edtManualInput = findViewById(R.id.edtManualInput);
        itemInfoTv = findViewById(R.id.itemInfoTv);
        lvFindItem = findViewById(R.id.lvFindItem);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MSG_SERIAL_SCANNED) {
                    String scanResult = (String) msg.obj;
                    getItems(scanResult);
                }
            }
        };

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.colorAccent)));
        getSupportActionBar().setTitle("Find Items");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        apiUrl = sharedPreferences.getString("URL", "");

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FindItemActivity.this, QRScannerActivity.class);
                QRScannerActivity.screenType = 7;
                startActivity(intent);
            }
        });

        showSoftKeyboard(edtManualInput);
        edtManualInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() >= 5 && charSequence.length() <= 9) {
                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String serial = charSequence.toString();
                            if (!TextUtils.isEmpty(serial)) {
                                getItems(serial);
                            }
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

    public void getItems(String inNumber) {
        findItemModelArrayList.clear();
        itemInfoTv.setVisibility(View.GONE);
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        //Util.showProgressDialog("Loading..", FindItemActivity.this);
        edtManualInput.requestFocus();
        showSoftKeyboard(edtManualInput);
        APIManager apiManager = new APIManager();
        apiManager.setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                progressDialog.dismiss();
                //Util.hideProgressDialog();
                if (objAPIResult != null) {
                    try {
                        if (objAPIResult.getString("Status").equals("Success")) {
                            itemInfoTv.setVisibility(View.VISIBLE);
                            String itemInfo = objAPIResult.getString("ItemInfo");
                            itemInfoTv.setText(itemInfo.replace("\\n", "\n"));
                            JSONArray documentJSONArr = objAPIResult.getJSONArray("ItemList");
                            for (int i = 0; i < documentJSONArr.length(); i++) {
                                JSONObject object = documentJSONArr.getJSONObject(i);
                                String name = object.getString("Name");
                                String Completed = object.getString("Completed");
                                String CompletedBy = object.getString("CompletedBy");
                                String Delivered = object.getString("Delivered");
                                String Depth = object.getString("Depth");
                                //String Description = object.getString("Description");
                                String Height = object.getString("Height");
                                String Length = object.getString("Length");
                                String LoadedInTruck = object.getString("LoadedInTruck");
                                //String OrderItemID = object.getString("OrderItemID");
                                String PieceNo = object.getString("PieceNo");
                                //String Price = object.getString("Price");
                                String Quantity = object.getString("Quantity");
                                String Width = object.getString("Width");
                                String Location = object.getString("Location");

                                findItemModelArrayList.add(new FindItemModel(name, PieceNo, Quantity, Width, Height, Length, Depth, LoadedInTruck,
                                        Completed, CompletedBy, Delivered, Location));
                            }

                            FindItemAdapter findItemAdapter = new FindItemAdapter(findItemModelArrayList, FindItemActivity.this);
                            lvFindItem.setAdapter(findItemAdapter);

                        } else {
                            //Util.hideProgressDialog();
                            itemInfoTv.setVisibility(View.GONE);
                            Toast.makeText(FindItemActivity.this, "" + objAPIResult.getString("Status"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        Util.showToast("Failed and try again", FindItemActivity.this);
                    }
                } else {
                    Util.showToast("Failed and try again", FindItemActivity.this);
                }
            }
        });
        apiManager.findItems(inNumber);
        edtManualInput.requestFocus();
        showSoftKeyboard(edtManualInput);
    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

}