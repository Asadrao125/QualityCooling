package sjcomputers.com.qualitycooling.Customer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import sjcomputers.com.qualitycooling.Global.APIManager;
import sjcomputers.com.qualitycooling.Global.APIManagerCallback;
import sjcomputers.com.qualitycooling.Global.UserData;
import sjcomputers.com.qualitycooling.Global.Util;
import sjcomputers.com.qualitycooling.R;

/**
 * Created by RabbitJang on 11/5/2018.
 */

public class CustomerTicketAdapter extends BaseAdapter {
    Activity activity;
    ArrayList<HashMap<String, Object>> ticketArr;
    @Override
    public int getCount() {
        return ticketArr.size() + 1;
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
        if(position == 0) {
            vi = layoutInflater.inflate(R.layout.item_customer_ticket_title, parent, false);
        }
        else {
            vi = layoutInflater.inflate(R.layout.item_customer_ticket, parent, false);
            if (position % 2 == 1) {
                vi.setBackgroundColor(Color.WHITE);
            } else {
                vi.setBackgroundColor(Color.LTGRAY);
            }
            configureItem(vi, position);
        }
        return vi;
    }

    private void configureItem(View view, int position) {
        final HashMap<String, Object> ticketObj = ticketArr.get(position - 1);
        TextView noTv = view.findViewById(R.id.textView1);
        TextView subjectTv = view.findViewById(R.id.textView2);
        TextView descTv = view.findViewById(R.id.textView3);
        TextView statusTv = view.findViewById(R.id.textView4);
        TextView lastUpdatedByTv = view.findViewById(R.id.textView5);
        TextView submittedByTv = view.findViewById(R.id.textView6);
        Button viewBt = view.findViewById(R.id.view_bt);
        Button deleteBt = view.findViewById(R.id.delete_bt);

        noTv.setText(String.format("%s", ticketObj.get("TicketID")));
        subjectTv.setText(String.format("%s", ticketObj.get("Subject")));
        descTv.setText(String.format("%s", ticketObj.get("Description")));
        statusTv.setText(String.format("%s", ticketObj.get("Status")));
        lastUpdatedByTv.setText(String.format("%s", ticketObj.get("LastUpdatedBy")));
        submittedByTv.setText(String.format("%s", ticketObj.get("SubmittedBy")));

        deleteBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int ticketId = (int) ticketObj.get("TicketID");
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("You are about to delete. Do you wish to proceed?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteTicket(ticketId);
                    }
                }).setNegativeButton("No", null).show();
            }
        });

        viewBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ticketId = (int) ticketObj.get("TicketID");
                Intent intent = new Intent(activity, TicketDetailActivity.class);
                TicketDetailActivity.ticketId = ticketId;
                activity.startActivity(intent);
            }
        });
    }

    private void deleteTicket(int ticketId) {
        Util.showProgressDialog("Deleting tickets..", activity);
        APIManager.getInstance().setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                Util.hideProgressDialog();
                if (objAPIResult != null) {
                    try {
                        if(objAPIResult.getString("ResponseStatus").equals("Success")) {
                            Util.showToast(objAPIResult.getString("ResponseMessage"), activity);
                            getCustomerTickets();
                        }
                    } catch (Exception e) {
                        Util.showToast("Failed and try again", activity);
                    }
                } else {
                    Util.showToast("Failed and try again", activity);
                }
            }
        });
        APIManager.getInstance().deleteCustomerTicket(ticketId);
    }

    public CustomerTicketAdapter(Activity activity) {
        this.activity = activity;
        getCustomerTickets();
    }

    private void getCustomerTickets() {
        ticketArr = new ArrayList<>();
        notifyDataSetChanged();

        Util.showProgressDialog("Getting tickets..", activity);
        APIManager.getInstance().setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                Util.hideProgressDialog();
                if (objAPIResult != null) {
                    try {
                        if(objAPIResult.getString("ResponseStatus").equals("Success")) {
                            JSONArray orderJSONArr = objAPIResult.getJSONArray("CustomerTicketList");
                            ticketArr = Util.toList(orderJSONArr);
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
        APIManager.getInstance().getCustomerTickets(UserData.getInstance().customerId);
    }
}
