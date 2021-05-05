package sjcomputers.com.qualitycooling.Customer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import sjcomputers.com.qualitycooling.DocumentActivity;
import sjcomputers.com.qualitycooling.Global.APIManager;
import sjcomputers.com.qualitycooling.Global.APIManagerCallback;
import sjcomputers.com.qualitycooling.Global.Util;
import sjcomputers.com.qualitycooling.Admin.JobActivity;
import sjcomputers.com.qualitycooling.R;

import static sjcomputers.com.qualitycooling.Global.Util.MSG_CUSTOMER_ORDER_ADDED;

/**
 * Created by RabbitJang on 11/5/2018.
 */

public class CustomerOrderAdapter extends BaseAdapter {
    Activity activity;
    ArrayList<HashMap<String, Object>> orderArr;
    ArrayList<HashMap<String, Object>> searchedOrderArr;
    String targetInNumber = "";
    public static Handler handler;
    int sortByAscIn;
    int sortByAscJobSite;
    int sortByAscDescription;
    int sortByAscStatus;
    int sortByAscPaymentStatus;
    int sortByAscNeededOn;
    int sortByAscCreatedOn;
    boolean showOldOrders;

    @Override
    public int getCount() {
        return searchedOrderArr.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        View vi;
        if(position == 0) {
            vi = layoutInflater.inflate(R.layout.item_customer_order_title, parent, false);
            configureTitleItem(vi);
        }
        else {
            vi = layoutInflater.inflate(R.layout.item_customer_order, parent, false);
            if (position % 2 == 1) {
                vi.setBackgroundColor(Color.WHITE);
            } else {
                vi.setBackgroundColor(Color.LTGRAY);
            }
            configureItem(vi, position);
        }
        return vi;
    }

