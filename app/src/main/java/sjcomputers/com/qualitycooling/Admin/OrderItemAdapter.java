package sjcomputers.com.qualitycooling.Admin;

import android.app.Activity;
import android.graphics.Color;
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

import sjcomputers.com.qualitycooling.Global.APIManager;
import sjcomputers.com.qualitycooling.Global.APIManagerCallback;
import sjcomputers.com.qualitycooling.Global.Util;
import sjcomputers.com.qualitycooling.R;

import static sjcomputers.com.qualitycooling.Global.Util.MSG_ORDER_ITEMS_MARK_CHANGED;
import static sjcomputers.com.qualitycooling.Global.Util.MSG_ORDER_ITEMS_MARK_COMPLETED;

public class OrderItemAdapter extends BaseAdapter {

    private Activity activity;
    public static ArrayList<HashMap<String, Object>> itemOrderList;
    public ArrayList<HashMap<String, Object>> searchedItemOrderList;
    public static JSONArray itemOrderJSONArr;
    public JSONArray searchedItemOrderJSONArr;

    public OrderItemAdapter(Activity activity) {
        this.activity = activity;
        getOrderItems(0);
    }

    @Override
    public int getCount() {
        return searchedItemOrderList.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return searchedItemOrderList.get(position);
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
            vi = layoutInflater.inflate(R.layout.item_order_list_title, parent, false);
        }
        else {
            vi = layoutInflater.inflate(R.layout.item_order_list, parent, false);
            if(position % 2 == 1) {
                vi.setBackgroundColor(Color.WHITE);

            }else {
                vi.setBackgroundColor(Color.LTGRAY);
            }
            configureItemOrderList(vi, position);
        }

        return vi;
    }

    public void showItemsWithStatus(int status) {
        searchedItemOrderJSONArr = new JSONArray();
        searchedItemOrderList = new ArrayList<>();
        for(int i = 0; i < itemOrderJSONArr.length(); i++) {
            JSONObject itemOrderJSONObj = null;
            try {
                itemOrderJSONObj = itemOrderJSONArr.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(status == 0) {
                searchedItemOrderJSONArr.put(itemOrderJSONObj);
                searchedItemOrderList.add(itemOrderList.get(i));
            }
            else if(status == 1) {
                try {
                    if(itemOrderJSONObj.getBoolean("IsCompleted")) {
                        searchedItemOrderJSONArr.put(itemOrderJSONObj);
                        searchedItemOrderList.add(itemOrderList.get(i));
                    }
                } catch (JSONException e) {

                }
            }
            else {
                try {
                    if(!itemOrderJSONObj.getBoolean("IsCompleted")) {
                        searchedItemOrderJSONArr.put(itemOrderJSONObj);
                        searchedItemOrderList.add(itemOrderList.get(i));
                    }
                } catch (JSONException e) {

                }
            }
        }

        OrderItemActivity.visibleCountTv.setText(String.format("Visible Count: %d", searchedItemOrderList.size()));
        notifyDataSetChanged();
    }

    public void configureItemOrderList(View vi, final int position) {
        TextView pieceNoTv = (TextView)vi.findViewById(R.id.item_list_piece_no);
        TextView nameTv = (TextView)vi.findViewById(R.id.item_list_name);
        TextView quantityTv = (TextView)vi.findViewById(R.id.item_list_qty);
        TextView widthTv = (TextView)vi.findViewById(R.id.item_list_width);
        TextView depthTv = (TextView)vi.findViewById(R.id.item_list_depth);
        TextView lengthTv = (TextView)vi.findViewById(R.id.item_list_length);
        TextView descTv = (TextView)vi.findViewById(R.id.item_list_desc);
        TextView linerTv = (TextView)vi.findViewById(R.id.item_list_liner);
        TextView vanesTv = (TextView)vi.findViewById(R.id.item_list_vanes);
        TextView damperTv = (TextView)vi.findViewById(R.id.item_list_damper);

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
        completeCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    JSONObject itemOrderJSONObj = searchedItemOrderJSONArr.getJSONObject(position - 1);
                    itemOrderJSONObj.put("IsCompleted", isChecked);
                    itemOrderObj.put("IsCompleted", isChecked);

                    boolean allItemCompleted = true;
                    for(int i = 0; i < itemOrderList.size(); i++) {
                        HashMap<String, Object> itemOrderObj = itemOrderList.get(i);
                        if(!(boolean)itemOrderObj.get("IsCompleted")) {
                            allItemCompleted = false;
                            break;
                        }
                    }

                    if(allItemCompleted) {
                        OrderItemActivity.handler.sendEmptyMessage(MSG_ORDER_ITEMS_MARK_COMPLETED);
                    }

                    OrderItemActivity.handler.sendEmptyMessage(MSG_ORDER_ITEMS_MARK_CHANGED);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

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

    public void getOrderItems(final int itemStatus) {
        itemOrderList = new ArrayList<>();
        searchedItemOrderList = new ArrayList<>();
        itemOrderJSONArr = new JSONArray();
        searchedItemOrderJSONArr = new JSONArray();

        APIManager.getInstance().setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                OrderItemActivity.isApiCalling = false;
                Util.hideProgressDialog();
                if (objAPIResult != null) {
                    try {
                        //if(objAPIResult.getString("StatusCode").equals("Success")) {
                            itemOrderJSONArr = objAPIResult.getJSONArray("Items");
                            itemOrderList = Util.toList(itemOrderJSONArr);

                            for(int i = 0; i < itemOrderJSONArr.length(); i++) {
                                JSONObject itemOrderJSONObj = itemOrderJSONArr.getJSONObject(i);
                                searchedItemOrderJSONArr.put(itemOrderJSONObj);
                            }

                            int itemCount = 0;
                            for(int i = 0; i < itemOrderList.size(); i++) {
                                HashMap<String, Object> itemOrderObj = itemOrderList.get(i);
                                searchedItemOrderList.add(itemOrderObj);

                                String itemName = (String) itemOrderObj.get("Name");
                                /*if(!itemName.equals("Slip") && !itemName.equals("Drive")) {
                                    itemCount ++;
                                }*/
                                itemCount ++;
                            }

                            OrderItemActivity.countTv.setText(String.format("Count: %d", itemCount));
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

        OrderItemActivity.isApiCalling = true;
        Util.showProgressDialog("Getting items..", activity);
        APIManager.getInstance().orderItems(OrderItemActivity.orderID);
    }
}
