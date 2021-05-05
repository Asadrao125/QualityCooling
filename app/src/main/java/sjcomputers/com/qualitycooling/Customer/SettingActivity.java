package sjcomputers.com.qualitycooling.Customer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

/**
 * Created by RabbitJang on 11/5/2018.
 */

public class SettingActivity extends AppCompatActivity {
    EditText oldPwdEt;
    EditText newPwdEt;
    EditText confirmPwdEt;

    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initValue();
        configureDesign();
    }

    private void initValue() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    private void configureDesign() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.colorAccent)));
        getSupportActionBar().setTitle("Setting");

        oldPwdEt = findViewById(R.id.editText1);
        newPwdEt = findViewById(R.id.editText2);
        confirmPwdEt = findViewById(R.id.editText3);

        Button resetBt = findViewById(R.id.button7);
        resetBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
    }

    private void changePassword() {
        String oldPwd = oldPwdEt.getText().toString();
        final String newPwd = newPwdEt.getText().toString();
        String confirmPwd = confirmPwdEt.getText().toString();

        if(oldPwd.equals("")) {
            Util.showToast("Please input old password", SettingActivity.this);
            return;
        }

        if(newPwd.equals("")) {
            Util.showToast("Please input new password", SettingActivity.this);
            return;
        }

        if(confirmPwd.equals("")) {
            Util.showToast("Please input confirm password", SettingActivity.this);
            return;
        }

        if(!newPwd.equals(confirmPwd)) {
            Util.showToast("New password and confirm password are not matched!", SettingActivity.this);
            return;
        }

        if(newPwd.equals(oldPwd))

        Util.showProgressDialog("Changing password..", SettingActivity.this);
        APIManager.getInstance().setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                Util.hideProgressDialog();
                if (objAPIResult != null) {
                    try {
                        Util.showToast(objAPIResult.getString("ResponseMessage"), SettingActivity.this);
                        if(objAPIResult.getString("ResponseStatus").equals("Success")) {
                            finish();
                            sharedPreferences.edit().putString("Password", newPwd).apply();
                        }
                    } catch (Exception e) {
                        Util.showToast("Failed and try again", SettingActivity.this);
                    }
                } else {
                    Util.showToast("Failed and try again", SettingActivity.this);
                }
            }
        });

        JSONObject object = new JSONObject();
        try {
            object.accumulate("UserID", UserData.getInstance().userId);
            object.accumulate("OldPassword", oldPwd);
            object.accumulate("Password", newPwd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        APIManager.getInstance().changePassword(object);
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
