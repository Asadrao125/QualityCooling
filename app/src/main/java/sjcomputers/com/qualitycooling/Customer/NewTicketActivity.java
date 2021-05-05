package sjcomputers.com.qualitycooling.Customer;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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

import static sjcomputers.com.qualitycooling.Global.Util.MSG_CUSTOMER_ORDER_ADDED;

/**
 * Created by RabbitJang on 11/5/2018.
 */

public class NewTicketActivity extends AppCompatActivity {
    EditText subjectEt;
    EditText descEt;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_ticket);
        configureDesign();
    }

    private void configureDesign() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.colorAccent)));
        getSupportActionBar().setTitle("New Ticket");

        subjectEt = findViewById(R.id.editText4);
        descEt = findViewById(R.id.editText5);
        Button saveBt = findViewById(R.id.button7);
        saveBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTicket();
            }
        });
    }

    private void saveTicket() {
        String subject = subjectEt.getText().toString();
        String desc = descEt.getText().toString();

        if(subject.equals("")) {
            Util.showToast("Please input subject", NewTicketActivity.this);
            return;
        }

        Util.showProgressDialog("Saving ticket..", NewTicketActivity.this);
        APIManager.getInstance().setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                Util.hideProgressDialog();
                if (objAPIResult != null) {
                    try {
                        if(objAPIResult.getString("ResponseStatus").equals("Success")) {
                            Util.showToast(objAPIResult.getString("ResponseMessage"), NewTicketActivity.this);
                            finish();
                        }
                    } catch (Exception e) {
                        Util.showToast("Failed and try again", NewTicketActivity.this);
                    }
                } else {
                    Util.showToast("Failed and try again", NewTicketActivity.this);
                }
            }
        });

        JSONObject object = new JSONObject();
        try {
            object.accumulate("ClientID", UserData.getInstance().customerId);
            object.accumulate("CreatedBy", UserData.getInstance().userId);
            object.accumulate("Description", desc);
            object.accumulate("Subject", subject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        APIManager.getInstance().saveCustomerTicket(object);
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
