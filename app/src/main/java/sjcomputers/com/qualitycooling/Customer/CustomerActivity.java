package sjcomputers.com.qualitycooling.Customer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import sjcomputers.com.qualitycooling.Global.Util;
import sjcomputers.com.qualitycooling.R;

/**
 * Created by RabbitJang on 11/5/2018.
 */

public class CustomerActivity extends AppCompatActivity {
    public static EditText searchInEt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
        configureDesign();
    }

    private void configureDesign() {
        setTitle("Quality Cooling Customer");
        Button ordersBt = findViewById(R.id.button1);
        Button invoicesBt = findViewById(R.id.button2);
        Button ticketsBt = findViewById(R.id.button3);
        Button settingsBt = findViewById(R.id.button4);
        Button callBt = findViewById(R.id.button5);

        ordersBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerActivity.this, CustomerOrderActivity.class);
                startActivity(intent);
            }
        });

        invoicesBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerActivity.this, CustomerInvoiceActivity.class);
                startActivity(intent);
            }
        });

        ticketsBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerActivity.this, CustomerTicketActivity.class);
                startActivity(intent);
            }
        });

        settingsBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        callBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "17188543470"));
                if (ActivityCompat.checkSelfPermission(CustomerActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Util.showToast("Please check call permission", CustomerActivity.this);
                    return;
                }
                startActivity(intent);
            }
        });
    }
}
