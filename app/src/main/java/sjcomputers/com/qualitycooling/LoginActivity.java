package sjcomputers.com.qualitycooling;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import sjcomputers.com.qualitycooling.Customer.CustomerActivity;
import sjcomputers.com.qualitycooling.Driver.DriverOrderActivity;
import sjcomputers.com.qualitycooling.Global.APIManager;
import sjcomputers.com.qualitycooling.Global.APIManagerCallback;
import sjcomputers.com.qualitycooling.Global.UserData;
import sjcomputers.com.qualitycooling.Global.Util;

import static sjcomputers.com.qualitycooling.Global.Util.MIN_DISTANCE_UPDATE;
import static sjcomputers.com.qualitycooling.Global.Util.MIN_TIME_LOCATION_UPDATE;

/**
 * Created by RabbitJang on 5/27/2018.
 */

public class LoginActivity extends AppCompatActivity implements LocationListener {
    EditText userNameEt;
    EditText passwordEt;
    private SharedPreferences sharedPreferences;
    private String apiUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        configureDesign();
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getApplicationContext()));

        apiUrl = sharedPreferences.getString("URL", "");
        if (apiUrl.equals("")) {
            apiUrl = "https://ductorder.com";
        }
        APIManager.SERVER_ADDR = apiUrl + "/services/service.svc";
        String savedUserName = sharedPreferences.getString("UserName", "");
        String savedPassword = sharedPreferences.getString("Password", "");
        if (!savedUserName.equals("") && !savedPassword.equals("")) {
            userNameEt.setText(savedUserName);
            passwordEt.setText(savedPassword);
            doLogin(savedUserName, savedPassword);
        }

        startLocationManager();
    }

    private void startLocationManager() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        } else {
            LocationManager myManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            boolean isGPSEnabled = myManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (isGPSEnabled) {
                myManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_LOCATION_UPDATE, MIN_DISTANCE_UPDATE, this);
            }

            boolean networkEnabled = myManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (networkEnabled) {
                myManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_LOCATION_UPDATE, MIN_DISTANCE_UPDATE, this);
            }
        }
    }

    private void configureDesign() {
        Button loginBt = (Button) findViewById(R.id.button2);
        userNameEt = (EditText) findViewById(R.id.userNameTxt);
        passwordEt = (EditText) findViewById(R.id.userPwdTxt);

        loginBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = userNameEt.getText().toString();
                String userPwd = passwordEt.getText().toString();
                if (userName.equals("") || userPwd.equals("")) {
                    Util.showToast("Please input valid username and password", LoginActivity.this);
                    return;
                }
                doLogin(userName, userPwd);
            }
        });
    }

    private void doLogin(final String userName, final String userPassword) {
        APIManager.getInstance().setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                Util.hideProgressDialog();
                if (objAPIResult != null) {
                    try {
                        if (objAPIResult.getString("StatusCode").equals("Success")) {
                            UserData.getInstance().authToken = objAPIResult.getString("AuthCode");
                            UserData.getInstance().userId = objAPIResult.getInt("UserId");
                            UserData.getInstance().cuttingButtonShow = objAPIResult.getInt("CuttingButtonShow");
                            UserData.getInstance().cuttingButtonText = objAPIResult.getString("CuttingButtonText");
                            UserData.getInstance().formingButtonShow = objAPIResult.getInt("FormingButtonShow");
                            UserData.getInstance().formingButtonText = objAPIResult.getString("FormingButtonText");
                            UserData.getInstance().itemInfoButtonShow = objAPIResult.getInt("ItemInfoButtonShow");
                            UserData.getInstance().itemInfoButtonText = objAPIResult.getString("ItemInfoButtonText");
                            UserData.getInstance().jobsButtonShow = objAPIResult.getInt("JobsButtonShow");
                            UserData.getInstance().jobsButtonText = objAPIResult.getString("JobsButtonText");
                            UserData.getInstance().loadingButtonShow = objAPIResult.getInt("LoadingButtonShow");
                            UserData.getInstance().loadingButtonText = objAPIResult.getString("LoadingButtonText");

                            if (!objAPIResult.get("CustomerID").equals(JSONObject.NULL)) {
                                UserData.getInstance().customerId = objAPIResult.getInt("CustomerID");
                            } else {
                                UserData.getInstance().customerId = 0;
                            }

                            int userRole = objAPIResult.getInt("UserRole");

                            sharedPreferences.edit().putString("UserName", userName).apply();
                            sharedPreferences.edit().putString("Password", userPassword).apply();

                            if (userRole == 2) {
                                Intent intent = new Intent(LoginActivity.this, CustomerActivity.class);
                                startActivity(intent);
                            } else {
                                getStatus(userRole);
                            }
                        } else {
                            Util.showToast(objAPIResult.getString("Message"), LoginActivity.this);
                        }
                    } catch (Exception e) {
                        Util.showToast("Login failed and try again", LoginActivity.this);
                    }
                } else {
                    Util.showToast("Login failed and try again", LoginActivity.this);
                }

            }
        });
        Util.showProgressDialog("Authenticating..", LoginActivity.this);
        APIManager.getInstance().authenticateUser(userName, userPassword);
    }

    private void getStatus(final int userRole) {
        UserData.getInstance().statusArray = new ArrayList<>();
        Util.showProgressDialog("Getting status..", LoginActivity.this);
        APIManager.getInstance().setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                Util.hideProgressDialog();
                if (objAPIResult != null) {
                    try {
                        //if(objAPIResult.getString("StatusCode").equals("Success")) {
                        JSONArray statusJSONArry = objAPIResult.getJSONArray("StatusList");
                        UserData.getInstance().statusArray = Util.toList(statusJSONArry);
                        UserData.getInstance().statuses = new String[UserData.getInstance().statusArray.size()];
                        for (int i = 0; i < UserData.getInstance().statusArray.size(); i++) {
                            UserData.getInstance().statuses[i] = UserData.getInstance().statusArray.get(i).get("Status").toString();
                        }

                        if (userRole == 3) {
                            /* Driver Panel */
                            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                            intent.putExtra("val", "3");
                            startActivity(intent);
                            finish();
                        } else {//if(userRole == 1) {
                            /* Admin Panel */
                            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                            intent.putExtra("val", "1");
                            startActivity(intent);
                            finish();
                        }

                        //}
                    } catch (Exception e) {
                        Util.showToast("Failed and try again", LoginActivity.this);
                    }
                } else {
                    Util.showToast("Failed and try again", LoginActivity.this);
                }
            }
        });
        APIManager.getInstance().getStatuses();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting:
                showUrlSetDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showUrlSetDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_url_set);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();

        final EditText urlEt = (EditText) dialog.findViewById(R.id.urlText);
        urlEt.setText(apiUrl);
        Button closeBt = (Button) dialog.findViewById(R.id.closeBtn);
        closeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
            }
        });

        Button okBt = (Button) dialog.findViewById(R.id.okBtn);
        okBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String urlStr = urlEt.getText().toString();
                if (urlStr.startsWith("http://") || urlStr.startsWith("https://")) {
                    apiUrl = urlStr;
                    sharedPreferences.edit().putString("URL", urlStr).apply();
                    APIManager.SERVER_ADDR = apiUrl + "/services/service.svc";
                    dialog.hide();
                } else {
                    Util.showToast("Please input correct URL", LoginActivity.this);
                    return;
                }
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            UserData.getInstance().lat = location.getLatitude();
            UserData.getInstance().lng = location.getLongitude();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
