package sjcomputers.com.qualitycooling.Customer;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import sjcomputers.com.qualitycooling.Global.APIManager;
import sjcomputers.com.qualitycooling.Global.APIManagerCallback;
import sjcomputers.com.qualitycooling.Global.Util;
import sjcomputers.com.qualitycooling.R;

import static sjcomputers.com.qualitycooling.Global.Util.MSG_CUSTOMER_NOTE_ADDED;
import static sjcomputers.com.qualitycooling.Global.Util.MSG_CUSTOMER_ORDER_ADDED;

/**
 * Created by RabbitJang on 11/5/2018.
 */

public class NoteAdapter extends BaseAdapter {
    Activity activity;
    ArrayList<HashMap<String, Object>> noteArr;
    public static Handler handler;
    @Override
    public int getCount() {
        return noteArr.size();
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
        View vi = inflater.inflate(R.layout.item_note, parent, false);
        configureItem(vi, position);
        return vi;
    }

    private void configureItem(View view, int position) {
        HashMap<String, Object> noteObj = noteArr.get(position);
        TextView nameTv = view.findViewById(R.id.textView30);
        TextView noteTv = view.findViewById(R.id.textView31);
        TextView dateTv = view.findViewById(R.id.textView32);
        nameTv.setText((String)noteObj.get("SubmittedBy"));
        noteTv.setText((String)noteObj.get("Note"));
        String createdDate = (String) noteObj.get("CreatedDate");
        dateTv.setText(createdDate);
    }

    public NoteAdapter(Activity activity) {
        this.activity = activity;
        noteArr = new ArrayList<>();
        getTicketDetail();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == MSG_CUSTOMER_NOTE_ADDED) {
                    getTicketDetail();
                }
            }
        };
    }

    private void getTicketDetail() {
        Util.showProgressDialog("Getting orders..", activity);
        APIManager.getInstance().setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                Util.hideProgressDialog();
                if (objAPIResult != null) {
                    try {
                        if(objAPIResult.getString("ResponseStatus").equals("Success")) {
                            TicketDetailActivity.ticketNoTv.setText(String.format("%d", TicketDetailActivity.ticketId));
                            TicketDetailActivity.submittedByTv.setText(objAPIResult.getString("SubmittedBy"));
                            TicketDetailActivity.lastUpdatedByTv.setText(objAPIResult.getString("LastUpdatedBy"));
                            TicketDetailActivity.statusTv.setText(objAPIResult.getString("Status"));

                            JSONArray noteJSONArr = objAPIResult.getJSONArray("NotesList");
                            noteArr = Util.toList(noteJSONArr);
                            notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        Util.showToast("Failed and try again", activity);
                    }
                } else {
                    Util.showToast("Failed and try again", activity);
                }
            }
        });
        APIManager.getInstance().getCustomerTicketDetail(TicketDetailActivity.ticketId);
    }
}
