package sjcomputers.com.qualitycooling.Admin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import sjcomputers.com.qualitycooling.Global.APIManager;
import sjcomputers.com.qualitycooling.Global.APIManagerCallback;
import sjcomputers.com.qualitycooling.Global.UserData;
import sjcomputers.com.qualitycooling.Global.Util;
import sjcomputers.com.qualitycooling.R;

import static sjcomputers.com.qualitycooling.Global.Util.MSG_ORDER_SEARCHED;
import static sjcomputers.com.qualitycooling.Global.Util.MSG_REFRESH_ORDER;

/**
 * Created by RabbitJang on 5/26/2018.
 */

public class OrderAdapter extends BaseAdapter {
    Activity activity;
    ArrayList<HashMap<String, Object>> orderArr;
    ArrayList<HashMap<String, Object>> searchedOrderArr;
    String targetInNumber = "";
    String targetCustomer = "";
    public static Handler handler;
    int sortByAscIn;
    int sortByAscCustomer;
    int sortByAscJobSite;
    int sortByAscPo;
    int sortByAscDescription;
    int sortByAscStatus;
    int sortByAscColor;
    int curIndex;
    int readCount = 30;
    int lastIndex;

    boolean isFirstRead;
    boolean isFirstSpinnerSelect;
    boolean isGettingOrders;

