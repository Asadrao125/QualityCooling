package sjcomputers.com.qualitycooling.Customer;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import sjcomputers.com.qualitycooling.R;

/**
 * Created by RabbitJang on 11/5/2018.
 */

public class CustomerOrderActivity extends AppCompatActivity {
    public static EditText searchInEt;
    public static CheckBox showOldOrdersCb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_order);
        configureDesign();
    }

    private void configureDesign() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.colorAccent)));
        getSupportActionBar().setTitle("Orders");

        searchInEt = (EditText)findViewById(R.id.search_IN_txt);
        showOldOrdersCb = findViewById(R.id.checkBox2);

        ListView orderLv = findViewById(R.id.order_lv);
        CustomerOrderAdapter adapter = new CustomerOrderAdapter(this);
        orderLv.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_order, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.new_order:
                Intent intent = new Intent(CustomerOrderActivity.this, NewOrderActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
