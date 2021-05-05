package sjcomputers.com.qualitycooling.Customer;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import sjcomputers.com.qualitycooling.Global.APIManager;
import sjcomputers.com.qualitycooling.Global.APIManagerCallback;
import sjcomputers.com.qualitycooling.Global.UserData;
import sjcomputers.com.qualitycooling.Global.Util;
import sjcomputers.com.qualitycooling.R;

import static sjcomputers.com.qualitycooling.Global.Util.MSG_CUSTOMER_JOB_SITE_ADDED;
import static sjcomputers.com.qualitycooling.Global.Util.MSG_CUSTOMER_ORDER_ADDED;

/**
 * Created by RabbitJang on 11/5/2018.
 */

public class NewJobSiteActivity extends AppCompatActivity {
    EditText nameEt;
    EditText siteEt;
    EditText addressEt;
    EditText cityEt;
    EditText stateEt;
    EditText zipCodeEt;
    EditText phoneEt;
    EditText notesEt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_job_site);
        configureDesign();
    }

    private void configureDesign() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.colorAccent)));
        getSupportActionBar().setTitle("New Job Site");

        nameEt = findViewById(R.id.editText1);
        siteEt = findViewById(R.id.editText2);
        addressEt = findViewById(R.id.editText3);
        cityEt = findViewById(R.id.editText4);
        stateEt = findViewById(R.id.editText5);
        zipCodeEt = findViewById(R.id.editText6);
        phoneEt = findViewById(R.id.editText7);
        notesEt = findViewById(R.id.editText8);

        Button saveBt = findViewById(R.id.button7);
        saveBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveJobSite();
            }
        });
    }

    private void saveJobSite() {
        String name = nameEt.getText().toString();
        String site = siteEt.getText().toString();
        String address = addressEt.getText().toString();
        String city = cityEt.getText().toString();
        String state = stateEt.getText().toString();
        String zipCode = zipCodeEt.getText().toString();
        String phone = phoneEt.getText().toString();
        String notes = notesEt.getText().toString();

        if(address.equals("")) {
            Util.showToast("Please input address", NewJobSiteActivity.this);
            return;
        }

        if(name.equals("")) {
            Util.showToast("Please input name", NewJobSiteActivity.this);
            return;
        }


        Util.showProgressDialog("Saving job site..", NewJobSiteActivity.this);
        APIManager.getInstance().setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                Util.hideProgressDialog();
                if (objAPIResult != null) {
                    try {
                        if(objAPIResult.getString("ResponseStatus").equals("Success")) {
                            Util.showToast(objAPIResult.getString("ResponseMessage"), NewJobSiteActivity.this);
                            finish();
                            NewOrderActivity.handler.sendEmptyMessage(MSG_CUSTOMER_JOB_SITE_ADDED);
                        }
                    } catch (Exception e) {
                        Util.showToast("Failed and try again", NewJobSiteActivity.this);
                    }
                } else {
                    Util.showToast("Failed and try again", NewJobSiteActivity.this);
                }
            }
        });

        JSONObject object = new JSONObject();
        try {
            object.accumulate("Address", address);
            object.accumulate("City", city);
            object.accumulate("CreatedById", UserData.getInstance().customerId);
            object.accumulate("CustomerId", UserData.getInstance().customerId);
            object.accumulate("Name", name);
            object.accumulate("PhoneNumber", phone);
            object.accumulate("Notes", notes);
            object.accumulate("SiteCode", site);
            object.accumulate("State", state);
            object.accumulate("ZipCode", zipCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        APIManager.getInstance().saveJobSite(object);
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
