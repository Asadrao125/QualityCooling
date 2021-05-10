package sjcomputers.com.qualitycooling.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import sjcomputers.com.qualitycooling.Global.APIManager;
import sjcomputers.com.qualitycooling.Global.APIManagerCallback;
import sjcomputers.com.qualitycooling.Global.UserData;
import sjcomputers.com.qualitycooling.Global.Util;
import sjcomputers.com.qualitycooling.ItemInfoActivity;
import sjcomputers.com.qualitycooling.LoadingActivity;
import sjcomputers.com.qualitycooling.R;

public class LoadingAdapter extends ArrayAdapter<LoadingModel> {
    private ArrayList<LoadingModel> dataSet;
    Context mContext;
    private SharedPreferences sharedPreferences;
    String apiUrl;

    private static class ViewHolder {
        TextView tvArEnergy, inNumber, itemName, jobSite, jobSiteAddress, pieceNo, itemInfo;
        CheckBox cbLoaded;
    }

    public LoadingAdapter(ArrayList<LoadingModel> data, Context context) {
        super(context, R.layout.item_itemlist, data);
        this.dataSet = data;
        this.mContext = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LoadingModel loadingModel = getItem(position);
        LoadingAdapter.ViewHolder viewHolder;
        final View result;
        if (convertView == null) {
            viewHolder = new LoadingAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_loading, parent, false);
            viewHolder.tvArEnergy = (TextView) convertView.findViewById(R.id.tvArEnergy);
            viewHolder.itemName = (TextView) convertView.findViewById(R.id.tvItemName);
            viewHolder.inNumber = (TextView) convertView.findViewById(R.id.tvInNumber);
            viewHolder.jobSite = (TextView) convertView.findViewById(R.id.tvJobSite);
            viewHolder.jobSiteAddress = (TextView) convertView.findViewById(R.id.tvJobSiteAddress);
            viewHolder.pieceNo = (TextView) convertView.findViewById(R.id.tvPieceNo);
            viewHolder.cbLoaded = convertView.findViewById(R.id.cbLoaded);
            viewHolder.itemInfo = convertView.findViewById(R.id.itemInfo);

            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (LoadingAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }
        viewHolder.tvArEnergy.setText("Customer: " + loadingModel.A_R_ENERGY);
        viewHolder.itemName.setText("Item Name: " + loadingModel.ItemName);
        viewHolder.inNumber.setText("IN: " + loadingModel.INNumber);
        viewHolder.jobSite.setText("Job site: " + loadingModel.JobSite);
        viewHolder.jobSiteAddress.setText("Job Site Address: " + loadingModel.JobSiteAddress);
        viewHolder.pieceNo.setText("Piece No: " + loadingModel.PieceNo);

        if (loadingModel.Loaded.equals("1")) {
            viewHolder.cbLoaded.setChecked(true);
        }

        viewHolder.cbLoaded.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //checkOrUncheck(loadingModel.OrderItemId, loadingModel.Loaded);
                if (b) {
                    check(loadingModel.OrderItemId, "1");
                } else {
                    check(loadingModel.OrderItemId, "0");
                }

            }
        });

        viewHolder.pieceNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        viewHolder.itemInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(LoadingActivity.inputVal)) {
                    Intent intent = new Intent(mContext, ItemInfoActivity.class);
                    intent.putExtra("scanned_value", LoadingActivity.inputVal);
                    mContext.startActivity(intent);
                } else {
                    Toast.makeText(mContext, "Please enter input", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return convertView;
    }

    private void checkOrUncheck(String orderItemId, String loaded) {
        Util.showProgressDialog("Loading..", mContext);
        APIManager apiManager = new APIManager();
        apiManager.setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                Util.hideProgressDialog();
                if (objAPIResult != null) {
                    try {
                        if (objAPIResult.getString("Status").equals("Success")) {
                            notifyDataSetChanged();
                        } else {
                            Util.showToast(objAPIResult.getString("Message"), mContext);
                        }
                    } catch (Exception e) {
                        Util.showToast("Failed and try again", mContext);
                    }
                } else {
                    Util.showToast("Failed and try again", mContext);
                }
            }
        });
        apiManager.checkOrUncheck(Integer.parseInt(orderItemId), Integer.parseInt(loaded));
    }

    public void check(String orderItemId, String loaded) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("orderItemId", orderItemId);
        params.put("loaded", loaded);
        params.put("authtoken", UserData.getInstance().authToken);
        params.put("userId", UserData.getInstance().userId);

        String token = UserData.getInstance().authToken;
        String userId = String.valueOf(UserData.getInstance().userId);

        apiUrl = sharedPreferences.getString("URL", "");
        Log.d("api_url_jd", "check: "+apiUrl);
        Log.d("log_data", "check: " + orderItemId + "\n" + loaded + "\n" + UserData.getInstance().authToken + "\n" + UserData.getInstance().userId);
        client.post(apiUrl + "/services/service.svc/LoadingCheckOrUncheck?orderItemId=" + orderItemId + "&loaded=" + loaded + "&authtoken=" + token + "&userId=" + userId, /*params,*/ new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        try {
                            JSONObject jsonObject = new JSONObject(res);

                            String msg = jsonObject.getString("Message");
                            Toast.makeText(mContext, "" + msg, Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    /* http://69.162.169.135/QC//services/service.svc/LoadingCheckOrUncheck */

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        Toast.makeText(mContext, "Error: " + res, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}