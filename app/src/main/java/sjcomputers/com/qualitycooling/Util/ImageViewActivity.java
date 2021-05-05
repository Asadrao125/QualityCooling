package sjcomputers.com.qualitycooling.Util;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import androidx.annotation.Nullable;
import sjcomputers.com.qualitycooling.Global.Util;
import sjcomputers.com.qualitycooling.R;

/**
 * Created by RabbitJang on 10/24/2018.
 */

public class ImageViewActivity extends Activity {
    public static String imageUrl;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        confirgureDesign();
    }

    private void confirgureDesign() {
        ImageView backIv = (ImageView)findViewById(R.id.imageView);
        ImageLoader.getInstance().displayImage(imageUrl, backIv, Util.optionsImg);
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}