    private void configureTitleItem(View view) {
        TextView inTv = view.findViewById(R.id.textView1);
        TextView jobSiteTv = view.findViewById(R.id.textView2);
        TextView descTv = view.findViewById(R.id.textView3);
        TextView statusTv = view.findViewById(R.id.textView4);
        TextView paymentStatusTv = view.findViewById(R.id.textView5);
        TextView needOnTv = view.findViewById(R.id.textView6);
        TextView createdOnTv = view.findViewById(R.id.textView7);

        if(sortByAscIn == 1) {
            inTv.setText("Order # (Asc)");
        }
        else if(sortByAscIn == 2){
            inTv.setText("Order # (Desc)");
        }
        else {
            inTv.setText("Order #");
        }

        if(sortByAscJobSite == 1) {
            jobSiteTv.setText("Job Site (Asc)");
        }
        else if(sortByAscJobSite == 2){
            jobSiteTv.setText("Job Site (Desc)");
        }
        else {
            jobSiteTv.setText("Job Site");
        }

        if(sortByAscDescription == 1) {
            descTv.setText("Description (Asc)");
        }
        else if(sortByAscDescription == 2){
            descTv.setText("Description (Desc)");
        }
        else {
            descTv.setText("Description");
        }

        if(sortByAscStatus == 1) {
            statusTv.setText("Status (Asc)");
        }
        else if(sortByAscStatus == 2) {
            statusTv.setText("Status (Desc)");
        }
        else {
            statusTv.setText("Status");
        }

        if(sortByAscPaymentStatus == 1) {
            paymentStatusTv.setText("Payment Status (Asc)");
        }
        else if(sortByAscPaymentStatus == 2){
            paymentStatusTv.setText("Payment Status (Desc)");
        }
        else {
            paymentStatusTv.setText("Payment Status");
        }

        if(sortByAscNeededOn == 1) {
            needOnTv.setText("Needed On (Asc)");
        }
        else if(sortByAscNeededOn == 2) {
            needOnTv.setText("Needed On (Desc)");
        }
        else {
            needOnTv.setText("Needed On");
        }

        if(sortByAscCreatedOn == 1) {
            createdOnTv.setText("Created On (Asc)");
        }
        else if(sortByAscCreatedOn == 2) {
            createdOnTv.setText("Created On (Desc)");
        }
        else {
            createdOnTv.setText("Created On");
        }

        inTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByAscJobSite = 0;
                sortByAscDescription = 0;
                sortByAscStatus = 0;
                sortByAscPaymentStatus = 0;
                sortByAscNeededOn = 0;
                sortByAscCreatedOn = 0;

                if(sortByAscIn == 1) {
                    sortByAscIn = 2;
                }
                else {
                    sortByAscIn = 1;
                }

                if(sortByAscIn == 1) {
                    sortByKey("INNumber", true);
                }
                else {
                    sortByKey("INNumber", false);
                }
            }
        });

        jobSiteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByAscIn = 0;
                sortByAscDescription = 0;
                sortByAscStatus = 0;
                sortByAscPaymentStatus = 0;
                sortByAscNeededOn = 0;
                sortByAscCreatedOn = 0;

                if(sortByAscJobSite == 1) {
                    sortByAscJobSite = 2;
                }
                else {
                    sortByAscJobSite = 1;
                }

                if(sortByAscJobSite == 1) {
                    sortByKey("JobSite", true);
                }
                else {
                    sortByKey("JobSite", false);
                }
            }
        });

        descTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByAscIn = 0;
                sortByAscJobSite = 0;
                sortByAscStatus = 0;
                sortByAscPaymentStatus = 0;
                sortByAscNeededOn = 0;
                sortByAscCreatedOn = 0;

                if(sortByAscDescription == 1) {
                    sortByAscDescription = 2;
                }
                else {
                    sortByAscDescription = 1;
                }

                if(sortByAscDescription == 1) {
                    sortByKey("Description", true);
                }
                else {
                    sortByKey("Description", false);
                }
            }
        });

        statusTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByAscIn = 0;
                sortByAscJobSite = 0;
                sortByAscDescription = 0;
                sortByAscPaymentStatus = 0;
                sortByAscNeededOn = 0;
                sortByAscCreatedOn = 0;

                if(sortByAscStatus == 1) {
                    sortByAscStatus = 2;
                }
                else {
                    sortByAscStatus = 1;
                }

                if(sortByAscStatus == 1) {
                    sortByKey("Status", true);
                }
                else {
                    sortByKey("Status", false);
                }
            }
        });

        paymentStatusTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByAscIn = 0;
                sortByAscJobSite = 0;
                sortByAscDescription = 0;
                sortByAscStatus = 0;
                sortByAscNeededOn = 0;
                sortByAscCreatedOn = 0;

                if(sortByAscPaymentStatus == 1) {
                    sortByAscPaymentStatus = 2;
                }
                else {
                    sortByAscPaymentStatus = 1;
                }

                if(sortByAscPaymentStatus == 1) {
                    sortByKey("PaymentStatus", true);
                }
                else {
                    sortByKey("PaymentStatus", false);
                }
            }
        });

        needOnTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByAscIn = 0;
                sortByAscJobSite = 0;
                sortByAscDescription = 0;
                sortByAscStatus = 0;
                sortByAscPaymentStatus = 0;
                sortByAscCreatedOn = 0;

                if(sortByAscNeededOn == 1) {
                    sortByAscNeededOn = 2;
                }
                else {
                    sortByAscNeededOn = 1;
                }

                if(sortByAscNeededOn == 1) {
                    sortByKey("DeliveryDate", true);
                }
                else {
                    sortByKey("DeliveryDate", false);
                }
            }
        });

        createdOnTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByAscIn = 0;
                sortByAscJobSite = 0;
                sortByAscDescription = 0;
                sortByAscStatus = 0;
                sortByAscPaymentStatus = 0;
                sortByAscNeededOn = 0;

                if(sortByAscCreatedOn == 1) {
                    sortByAscCreatedOn = 2;
                }
                else {
                    sortByAscCreatedOn = 1;
                }

                if(sortByAscCreatedOn == 1) {
                    sortByKey("CreatedDate", true);
                }
                else {
                    sortByKey("CreatedDate", false);
                }
            }
        });
    }

    private void sortByKey(final String key, boolean asc) {
        if(asc) {
            Collections.sort(searchedOrderArr, new Comparator<HashMap<String, Object>>() {
                @Override
                public int compare(HashMap<String, Object> lhs, HashMap<String, Object> rhs) {
                    return ((String)lhs.get(key)).compareTo((String)rhs.get(key));
                }
            });
        }
        else {
            Collections.sort(searchedOrderArr, new Comparator<HashMap<String, Object>>() {
                @Override
                public int compare(HashMap<String, Object> lhs, HashMap<String, Object> rhs) {
                    return ((String)rhs.get(key)).compareTo((String)lhs.get(key));
                }
            });
        }
        notifyDataSetChanged();
    }

    private void configureItem(View view, int position) {
        final HashMap<String, Object> orderObj = searchedOrderArr.get(position - 1);
        TextView inTv = view.findViewById(R.id.textView1);
        TextView jobSiteTv = view.findViewById(R.id.textView2);
        TextView descTv = view.findViewById(R.id.textView3);
        TextView statusTv = view.findViewById(R.id.textView4);
        TextView paymentStatusTv = view.findViewById(R.id.textView5);
        TextView needOnTv = view.findViewById(R.id.textView6);
        TextView createdOnTv = view.findViewById(R.id.textView7);
        inTv.setText((String)orderObj.get("INNumber"));
        jobSiteTv.setText(String.format("%s", orderObj.get("JobSite")));
        descTv.setText((String)orderObj.get("Description"));
        statusTv.setText((String)orderObj.get("Status"));
        paymentStatusTv.setText(String.format("%s", orderObj.get("PaymentStatus")));
        needOnTv.setText(String.format("%s", orderObj.get("DeliveryDate")));
        createdOnTv.setText(String.format("%s", orderObj.get("CreatedDate")));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int orderId = (int) orderObj.get("OrderID");
                Intent intent = new Intent(activity, CustomerDocumentActivity.class);
                CustomerDocumentActivity.orderId = orderId;
                DocumentActivity.documentType = 2;
                activity.startActivity(intent);
            }
        });
    }

    public CustomerOrderAdapter(Activity activity) {
        initValue(activity);
        getOrders();
        configureSearch();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == MSG_CUSTOMER_ORDER_ADDED) {
                    getOrders();
                }
            }
        };

        CustomerOrderActivity.showOldOrdersCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                initSortKeys();
                showOldOrders = isChecked;
                searchItem();
            }
        });
    }

    private void initValue(Activity activity) {
        this.activity = activity;
    }

    private void initSortKeys() {
        sortByAscIn = 1;
        sortByAscJobSite = 0;
        sortByAscDescription = 0;
        sortByAscStatus = 0;
        sortByAscPaymentStatus = 0;
        sortByAscNeededOn = 0;
        sortByAscCreatedOn = 0;
    }

    public void getOrders() {
        initSortKeys();

        orderArr = new ArrayList<>();
        searchedOrderArr = new ArrayList<>();
        notifyDataSetChanged();

        Util.showProgressDialog("Getting orders..", activity);
        APIManager.getInstance().setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                Util.hideProgressDialog();
                if (objAPIResult != null) {
                    try {
                        //if(objAPIResult.getString("StatusCode").equals("Success")) {
                        JSONArray orderJSONArr = objAPIResult.getJSONArray("Orders");
                        orderArr = Util.toList(orderJSONArr);
                        for(int i = 0; i < orderArr.size(); i++) {
                            HashMap<String, Object> orderObj = orderArr.get(i);
                            if(orderObj.get("DeliveryDate").equals(JSONObject.NULL)) {
                                orderObj.put("DeliveryDate", "");
                            }
                        }

                        searchItem();
                        notifyDataSetChanged();
                    } catch (Exception e) {
                        Util.showToast("Failed and try again", activity);
                    }
                } else {
                    Util.showToast("Failed and try again", activity);
                }
            }
        });
        APIManager.getInstance().getCustomerOrders();
    }

    public void configureSearch() {
        CustomerOrderActivity.searchInEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!JobActivity.isLive) {
                    targetInNumber = CustomerOrderActivity.searchInEt.getText().toString();
                    searchItem();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void searchItem() {
        if(searchedOrderArr.size() > 0)
            searchedOrderArr.clear();

        for(int i = 0; i < orderArr.size(); i++) {
            HashMap<String, Object> info = orderArr.get(i);
            boolean orderShouldAdd = true;
            if(!showOldOrders) {
                String status = (String) info.get("Status");
                if(status.equals("Picked Up") || status.equals("Delivered") || status.equals("Cancelled")) {
                    orderShouldAdd = false;
                }
            }

            //search by IN #
            String inContent = (String)info.get("INNumber");
            int isInFound = inContent.indexOf(targetInNumber);
            //search by Customer

            if(isInFound != -1 && orderShouldAdd)
                searchedOrderArr.add(info);
        }

        notifyDataSetChanged();
    }
}
