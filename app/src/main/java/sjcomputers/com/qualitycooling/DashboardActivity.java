package sjcomputers.com.qualitycooling;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import sjcomputers.com.qualitycooling.Admin.JobActivity;
import sjcomputers.com.qualitycooling.Admin.OrderAdapter;
import sjcomputers.com.qualitycooling.Global.UserData;

import static sjcomputers.com.qualitycooling.Global.Util.MSG_CUTTING_CONFIRMED;
import static sjcomputers.com.qualitycooling.Global.Util.MSG_FORMING_CONFIRMED;
import static sjcomputers.com.qualitycooling.Global.Util.MSG_REFRESH_ORDER;

public class DashboardActivity extends AppCompatActivity {
    public static Handler handler;
    Dialog dialog;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        setupView();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == MSG_CUTTING_CONFIRMED) {
                    JSONObject object = (JSONObject) msg.obj;
                    showConfirmDialog(object);
                } else if(msg.what == MSG_FORMING_CONFIRMED) {
                    JSONObject object = (JSONObject) msg.obj;
                    showConfirmDialog(object);
                }
            }
        };
    }

    private void showConfirmDialog(JSONObject object) {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_confirm);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();

        TextView messageTv = dialog.findViewById(R.id.message_tv);
        TextView customerTv = dialog.findViewById(R.id.customer_tv);
        TextView inNumberTv = dialog.findViewById(R.id.in_number_tv);
        TextView jobSiteTv = dialog.findViewById(R.id.jobsite_tv);
        TextView jobSiteAddressTv = dialog.findViewById(R.id.jobsite_address_tv);

        try {
            messageTv.setText(object.getString("Message"));
            customerTv.setText(String.format("Customer: %s", object.getString("Customer")));
            inNumberTv.setText(String.format("IN #: %s", object.getString("INNumber")));
            jobSiteTv.setText(String.format("Job Site: %s", object.getString("JobSite")));
            jobSiteAddressTv.setText(String.format("Job Address: %s", object.getString("JobSiteAddress")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Button closeBt = dialog.findViewById(R.id.closeBtn);
        Button okBt = dialog.findViewById(R.id.okBtn);
        closeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        okBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void setupView() {
        Button jobsBt = findViewById(R.id.bt_jobs);
        Button cuttingBt = findViewById(R.id.bt_cutting);
        Button formingBt = findViewById(R.id.bt_forming);
        Button loadingBt = findViewById(R.id.bt_loading);
        Button itemInfoBt = findViewById(R.id.bt_item_info);

        jobsBt.setVisibility(UserData.getInstance().jobsButtonShow == 1? View.VISIBLE : View.INVISIBLE);
        cuttingBt.setVisibility(UserData.getInstance().cuttingButtonShow == 1? View.VISIBLE : View.INVISIBLE);
        formingBt.setVisibility(UserData.getInstance().formingButtonShow == 1? View.VISIBLE : View.INVISIBLE);
        loadingBt.setVisibility(UserData.getInstance().loadingButtonShow == 1? View.VISIBLE : View.INVISIBLE);
        itemInfoBt.setVisibility(UserData.getInstance().itemInfoButtonShow == 1? View.VISIBLE : View.INVISIBLE);

        jobsBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, JobActivity.class);
                startActivity(intent);
            }
        });

        cuttingBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, QRScannerActivity.class);
                QRScannerActivity.screenType = 0;
                startActivity(intent);
            }
        });

        formingBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, QRScannerActivity.class);
                QRScannerActivity.screenType = 1;
                startActivity(intent);
            }
        });

        loadingBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, LoadingActivity.class);
                startActivity(intent);
            }
        });

        itemInfoBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, ItemInfoActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                sharedPreferences.edit().putString("UserName", "").apply();
                sharedPreferences.edit().putString("Password", "").apply();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
