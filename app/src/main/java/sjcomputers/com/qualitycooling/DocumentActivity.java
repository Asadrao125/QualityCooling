package sjcomputers.com.qualitycooling;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.regex.Pattern;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import sjcomputers.com.qualitycooling.Global.APIManager;
import sjcomputers.com.qualitycooling.Global.APIManagerCallback;
import sjcomputers.com.qualitycooling.Global.UserData;
import sjcomputers.com.qualitycooling.Global.Util;
import sjcomputers.com.qualitycooling.Util.RotateImageActivity;

import static sjcomputers.com.qualitycooling.Global.Util.BitmapToString;
import static sjcomputers.com.qualitycooling.Global.Util.CAMERA_IMAGE;
import static sjcomputers.com.qualitycooling.Global.Util.DOCUMENT_FILE;
import static sjcomputers.com.qualitycooling.Global.Util.MSG_DOCUMENT_ADDED;
import static sjcomputers.com.qualitycooling.Global.Util.MSG_IMAGE_ROTATED;

/**
 * Created by RabbitJang on 8/29/2018.
 */

public class DocumentActivity extends AppCompatActivity {
    public static int orderId;
    public static String driverOrderId;

    public static int documentType; //0: Admin, 1: Driver, 2: Customer
    private Bitmap selectedBmp;
    private String fileName;
    Dialog documentUploadDialog;
    public static Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == MSG_IMAGE_ROTATED) {
                    selectedBmp = (Bitmap) msg.obj;
                    uploadPicture();
                }
            }
        };
        configureDesign();
    }

    private void configureDesign() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.colorAccent)));
        /*if(documentType == 1) {
            getSupportActionBar().setTitle("Order #:" + driverOrderId);
        } else {
            getSupportActionBar().setTitle("Order #:" + String.format("%d", orderId));
        }*/
        getSupportActionBar().setTitle("");

        ListView documentLv = (ListView)findViewById(R.id.document_lv);
        DocumentAdapter adapter = new DocumentAdapter(this);
        documentLv.setAdapter(adapter);

        Button uploadBt = (Button)findViewById(R.id.button4);
        if(documentType == 1) {
            uploadBt.setVisibility(View.GONE);
        }
        uploadBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUploadDocumentDialog();
            }
        });
    }

    private void showUploadDocumentDialog() {
        documentUploadDialog = new Dialog(this);
        documentUploadDialog.requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        documentUploadDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        documentUploadDialog.setContentView(R.layout.dialog_upload);
        documentUploadDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        documentUploadDialog.getWindow().setGravity(Gravity.CENTER);
        documentUploadDialog.show();

        final EditText fileNameEt = (EditText)documentUploadDialog.findViewById(R.id.urlText);
        Button closeBt = (Button)documentUploadDialog.findViewById(R.id.closeBtn);
        closeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                documentUploadDialog.hide();
            }
        });

        Button picBt = (Button)documentUploadDialog.findViewById(R.id.pic_bt);
        picBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileName = fileNameEt.getText().toString();
                if(fileName.equals("")) {
                    Util.showToast("Please input file name", DocumentActivity.this);
                    return;
                }

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    boolean permission1 = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
                    boolean permission2 = checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
                    if(permission1 && permission2) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "image.jpg");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(DocumentActivity.this, getApplicationContext().getPackageName() + ".my.package.name.provider", file));
                        intent.putExtra("return-data", true);
                        startActivityForResult(intent, CAMERA_IMAGE);
                    }
                    else {
                        Util.showToast("Please check storage permission and camera permission", DocumentActivity.this);
                    }
                }
                else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File file = new File(Environment.getExternalStorageDirectory() + File.separator + "image.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(DocumentActivity.this, getApplicationContext().getPackageName() + ".my.package.name.provider", file));
                    intent.putExtra("return-data", true);
                    startActivityForResult(intent, CAMERA_IMAGE);
                }
            }
        });

        Button fileBt = (Button)documentUploadDialog.findViewById(R.id.file_bt);
        fileBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileName = fileNameEt.getText().toString();
                if(fileName.equals("")) {
                    Util.showToast("Please input file name", DocumentActivity.this);
                    return;
                }

                new MaterialFilePicker()
                        .withActivity(DocumentActivity.this)
                        .withRequestCode(DOCUMENT_FILE)
                        .withFilter(Pattern.compile(".*\\.*$")) // Filtering files and directories by file name using regexp
                        .withFilterDirectories(true) // Set directories filterable (false by default)
                        .withHiddenFiles(false) // Show hidden files and folders
                        .start();
            }
        });
    }

    private void uploadPicture() {
        String pictureString = BitmapToString(selectedBmp);
        APIManager.getInstance().setCallback( new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                Util.hideProgressDialog();
                if (objAPIResult != null) {
                    try {
                        //if(objAPIResult.getString("StatusCode").equals("Success")) {
                        documentUploadDialog.hide();
                        DocumentAdapter.handler.sendEmptyMessage(MSG_DOCUMENT_ADDED);
                        /*} else {
                            Util.showToast("Failed and try again", DocumentActivity.this);
                            Log.d("result", "success");
                        }*/
                    } catch (Exception e) {
                        Util.showToast("Failed and try again", DocumentActivity.this);
                    }
                } else {
                    Util.showToast("Failed and try again", DocumentActivity.this);
                }

            }
        });

        Util.showProgressDialog("Uploading picture..", DocumentActivity.this);
        APIManager.getInstance().uploadPicture(orderId, UserData.getInstance().userId, pictureString, fileName);
    }

    private void uploadDocumentFile(File file, String extension) {
        String documentStr = "";
        try {
            RandomAccessFile f = new RandomAccessFile(file, "r");
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength)
                throw new IOException("File size >= 2 GB");
            // Read file and return data
            byte[] data = new byte[length];
            f.readFully(data);
            documentStr =  Base64.encodeToString(data, Base64.DEFAULT);
            f.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        APIManager.getInstance().setCallback( new APIManagerCallback() {
            @Override
            public void APICallback(JSONObject objAPIResult) {
                Util.hideProgressDialog();
                if (objAPIResult != null) {
                    try {
                        //if(objAPIResult.getString("StatusCode").equals("Success")) {
                        documentUploadDialog.hide();
                        DocumentAdapter.handler.sendEmptyMessage(MSG_DOCUMENT_ADDED);
                        /*} else {
                            Util.showToast("Failed and try again", DocumentActivity.this);
                            Log.d("result", "success");
                        }*/
                    } catch (Exception e) {
                        Util.showToast("Failed and try again", DocumentActivity.this);
                    }
                } else {
                    Util.showToast("Failed and try again", DocumentActivity.this);
                }

            }
        });

        Util.showProgressDialog("Uploading document file..", DocumentActivity.this);
        APIManager.getInstance().uploadDocument(orderId, UserData.getInstance().userId, documentStr, String.format("%s%s", fileName, extension));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == -1) {
            if(requestCode == CAMERA_IMAGE) {
                File file = new File(Environment.getExternalStorageDirectory()+File.separator + "image.jpg");
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
                try {
                    ExifInterface ei = new ExifInterface(file.getAbsolutePath());
                    //bitmap = Util.getResizedBitmap(bitmap, 640);
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            bitmap = Util.RotateBitmap(bitmap, 90);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            bitmap = Util.RotateBitmap(bitmap, 180);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            bitmap = Util.RotateBitmap(bitmap, 270);
                            break;
                        default:
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                file.delete();
                Util.deleteFileFromMediaStore(getContentResolver(), file);
                selectedBmp = bitmap;

                Intent intent = new Intent(DocumentActivity.this, RotateImageActivity.class);
                RotateImageActivity.bitmap = selectedBmp;
                startActivity(intent);
            }
            else if(requestCode == DOCUMENT_FILE) {
                String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
                File file = new File(filePath);
                int dotPos = filePath.lastIndexOf(".");
                String extension = filePath.substring(dotPos, filePath.length());
                uploadDocumentFile(file, extension);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
