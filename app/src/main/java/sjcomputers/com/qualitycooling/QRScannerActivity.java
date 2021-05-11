package sjcomputers.com.qualitycooling;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.view.MenuItem;

import org.json.JSONObject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import me.dm7.barcodescanner.zbar.ZBarScannerView;
import sjcomputers.com.qualitycooling.Global.APIManager;
import sjcomputers.com.qualitycooling.Global.APIManagerCallback;
import sjcomputers.com.qualitycooling.Global.Util;

import static sjcomputers.com.qualitycooling.Global.Util.MSG_CUTTING_CONFIRMED;
import static sjcomputers.com.qualitycooling.Global.Util.MSG_FORMING_CONFIRMED;
import static sjcomputers.com.qualitycooling.Global.Util.MSG_SERIAL_SCANNED;

/**
 * Created by RabbitJang on 11/14/2017.
 */

public class QRScannerActivity extends AppCompatActivity implements ZBarScannerView.ResultHandler {
    private ZBarScannerView mScannerView;
    public static int screenType; //0: Cutting, 1: Forming, 2: Loading, 3: Item Info

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZBarScannerView(this);
        setContentView(mScannerView);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.colorAccent)));
        if (screenType == 0) {
            getSupportActionBar().setTitle("Cutting");
        } else if (screenType == 1) {
            getSupportActionBar().setTitle("Forming");
        } else if (screenType == 2) {
            getSupportActionBar().setTitle("Loading");
        } else if (screenType == 3) {
            getSupportActionBar().setTitle("Knocked Together");
        } else {
            getSupportActionBar().setTitle("Item Info");
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            boolean permission = checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
            if (!permission) {
                ActivityCompat.requestPermissions(QRScannerActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(me.dm7.barcodescanner.zbar.Result result) {
        mScannerView.stopCamera();
        String scanResult = result.getContents();

        if (screenType == 0 || screenType == 1) {
            APIManager apiManager = new APIManager();
            apiManager.setCallback(new APIManagerCallback() {
                @Override
                public void APICallback(JSONObject objAPIResult) {
                    Util.hideProgressDialog();
                    if (objAPIResult != null) {
                        try {
                            if (objAPIResult.getString("Status").equals("Success")) {
                                String message = objAPIResult.getString("Message");
                                Util.showToast(message, QRScannerActivity.this);
                                Message message1 = new Message();
                                message1.obj = objAPIResult;
                                if (screenType == 0) {
                                    message1.what = MSG_CUTTING_CONFIRMED;
                                } else {
                                    message1.what = MSG_FORMING_CONFIRMED;
                                }
                                DashboardActivity.handler.sendMessage(message1);

                                finish();
                            } else {
                                Util.showToast(objAPIResult.getString("Message"), QRScannerActivity.this);
                            }
                        } catch (Exception e) {
                            Util.showToast("Failed and try again", QRScannerActivity.this);
                        }
                    } else {
                        Util.showToast("Failed and try again", QRScannerActivity.this);
                    }

                }
            });

            Util.showProgressDialog("Updating..", QRScannerActivity.this);
            if (screenType == 0) {
                apiManager.cutting(scanResult);
            } else if (screenType == 1) {
                apiManager.forming(scanResult);
            }
        } else if (screenType == 2 || screenType == 3) {
            Message message = new Message();
            message.what = MSG_SERIAL_SCANNED;
            message.obj = scanResult;
            if (screenType == 2) {
                LoadingActivity.handler.sendMessage(message);
            } else if (screenType == 3) {
                KnockedTogetherActivity.handler.sendMessage(message);
            } else {
                ItemInfoActivity.handler.sendMessage(message);
            }
            finish();
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
