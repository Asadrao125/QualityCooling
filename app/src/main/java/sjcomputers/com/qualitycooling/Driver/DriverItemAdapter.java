package sjcomputers.com.qualitycooling.Driver;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import sjcomputers.com.qualitycooling.Admin.OrderItemActivity;
import sjcomputers.com.qualitycooling.Global.APIManager;
import sjcomputers.com.qualitycooling.Global.APIManagerCallback;
import sjcomputers.com.qualitycooling.Global.Util;
import sjcomputers.com.qualitycooling.R;

import static sjcomputers.com.qualitycooling.Global.Util.MSG_ORDER_ITEMS_MARK_CHANGED;
import static sjcomputers.com.qualitycooling.Global.Util.MSG_ORDER_ITEMS_MARK_COMPLETED;

public class DriverItemAdapter extends BaseAdapter {
    Activity activity;
    public static ArrayList<HashMap<String, Object>> itemOrderList;
    public ArrayList<HashMap<String, Object>> searchedItemOrderList;

    public static JSONArray itemOrderJSONArr;
    public JSONArray searchedItemOrderJSONArr;

    @Override
    public int getCount() {
        return searchedItemOrderList.size() + 1;
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
        if (position == 0) {
            vi = layoutInflater.inflate(R.layout.item_order_list_title, parent, false);
        } else {
            vi = layoutInflater.inflate(R.layout.item_order_list, parent, false);
            if (position % 2 == 1) {
                vi.setBackgroundColor(Color.WHITE);

            } else {
                vi.setBackgroundColor(Color.LTGRAY);
            }
            configureItem(vi, position);
        }
        return vi;
    }

