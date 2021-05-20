package sjcomputers.com.qualitycooling.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import sjcomputers.com.qualitycooling.Global.APIManager;
import sjcomputers.com.qualitycooling.Global.APIManagerCallback;
import sjcomputers.com.qualitycooling.Global.UserData;
import sjcomputers.com.qualitycooling.Global.Util;
import sjcomputers.com.qualitycooling.ItemInfoActivity;
import sjcomputers.com.qualitycooling.KnockedTogetherActivity;
import sjcomputers.com.qualitycooling.LoadingActivity;
import sjcomputers.com.qualitycooling.R;
import sjcomputers.com.qualitycooling.models.KnockedTogetherModel;

public class KnockedTogetherAdapter extends ArrayAdapter<KnockedTogetherModel> {
    private ArrayList<KnockedTogetherModel> dataSet;
    Context mContext;
    private SharedPreferences sharedPreferences;
    String apiUrl;

    private static class ViewHolder {
        TextView customer, inNumber, itemName, jobSite, jobSiteAddress, pieceNo, itemInfo;
        CheckBox cbCompleted, cbDelivered;
    }

    public KnockedTogetherAdapter(ArrayList<KnockedTogetherModel> data, Context context) {
        super(context, R.layout.item_itemlist, data);
        this.dataSet = data;
        this.mContext = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        apiUrl = sharedPreferences.getString("URL", "");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        KnockedTogetherModel knockedTogetherModel = getItem(position);
        KnockedTogetherAdapter.ViewHolder viewHolder;
        final View result;
        if (convertView == null) {
            viewHolder = new KnockedTogetherAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_knocked_together, parent, false);
            viewHolder.itemName = (TextView) convertView.findViewById(R.id.tvItemName);
            viewHolder.customer = (TextView) convertView.findViewById(R.id.tvCustomer);
            viewHolder.inNumber = (TextView) convertView.findViewById(R.id.tvInNumber);
            viewHolder.jobSite = (TextView) convertView.findViewById(R.id.tvJobSite);
            viewHolder.jobSiteAddress = (TextView) convertView.findViewById(R.id.tvJobSiteAddress);
            viewHolder.pieceNo = (TextView) convertView.findViewById(R.id.tvPieceNo);
            viewHolder.cbCompleted = convertView.findViewById(R.id.cbCompleted);
            viewHolder.itemInfo = convertView.findViewById(R.id.itemInfo);
            viewHolder.cbDelivered = convertView.findViewById(R.id.cbDelivered);

            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (KnockedTogetherAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }
        viewHolder.itemName.setText("Item Name: " + knockedTogetherModel.ItemName);
        viewHolder.customer.setText("Customer: " + knockedTogetherModel.Customer);
        viewHolder.inNumber.setText("IN #: " + knockedTogetherModel.INNumber);
        viewHolder.jobSite.setText("Job Site: " + knockedTogetherModel.JobSite);
        viewHolder.jobSiteAddress.setText("Job Site Address: " + knockedTogetherModel.JobSiteAddress);
        viewHolder.pieceNo.setText("Piece #: " + knockedTogetherModel.PieceNo);

        if (knockedTogetherModel.Completed.equals("1")) {
            viewHolder.cbCompleted.setChecked(true);
        }

        if (knockedTogetherModel.Delivered.equals("1")) {
            viewHolder.cbDelivered.setChecked(true);
        }

        viewHolder.cbCompleted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    checkcheckcheck(knockedTogetherModel.OrderItemId, "1", knockedTogetherModel.INNumber);
                } else {
                    checkcheckcheck(knockedTogetherModel.OrderItemId, "0", knockedTogetherModel.INNumber);
                }
            }
        });

        viewHolder.cbDelivered.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    check_uncheck_delivered(knockedTogetherModel.OrderItemId, "1");
                } else {
                    check_uncheck_delivered(knockedTogetherModel.OrderItemId, "0");
                }
            }
        });

        viewHolder.itemInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(KnockedTogetherActivity.inputVal)) {
                    Intent intent = new Intent(mContext, ItemInfoActivity.class);
                    intent.putExtra("scanned_value", KnockedTogetherActivity.inputVal);
                    mContext.startActivity(intent);
                } else {
                    Toast.makeText(mContext, "Please enter input", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return convertView;
    }

    public void checkcheckcheck(String orderId, String completed, String inNumber) {
        Util.showProgressDialog("Loading..", mContext);
        APIManager apiManager = new APIManager();
        apiManager.setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                Util.hideProgressDialog();
                if (objAPIResult != null) {
                    try {
                        if (objAPIResult.getString("Status").equals("Success")) {
                            JSONObject jsonObject = new JSONObject(objAPIResult.toString());
                            String msg = jsonObject.getString("Message");
                            Toast.makeText(mContext, "" + msg, Toast.LENGTH_SHORT).show();

                            String button1_text = jsonObject.getString("Button1Text");
                            String button2_text = jsonObject.getString("Button2Text");
                            String ShowPopup = jsonObject.getString("ShowPopup");

                            if (ShowPopup.equals("1")) {
                                showDialog(button1_text, button2_text, inNumber);
                            }

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
        apiManager.checkOrUncheckKnocked(orderId, completed);
    }

    private void showDialog(String button1_text, String button2_text, String inNumber) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Confirmation")
                .setCancelable(false)
                .setPositiveButton(button2_text, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String NewString = button2_text.replaceAll(" ", "_");
                        //finalHit(inNumber, NewString);
                        finalHit2(inNumber, NewString);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(button1_text, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String NewString = button1_text.replaceAll(" ", "_");
                        //finalHit(inNumber, NewString);
                        finalHit2(inNumber, NewString);
                        dialog.dismiss();
                    }
                })
                .setNeutralButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void finalHit2(String inNumber, String buttonText) {
        Util.showProgressDialog("Loading..", mContext);
        APIManager apiManager = new APIManager();
        apiManager.setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                Util.hideProgressDialog();
                if (objAPIResult != null) {
                    try {

                        if (objAPIResult.getString("Status").equals("Success")) {
                            JSONObject jsonObject = new JSONObject(objAPIResult.toString());
                            String msg = jsonObject.getString("Message");
                            Toast.makeText(mContext, "" + msg, Toast.LENGTH_SHORT).show();

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
        apiManager.showPopup(inNumber, buttonText);
    }

    public void check_uncheck_delivered(String orderItemId, String delivered) {
        Util.showProgressDialog("Loading..", mContext);
        APIManager apiManager = new APIManager();
        apiManager.setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                Util.hideProgressDialog();
                if (objAPIResult != null) {
                    try {
                        if (objAPIResult.getString("Status").equals("Success")) {
                            JSONObject jsonObject = new JSONObject(objAPIResult.toString());
                            String msg = jsonObject.getString("Message");
                            Toast.makeText(mContext, "" + msg, Toast.LENGTH_SHORT).show();
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
        apiManager.checkUncheckDelivered(orderItemId, delivered);
    }

    public void finalHit(String innumber, String buttonText) {
        Util.showProgressDialog("Loading..", mContext);
        AsyncHttpClient client = new AsyncHttpClient();
        String token = UserData.getInstance().authToken;
        String userId = String.valueOf(UserData.getInstance().userId);

        apiUrl = sharedPreferences.getString("URL", "");
        client.post(apiUrl + "/services/service.svc/MarkOrderReadyFor?innumber=" + innumber + "&buttonText=" + buttonText + "&userId=" + userId + "&authtoken=" + token, new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        Util.hideProgressDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(res);
                            String msg = jsonObject.getString("Message");
                            Toast.makeText(mContext, "" + msg, Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        Util.hideProgressDialog();
                        Toast.makeText(mContext, "Error: " + res, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    public void check(String orderItemId, String completed, String inNumber) {
        Util.showProgressDialog("Loading..", mContext);
        AsyncHttpClient client = new AsyncHttpClient();
        String token = UserData.getInstance().authToken;
        String userId = String.valueOf(UserData.getInstance().userId);

        apiUrl = sharedPreferences.getString("URL", "");
        client.post(apiUrl + "/services/service.svc/KnockedTogetherCheckOrUncheck?orderItemId=" + orderItemId + "&completed=" + completed + "&authtoken=" + token + "&userId=" + userId, new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        Util.hideProgressDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(res);
                            String msg = jsonObject.getString("Message");
                            Toast.makeText(mContext, "" + msg, Toast.LENGTH_SHORT).show();
                            String button1_text = jsonObject.getString("Button1Text");
                            String button2_text = jsonObject.getString("Button2Text");
                            String ShowPopup = jsonObject.getString("ShowPopup");

                            if (ShowPopup.equals("1")) {
                                showDialog(button1_text, button2_text, inNumber);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        Util.hideProgressDialog();
                        Toast.makeText(mContext, "Error: " + res, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

}