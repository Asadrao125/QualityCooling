package sjcomputers.com.qualitycooling.Driver;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.shehabic.droppy.DroppyClickCallbackInterface;
import com.shehabic.droppy.DroppyMenuItem;
import com.shehabic.droppy.DroppyMenuPopup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


import androidx.core.app.ActivityCompat;
import sjcomputers.com.qualitycooling.Global.APIManager;
import sjcomputers.com.qualitycooling.Global.APIManagerCallback;
import sjcomputers.com.qualitycooling.Global.UserData;
import sjcomputers.com.qualitycooling.Global.Util;
import sjcomputers.com.qualitycooling.R;

import static sjcomputers.com.qualitycooling.Global.Util.MSG_ORDER_DELIVERED;
import static sjcomputers.com.qualitycooling.Global.Util.MSG_REFRESH_ORDER;

public class DriverOrderAdapter extends BaseAdapter {
    Activity activity;
    public static ArrayList<HashMap<String, Object>> driverOrderArry;
    ArrayList<HashMap<String, Object>> searchedDriverOrderArr;
    String targetInNumber = "";
    String targetCustomer = "";
    public static Handler handler;
    public String vehicleId;

    int curIndex;
    int readCount = 30;
    int lastIndex;
    boolean isFirstRead;
    boolean isFirstSpinnerSelect;

