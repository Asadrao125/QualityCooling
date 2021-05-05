package sjcomputers.com.qualitycooling.Customer;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

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

public class NewOrderActivity extends AppCompatActivity {
    String newRefNo;
    String neededDateStr;
    String neededTimeStr;
    ArrayList<HashMap<String, Object>> jobSiteArr;
    int jobSiteId;
    String description;
    String notes;
    String deliveryInstruction;

    TextView inTv;
    Spinner jobSiteSpinner;
    EditText descEt;
    EditText notesEt;
    EditText instructionEt;

    public static Handler handler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);
        initValue();
        configureDesign();
        getNewRefNo();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == MSG_CUSTOMER_JOB_SITE_ADDED) {
                    getCustomerJobSites();
                }
            }
        };
    }

    private void initValue() {
        jobSiteId = 0;
    }

    private void configureDesign() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.colorAccent)));
        getSupportActionBar().setTitle("New Order");

        inTv = findViewById(R.id.textView21);
        jobSiteSpinner = findViewById(R.id.spinner);
        descEt = findViewById(R.id.editText);
        final TextView neededDateTv = findViewById(R.id.textView23);
        final TextView neededTimeTv = findViewById(R.id.textView25);
        notesEt = findViewById(R.id.editText2);
        instructionEt = findViewById(R.id.editText3);

        Date curDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String dateStr = dateFormat.format(curDate);
        neededDateTv.setText(dateStr);

        dateFormat = new SimpleDateFormat("HH:mm");
        String timeStr = dateFormat.format(curDate);
        neededTimeTv.setText(timeStr);

        Button saveBt = findViewById(R.id.button7);
        saveBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                description = descEt.getText().toString();
                notes = notesEt.getText().toString();
                deliveryInstruction = instructionEt.getText().toString();
                if(jobSiteId == 0) {
                    Util.showToast("Select job site", NewOrderActivity.this);
                    return;
                }

                if(description.equals("")) {
                    Util.showToast("Input description", NewOrderActivity.this);
                    return;
                }

                if(notes.equals("")) {
                    Util.showToast("Input notes", NewOrderActivity.this);
                    return;
                }

                if(deliveryInstruction.equals("")) {
                    Util.showToast("Input delivery instruction", NewOrderActivity.this);
                    return;
                }

                saveCustomerOrder();
            }
        });

        neededDateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year, final int month, final int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.clear();
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        Date date = calendar.getTime();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                        neededDateStr = simpleDateFormat.format(date);
                        neededDateTv.setText(neededDateStr);
                    }
                };

                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(NewOrderActivity.this, dateListener, year, month, dayOfMonth);
                datePickerDialog.setTitle("Select Date");
                datePickerDialog.show();
            }
        });

        neededTimeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.clear();
                        calendar.set(Calendar.HOUR, selectedHour);
                        calendar.set(Calendar.MINUTE, selectedMinute);

                        Date date = calendar.getTime();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
                        neededTimeStr = simpleDateFormat.format(date);
                        neededTimeTv.setText(neededTimeStr);
                    }
                };

                Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(NewOrderActivity.this, timeListener, hour, minute, true);//Yes 24 hour time
                timePickerDialog.setTitle("Select Time");
                timePickerDialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_jobsite, menu);
        return true;
    }

    private void getNewRefNo() {
        Util.showProgressDialog("Getting new ref no..", NewOrderActivity.this);
        APIManager.getInstance().setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                Util.hideProgressDialog();
                if (objAPIResult != null) {
                    try {
                        //if(objAPIResult.getString("StatusCode").equals("Success")) {
                        newRefNo = objAPIResult.getString("NewRefNo");
                        inTv.setText(newRefNo);
                        getCustomerJobSites();
                        //}
                    } catch (Exception e) {
                        Util.showToast("Failed and try again", NewOrderActivity.this);
                    }
                } else {
                    Util.showToast("Failed and try again", NewOrderActivity.this);
                }
            }
        });
        APIManager.getInstance().getNewRefInfo();
    }

    private void getCustomerJobSites() {
        Util.showProgressDialog("Getting job sites..", NewOrderActivity.this);
        APIManager.getInstance().setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                Util.hideProgressDialog();
                if (objAPIResult != null) {
                    try {
                        //if(objAPIResult.getString("StatusCode").equals("Success")) {
                        JSONArray jobSiteJSONArr = objAPIResult.getJSONArray("JobSiteList");
                        jobSiteArr = Util.toList(jobSiteJSONArr);

                        String[] jobSites = new String[jobSiteArr.size()];
                        for(int i = 0; i < jobSiteArr.size(); i++) {
                            HashMap<String, Object> jobSiteObj = jobSiteArr.get(i);
                            jobSites[i] = (String) jobSiteObj.get("Address");
                        }
                        ArrayAdapter<String> jobSiteAdapter = new ArrayAdapter<String>(NewOrderActivity.this, R.layout.item_spinner, jobSites);
                        jobSiteSpinner.setAdapter(jobSiteAdapter);
                        jobSiteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                HashMap<String, Object> jobSiteObj = jobSiteArr.get(position);
                                jobSiteId = (int)jobSiteObj.get("Id");
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        //}
                    } catch (Exception e) {
                        Util.showToast("Failed and try again", NewOrderActivity.this);
                    }
                } else {
                    Util.showToast("Failed and try again", NewOrderActivity.this);
                }
            }
        });
        APIManager.getInstance().getCustomerJobSites();
    }

    private void saveCustomerOrder() {
        Util.showProgressDialog("Saving order..", NewOrderActivity.this);
        APIManager.getInstance().setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                Util.hideProgressDialog();
                if (objAPIResult != null) {
                    try {
                        if(objAPIResult.getString("ResponseStatus").equals("Success")) {
                            Util.showToast(objAPIResult.getString("ResponseMessage"), NewOrderActivity.this);
                            finish();
                            CustomerOrderAdapter.handler.sendEmptyMessage(MSG_CUSTOMER_ORDER_ADDED);
                        }
                    } catch (Exception e) {
                        Util.showToast("Failed and try again", NewOrderActivity.this);
                    }
                } else {
                    Util.showToast("Failed and try again", NewOrderActivity.this);
                }
            }
        });

        JSONObject object = new JSONObject();
        try {
            object.accumulate("RefNo", newRefNo);
            object.accumulate("CustomerId", UserData.getInstance().customerId);
            object.accumulate("JobSiteId", jobSiteId);
            object.accumulate("Description", description);
            object.accumulate("CreatedById", UserData.getInstance().customerId);
            object.accumulate("DeliveryInstructions", deliveryInstruction);
            object.accumulate("Notes", notes);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        APIManager.getInstance().saveCustomerOrder(object);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.new_job_site:
                Intent intent = new Intent(NewOrderActivity.this, NewJobSiteActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
