package sjcomputers.com.qualitycooling.Global;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import sjcomputers.com.qualitycooling.R;

/**
 * Created by RabbitJang on 5/27/2018.
 */

public class Util {
    public static final int MSG_ORDER_DELIVERED = 1000;
    public static final int MSG_REFRESH_ORDER   = 1001;
    public static final int MSG_DOCUMENT_ADDED  = 1002;
    public static final int MSG_ORDER_SEARCHED  = 1003;
    public static final int MSG_IMAGE_ROTATED  = 24;
    public static final int MSG_CUSTOMER_ORDER_ADDED  = 1004;
    public static final int MSG_CUSTOMER_JOB_SITE_ADDED  = 1005;
    public static final int MSG_CUSTOMER_NOTE_ADDED  = 1006;
    public static final int MSG_ORDER_ITEMS_MARK_COMPLETED   = 1007;
    public static final int MSG_ORDER_ITEMS_MARK_CHANGED   = 1008;
    public static final int MSG_DRIVER_ORDER_ITEM_ADDED   = 1009;
    public static final int MSG_CUTTING_CONFIRMED  = 1010;
    public static final int MSG_FORMING_CONFIRMED  = 1011;
    public static final int MSG_SERIAL_SCANNED  = 1012;

    public static int QR_CODE_SCANNED = 1010;


    public static final int CAMERA_IMAGE   = 22;
    public static final int DOCUMENT_FILE  = 23;

    public static final int MIN_TIME_LOCATION_UPDATE = 5000;
    public static final int MIN_DISTANCE_UPDATE = 0;
    public static final int STATUS_UPDATE_INTERVAL = 1000 * 10; //-----Unit: 10s
    public static final int LOCATION_UPDATE_INTERVAL = 1000 * 15; //-----Unit: 10s


    public static Toast toast = null;
    public static ProgressDialog progressDlg = null;

    public static DisplayImageOptions optionsImg = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    public static void showToast (String toastStr, Context context) {
        if (toast == null || toast.getView().getWindowVisibility() != View.VISIBLE) {
            toast = Toast.makeText(context, toastStr, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    public static void showProgressDialog(String titleStr, Context context)  {
        progressDlg = new ProgressDialog(context, R.style.AppDialogTheme);
        progressDlg.setIndeterminate(false);
        progressDlg.setCancelable(false);
        progressDlg.setMessage(titleStr);
        progressDlg.show();
    }

    public static void hideProgressDialog() {
        if(progressDlg != null) {
            progressDlg.dismiss();
            progressDlg.cancel();
        }
    }

    public static HashMap<String, Object> toMap(JSONObject object) throws JSONException {
        HashMap<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static ArrayList<HashMap<String, Object>> toList(JSONArray array) throws JSONException {
        ArrayList<HashMap<String, Object>> list = new ArrayList<>();
        for(int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            HashMap<String, Object> hashObj = (HashMap<String, Object>) toMap(obj);
            list.add(hashObj);
        }
        return list;
    }

    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static void deleteFileFromMediaStore(final ContentResolver contentResolver, final File file) {
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            String canonicalPath;
            try {
                canonicalPath = file.getCanonicalPath();
            } catch (IOException e) {
                canonicalPath = file.getAbsolutePath();
            }
            final Uri uri = MediaStore.Files.getContentUri("external");
            final int result = contentResolver.delete(uri,
                    MediaStore.Files.FileColumns.DATA + "=?", new String[]{canonicalPath});
            if (result == 0) {
                final String absolutePath = file.getAbsolutePath();
                if (!absolutePath.equals(canonicalPath)) {
                    contentResolver.delete(uri,
                            MediaStore.Files.FileColumns.DATA + "=?", new String[]{absolutePath});
                }
            }
        }
    }

    public static String BitmapToString(Bitmap bitmap) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            String temp = Base64.encodeToString(b, Base64.DEFAULT);
            return temp;
        } catch (NullPointerException e) {
            return null;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }


    public static float convertPixelsToDp(float px, Context context){
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static float convertDpToPixel(float dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
