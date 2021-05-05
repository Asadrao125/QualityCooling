package sjcomputers.com.qualitycooling.Driver;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import sjcomputers.com.qualitycooling.Global.APIManager;
import sjcomputers.com.qualitycooling.Global.APIManagerCallback;
import sjcomputers.com.qualitycooling.Global.Util;
import sjcomputers.com.qualitycooling.R;

public class ItemSearchAdapter extends BaseAdapter {
    Activity activity;
    ArrayList<HashMap<String, Object>> itemArr;

    @Override
    public int getCount() {
        return itemArr.size();
    }

    @Override
    public Object getItem(int i) {
        return itemArr.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View vi = inflater.inflate(R.layout.item_item, viewGroup, false);
        configureItem(vi, i);
        return vi;
    }

    private void configureItem(View view, int position) {
        HashMap<String, Object> itemObj = itemArr.get(position);
        TextView nameTv = view.findViewById(R.id.textView45);
        TextView backTv = view.findViewById(R.id.textView46);
        nameTv.setText(String.format("%s", itemObj.get("ItemName")));
        boolean selected = (boolean) itemObj.get("Selected");
        if(selected) {
            backTv.setVisibility(View.VISIBLE);
        }
        else {
            backTv.setVisibility(View.INVISIBLE);
        }
    }

    public ArrayList<HashMap<String, Object>> getItemArr() {
        return itemArr;
    }

    public ItemSearchAdapter(Activity activity) {
        this.activity = activity;
        searchItems("");
    }

    public void searchItems(String searchText) {
        itemArr = new ArrayList<>();
        NewItemActivity.itemID = 0;
        NewItemActivity.itemName = "";
        notifyDataSetChanged();

        Util.showProgressDialog("Loading..", activity);
        APIManager.getInstance().setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                Util.hideProgressDialog();
                if(objAPIResult != null) {
                    try {
                        if(objAPIResult.getInt("Status") == 1) {
                            JSONArray itemJSONArr = objAPIResult.getJSONArray("ItemList");
                            itemArr = Util.toList(itemJSONArr);
                            for(int i = 0; i < itemArr.size(); i++) {
                                HashMap<String, Object> itemObj = itemArr.get(i);
                                itemObj.put("Selected", false);
                            }
                            notifyDataSetChanged();
                        }
                    } catch (Exception e) {

                    }
                }
            }
        });
        APIManager.getInstance().searchValue(searchText);
    }
}