package sjcomputers.com.qualitycooling.Driver;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.Signature;
import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import sjcomputers.com.qualitycooling.Global.APIManager;
import sjcomputers.com.qualitycooling.Global.APIManagerCallback;
import sjcomputers.com.qualitycooling.Global.Util;
import sjcomputers.com.qualitycooling.R;

import static sjcomputers.com.qualitycooling.Global.Util.MSG_DRIVER_ORDER_ITEM_ADDED;

public class NewItemActivity extends AppCompatActivity {
    ItemSearchAdapter adapter;
    public static int itemID;
    public static String itemName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);
        initValue();
        configureDesign();
    }

    private void initValue() {
        itemID = 0;
        itemName = "";
    }

    private void configureDesign() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.colorAccent)));
        getSupportActionBar().setTitle("Add New Item");

        final ListView itemLv = findViewById(R.id.item_lv);
        adapter = new ItemSearchAdapter(this);
        itemLv.setAdapter(adapter);
        itemLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayList<HashMap<String, Object>> itemArr = adapter.getItemArr();
                for(int j = 0; j < itemArr.size(); j++) {
                    HashMap<String, Object> itemObj = itemArr.get(j);
                    itemObj.put("Selected", false);
                }

                HashMap<String, Object> selectedItemObj = itemArr.get(i);
                selectedItemObj.put("Selected", true);
                itemID = (int) selectedItemObj.get("ItemId");
                itemName = (String) selectedItemObj.get("ItemName");
                adapter.notifyDataSetChanged();
            }
        });

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                adapter.searchItems(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        final EditText qtyEt = findViewById(R.id.editText12);

        Button addItemBt = findViewById(R.id.button9);
        addItemBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String qtyStr = qtyEt.getText().toString();
                if(qtyStr.equals("")) {
                    Util.showToast("Please input quantity", NewItemActivity.this);
                    return;
                }

                if(itemID == 0) {
                    Util.showToast("Please select item", NewItemActivity.this);
                    return;
                }

                int qty = 0;
                try {
                    qty =Integer.parseInt(qtyStr);
                } catch (Exception e) {

                }

                createNewItem(qty);
            }
        });
    }

    private void createNewItem(int qty) {
        APIManager.getInstance().setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                Util.hideProgressDialog();
                if (objAPIResult != null) {
                    try {
                        //if(objAPIResult.getString("StatusCode").equals("Success")) {
                        finish();
                        DriverItemActivity.handler.sendEmptyMessage(MSG_DRIVER_ORDER_ITEM_ADDED);
                        /*} else {
                            Util.showToast("Failed and try again", activity);
                        }*/
                    } catch (Exception e) {
                        Util.showToast("Failed and try again", NewItemActivity.this);
                    }
                } else {
                    Util.showToast("Failed and try again", NewItemActivity.this);
                }
            }
        });

        JSONObject object = new JSONObject();
        try {
            object.accumulate("OrderID", SignatureActivity.orderId);
            object.accumulate("ItemID", itemID);
            object.accumulate("Name", itemName);
            object.accumulate("Quantity", qty);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Util.showProgressDialog("Adding Item..", NewItemActivity.this);
        APIManager.getInstance().createOrderItem(object);
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
