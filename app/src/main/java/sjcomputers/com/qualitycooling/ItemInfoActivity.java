package sjcomputers.com.qualitycooling;

import android.app.Dialog;
import android.content.Context;
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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import sjcomputers.com.qualitycooling.Admin.OrderItemActivity;
import sjcomputers.com.qualitycooling.Global.APIManager;
import sjcomputers.com.qualitycooling.Global.APIManagerCallback;
import sjcomputers.com.qualitycooling.Global.Util;
import sjcomputers.com.qualitycooling.models.ItemModel;

import static sjcomputers.com.qualitycooling.Global.Util.MSG_SERIAL_SCANNED;

public class ItemInfoActivity extends AppCompatActivity {
    Dialog inputDialog;
    public static Handler handler;
    ListView documentLv, itemListLv;
    TextView itemInfoTv;
    ItemListAdapter itemListAdapter;
    public static int IN_Number;
    Button btnViewJob;
    ArrayList<ItemModel> itemModelArrayList = new ArrayList<>();
    String val = "d";
    String Status, finalStatus;
    EditText edtManualInput;
    Handler handler2 = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_info);

        setupView();
        btnViewJob = findViewById(R.id.btnViewJob);
        edtManualInput = findViewById(R.id.edtManualInput);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MSG_SERIAL_SCANNED) {
                    String scanResult = (String) msg.obj;
                    itemInfo(encodeURLPathComponent(scanResult));
                    itemList(encodeURLPathComponent(scanResult));
                    val = encodeURLPathComponent(scanResult);
                }
            }
        };

        String scanned_value = getIntent().getStringExtra("scanned_value");
        if (!TextUtils.isEmpty(scanned_value)) {
            itemInfo(scanned_value);
            itemList(scanned_value);
        }

        btnViewJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ItemInfoActivity.this, OrderItemActivity.class);
                Bundle b = new Bundle();
                b.putInt("OrderID", IN_Number);
                b.putString("Title", String.valueOf(IN_Number));
                b.putString("Status", finalStatus);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        showSoftKeyboard(edtManualInput);
        /*edtManualInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    String input = edtManualInput.getText().toString().trim();
                    if (!TextUtils.isEmpty(input)) {
                        itemInfo(input);
                        itemList(input);
                        edtManualInput.setText("");
                    } else {
                        Toast.makeText(ItemInfoActivity.this, "Please enter input value", Toast.LENGTH_SHORT).show();
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
                            itemInfo(serial);
                            itemList(serial);
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

    private void itemInfo(String value) {
        itemModelArrayList.clear();
        Util.showProgressDialog("Loading..", ItemInfoActivity.this);
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
                        if (objAPIResult.getString("Status").equals("Success")) {
                            String itemInfo = objAPIResult.getString("ItemInfo");
                            itemInfoTv.setText(itemInfo.replace("\\n", "\n"));
                            IN_Number = objAPIResult.getInt("OrderId");

                            Log.d("kfnkfnknfk", "APICallback: " + IN_Number);

                            Log.d("sooooooor", "APICallback: " + itemInfo);
                            String newString = itemInfo.replace("\\n", "\n");
                            String newString2[] = newString.split("\n");
                            for (int i = 0; i < newString2.length; i++) {
                                Log.d("museebathhb", "APICallback: " + newString2[i]);
                                Status = newString2[2];
                            }

                            String newStatus[] = Status.split(":");
                            finalStatus = newStatus[1].trim();

                            JSONArray documentJSONArr = objAPIResult.getJSONArray("Documents");
                            ArrayList<HashMap<String, Object>> documentArr = Util.toList(documentJSONArr);
                            DocumentAdapter1 adapter = new DocumentAdapter1(ItemInfoActivity.this, documentArr);
                            documentLv.setAdapter(adapter);
                        } else {
                            Util.showToast(objAPIResult.getString("Message"), ItemInfoActivity.this);
                        }
                    } catch (Exception e) {
                        Util.showToast("Failed and try again", ItemInfoActivity.this);
                    }
                } else {
                    Util.showToast("Failed and try again", ItemInfoActivity.this);
                }
            }
        });
        apiManager.itemInfo(value);
        showSoftKeyboard(edtManualInput);
        edtManualInput.requestFocus();
    }

    private void itemList(String value) {
        Log.d("1234567", "checkcheckcheck: val= " + val + "\n" + "value: " + value);
        itemModelArrayList.clear();
        showSoftKeyboard(edtManualInput);
        edtManualInput.requestFocus();
        APIManager apiManager = new APIManager();
        apiManager.setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                //Util.hideProgressDialog();
                if (objAPIResult != null) {
                    try {
                        if (objAPIResult.getString("Status").equals("Success")) {
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

                                itemModelArrayList.add(new ItemModel(name, PieceNo, Quantity, Width, Height, Length, Depth, LoadedInTruck,
                                        Completed, CompletedBy, Delivered));
                            }

                            itemListAdapter = new ItemListAdapter(itemModelArrayList, ItemInfoActivity.this);
                            itemListLv.setAdapter(itemListAdapter);

                        } else {
                            Util.showToast(objAPIResult.getString("Message"), ItemInfoActivity.this);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        apiManager.itemInfo(value);
        showSoftKeyboard(edtManualInput);
        edtManualInput.requestFocus();
    }

    private void setupView() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.colorAccent)));
        getSupportActionBar().setTitle("Item Info");

        itemInfoTv = findViewById(R.id.info_tv);
        documentLv = findViewById(R.id.document_lv);
        itemListLv = findViewById(R.id.itemListLv);

        Button scanBt = findViewById(R.id.scan_bt);
        Button manualBt = findViewById(R.id.manual_bt);

        scanBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemInfoActivity.this, QRScannerActivity.class);
                QRScannerActivity.screenType = 5;
                startActivity(intent);
            }
        });

        manualBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputDialog = new Dialog(ItemInfoActivity.this);
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

                Button okBt = (Button) inputDialog.findViewById(R.id.button8);
                okBt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String serial = serialEt.getText().toString();
                        if (serial.equals("")) {
                            Util.showToast("Input valid serial number", ItemInfoActivity.this);
                            return;
                        }

                        itemInfo(serial);
                        itemList(serial);
                        val = serial;
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

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public static String encodeURLPathComponent(String path) {
        try {
            return new URI(null, null, path, null).toASCIIString();
        } catch (URISyntaxException e) {
            // do some error handling
        }
        return "";
    }

}