    public DriverOrderAdapter(Activity activity) {
        this.activity = activity;
        initValue();
        //getDriverOrders();
        configureSearch();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MSG_ORDER_DELIVERED) {
                    getDriverOrders();
                } else if (msg.what == MSG_REFRESH_ORDER) {

                    isFirstRead = true;
                    isFirstSpinnerSelect = true;

                    initValue();
                    getDriverOrders();

                }
            }
        };

        DriverOrderActivity.prevBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curIndex != 0) {
                    curIndex--;
                    //getOrders();
                    DriverOrderActivity.pageSpinner.setSelection(curIndex);
                }
            }
        });

        DriverOrderActivity.nextBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curIndex != lastIndex) {
                    curIndex++;
                    //getOrders();
                    DriverOrderActivity.pageSpinner.setSelection(curIndex);
                }
            }
        });

        DriverOrderActivity.spinner5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                vehicleId = DriverOrderActivity.vehicleIdList.get(position);
                getDriverOrders();
                Util.hideProgressDialog();
                Log.d("kknkfkjjll", "onItemSelected: " + vehicleId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public int getCount() {
        return searchedDriverOrderArr.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return searchedDriverOrderArr.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        View vi;
        if (position == 0) {
            vi = layoutInflater.inflate(R.layout.item_driver_order_title, parent, false);
            vi.setLayoutParams(new AbsListView.LayoutParams(-1, 1));
            vi.setVisibility(View.GONE);
        } else {
            vi = layoutInflater.inflate(R.layout.item_driver_order, parent, false);
            if (position % 2 == 1) {
                vi.setBackgroundColor(Color.WHITE);
            } else {
                vi.setBackgroundColor(Color.LTGRAY);
            }
            configureDriverOrderItem(vi, position);
        }

        return vi;
    }

    private void initValue() {
        driverOrderArry = new ArrayList<>();
        searchedDriverOrderArr = new ArrayList<>();
        curIndex = 0;
        isFirstRead = true;
        isFirstSpinnerSelect = true;
    }

    private void configureDriverOrderItem(View vi, final int position) {
        final HashMap orderObj = searchedDriverOrderArr.get(position - 1);

        TextView customerTv = (TextView) vi.findViewById(R.id.d_textView1);
        TextView inTv = (TextView) vi.findViewById(R.id.d_textView2);
        TextView jobsiteTv = (TextView) vi.findViewById(R.id.d_textView4);
        TextView pickup_addressTv = (TextView) vi.findViewById(R.id.d_textView6);
        TextView dropoff_addressTv = (TextView) vi.findViewById(R.id.d_textView7);
        TextView descriptionTv = (TextView) vi.findViewById(R.id.d_textView8);
        TextView notesTv = (TextView) vi.findViewById(R.id.d_textView9);
        TextView phoneTv = vi.findViewById(R.id.d_textView10);
        TextView vehicleTv = vi.findViewById(R.id.d_textView11);
        TextView vehicleName = vi.findViewById(R.id.d_textView12);
        ImageView clipBoardIv = vi.findViewById(R.id.clipboard_iv);
        Button pickBt = (Button) vi.findViewById(R.id.pick_bt);

        final String status = (String) orderObj.get("Status");
        pickBt.setText(status);

        pickBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(status.equals("Ready for Delivery")) {
                    deliverOrder(position - 1);
                }
                else if(status.equals("On Delivery")) {
                    SignatureActivity.orderId = (int) orderObj.get("OrderID");
                    Intent intent = new Intent(activity, SignatureActivity.class);
                    activity.startActivity(intent);
                }*/

                SignatureActivity.orderId = (int) orderObj.get("OrderID");
                SignatureActivity.driverOrderId = (String) orderObj.get("RefNo");
                DriverOrderActivity.orderId = (int) orderObj.get("OrderID");

                Intent intent = new Intent(activity, SignatureActivity.class);
                activity.startActivity(intent);
            }
        });

        vehicleTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignatureActivity.orderId = (int) orderObj.get("OrderID");
                SignatureActivity.driverOrderId = (String) orderObj.get("RefNo");
                DriverOrderActivity.orderId = (int) orderObj.get("OrderID");

                Intent intent = new Intent(activity, SignatureActivity.class);
                activity.startActivity(intent);
            }
        });

        clipBoardIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setClipboard(activity, orderObj.get("DropoffAddress").toString());
                Util.showToast("Drop off address is copied to clipboard!", activity);
            }
        });

        customerTv.setText(orderObj.get("CustomerName").toString());

        String s = orderObj.get("INNumber").toString();
        s = s.replaceFirst("^0*", "");

        inTv.setText(s);
        jobsiteTv.setText(orderObj.get("JobSite").toString());
        pickup_addressTv.setText(orderObj.get("PickupAddress").toString());
        dropoff_addressTv.setText(orderObj.get("DropoffAddress").toString());
        descriptionTv.setText(orderObj.get("Description").toString());
        notesTv.setText(orderObj.get("Notes").toString());
        vehicleName.setText(orderObj.get("Vehicle").toString());
        String contactNo = orderObj.get("ContactNo").toString();
        phoneTv.setText(contactNo);
        /*vehicleTv.setText(orderObj.get("Vehicle").toString());*/
        vehicleTv.setText(orderObj.get("INNumber").toString());
        final String[] phoneNumbers = contactNo.split(",");
        DroppyMenuPopup.Builder droppyBuilder = new DroppyMenuPopup.Builder(activity, phoneTv);
        for (int i = 0; i < phoneNumbers.length; i++) {
            String phoneNumber = phoneNumbers[i];
            droppyBuilder.addMenuItem(new DroppyMenuItem(phoneNumber));
        }
        droppyBuilder.setOnClick(new DroppyClickCallbackInterface() {
            @Override
            public void call(View v, int id) {
                String phoneNumber = phoneNumbers[id];
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Util.showToast("Please check call permission", activity);
                    return;
                }
                activity.startActivity(intent);
            }
        });
        droppyBuilder.build();

    }

    private void setClipboard(Context context, String text) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
    }

    private void deliverOrder(final int index) {
        APIManager.getInstance().setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                Util.hideProgressDialog();
                if (objAPIResult != null) {
                    try {
                        //if(objAPIResult.getString("StatusCode").equals("Success")) {
                        getDriverOrders();
                        //}
                    } catch (Exception e) {
                        Util.showToast("Failed and try again", activity);
                    }
                } else {
                    Util.showToast("Failed and try again", activity);
                }
            }
        });

        Util.showProgressDialog("Delivering order..", activity);
        APIManager.getInstance().deliverOrder(Integer.parseInt(driverOrderArry.get(index).get("OrderID").toString()), UserData.getInstance().userId);
    }

    /*private void showMap(int index) {
        Intent intent = new Intent(activity, DriverPositionActivity.class);
        HashMap<String, Object> driverOrder = driverOrderArry.get(index);

        intent.putExtra("drop_latitude", driverOrder.get("DropoffLat").toString());
        intent.putExtra("drop_longitude",driverOrder.get("DropoffLong").toString());
        intent.putExtra("pick_latitude", driverOrder.get("PickupLat").toString());
        intent.putExtra("pick_longitude", driverOrder.get("PickupLong").toString());
        intent.putExtra("innumber", driverOrder.get("INNumber").toString());
        intent.putExtra("customer", driverOrder.get("CustomerName").toString());
        intent.putExtra("orderId", driverOrder.get("OrderID").toString());
        activity.startActivity(intent);
    }*/

    public void refreshOrders() {
        initValue();
        DriverOrderActivity.driverSearchInEt.setText("");
        DriverOrderActivity.driverSearchCusEt.setText("");
        getDriverOrders();
    }

    public void getDriverOrders() {
        Handler handler = new Handler();
        ProgressDialog pd = new ProgressDialog(activity);
        pd.setMessage("Getting orders..");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pd.dismiss();
            }
        }, 1000);

        APIManager.getInstance().setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                //Util.hideProgressDialog();
                if (objAPIResult != null) {
                    try {
                        //if(objAPIResult.getString("StatusCode").equals("Success")) {
                        JSONArray orderJSONArr = objAPIResult.getJSONArray("Orders");
                        DriverOrderActivity.tvCount.setText("Count: " + orderJSONArr.length());
                        DriverOrderActivity.tvVisibleCount.setText("Visible Count: " + orderJSONArr.length());
                        driverOrderArry = Util.toList(orderJSONArr);

                        int totalCount = objAPIResult.getInt("Count");
                        if (totalCount % readCount == 0) {
                            lastIndex = totalCount / readCount - 1;
                        } else {
                            lastIndex = totalCount / readCount;
                        }

                        if (isFirstRead) {
                            String[] pages = new String[lastIndex + 1];
                            for (int i = 0; i < lastIndex + 1; i++) {
                                pages[i] = String.valueOf(i + 1);
                            }

                            final ArrayAdapter<String> pageSpinnerAdapter = new ArrayAdapter<String>(activity, R.layout.item_spinner, pages);
                            DriverOrderActivity.pageSpinner.setAdapter(pageSpinnerAdapter);
                            DriverOrderActivity.pageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    if (!isFirstSpinnerSelect) {
                                        curIndex = position;
                                        getDriverOrders();
                                    }

                                    isFirstSpinnerSelect = false;
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }

                        isFirstRead = false;

                        searchedDriverOrderArr = (ArrayList<HashMap<String, Object>>) driverOrderArry.clone();
                        notifyDataSetChanged();
                        if (driverOrderArry.size() == 0) {
                            Util.showToast("No order to pick up", activity);
                        }
                        /*} else {
                            Util.showToast("Failed and try again", activity);
                        }*/
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Util.showToast("Failed and try again", activity);
                }
            }
        });

        //Util.showProgressDialog("Getting orders..", activity);
        Log.d("lldkkhdkhd", "run: \n" + DriverOrderActivity.status + "\n" + curIndex * readCount + "\n" + readCount + "\n" + DriverOrderActivity.driverSearchInEt.getText().toString() + "\n" + vehicleId);

        if (vehicleId == null || vehicleId.isEmpty()) {
            vehicleId = "0";
        }

        Log.d("statusmjnj", "run: " + DriverOrderActivity.status);
        APIManager.getInstance().getDriverOrders(DriverOrderActivity.status, curIndex * readCount, readCount, DriverOrderActivity.driverSearchInEt.getText().toString(), vehicleId);
    }

    public void configureSearch() {
        DriverOrderActivity.driverSearchCusEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                targetCustomer = DriverOrderActivity.driverSearchCusEt.getText().toString();
                searchItem();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void searchItem() {
        if (searchedDriverOrderArr.size() > 0)
            searchedDriverOrderArr.clear();

        for (int i = 0; i < driverOrderArry.size(); i++) {
            HashMap<String, Object> info = driverOrderArry.get(i);
            //search by IN #
            String inContent = (String) info.get("INNumber");
            int isInFound = inContent.indexOf(targetInNumber);
            //search by Customer
            String customerContent = (String) info.get("CustomerName");
            String lowerCustomerContent = customerContent.toLowerCase();
            int isCustomerFound = lowerCustomerContent.indexOf(targetCustomer);

            if (isInFound != -1 && isCustomerFound != -1)
                searchedDriverOrderArr.add(info);
        }
        notifyDataSetChanged();
    }
}
