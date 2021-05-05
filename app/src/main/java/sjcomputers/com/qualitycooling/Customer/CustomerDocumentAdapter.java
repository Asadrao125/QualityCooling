package sjcomputers.com.qualitycooling.Customer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import sjcomputers.com.qualitycooling.DocumentActivity;
import sjcomputers.com.qualitycooling.Global.APIManager;
import sjcomputers.com.qualitycooling.Global.APIManagerCallback;
import sjcomputers.com.qualitycooling.Global.Util;
import sjcomputers.com.qualitycooling.R;

import static sjcomputers.com.qualitycooling.Global.Util.MSG_DOCUMENT_ADDED;

/**
 * Created by RabbitJang on 11/5/2018.
 */

public class CustomerDocumentAdapter extends BaseAdapter {
    Activity activity;
    ArrayList<HashMap<String, Object>> documentArr;
    public static Handler handler;
    @Override
    public int getCount() {
        return documentArr.size();
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
        View vi = inflater.inflate(R.layout.item_document, parent, false);
        configureDocumentItem(vi, position);
        return vi;
    }

    private void configureDocumentItem(View view, final int position) {
        HashMap<String, Object> documentObj = documentArr.get(position);
        TextView nameTv = (TextView)view.findViewById(R.id.textView15);
        TextView dateTv = (TextView)view.findViewById(R.id.textView18);
        nameTv.setText((String)documentObj.get("Name"));
        dateTv.setText((String)documentObj.get("CreatedDate"));
        Button seeBt = view.findViewById(R.id.button3);
        seeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> documentObj = documentArr.get(position);
                getDocument((int) documentObj.get("DocumentID"));
            }
        });
    }

    public CustomerDocumentAdapter(Activity activity) {
        this.activity = activity;
        getOrderDocuments();
        /*handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == MSG_DOCUMENT_ADDED) {
                    getOrderDocuments();
                }
            }
        };*/
    }

    private void getOrderDocuments() {
        documentArr = new ArrayList<>();
        notifyDataSetChanged();
        APIManager.getInstance().setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                Util.hideProgressDialog();
                if(objAPIResult != null) {
                    try {
                        JSONArray documentJSONArr = objAPIResult.getJSONArray("Documents");
                        documentArr = Util.toList(documentJSONArr);
                        notifyDataSetChanged();
                    } catch (JSONException e) {
                        Util.showToast("Load failed and try again", activity);
                    }
                }
            }
        });
        Util.showProgressDialog("Loading documents..", activity);
        APIManager.getInstance().getOrderDocuments(CustomerDocumentActivity.orderId);
    }

    private void getDocument(int documentId) {
        APIManager.getInstance().setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                Util.hideProgressDialog();
                if(objAPIResult != null) {
                    try {
                        String pdfUrl = objAPIResult.getString("DocumentUrl");
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl));
                        activity.startActivity(browserIntent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        Util.showProgressDialog("Loading document..", activity);
        APIManager.getInstance().getOrderDocument(documentId);
    }
}