    @Override
    public int getCount() {
        return searchedOrderArr.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return searchedOrderArr.get(position);
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
            vi = layoutInflater.inflate(R.layout.item_order_title, parent, false);
            configureTitleItem(vi);
        } else {
            vi = layoutInflater.inflate(R.layout.item_order, parent, false);
            if (position % 2 == 1) {
                vi.setBackgroundColor(Color.WHITE);
            } else {
                vi.setBackgroundColor(Color.LTGRAY);
            }

            final HashMap orderObj = searchedOrderArr.get(position - 1);
            vi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String customer = (String) orderObj.get("CustomerName");
                    String jobSite = String.format("%s", orderObj.get("JobSite"));
                    String status = (String) orderObj.get("Status");
                    String title = (String) (orderObj.get("INNumber").toString());
                    int orderID = Integer.parseInt(orderObj.get("OrderID").toString());
                    //String dueDate = (String)(orderObj.get("DeliveryDate")).toString();

                    Intent intent = new Intent(activity, OrderItemActivity.class);
                    Bundle b = new Bundle();
                    b.putString("Customer", customer);
                    b.putString("Jobsite", jobSite);
                    b.putString("Status", status);
                    b.putString("Title", title);
                    b.putInt("OrderID", orderID);
                    intent.putExtras(b);
                    activity.startActivity(intent);
                }
            });

            TextView seqTv = (TextView) vi.findViewById(R.id.textView1);
            TextView inTv = (TextView) vi.findViewById(R.id.textView2);
            TextView customerTv = (TextView) vi.findViewById(R.id.textView4);
            TextView jobsiteTv = (TextView) vi.findViewById(R.id.textView5);
            TextView poTv = (TextView) vi.findViewById(R.id.textView6);
            TextView descrtiptionTv = (TextView) vi.findViewById(R.id.textView7);
            final Spinner statusSpinner = (Spinner) vi.findViewById(R.id.status_spinner);
            TextView colorTv = (TextView) vi.findViewById(R.id.textView10);
            TextView textView11 = vi.findViewById(R.id.textView11);

            seqTv.setText(Integer.toString(position + curIndex * readCount));
            inTv.setText(orderObj.get("INNumber").toString());
            customerTv.setText(orderObj.get("CustomerName").toString());
            jobsiteTv.setText(orderObj.get("JobSite").toString());
            poTv.setText(orderObj.get("PONumber").toString());
            descrtiptionTv.setText(orderObj.get("Description").toString());
            textView11.setText(orderObj.get("DeliveryDate").toString());

            int originalStatusIndex = 0;
            for (int i = 0; i < UserData.getInstance().statuses.length; i++) {
                String status = UserData.getInstance().statuses[i];
                if (status.equals(orderObj.get("Status").toString())) {
                    originalStatusIndex = i;
                    break;
                }
            }

            ArrayAdapter<String> statusAdapter = new ArrayAdapter<String>(activity, R.layout.item_spinner, UserData.getInstance().statuses);
            statusSpinner.setAdapter(statusAdapter);
            statusSpinner.setSelection(originalStatusIndex);
            final int finalOriginalStatusIndex = originalStatusIndex;
            statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    int index = statusSpinner.getSelectedItemPosition();
                    int selectedStatusID = Integer.parseInt(UserData.getInstance().statusArray.get(index).get("ID").toString());
                    int orderID = Integer.parseInt(orderObj.get("OrderID").toString());
                    updateOrderStatus(orderID, selectedStatusID, finalOriginalStatusIndex, index);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            colorTv.setText(orderObj.get("Color").toString());
        }
        return vi;
    }

    private void configureTitleItem(View view) {
        TextView inTv = view.findViewById(R.id.textView2);
        TextView customerTv = view.findViewById(R.id.textView3);
        TextView jobSiteTv = view.findViewById(R.id.textView4);
        TextView poTv = view.findViewById(R.id.textView5);
        TextView descTv = view.findViewById(R.id.textView6);
        TextView statusTv = view.findViewById(R.id.textView7);
        TextView colorTv = view.findViewById(R.id.textView9);
        TextView textView11 = view.findViewById(R.id.textView11);

        inTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByAscCustomer = 0;
                sortByAscJobSite = 0;
                sortByAscPo = 0;
                sortByAscDescription = 0;
                sortByAscStatus = 0;
                sortByAscColor = 0;

                if (sortByAscIn == 1) {
                    sortByAscIn = 2;
                } else {
                    sortByAscIn = 1;
                }

                if (sortByAscIn == 1) {
                    sortByKey("INNumber", true);
                } else {
                    sortByKey("INNumber", false);
                }
            }
        });

        customerTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByAscIn = 0;
                sortByAscJobSite = 0;
                sortByAscPo = 0;
                sortByAscDescription = 0;
                sortByAscStatus = 0;
                sortByAscColor = 0;

                if (sortByAscCustomer == 1) {
                    sortByAscCustomer = 2;
                } else {
                    sortByAscCustomer = 1;
                }

                if (sortByAscCustomer == 1) {
                    sortByKey("CustomerName", true);
                } else {
                    sortByKey("CustomerName", false);
                }
            }
        });

        jobSiteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByAscIn = 0;
                sortByAscCustomer = 0;
                sortByAscPo = 0;
                sortByAscDescription = 0;
                sortByAscStatus = 0;
                sortByAscColor = 0;

                if (sortByAscJobSite == 1) {
                    sortByAscJobSite = 2;
                } else {
                    sortByAscJobSite = 1;
                }

                if (sortByAscJobSite == 1) {
                    sortByKey("JobSite", true);
                } else {
                    sortByKey("JobSite", false);
                }
            }
        });

        poTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByAscIn = 0;
                sortByAscCustomer = 0;
                sortByAscJobSite = 0;
                sortByAscDescription = 0;
                sortByAscStatus = 0;
                sortByAscColor = 0;

                if (sortByAscPo == 1) {
                    sortByAscPo = 2;
                } else {
                    sortByAscPo = 1;
                }

                if (sortByAscPo == 1) {
                    sortByKey("PONumber", true);
                } else {
                    sortByKey("PONumber", false);
                }
            }
        });

        descTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByAscIn = 0;
                sortByAscCustomer = 0;
                sortByAscJobSite = 0;
                sortByAscPo = 0;
                sortByAscStatus = 0;
                sortByAscColor = 0;

                if (sortByAscDescription == 1) {
                    sortByAscDescription = 2;
                } else {
                    sortByAscDescription = 1;
                }

                if (sortByAscDescription == 1) {
                    sortByKey("Description", true);
                } else {
                    sortByKey("Description", false);
                }
            }
        });

        statusTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByAscIn = 0;
                sortByAscCustomer = 0;
                sortByAscJobSite = 0;
                sortByAscPo = 0;
                sortByAscDescription = 0;
                sortByAscColor = 0;

                if (sortByAscStatus == 1) {
                    sortByAscStatus = 2;
                } else {
                    sortByAscStatus = 1;
                }

                if (sortByAscStatus == 1) {
                    sortByKey("Status", true);
                } else {
                    sortByKey("Status", false);
                }
            }
        });

        colorTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByAscIn = 0;
                sortByAscCustomer = 0;
                sortByAscJobSite = 0;
                sortByAscPo = 0;
                sortByAscDescription = 0;
                sortByAscStatus = 0;

                if (sortByAscColor == 1) {
                    sortByAscColor = 2;
                } else {
                    sortByAscColor = 1;
                }

                if (sortByAscColor == 1) {
                    sortByKey("Color", true);
                } else {
                    sortByKey("Color", false);
                }
            }
        });

        if (sortByAscIn == 1) {
            inTv.setText("Order # (Asc)");
        } else if (sortByAscIn == 2) {
            inTv.setText("Order # (Desc)");
        } else {
            inTv.setText("Order #");
        }

        if (sortByAscCustomer == 1) {
            customerTv.setText("Customer (Asc)");
        } else if (sortByAscCustomer == 2) {
            customerTv.setText("Customer (Desc)");
        } else {
            customerTv.setText("Customer");
        }

        if (sortByAscJobSite == 1) {
            jobSiteTv.setText("Job Site (Asc)");
        } else if (sortByAscJobSite == 2) {
            jobSiteTv.setText("Job Site (Desc)");
        } else {
            jobSiteTv.setText("Job Site");
        }

        if (sortByAscPo == 1) {
            poTv.setText("PO # (Asc)");
        } else if (sortByAscPo == 2) {
            poTv.setText("PO # (Desc)");
        } else {
            poTv.setText("PO #");
        }

        if (sortByAscDescription == 1) {
            descTv.setText("Description (Asc)");
        } else if (sortByAscDescription == 2) {
            descTv.setText("Description (Desc)");
        } else {
            descTv.setText("Description");
        }

        if (sortByAscStatus == 1) {
            statusTv.setText("Status (Asc)");
        } else if (sortByAscStatus == 2) {
            statusTv.setText("Status (Desc)");
        } else {
            statusTv.setText("Status");
        }

        if (sortByAscColor == 1) {
            colorTv.setText("Color (Asc)");
        } else if (sortByAscColor == 2) {
            colorTv.setText("Color (Desc)");
        } else {
            colorTv.setText("Color");
        }

    }

    private void sortByKey(final String key, boolean asc) {
        if (asc) {
            Collections.sort(searchedOrderArr, new Comparator<HashMap<String, Object>>() {
                @Override
                public int compare(HashMap<String, Object> lhs, HashMap<String, Object> rhs) {
                    return ((String) lhs.get(key)).compareTo((String) rhs.get(key));
                }
            });
        } else {
            Collections.sort(searchedOrderArr, new Comparator<HashMap<String, Object>>() {
                @Override
                public int compare(HashMap<String, Object> lhs, HashMap<String, Object> rhs) {
                    return ((String) rhs.get(key)).compareTo((String) lhs.get(key));
                }
            });
        }
        notifyDataSetChanged();
    }

    private void sortByKeyWithInt(final String key, boolean asc) {
        if (asc) {
            Collections.sort(searchedOrderArr, new Comparator<HashMap<String, Object>>() {
                @Override
                public int compare(HashMap<String, Object> lhs, HashMap<String, Object> rhs) {
                    return (String.valueOf(lhs.get(key))).compareTo(String.valueOf(rhs.get(key)));
                }
            });
        } else {
            Collections.sort(searchedOrderArr, new Comparator<HashMap<String, Object>>() {
                @Override
                public int compare(HashMap<String, Object> lhs, HashMap<String, Object> rhs) {
                    return (String.valueOf(rhs.get(key))).compareTo(String.valueOf(lhs.get(key)));
                }
            });
        }
        notifyDataSetChanged();
    }

    private void updateOrderStatus(int orderID, int selectedStatusID, int originalStatusIndex, int index) {
        if (originalStatusIndex == index) {
            return;
        }

        Util.showProgressDialog("Updating status..", activity);
        APIManager apiManager = new APIManager();
        apiManager.setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                Util.hideProgressDialog();
                if (objAPIResult != null) {
                    try {

                    } catch (Exception e) {
                        Util.showToast("Failed and try again", activity);
                    }
                } else {
                    Util.showToast("Failed and try again", activity);
                }
            }
        });
        apiManager.changeOrderStatus(orderID, selectedStatusID);
    }

    public OrderAdapter(final Activity activity, int readCount) {
        this.activity = activity;
        this.readCount = readCount;
        initValue();
        Log.d("AAAAA-----", "OrderAdapter");
        getOrders();
        configureSearch();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MSG_REFRESH_ORDER) {
                    curIndex = 0;
                    JobActivity.searchInEt.setText("");
                    JobActivity.searchCusEt.setText("");
                    if (!JobActivity.isLive) {
                        isFirstRead = true;
                        isFirstSpinnerSelect = true;
                        Log.d("AAAAA-----", "HandlerRefresh");
                        getOrders();
                    }
                } else if (msg.what == MSG_ORDER_SEARCHED) {
                    searchOrders();
                }
            }
        };

        JobActivity.prevBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curIndex != 0) {
                    curIndex--;
                    JobActivity.pageSpinner.setSelection(curIndex);
                }
            }
        });

        JobActivity.nextBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curIndex != lastIndex) {
                    curIndex++;
                    JobActivity.pageSpinner.setSelection(curIndex);
                }
            }
        });
    }

    private void initValue() {
        isGettingOrders = false;
        orderArr = new ArrayList<>();
        searchedOrderArr = new ArrayList<>();
        curIndex = 0;
        isFirstRead = true;
        isFirstSpinnerSelect = true;
    }

    public void searchOrders() {
        String orderNo = JobActivity.searchInEt.getText().toString();
        if (orderNo.equals("")) {
            Util.showToast("Please input valid order #", activity);
            return;
        }

        orderArr = new ArrayList<>();
        searchedOrderArr = new ArrayList<>();
        notifyDataSetChanged();
        hideKeyboard(activity);

        Util.showProgressDialog("Searching orders..", activity);
        APIManager apiManager = new APIManager();
        apiManager.setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                Util.hideProgressDialog();
                if (objAPIResult != null) {
                    try {
                        //if(objAPIResult.getString("StatusCode").equals("Success")) {
                        JSONArray orderJSONArr = objAPIResult.getJSONArray("Orders");
                        orderArr = Util.toList(orderJSONArr);

                        for (int i = 0; i < orderArr.size(); i++) {
                            HashMap<String, Object> orderObj = orderArr.get(i);
                            if (orderObj.get("JobSite").equals(JSONObject.NULL)) {
                                orderObj.put("JobSite", "");
                            }
                            if (orderObj.get("PONumber").equals(JSONObject.NULL)) {
                                orderObj.put("PONumber", "");
                            }
                            if (orderObj.get("Color").equals(JSONObject.NULL)) {
                                orderObj.put("Color", "");
                            }
                        }

                        searchedOrderArr = (ArrayList<HashMap<String, Object>>) orderArr.clone();
                        notifyDataSetChanged();
                        //}
                    } catch (Exception e) {
                        Util.showToast("Failed and try again", activity);
                    }
                } else {
                    Util.showToast("Failed and try again", activity);
                }
            }
        });

        apiManager.searchOrders(orderNo, JobActivity.status);
    }

    public void getOrders() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                Util.showProgressDialog("Getting orders..", activity);
                APIManager apiManager = new APIManager();
                apiManager.setCallback(new APIManagerCallback() {
                    @Override
                    public void APICallback(JSONObject objAPIResult) {
                        isGettingOrders = false;
                        Util.hideProgressDialog();
                        if (objAPIResult != null) {
                            try {
                                //if(objAPIResult.getString("StatusCode").equals("Success")) {
                                JSONArray orderJSONArr = objAPIResult.getJSONArray("Orders");
                                orderArr = Util.toList(orderJSONArr);
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
                                    JobActivity.pageSpinner.setAdapter(pageSpinnerAdapter);
                                    JobActivity.pageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            if (!isFirstSpinnerSelect) {
                                                curIndex = position;
                                                Log.d("AAAAA-----", "PageSpinnerSelec");
                                                getOrders();
                                            }

                                            isFirstSpinnerSelect = false;
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {

                                        }
                                    });
                                }

                                isFirstRead = false;

                                for (int i = 0; i < orderArr.size(); i++) {
                                    HashMap<String, Object> orderObj = orderArr.get(i);
                                    if (orderObj.get("JobSite").equals(JSONObject.NULL)) {
                                        orderObj.put("JobSite", "");
                                    }
                                    if (orderObj.get("PONumber").equals(JSONObject.NULL)) {
                                        orderObj.put("PONumber", "");
                                    }
                                    if (orderObj.get("Color").equals(JSONObject.NULL)) {
                                        orderObj.put("Color", "");
                                    }
                                }


                                searchedOrderArr = (ArrayList<HashMap<String, Object>>) orderArr.clone();
                                notifyDataSetChanged();
                                //}
                            } catch (Exception e) {
                                Util.showToast("Failed and try again", activity);
                            }
                        } else {
                            Util.showToast("Failed and try again", activity);
                        }
                    }
                });
                apiManager.getOrders(JobActivity.status, curIndex * readCount, readCount);
            }
        }, 100);

        isGettingOrders = true;
    }

    public void configureSearch() {
        JobActivity.searchInEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                targetInNumber = JobActivity.searchInEt.getText().toString();
                if (!JobActivity.isLive) {
                    searchItem();
                } else {
                    if (targetInNumber.equals("")) {
                        isFirstRead = true;
                        isFirstSpinnerSelect = true;
                        if (isGettingOrders) {
                            return;
                        }

                        Log.d("AAAAA-----", "TextChanged");
                        getOrders();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        JobActivity.searchCusEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                targetCustomer = JobActivity.searchCusEt.getText().toString();
                searchItem();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void searchItem() {
        if (searchedOrderArr.size() > 0) {
            searchedOrderArr.clear();
        }

        for (int i = 0; i < orderArr.size(); i++) {
            HashMap<String, Object> info = orderArr.get(i);
            //search by Order #
            String inContent = (String) info.get("INNumber");
            int isInFound = inContent.indexOf(targetInNumber);
            //search by Customer
            String customerContent = (String) info.get("CustomerName");
            String lowerCustomerContent = customerContent.toLowerCase();
            int isCustomerFound = lowerCustomerContent.indexOf(targetCustomer);

            if (isInFound != -1 && isCustomerFound != -1)
                searchedOrderArr.add(info);
        }
        notifyDataSetChanged();
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