    public void configureItem(View vi, final int position) {
        TextView pieceNoTv = (TextView) vi.findViewById(R.id.item_list_piece_no);
        TextView nameTv = (TextView) vi.findViewById(R.id.item_list_name);
        TextView quantityTv = (TextView) vi.findViewById(R.id.item_list_qty);
        TextView widthTv = (TextView) vi.findViewById(R.id.item_list_width);
        TextView depthTv = (TextView) vi.findViewById(R.id.item_list_depth);
        TextView lengthTv = (TextView) vi.findViewById(R.id.item_list_length);
        TextView descTv = (TextView) vi.findViewById(R.id.item_list_desc);
        TextView linerTv = (TextView) vi.findViewById(R.id.item_list_liner);
        TextView vanesTv = (TextView) vi.findViewById(R.id.item_list_vanes);
        TextView damperTv = (TextView) vi.findViewById(R.id.item_list_damper);

        final CheckBox loadedCb = (CheckBox) vi.findViewById(R.id.loaded_cb);
        final CheckBox deliveredCb = (CheckBox) vi.findViewById(R.id.delivered_cb);
        final CheckBox completeCb = (CheckBox) vi.findViewById(R.id.complete_cb);

        final HashMap<String, Object> itemOrderObj = searchedItemOrderList.get(position - 1);
        final boolean isLoaded = (boolean) itemOrderObj.get("LoadedInTruck");
        final boolean isDelivered = (boolean) itemOrderObj.get("Delivered");
        final boolean isCompleted = (boolean) itemOrderObj.get("IsCompleted");

        pieceNoTv.setText(String.format("%s", itemOrderObj.get("PieceNo")));
        nameTv.setText(itemOrderObj.get("Name").toString());
        quantityTv.setText(itemOrderObj.get("Quantity").toString());
        widthTv.setText(String.format("%s", itemOrderObj.get("Width")));
        depthTv.setText(String.format("%s", itemOrderObj.get("Depth")));
        lengthTv.setText(String.format("%s", itemOrderObj.get("Length")));
        descTv.setText(String.format("%s", itemOrderObj.get("Description")));
        linerTv.setText(String.format("%s", itemOrderObj.get("Liner")));
        vanesTv.setText(String.format("%s", itemOrderObj.get("Vanes")));
        damperTv.setText(String.format("%s", itemOrderObj.get("Damper")));

        completeCb.setChecked(isCompleted);
        completeCb.setEnabled(false);

        loadedCb.setChecked(isLoaded);
        loadedCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    JSONObject itemOrderJSONObj = searchedItemOrderJSONArr.getJSONObject(position - 1);
                    itemOrderJSONObj.put("LoadedInTruck", isChecked);
                    itemOrderObj.put("LoadedInTruck", isChecked);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        deliveredCb.setChecked(isDelivered);
        deliveredCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    JSONObject itemOrderJSONObj = searchedItemOrderJSONArr.getJSONObject(position - 1);
                    itemOrderJSONObj.put("Delivered", isChecked);
                    itemOrderObj.put("Delivered", isChecked);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public DriverItemAdapter(Activity activity) {
        this.activity = activity;
        getOrderItems(0);
    }

    public void getOrderItems(final int itemStatus) {
        itemOrderList = new ArrayList<>();
        searchedItemOrderList = new ArrayList<>();
        itemOrderJSONArr = new JSONArray();
        searchedItemOrderJSONArr = new JSONArray();

        APIManager.getInstance().setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                Util.hideProgressDialog();
                if (objAPIResult != null) {
                    try {
                        //if(objAPIResult.getString("StatusCode").equals("Success")) {
                        Log.d("getOrderItems", "APICallback: " + objAPIResult);
                        itemOrderJSONArr = objAPIResult.getJSONArray("Items");
                        itemOrderList = Util.toList(itemOrderJSONArr);
                        //int j = 0;
                        for (int i = 0; i < itemOrderJSONArr.length(); i++) {
                            DriverItemActivity.tvCount.setText("Count: " + itemOrderJSONArr.length());
                            JSONObject itemOrderJSONObj = itemOrderJSONArr.getJSONObject(i);
                            String pieceNo = itemOrderJSONObj.getString("PieceNo");
                            /*if (pieceNo != null && !pieceNo.isEmpty() && !pieceNo.equals("null")) {
                                j++;
                            }*/
                            searchedItemOrderJSONArr.put(itemOrderJSONObj);
                        }

                        //DriverItemActivity.tvActualCount.setText("Actual Count: " + j);

                        int itemCount = 0;
                        for (int i = 0; i < itemOrderList.size(); i++) {
                            HashMap<String, Object> itemOrderObj = itemOrderList.get(i);
                            searchedItemOrderList.add(itemOrderObj);

                            String itemName = (String) itemOrderObj.get("Name");
                            if (!itemName.equals("Slip") && !itemName.equals("Drive")) {
                                itemCount++;
                            }
                        }

                        //OrderItemActivity.countTv.setText(String.format("Count: %d", itemCount));
                        //notifyDataSetChanged();

                        showItemsWithStatus(itemStatus);
                        /*} else {
                            Util.showToast("Failed and try again", activity);
                        }*/
                    } catch (Exception e) {
                        Util.showToast("Failed and try again", activity);
                    }
                } else {
                    Util.showToast("Failed and try again", activity);
                }
            }
        });

        Util.showProgressDialog("Getting items..", activity);
        APIManager.getInstance().orderItems(DriverItemActivity.orderID);
    }

    public void showItemsWithStatus(int status) {
        int j = 0;
        searchedItemOrderJSONArr = new JSONArray();
        searchedItemOrderList = new ArrayList<>();

        for (int i = 0; i < itemOrderJSONArr.length(); i++) {
            JSONObject itemOrderJSONObj = null;
            try {
                itemOrderJSONObj = itemOrderJSONArr.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (status == 0) {
                searchedItemOrderJSONArr.put(itemOrderJSONObj);
                searchedItemOrderList.add(itemOrderList.get(i));

                String pieceNo = null;
                try {
                    pieceNo = itemOrderJSONObj.getString("PieceNo");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (pieceNo != null && !pieceNo.isEmpty() && !pieceNo.equals("null")) {
                    j++;
                }
                DriverItemActivity.tvCount.setText("Count: " + searchedItemOrderJSONArr.length());

            } else if (status == 1) {
                try {
                    if (!itemOrderJSONObj.getBoolean("IsCompleted")) {
                        searchedItemOrderJSONArr.put(itemOrderJSONObj);
                        searchedItemOrderList.add(itemOrderList.get(i));

                        DriverItemActivity.tvCount.setText("Count: " + searchedItemOrderJSONArr.length());

                        String pieceNo = itemOrderJSONObj.getString("PieceNo");
                        if (pieceNo != null && !pieceNo.isEmpty() && !pieceNo.equals("null")) {
                            j++;
                        }

                    }
                } catch (JSONException e) {

                }
            } else {
                try {
                    if (itemOrderJSONObj.getBoolean("IsCompleted")) {
                        searchedItemOrderJSONArr.put(itemOrderJSONObj);
                        searchedItemOrderList.add(itemOrderList.get(i));

                        DriverItemActivity.tvCount.setText("Count: " + searchedItemOrderJSONArr.length());

                        String pieceNo = itemOrderJSONObj.getString("PieceNo");
                        if (pieceNo != null && !pieceNo.isEmpty() && !pieceNo.equals("null")) {
                            j++;
                        }

                    }
                } catch (JSONException e) {

                }
            }
        }
        DriverItemActivity.tvActualCount.setText("Actual Count: " + j);
        notifyDataSetChanged();
    }
}
