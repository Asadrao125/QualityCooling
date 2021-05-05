package sjcomputers.com.qualitycooling.Util;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import sjcomputers.com.qualitycooling.DocumentActivity;
import sjcomputers.com.qualitycooling.R;

import static sjcomputers.com.qualitycooling.Global.Util.MSG_IMAGE_ROTATED;
import static sjcomputers.com.qualitycooling.Global.Util.MSG_REFRESH_ORDER;

/**
 * Created by RabbitJang on 11/1/2018.
 */

public class RotateImageActivity extends AppCompatActivity {
    public static Bitmap bitmap;
    ImageView bitmapIv;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotate_image);
        configureDesign();
        getSupportActionBar().setTitle("Rotate Image");
    }

    private void configureDesign() {
        bitmapIv = findViewById(R.id.imageView);
        bitmapIv.setImageBitmap(bitmap);

        Button rotateLBt = findViewById(R.id.button1);
        Button rotateRBt = findViewById(R.id.button2);

        rotateLBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmap = rotateBitmap(1);
                bitmapIv.setImageBitmap(bitmap);
            }
        });

        rotateRBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmap = rotateBitmap(0);
                bitmapIv.setImageBitmap(bitmap);
            }
        });
    }

    public static Bitmap rotateBitmap(int direction) {
        float angle;
        Matrix matrix = new Matrix();
        if(direction == 0) {
            angle = 90;
        }
        else {
            angle = -90;
        }
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ok, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ok:
                finish();
                Message message = new Message();
                message.what = MSG_IMAGE_ROTATED;
                message.obj = bitmap;
                DocumentActivity.handler.sendMessage(message);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {

    }
}
