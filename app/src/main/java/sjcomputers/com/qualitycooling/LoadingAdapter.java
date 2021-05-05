package sjcomputers.com.qualitycooling;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import sjcomputers.com.qualitycooling.Adapters.LoadingModel;
import sjcomputers.com.qualitycooling.Global.APIManager;
import sjcomputers.com.qualitycooling.Global.APIManagerCallback;
import sjcomputers.com.qualitycooling.Global.Util;

public class LoadingAdapter extends BaseAdapter {
    Activity activity;
    public ArrayList<HashMap<String, Object>> itemArr;

    @Override
    public int getCount() {
        return itemArr.size();
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
        LayoutInflater inflater = activity.getLayoutInflater();
        View vi = inflater.inflate(R.layout.item_load, parent, false);
        configureItem(vi, position);
        return vi;
    }

    private void configureItem(View view, int position) {
        final HashMap<String, Object> itemObj = itemArr.get(position);

        TextView customerTv = view.findViewById(R.id.textView1);
        TextView inTv = view.findViewById(R.id.textView2);
        TextView jobSiteTv = view.findViewById(R.id.textView3);
        TextView jobAddressTv = view.findViewById(R.id.textView4);
        TextView itemNameTv = view.findViewById(R.id.textView5);
        TextView pieceTv = view.findViewById(R.id.textView6);
        CheckBox loadCb = view.findViewById(R.id.checkBox3);
        customerTv.setText(String.format("Customer: %s", itemObj.get("Customer")));
        inTv.setText(String.format("IN #: %s", itemObj.get("INNumber")));
        jobSiteTv.setText(String.format("Job Site: %s", itemObj.get("JobSite")));
        jobAddressTv.setText(String.format("Job Site Address: %s", itemObj.get("JobSiteAddress")));
        itemNameTv.setText(String.format("Item Name: %s", itemObj.get("ItemName")));
        pieceTv.setText(String.format("Piece #: %s", itemObj.get("PieceNo")));

        String loaded = String.valueOf(itemObj.get("Loaded"));
        //loadCb.setChecked(loaded == 1 ? true : false);
        if (loaded.equals("1")) {
            loadCb.setChecked(true);
        }
        loadCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkOrUncheck(itemObj, isChecked);
            }
        });
    }

    private void checkOrUncheck(final HashMap<String, Object> itemObj, final boolean isChecked) {
        final int orderItemId = (int) itemObj.get("OrderItemId");

        Util.showProgressDialog("Loading..", activity);
        APIManager apiManager = new APIManager();
        apiManager.setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                Util.hideProgressDialog();
                if (objAPIResult != null) {
                    try {
                        if (objAPIResult.getString("Status").equals("Success")) {
                            for (int i = 0; i < itemArr.size(); i++) {
                                HashMap<String, Object> item = itemArr.get(i);
                                if ((int) item.get("OrderItemId") == orderItemId) {
                                    item.put("Loaded", isChecked ? 1 : 0);
                                }
                            }

                            notifyDataSetChanged();
                        } else {
                            Util.showToast(objAPIResult.getString("Message"), activity);
                        }
                    } catch (Exception e) {
                        Util.showToast("Failed and try again", activity);
                    }
                } else {
                    Util.showToast("Failed and try again", activity);
                }

            }
        });
        apiManager.checkOrUncheck(orderItemId, isChecked ? 1 : 0);
    }

    public LoadingAdapter(ArrayList<LoadingModel> loadingModelArrayList, Activity activity) {
        this.activity = activity;
        itemArr = new ArrayList<>();
    }

    public void addItem(HashMap<String, Object> itemObj) {
        itemArr.add(itemObj);
        notifyDataSetChanged();
    }
}
