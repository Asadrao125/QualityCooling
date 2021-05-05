package sjcomputers.com.qualitycooling.Customer;

import android.app.Activity;
import android.graphics.Color;
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
import sjcomputers.com.qualitycooling.Global.UserData;
import sjcomputers.com.qualitycooling.Global.Util;
import sjcomputers.com.qualitycooling.R;

/**
 * Created by RabbitJang on 11/5/2018.
 */

public class CustomerInvoiceAdapter extends BaseAdapter {
    Activity activity;
    ArrayList<HashMap<String, Object>> invoiceArr;
    @Override
    public int getCount() {
        return invoiceArr.size() + 1;
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
            vi = layoutInflater.inflate(R.layout.item_customer_invoice_title, parent, false);
        }
        else {
            vi = layoutInflater.inflate(R.layout.item_customer_invoice, parent, false);
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
        HashMap<String, Object> invoiceObj = invoiceArr.get(position - 1);
        TextView noTv = view.findViewById(R.id.textView1);
        TextView jobSiteTv = view.findViewById(R.id.textView2);
        TextView statusTv = view.findViewById(R.id.textView3);
        TextView orderDateTv = view.findViewById(R.id.textView4);
        TextView deliveryDateTv = view.findViewById(R.id.textView5);
        TextView numOfPageTv = view.findViewById(R.id.textView6);
        TextView totalAmountTv = view.findViewById(R.id.textView7);
        TextView paidAmountTv = view.findViewById(R.id.textView8);
        TextView paymentStatusTv = view.findViewById(R.id.textView9);
        noTv.setText(String.format("%s", invoiceObj.get("InvoiceNo")));
        jobSiteTv.setText(String.format("%s", invoiceObj.get("JobSite")));
        statusTv.setText(String.format("%s", invoiceObj.get("Status")));
        orderDateTv.setText(String.format("%s", invoiceObj.get("OrderDate")));
        deliveryDateTv.setText(String.format("%s", invoiceObj.get("DeliveryDate")));
        numOfPageTv.setText(String.format("%s", invoiceObj.get("NoOfPages")));
        totalAmountTv.setText(String.format("%s", invoiceObj.get("TotalAmt")));
        paidAmountTv.setText(String.format("%s", invoiceObj.get("PaidAmt")));
        paymentStatusTv.setText(String.format("%s", invoiceObj.get("PaymentStatus")));
    }

    public CustomerInvoiceAdapter(Activity activity) {
        this.activity = activity;
        getCustomerInvoices();
    }

    private void getCustomerInvoices() {
        invoiceArr = new ArrayList<>();
        notifyDataSetChanged();

        Util.showProgressDialog("Getting invoices..", activity);
        APIManager.getInstance().setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                Util.hideProgressDialog();
                if (objAPIResult != null) {
                    try {
                        if(objAPIResult.getString("ResponseStatus").equals("Success")) {
                            JSONArray orderJSONArr = objAPIResult.getJSONArray("CustomerInoviceList");
                            invoiceArr = Util.toList(orderJSONArr);
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
        APIManager.getInstance().getCustomerInvoices(UserData.getInstance().customerId);
    }
}
