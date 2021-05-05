package sjcomputers.com.qualitycooling.Customer;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import sjcomputers.com.qualitycooling.Global.APIManager;
import sjcomputers.com.qualitycooling.Global.APIManagerCallback;
import sjcomputers.com.qualitycooling.Global.UserData;
import sjcomputers.com.qualitycooling.Global.Util;
import sjcomputers.com.qualitycooling.R;

import static sjcomputers.com.qualitycooling.Global.Util.MSG_CUSTOMER_NOTE_ADDED;
import static sjcomputers.com.qualitycooling.Global.Util.MSG_CUSTOMER_ORDER_ADDED;

/**
 * Created by RabbitJang on 11/5/2018.
 */

public class TicketDetailActivity extends AppCompatActivity {
    public static int ticketId;
    public static TextView ticketNoTv;
    public static TextView submittedByTv;
    public static TextView lastUpdatedByTv;
    public static TextView statusTv;
    Dialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);
        configureDesign();
    }

    private void configureDesign(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.colorAccent)));
        getSupportActionBar().setTitle("Ticket Detail: #" + String.format("%d", ticketId));

        ticketNoTv = findViewById(R.id.textView31);
        submittedByTv = findViewById(R.id.textView33);
        lastUpdatedByTv = findViewById(R.id.textView35);
        statusTv = findViewById(R.id.textView37);

        ListView noteLv = findViewById(R.id.note_lv);
        NoteAdapter adapter = new NoteAdapter(this);
        noteLv.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.new_note:
                showAddNoteDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAddNoteDialog() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_add_note);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();

        final EditText noteEt = (EditText)dialog.findViewById(R.id.note_et);
        Button closeBt = (Button)dialog.findViewById(R.id.closeBtn);
        closeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
            }
        });

        Button okBt = (Button)dialog.findViewById(R.id.okBtn);
        okBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String note = noteEt.getText().toString();
                if(note.equals("")) {
                    Util.showToast("Please input note", TicketDetailActivity.this);
                    return;
                }
                addNote(note);
            }
        });
    }

    private void addNote(String note) {
        Util.showProgressDialog("Adding note..", TicketDetailActivity.this);
        APIManager.getInstance().setCallback(new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                Util.hideProgressDialog();
                if (objAPIResult != null) {
                    try {
                        if(objAPIResult.getString("ResponseStatus").equals("Success")) {
                            Util.showToast(objAPIResult.getString("ResponseMessage"), TicketDetailActivity.this);
                            dialog.hide();
                            NoteAdapter.handler.sendEmptyMessage(MSG_CUSTOMER_NOTE_ADDED);
                        }
                    } catch (Exception e) {
                        Util.showToast("Failed and try again", TicketDetailActivity.this);
                    }
                } else {
                    Util.showToast("Failed and try again", TicketDetailActivity.this);
                }
            }
        });

        JSONObject object = new JSONObject();
        try {
            object.accumulate("FromId", UserData.getInstance().customerId);
            object.accumulate("CreatedBy", UserData.getInstance().userId);
            object.accumulate("Note", note);
            object.accumulate("TicketID", ticketId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        APIManager.getInstance().addTicketNote(object);
    }
}
