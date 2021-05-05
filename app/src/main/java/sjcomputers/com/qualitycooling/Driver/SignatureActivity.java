package sjcomputers.com.qualitycooling.Driver;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.gcacace.signaturepad.views.SignaturePad;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import sjcomputers.com.qualitycooling.Admin.OrderItemActivity;
import sjcomputers.com.qualitycooling.DocumentActivity;
import sjcomputers.com.qualitycooling.Global.APIManager;
import sjcomputers.com.qualitycooling.Global.APIManagerCallback;
import sjcomputers.com.qualitycooling.Global.UserData;
import sjcomputers.com.qualitycooling.Global.Util;
import sjcomputers.com.qualitycooling.R;

import static sjcomputers.com.qualitycooling.Global.Util.BitmapToString;
import static sjcomputers.com.qualitycooling.Global.Util.MSG_ORDER_DELIVERED;

public class SignatureActivity extends AppCompatActivity {
    public static int orderId;
    public static String driverOrderId;

    private SignaturePad mSignaturePad;
    private Button mClearButton;
    private Button mSaveButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);
        initValue();
        configureDesign();
    }

    private void initValue() {
        //itemArr = new ArrayList<>();
    }

    private void configureDesign() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.colorAccent)));
        getSupportActionBar().setTitle("Driver Signature");

        mSignaturePad = findViewById(R.id.signature_pad);
        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {

            }

            @Override
            public void onSigned() {

            }

            @Override
            public void onClear() {

            }
        });

        final EditText noteEt = findViewById(R.id.editText9);

        Button addItemBt = findViewById(R.id.button8);
        addItemBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignatureActivity.this, DriverItemActivity.class);
                DriverItemActivity.orderID = orderId;
                startActivity(intent);
            }
        });

        Button documentsBt = findViewById(R.id.button9);
        documentsBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignatureActivity.this, DocumentActivity.class);
                DocumentActivity.orderId = orderId;
                DocumentActivity.driverOrderId = driverOrderId;
                DocumentActivity.documentType = 1;
                startActivity(intent);
            }
        });

        mSaveButton = (Button)findViewById(R.id.saveSignature);
        mClearButton = (Button)findViewById(R.id.clearSignature);

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String note = noteEt.getText().toString();
                if(note.equals("")) {
                    Util.showToast("Please input note", SignatureActivity.this);
                    return;
                }

                /*int itemCount = 0;
                for(int i = 0; i < SignatureActivity.itemArr.size(); i++) {
                    HashMap<String, Object> itemObj = SignatureActivity.itemArr.get(i);
                    int itemId = (int) itemObj.get("ItemId");
                    if(itemId != 0) {
                        itemCount ++;
                    }
                }

                if(itemCount == 0) {
                    Util.showToast("Please add item and qty", SignatureActivity.this);
                    return;
                }*/

                Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
                confirmSignature(signatureBitmap, note, orderId);
            }
        });

        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSignaturePad.clear();
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

    private void confirmSignature(Bitmap signatureBitmap, String note, int orderId) {
        String signatureString = BitmapToString(signatureBitmap);
        APIManager.getInstance().setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                Util.hideProgressDialog();
                if (objAPIResult != null) {
                    try {
                        //if(objAPIResult.getString("StatusCode").equals("Success")) {
                        DriverOrderAdapter.handler.sendEmptyMessage(MSG_ORDER_DELIVERED);
                        Util.showToast("Updated signature successfully!", SignatureActivity.this);
                        finish();
                        /*} else {
                            Util.showToast("Failed and try again", DriverPositionActivity.this);
                            Log.d("result", "success");
                        }*/
                    } catch (Exception e) {
                        Util.showToast("Failed and try again", SignatureActivity.this);
                    }
                } else {
                    Util.showToast("Failed and try again", SignatureActivity.this);
                }

            }
        });

        Util.showProgressDialog("Updating signature..", SignatureActivity.this);

        JSONObject object = new JSONObject();
        try {
            /*JSONArray itemJSONArr = new JSONArray();
            for(int i = 0; i < SignatureActivity.itemArr.size(); i++) {
                HashMap<String, Object> itemObj = SignatureActivity.itemArr.get(i);
                JSONObject itemJSONObj = new JSONObject();
                itemJSONObj.put("ItemId", itemObj.get("ItemId"));
                itemJSONObj.put("Quantity", itemObj.get("Quantity"));
                itemJSONArr.put(itemJSONObj);
            }*/

            object.accumulate("orderId", orderId);
            object.accumulate("userId", UserData.getInstance().userId);
            object.accumulate("authtoken", UserData.getInstance().authToken);
            object.accumulate("Notes", note);
            //object.accumulate("ItemDetail", itemJSONArr);
            object.accumulate("Signature", signatureString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        APIManager.getInstance().updateSignature(object);
    }
}
